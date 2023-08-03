package com.faaiz.noteapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.faaiz.noteapp.Model.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NoteDB extends RoomDatabase {
    public static NoteDB database;
    public static String DATABASE_NAME = "NoteDB";

    public synchronized static NoteDB getInstance(Context context){
        if(database == null){
            database = Room.databaseBuilder(context.getApplicationContext(), NoteDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract DAO dao();
}
