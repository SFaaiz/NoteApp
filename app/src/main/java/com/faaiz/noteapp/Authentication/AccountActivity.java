package com.faaiz.noteapp.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.faaiz.noteapp.MainActivity;
import com.faaiz.noteapp.Model.User;
import com.faaiz.noteapp.R;
import com.faaiz.noteapp.databinding.ActivityAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AccountActivity extends AppCompatActivity {

    ActivityAccountBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    public static String UID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        binding = ActivityAccountBinding.inflate(getLayoutInflater());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        createProgressDialog();

        Button signUpBtn = findViewById(R.id.signUpBtn);
        EditText etUserName = findViewById(R.id.et_userName);
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        TextView tvGotoLogin = findViewById(R.id.gotoLogin);

        tvGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String userName = etUserName.getText().toString().trim();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if(userName.isEmpty()) {
                    Toast.makeText(AccountActivity.this, "Please enter a user name", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("TAG", "onClick: button clicked");
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AccountActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    String uid = task.getResult().getUser().getUid();
                                    UID = uid;
                                    User user = new User(userName,email,password);
                                    database.getReference().child("Users").child(uid).setValue(user);
                                    Toast.makeText(AccountActivity.this, "Account Created Successfully!!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Log.w("TAG", "onComplete: " + task.getException().getMessage().toString());
                                    Toast.makeText(AccountActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

    public void createProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");
    }
}