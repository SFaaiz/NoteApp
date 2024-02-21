package com.faaiz.noteapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.faaiz.noteapp.Adapters.NoteAdapter;
import com.faaiz.noteapp.Authentication.AccountActivity;
import com.faaiz.noteapp.Authentication.AccountsHomeActivity;
import com.faaiz.noteapp.Database.NoteDB;
import com.faaiz.noteapp.Model.Note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.faaiz.noteapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    // updated

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    public static final int UPDATE_CODE = 102;
    public static final int ADD_CODE = 101;
    NoteDB database;
    static List<Note> list;
    NoteAdapter noteAdapter;
    Note longPressedNote;
    SharedPreferences sharedPreferences;
    public static final String NOTE_SHARED_PREFERENCE = "noteSharedPreference";
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Write a message to the database
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("message");

//        myRef.setValue("Hello, Faaiz!");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue(String.class);
                Log.d("FireBase", "onDataChange: " + data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "onCancelled: " , error.toException());
            }
        });


        sharedPreferences = getSharedPreferences(NOTE_SHARED_PREFERENCE,MODE_PRIVATE);

//        SharedPreferences.Editor editor = sharedPreferences.edit();

        database = NoteDB.getInstance(MainActivity.this);
        list = database.dao().getAll();
        updateNotesList();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteNote.class);
                startActivityForResult(intent, ADD_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == ADD_CODE){
                Note newNote = (Note) data.getSerializableExtra("note");
                database.dao().insert(newNote);
                list.clear();
                list.addAll(database.dao().getAll());
                noteAdapter.notifyDataSetChanged();
            }
            if(requestCode == UPDATE_CODE){
                Note editNote = (Note) data.getSerializableExtra("note");
                database.dao().update(editNote.getId(), editNote.getTitle(), editNote.getContent(), editNote.getDate(), editNote.isPinned());
                list.clear();
                list.addAll(database.dao().getAll());
                noteAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateNotesList(){
        binding.recyclerViewMain.setHasFixedSize(true);
        noteAdapter = new NoteAdapter(list, this, onNotesClickListener);
        if(sharedPreferences.getString("listStyle", "").equals("")){
            binding.recyclerViewMain.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }else{
            binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
        binding.recyclerViewMain.setAdapter(noteAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Menu mainMenu = menu;
        if(sharedPreferences.getString("listStyle", "").equals("")){
            menu.getItem(1).setTitle("List View");
        }else{
            menu.getItem(1).setTitle("Grid View");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.searchIcon) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.changeView){
            SharedPreferences sp = getSharedPreferences(NOTE_SHARED_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            if(sp.getString("listStyle", "").equals("")){
                editor.putString("listStyle","list");
                item.setTitle("Grid View");
            }else{
                editor.putString("listStyle", "");
                item.setTitle("List View");
            }
            editor.apply();
            updateNotesList();
            return true;
        }

        if(id == R.id.accountsOption){
            if(auth.getCurrentUser() != null){
                Intent intent = new Intent(MainActivity.this, AccountsHomeActivity.class);
                startActivity(intent);
                return true;
            }
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    OnNotesClickListener onNotesClickListener = new OnNotesClickListener() {
        @Override
        public void setOnNotesClick(Note note) {
            Intent intent = new Intent(MainActivity.this, WriteNote.class);
            intent.putExtra("editNote", note);
            startActivityForResult(intent, UPDATE_CODE);
        }

        @Override
        public void setOnNotesLongClick(Note note, CardView card) {
            longPressedNote = note;
            showPopUpMenu(card);
        }


    };

    private void showPopUpMenu(CardView card) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, card);
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