package com.faaiz.noteapp;

import static com.faaiz.noteapp.MainActivity.ADD_CODE;
import static com.faaiz.noteapp.MainActivity.NOTE_SHARED_PREFERENCE;
import static com.faaiz.noteapp.MainActivity.UPDATE_CODE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.faaiz.noteapp.Adapters.NoteAdapter;
import com.faaiz.noteapp.Database.NoteDB;
import com.faaiz.noteapp.Model.Note;
import com.faaiz.noteapp.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ActivitySearchBinding binding;
    NoteAdapter noteAdapter;
    NoteDB database;
    List<Note> list;
    Note longPressedNote;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchView = findViewById(R.id.searchView);
        database = NoteDB.getInstance(this);

        list = database.dao().getAll();

        Log.d("TAG", "onCreate: activity created");

        updateNoteList();
        binding.recyclerViewWrite.setVisibility(View.INVISIBLE);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TAG", "onQueryTextChange: inside onQueryTextChange");
                noteAdapter.getFilter().filter(newText);
                binding.recyclerViewWrite.setVisibility(View.VISIBLE);
//                updateNoteList(true);
                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == UPDATE_CODE){
                Log.d("TAG", "onActivityResult: inside onActivityResult");
                Note updatedNote = (Note) data.getSerializableExtra("note");
                database.dao().update(updatedNote.getId(), updatedNote.getTitle(), updatedNote.getContent(), updatedNote.getDate(), updatedNote.isPinned());
                list.clear();
                list.addAll(database.dao().getAll());
//                noteAdapter.notifyDataSetChanged();
                updateNoteList();
                binding.recyclerViewWrite.setVisibility(View.VISIBLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void updateNoteList(){
        binding.recyclerViewWrite.setHasFixedSize(true);
        SharedPreferences sharedPreferences = getSharedPreferences(NOTE_SHARED_PREFERENCE,MODE_PRIVATE);
        if(sharedPreferences.getString("listStyle", "").equals("")){
            binding.recyclerViewWrite.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }else{
            binding.recyclerViewWrite.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
//        binding.recyclerViewWrite.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteAdapter = new NoteAdapter(list,this,onNotesClickListener);
        binding.recyclerViewWrite.setAdapter(noteAdapter);
    }

    OnNotesClickListener onNotesClickListener = new OnNotesClickListener() {
        @Override
        public void setOnNotesClick(Note note) {
            Intent intent = new Intent(SearchActivity.this, WriteNote.class);
            intent.putExtra("editNote", note);
            startActivityForResult(intent, UPDATE_CODE);
        }

        @Override
        public void setOnNotesLongClick(Note note, CardView card) {
            longPressedNote =  note;
            showPopUpMenu(card);
        }
    };

    private void showPopUpMenu(CardView card) {
        PopupMenu popupMenu = new PopupMenu(SearchActivity.this, card);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        popupMenu.show();
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.togglePin){
            if(longPressedNote.isPinned()){
                longPressedNote.setPinned(false);
                Toast.makeText(this, "Unpinned!!", Toast.LENGTH_SHORT).show();
            }else{
                longPressedNote.setPinned(true);
                Toast.makeText(this, "Pinned!!", Toast.LENGTH_SHORT).show();
            }
            database.dao().update(longPressedNote.getId(), longPressedNote.getTitle(), longPressedNote.getContent(), longPressedNote.getDate(), longPressedNote.isPinned());
            list.clear();
            list.addAll(database.dao().getAll());
            noteAdapter.notifyDataSetChanged();
            return true;
        }
        if(id == R.id.Delete){
            database.dao().delete(longPressedNote);
            list.clear();
            list.addAll(database.dao().getAll());
            noteAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Note Deleted!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}