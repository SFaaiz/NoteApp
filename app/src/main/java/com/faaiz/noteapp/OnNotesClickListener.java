package com.faaiz.noteapp;

import androidx.cardview.widget.CardView;

import com.faaiz.noteapp.Model.Note;

public interface OnNotesClickListener {
    void setOnNotesClick(Note note);
    void setOnNotesLongClick(Note note, CardView card);
}
