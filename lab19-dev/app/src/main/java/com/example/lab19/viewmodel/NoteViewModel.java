package com.example.lab19.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.lab19.data.NoteRepository;
import com.example.lab19.data.local.NoteEntity;

import java.util.List;

/**
 * NoteViewModel — gère l'état de l'écran et survit aux changements de configuration.
 *
 * Ce ViewModel :
 *  - ne contient pas de références à des composants UI (pas de Context, pas de View)
 *  - délègue toutes les opérations sur les données au NoteRepository
 *  - expose un LiveData observable à l'Activity
 *
 * Grâce à AndroidViewModel, la base Room reste accessible via l'Application context.
 */
public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository repository;
    private final LiveData<List<NoteEntity>> notesLiveData;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        notesLiveData = repository.getAllNotes();
    }

    /**
     * Demande l'ajout d'une note via le repository.
     */
    public void addNote(NoteEntity note) {
        repository.addNote(note);
    }

    /**
     * Demande la suppression d'une note spécifique.
     */
    public void removeNote(NoteEntity note) {
        repository.deleteNote(note);
    }

    /**
     * Demande la suppression de toutes les notes.
     */
    public void clearAllNotes() {
        repository.deleteAll();
    }

    /**
     * Retourne le LiveData observable contenant la liste des notes.
     * L'Activity s'abonne à ce LiveData pour mettre à jour automatiquement l'interface.
     */
    public LiveData<List<NoteEntity>> getNotesLiveData() {
        return notesLiveData;
    }
}
