package com.example.lab19.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * NoteDao — interface d'accès aux données Room.
 * Toutes les opérations CRUD sur la table "notes" passent par ici.
 * Room génère automatiquement l'implémentation à la compilation.
 */
@Dao
public interface NoteDao {

    /**
     * Insère une nouvelle note dans la base.
     * En cas de conflit (id identique), remplace l'entrée existante.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(NoteEntity note);

    /**
     * Supprime la note passée en paramètre (via son id).
     */
    @Delete
    void removeNote(NoteEntity note);

    /**
     * Supprime toutes les notes de la table.
     */
    @Query("DELETE FROM notes")
    void clearAllNotes();

    /**
     * Retourne toutes les notes triées de la plus récente à la plus ancienne.
     * Le LiveData garantit que l'UI est notifiée automatiquement à chaque changement.
     */
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    LiveData<List<NoteEntity>> fetchAllNotes();
}
