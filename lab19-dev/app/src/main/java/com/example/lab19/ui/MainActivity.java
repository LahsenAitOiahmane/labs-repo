package com.example.lab19.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab19.R;
import com.example.lab19.data.local.NoteEntity;
import com.example.lab19.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * MainActivity — point d'entrée de l'application NoteKeeper.
 *
 * Rôle (pattern MVVM) :
 *  - Observer le NoteViewModel pour réagir aux changements de données
 *  - Déléguer les actions utilisateur (ajout, suppression) au ViewModel
 *  - Gérer l'affichage : RecyclerView, état vide, FAB, BottomSheet
 *
 * L'Activity ne contient aucune logique de base de données ou de réseau.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private NoteViewModel noteViewModel;
    private NoteCardAdapter noteAdapter;
    private View llEmptyState;
    private RecyclerView recyclerNotes;
    private TextView tvNoteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupFab();
        setupDeleteAllButton();
    }

    // --- Initialisation des vues ---

    private void initViews() {
        llEmptyState = findViewById(R.id.ll_empty_state);
        recyclerNotes = findViewById(R.id.recycler_notes);
        tvNoteCount = findViewById(R.id.tv_note_count);

        Log.d(TAG, "Vues initialisées");
    }

    // --- Configuration du RecyclerView ---

    private void setupRecyclerView() {
        noteAdapter = new NoteCardAdapter();
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotes.setHasFixedSize(false);
        recyclerNotes.setAdapter(noteAdapter);

        // Clic simple : afficher un résumé de la note
        noteAdapter.setOnNoteClickListener(note -> {
            String snippet = "📌 " + note.getNoteTitle() + "\n" + note.getNoteContent();
            Snackbar.make(
                    findViewById(R.id.coordinator_root),
                    snippet,
                    Snackbar.LENGTH_LONG
            ).show();
        });

        // Clic long : confirmation avant suppression
        noteAdapter.setOnNoteLongClickListener(note -> showDeleteDialog(note));

        Log.d(TAG, "RecyclerView configuré");
    }

    // --- Connexion au ViewModel + observation du LiveData ---

    private void setupViewModel() {
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        noteViewModel.getNotesLiveData().observe(this, this::onNotesUpdated);

        Log.d(TAG, "ViewModel connecté");
    }

    /**
     * Appelé automatiquement par le LiveData dès que la liste de notes change.
     */
    private void onNotesUpdated(List<NoteEntity> notes) {
        noteAdapter.refreshNotes(notes);

        boolean isEmpty = (notes == null || notes.isEmpty());
        llEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerNotes.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        int count = (notes != null) ? notes.size() : 0;
        tvNoteCount.setText(String.valueOf(count));

        Log.d(TAG, "Liste mise à jour : " + count + " note(s)");
    }

    // --- FAB : ouvrir le BottomSheet ---

    private void setupFab() {
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_add_note);

        // Réduire le FAB lors du défilement
        recyclerNotes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (dy > 0 && fab.isExtended()) {
                    fab.shrink();
                } else if (dy < 0 && !fab.isExtended()) {
                    fab.extend();
                }
            }
        });

        fab.setOnClickListener(v -> openAddNoteSheet());
    }

    /**
     * Affiche le formulaire d'ajout en bas de l'écran.
     */
    private void openAddNoteSheet() {
        AddNoteBottomSheet sheet = new AddNoteBottomSheet();

        sheet.setOnNoteSubmitListener((title, content) -> {
            NoteEntity newNote = new NoteEntity(title, content, System.currentTimeMillis());
            noteViewModel.addNote(newNote);

            Snackbar.make(
                    findViewById(R.id.coordinator_root),
                    getString(R.string.msg_note_saved),
                    Snackbar.LENGTH_SHORT
            ).show();
        });

        sheet.show(getSupportFragmentManager(), AddNoteBottomSheet.TAG_SHEET);
    }

    // --- Bouton "Tout supprimer" ---

    private void setupDeleteAllButton() {
        findViewById(R.id.btn_delete_all).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_delete_title))
                    .setMessage("Supprimer toutes les notes ? Cette action est irréversible.")
                    .setPositiveButton(getString(R.string.dialog_confirm), (dialog, which) -> {
                        noteViewModel.clearAllNotes();
                        Snackbar.make(
                                findViewById(R.id.coordinator_root),
                                getString(R.string.msg_all_deleted),
                                Snackbar.LENGTH_SHORT
                        ).show();
                    })
                    .setNegativeButton(getString(R.string.dialog_cancel), null)
                    .show();
        });
    }

    // --- Dialogue de confirmation pour suppression d'une note ---

    private void showDeleteDialog(NoteEntity note) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_delete_title))
                .setMessage("\"" + note.getNoteTitle() + "\"\n\n" + getString(R.string.dialog_delete_message))
                .setPositiveButton(getString(R.string.dialog_confirm), (dialog, which) -> {
                    noteViewModel.removeNote(note);
                    Snackbar.make(
                            findViewById(R.id.coordinator_root),
                            getString(R.string.msg_note_deleted),
                            Snackbar.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton(getString(R.string.dialog_cancel), null)
                .show();
    }
}
