package com.faaiz.noteapp.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.faaiz.noteapp.MainActivity;
import com.faaiz.noteapp.Model.User;
import com.faaiz.noteapp.R;
import com.faaiz.noteapp.databinding.ActivityAccountsHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountsHomeActivity extends AppCompatActivity {

    ActivityAccountsHomeBinding binding;
    FirebaseDatabase database;
    DatabaseReference myRefUsername;
    DatabaseReference myRefEmail;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountsHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        myRefUsername = database.getReference().child("Users").child(AccountActivity.UID);
//        myRefUsername = database.getReference().child("Users").child(AccountActivity.UID).child("userName");
//        myRefEmail = database.getReference().child("Users").child(AccountActivity.UID).child("userName");
        auth = FirebaseAuth.getInstance();

        myRefUsername.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = "";
                String email = "";
                for(DataSnapshot data: snapshot.getChildren()){
                    System.out.println(data.getValue() + " printing");
                    User user = data.getValue(User.class);
                    username = user.getUserName();
                    email = user.getEmail();
                }
                binding.tvUsername.setText("Username:  " + username);
                binding.tvEmail.setText("Email:  " + email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "onCancelled: ", error.toException());
            }
        });

        binding.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountsHomeActivity.this);
                alertDialog.setTitle("Sign Out");
                alertDialog.setMessage("Are your sure, you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                auth.signOut();
                                Toast.makeText(AccountsHomeActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AccountsHomeActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                
                            }
                        });
                alertDialog.show();
            }
        });
    }

    public boolean showAlertDialogBox(){
        final boolean[] toshow = {false};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountsHomeActivity.this);
        alertDialog.setTitle("Sign Out");
        alertDialog.setMessage("Are your sure, you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toshow[0] = true;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertDialog.show();
        return toshow[0];
    }
}