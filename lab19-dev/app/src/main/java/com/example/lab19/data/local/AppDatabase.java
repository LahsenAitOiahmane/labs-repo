package com.example.lab19.data.local;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * AppDatabase — point d'entrée unique vers la base de données SQLite via Room.
 * Implémente le pattern Singleton thread-safe avec double vérification (double-checked locking).
 */
@Database(entities = {NoteEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = "AppDatabase";
    private static final String DB_NAME = "notekeeper.db";

    /** Instance unique — volatile pour garantir la visibilité entre threads */
    private static volatile AppDatabase sharedInstance;

    /**
     * Fournit l'accès à l'interface NoteDao.
     * Room génère l'implémentation concrète lors de la compilation.
     */
    public abstract NoteDao noteDao();

    /**
     * Retourne l'instance unique de la base Room.
     * Crée la base si elle n'existe pas encore.
     *
     * @param context Le contexte Android (application context utilisé en interne)
     * @return L'instance singleton d'AppDatabase
     */
    public static AppDatabase getInstance(Context context) {
        if (sharedInstance == null) {
            synchronized (AppDatabase.class) {
                if (sharedInstance == null) {
                    Log.d(TAG, "Création de la base de données : " + DB_NAME);
                    sharedInstance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME
                            )
                            // En dev : reconstruction si le schéma change
                            // En prod : utiliser des migrations explicites
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return sharedInstance;
    }
}
