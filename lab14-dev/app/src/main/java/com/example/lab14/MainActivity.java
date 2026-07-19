package com.example.lab14;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab14.model.Employee;
import com.example.lab14.security.TokenVault;
import com.example.lab14.storage.EmployeesJsonManager;
import com.example.lab14.storage.ExternalExportManager;
import com.example.lab14.storage.LocalTextManager;
import com.example.lab14.storage.SettingsManager;
import com.example.lab14.storage.TemporaryCache;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "VaultGuardApp";
    private final List<String> languageOptions = Arrays.asList("Français", "English", "Español");

    private TextInputEditText etUserName;
    private TextInputEditText etSecureToken;
    private Spinner spLanguage;
    private MaterialSwitch switchDarkMode;
    private TextView tvConsoleOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUserName = findViewById(R.id.etUserName);
        etSecureToken = findViewById(R.id.etSecureToken);
        spLanguage = findViewById(R.id.spLanguage);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        tvConsoleOutput = findViewById(R.id.tvConsoleOutput);

        setupLanguageSpinner();

        Button btnSavePrefs = findViewById(R.id.btnSavePrefs);
        Button btnLoadPrefs = findViewById(R.id.btnLoadPrefs);
        Button btnSaveJson = findViewById(R.id.btnSaveJson);
        Button btnLoadJson = findViewById(R.id.btnLoadJson);
        Button btnClearAll = findViewById(R.id.btnClearAll);

        btnSavePrefs.setOnClickListener(v -> savePreferencesData());
        btnLoadPrefs.setOnClickListener(v -> loadPreferencesData());
        btnSaveJson.setOnClickListener(v -> saveJsonAndFiles());
        btnLoadJson.setOnClickListener(v -> loadJsonAndFiles());
        btnClearAll.setOnClickListener(v -> performFullWipe());

        loadPreferencesData();
    }

    private void setupLanguageSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languageOptions);
        spLanguage.setAdapter(adapter);
    }

    private void savePreferencesData() {
        String userName = etUserName.getText() != null ? etUserName.getText().toString().trim() : "";
        String language = languageOptions.get(Math.max(0, spLanguage.getSelectedItemPosition()));
        boolean isDarkMode = switchDarkMode.isChecked();

        boolean isSaved = SettingsManager.saveSettings(this, userName, language, isDarkMode, false);

        String token = etSecureToken.getText() != null ? etSecureToken.getText().toString() : "";
        if (!token.isEmpty()) {
            try {
                TokenVault.saveSecureToken(this, token);
            } catch (Exception e) {
                tvConsoleOutput.setText("Erreur chiffrement token : " + e.getMessage());
                return;
            }
        }

        Log.d(TAG, "Préférences sauvegardées : ok=" + isSaved + ", userName=" + userName + ", lang=" + language);

        try {
            TemporaryCache.writeToCache(this, "latest_action.log", "Action: Save Prefs | User: " + userName);
        } catch (Exception ignored) {}

        tvConsoleOutput.setText(
                "✅ Préférences enregistrées.\n" +
                "Utilisateur : " + userName + "\n" +
                "Langue : " + language + "\n" +
                "Mode Sombre : " + isDarkMode + "\n" +
                "Token : [Stocké de manière chiffrée]"
        );
    }

    private void loadPreferencesData() {
        SettingsManager.SettingsData data = SettingsManager.loadSettings(this);

        etUserName.setText(data.userName);
        switchDarkMode.setChecked(data.isDarkMode);

        int langIndex = languageOptions.indexOf(data.language);
        spLanguage.setSelection(langIndex >= 0 ? langIndex : 0);

        int tokenLength = 0;
        try {
            String token = TokenVault.loadSecureToken(this);
            tokenLength = token == null ? 0 : token.length();
        } catch (Exception ignored) {}

        tvConsoleOutput.setText(
                "🔄 Préférences chargées.\n" +
                "Utilisateur : " + data.userName + "\n" +
                "Langue : " + data.language + "\n" +
                "Mode Sombre : " + data.isDarkMode + "\n" +
                "Token (longueur) : " + tokenLength
        );

        Log.d(TAG, "Préférences chargées : userName=" + data.userName + " | Token Length=" + tokenLength);
    }

    private void saveJsonAndFiles() {
        List<Employee> employees = Arrays.asList(
                new Employee(101, "Alice Martin", "Ingénierie"),
                new Employee(102, "Bob Durand", "Ressources Humaines"),
                new Employee(103, "Charlie Dupont", "Direction")
        );

        try {
            EmployeesJsonManager.saveEmployees(this, employees);
            LocalTextManager.writeTextFile(this, "audit_log.txt", "Audit : Données JSON sauvegardées avec succès.");
            ExternalExportManager.exportData(this, "public_export.txt", "Ceci est un export public de VaultGuard.");
        } catch (Exception e) {
            tvConsoleOutput.setText("Erreur lors de la sauvegarde fichiers : " + e.getMessage());
            return;
        }

        Log.d(TAG, "Fichiers internes et export générés.");
        tvConsoleOutput.setText("✅ Fichiers sauvegardés.\nEmployés générés : " + employees.size() + "\nFichier texte interne créé.");
    }

    private void loadJsonAndFiles() {
        List<Employee> employees = EmployeesJsonManager.loadEmployees(this);

        String internalLog;
        try {
            internalLog = LocalTextManager.readTextFile(this, "audit_log.txt");
        } catch (Exception e) {
            internalLog = "(Fichier introuvable)";
        }
        
        String externalLog;
        try {
            externalLog = ExternalExportManager.readExportedData(this, "public_export.txt");
        } catch (Exception e) {
            externalLog = "(Export introuvable)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🔄 Fichiers chargés.\n\n");
        sb.append("Log Interne : ").append(internalLog).append("\n");
        sb.append("Export Externe : ").append(externalLog).append("\n\n");
        sb.append("Employés (").append(employees.size()).append(") :\n");
        
        for (Employee emp : employees) {
            sb.append(" • [").append(emp.employeeId).append("] ")
              .append(emp.fullName).append(" (").append(emp.department).append(")\n");
        }

        tvConsoleOutput.setText(sb.toString());
        Log.d(TAG, "Fichiers chargés avec " + employees.size() + " employés.");
    }

    private void performFullWipe() {
        SettingsManager.wipeSettings(this);

        try {
            TokenVault.wipeSecureToken(this);
        } catch (Exception ignored) {}

        EmployeesJsonManager.deleteEmployeesData(this);
        LocalTextManager.deleteTextFile(this, "audit_log.txt");
        ExternalExportManager.deleteExportedData(this, "public_export.txt");

        int purgedCacheFiles = TemporaryCache.clearCache(this);

        etUserName.setText("");
        etSecureToken.setText("");
        switchDarkMode.setChecked(false);
        spLanguage.setSelection(0);

        tvConsoleOutput.setText(
                "🗑️ Nettoyage complet terminé.\n" +
                "- Préférences effacées.\n" +
                "- Token sécurisé supprimé.\n" +
                "- Fichiers JSON et TXT internes supprimés.\n" +
                "- Export externe supprimé.\n" +
                "- Fichiers cache purgés : " + purgedCacheFiles
        );

        Log.d(TAG, "Wipe complet exécuté. Aucune donnée sensible dans les logs.");
    }
}