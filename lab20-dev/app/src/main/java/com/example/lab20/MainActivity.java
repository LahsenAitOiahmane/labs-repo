package com.example.lab20;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * PhoneVault — Activité principale.
 *
 * Fonctionnalités :
 *  1. Demande la permission READ_CONTACTS ;
 *  2. Lit les contacts du téléphone via ContentResolver ;
 *  3. Les affiche dans un RecyclerView avec cartes personnalisées ;
 *  4. Synchronise les contacts vers le backend via Retrofit ;
 *  5. Recherche des contacts dans la base distante.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PhoneVault";

    // ─── UI ─────────────────────────────────────────────────────
    private MaterialButton   btnImport, btnPush, btnSearch;
    private EditText         etQuery;
    private RecyclerView     recyclerView;
    private TextView         tvCount, tvEmpty, tvSectionLabel;
    private ProgressBar      progressBar;

    // ─── Données & réseau ────────────────────────────────────────
    private EntryListAdapter adapter;
    private List<PhoneEntry> phoneEntries = new ArrayList<>();
    private VaultApi         vaultApi;

    // ─── Launcher permission ─────────────────────────────────────
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            loadDeviceContacts();
                        } else {
                            showMessage(getString(R.string.toast_permission_denied));
                        }
                    }
            );

    // ════════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupRecyclerView();
        setupListeners();

        vaultApi = ApiClient.getVaultApi();
    }

    // ─── Liaison des vues ────────────────────────────────────────

    private void bindViews() {
        btnImport      = findViewById(R.id.btn_import_contacts);
        btnPush        = findViewById(R.id.btn_push_to_server);
        btnSearch      = findViewById(R.id.btn_execute_search);
        etQuery        = findViewById(R.id.et_search_query);
        recyclerView   = findViewById(R.id.recycler_contacts);
        tvCount        = findViewById(R.id.tv_contact_count);
        tvEmpty        = findViewById(R.id.tv_empty_state);
        tvSectionLabel = findViewById(R.id.tv_section_label);
        progressBar    = findViewById(R.id.progress_indicator);
    }

    // ─── RecyclerView ────────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new EntryListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    // ─── Listeners des boutons ───────────────────────────────────

    private void setupListeners() {
        btnImport.setOnClickListener(v -> checkPermissionAndLoad());
        btnPush.setOnClickListener(v -> pushContactsToServer());
        btnSearch.setOnClickListener(v -> executeSearch());

        // Déclencher la recherche sur la touche "Entrée" du clavier
        etQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                executeSearch();
                return true;
            }
            return false;
        });
    }

    // ─── Gestion des permissions ─────────────────────────────────

    private void checkPermissionAndLoad() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            loadDeviceContacts();
        } else {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    // ─── Chargement des contacts du téléphone ───────────────────

    private void loadDeviceContacts() {
        showProgress(true);
        phoneEntries.clear();

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int nameIdx  = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                if (nameIdx < 0 || phoneIdx < 0) continue;

                String name  = cursor.getString(nameIdx);
                String phone = cursor.getString(phoneIdx);

                if (name != null && phone != null) {
                    phoneEntries.add(new PhoneEntry(name, phone));
                }
            }
            cursor.close();
        }

        updateListDisplay(phoneEntries, "Contacts du téléphone");
        showProgress(false);

        String msg = phoneEntries.isEmpty()
                ? "Aucun contact trouvé sur cet appareil"
                : phoneEntries.size() + " contact(s) importé(s)";
        showMessage(msg);
    }

    // ─── Synchronisation vers le serveur ────────────────────────

    private void pushContactsToServer() {
        if (phoneEntries.isEmpty()) {
            showMessage("Importez d'abord les contacts du téléphone");
            return;
        }

        showProgress(true);
        btnPush.setEnabled(false);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount    = new AtomicInteger(0);
        int total = phoneEntries.size();

        for (PhoneEntry entry : phoneEntries) {
            vaultApi.uploadEntry(entry).enqueue(new Callback<ServerResponse>() {

                @Override
                public void onResponse(@NonNull Call<ServerResponse> call,
                                       @NonNull Response<ServerResponse> response) {
                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().isSuccess()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                        Log.w(TAG, "Réponse non valide pour : " + entry.getDisplayName());
                    }
                    checkSyncCompletion(total, successCount.get(), failCount.get());
                }

                @Override
                public void onFailure(@NonNull Call<ServerResponse> call,
                                      @NonNull Throwable t) {
                    failCount.incrementAndGet();
                    Log.e(TAG, "Erreur réseau pour " + entry.getDisplayName(), t);
                    checkSyncCompletion(total, successCount.get(), failCount.get());
                }
            });
        }
    }

    /** Appelé après chaque réponse pour détecter la fin de la synchronisation */
    private void checkSyncCompletion(int total, int success, int fail) {
        if (success + fail >= total) {
            runOnUiThread(() -> {
                showProgress(false);
                btnPush.setEnabled(true);
                showMessage("✓ " + success + " synchronisé(s)  |  ✗ " + fail + " échec(s)");
            });
        }
    }

    // ─── Recherche distante ──────────────────────────────────────

    private void executeSearch() {
        String keyword = etQuery.getText().toString().trim();

        if (keyword.isEmpty()) {
            showMessage(getString(R.string.toast_no_keyword));
            return;
        }

        showProgress(true);

        vaultApi.lookupByKeyword(keyword).enqueue(new Callback<List<PhoneEntry>>() {

            @Override
            public void onResponse(@NonNull Call<List<PhoneEntry>> call,
                                   @NonNull Response<List<PhoneEntry>> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<PhoneEntry> results = response.body();
                    updateListDisplay(results, "Résultats de recherche (" + results.size() + ")");
                } else {
                    showMessage("Aucun résultat pour « " + keyword + " »");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PhoneEntry>> call,
                                  @NonNull Throwable t) {
                showProgress(false);
                Log.e(TAG, "Échec de la recherche", t);
                showMessage(getString(R.string.toast_network_error));
            }
        });
    }

    // ─── Helpers d'interface ─────────────────────────────────────

    /**
     * Met à jour le RecyclerView avec une nouvelle liste et
     * ajuste le libellé de section et le compteur.
     */
    private void updateListDisplay(List<PhoneEntry> list, String sectionLabel) {
        adapter.refreshData(list);
        tvSectionLabel.setText(sectionLabel);
        tvCount.setText(list.size() + " contact(s)");
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /** Affiche ou masque l'indicateur de chargement. */
    private void showProgress(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /** Affiche un Snackbar informatif (plus moderne que Toast). */
    private void showMessage(String message) {
        View root = findViewById(R.id.root_layout);
        if (root != null) {
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}