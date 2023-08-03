package com.faaiz.noteapp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.faaiz.noteapp.Model.Note;

import java.util.List;

@Dao
public interface DAO {

    @Insert
    void insert(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM noteTable ORDER BY id DESC")
    List<Note> getAll();

    @Query("UPDATE noteTable SET title = :title, content = :content, date = :date, isPinned = :isPinned WHERE id = :id")
    void update(int id, String title, String content, String date, boolean isPinned);
}
