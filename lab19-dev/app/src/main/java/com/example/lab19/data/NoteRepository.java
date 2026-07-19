package com.example.lab19.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.lab19.data.local.AppDatabase;
import com.example.lab19.data.local.NoteDao;
import com.example.lab19.data.local.NoteEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NoteRepository — couche intermédiaire entre le ViewModel et les sources de données.
 *
 * Responsabilités :
 *  - Centraliser l'accès aux données (Room ici, potentiellement une API distante plus tard)
 *  - Déclencher les opérations d'écriture sur un thread secondaire
 *  - Exposer les données observables (LiveData) au ViewModel
 */
public class NoteRepository {

    private static final String TAG = "NoteRepository";

    private final NoteDao noteDao;
    private final LiveData<List<NoteEntity>> allNotes;

    /**
     * Thread pool dédié aux opérations de base de données.
     * Un seul thread suffit ici pour éviter les conflits d'écriture concurrente.
     */
    private final ExecutorService dbExecutor;

    public NoteRepository(Application app) {
        AppDatabase database = AppDatabase.getInstance(app);
        noteDao = database.noteDao();
        allNotes = noteDao.fetchAllNotes();
        dbExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Insère une nouvelle note dans la base en arrière-plan.
     */
    public void addNote(NoteEntity note) {
        dbExecutor.execute(() -> {
            Log.d(TAG, "Insertion note : " + note.getNoteTitle());
            noteDao.insertNote(note);
        });
    }

    /**
     * Supprime la note spécifiée en arrière-plan.
     */
    public void deleteNote(NoteEntity note) {
        dbExecutor.execute(() -> {
            Log.d(TAG, "Suppression note id=" + note.getNoteId());
            noteDao.removeNote(note);
        });
    }

    /**
     * Supprime toutes les notes de la base en arrière-plan.
     */
    public void deleteAll() {
        dbExecutor.execute(() -> {
            Log.d(TAG, "Suppression de toutes les notes");
            noteDao.clearAllNotes();
        });
    }

    /**
     * Retourne la liste observable des notes.
     * Room met automatiquement ce LiveData à jour à chaque changement de la base.
     */
    public LiveData<List<NoteEntity>> getAllNotes() {
        return allNotes;
    }
}
