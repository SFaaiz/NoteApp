package com.faaiz.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.faaiz.noteapp.Model.Note;
import com.faaiz.noteapp.databinding.ActivityWriteNoteBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteNote extends AppCompatActivity {

    ActivityWriteNoteBinding binding;
    Note note;
    boolean isNewNote = true;
    TextView tvTitle, tvNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

        binding = ActivityWriteNoteBinding.inflate(getLayoutInflater());

        tvTitle = findViewById(R.id.tvTitle);
        tvNotes = findViewById(R.id.tvContent);

        note = new Note();

        try{
            note = (Note) getIntent().getSerializableExtra("editNote");
            tvTitle.setText(note.getTitle());
            tvNotes.setText(note.getContent());
            isNewNote = false;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("TAG", "onCreateOptionsMenu: options menu created");
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("TAG", "onOptionsItemSelected: item selected");

        String title = tvTitle.getText().toString();
        String details = tvNotes.getText().toString();

        if(title.isEmpty()){
            Toast.makeText(this, "Please give a title", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(details.isEmpty()){
            Toast.makeText(this, "Please give a description", Toast.LENGTH_SHORT).show();
            return true;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE d MM yyyy HH:mm a");
        Date date = new Date();

        if(isNewNote) note = new Note();
        note.setTitle(title);
        note.setContent(details);
        note.setDate(simpleDateFormat.format(date));

        Log.d("TAG", "onOptionsItemSelected: inside onOptionsItemSelected " + title + " " + details);

        Intent intent = new Intent();
        intent.putExtra("note", note);
        setResult(Activity.RESULT_OK,intent);
        finish();
        return true;
    }
}