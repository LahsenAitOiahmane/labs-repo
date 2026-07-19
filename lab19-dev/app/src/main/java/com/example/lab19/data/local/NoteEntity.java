package com.example.lab19.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * NoteEntity — représente une entrée dans la table "notes" de la base SQLite.
 * Chaque note possède un identifiant auto-généré, un titre et un contenu.
 */
@Entity(tableName = "notes")
public class NoteEntity {

    @PrimaryKey(autoGenerate = true)
    private int noteId;

    private String noteTitle;
    private String noteContent;
    private long createdAt;

    public NoteEntity(String noteTitle, String noteContent, long createdAt) {
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.createdAt = createdAt;
    }

    // --- Getters ---

    public int getNoteId() {
        return noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // --- Setter for Room (auto-generated id) ---

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }
}
