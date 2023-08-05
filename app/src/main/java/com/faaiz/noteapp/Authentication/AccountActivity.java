package com.faaiz.noteapp.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class AccountActivity extends AppCompatActivity {

    ActivityAccountBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    public static String UID = "";
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        binding = ActivityAccountBinding.inflate(getLayoutInflater());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        createProgressDialog();

        Button signUpBtn = findViewById(R.id.signUpBtn);
        Button googleBtn = findViewById(R.id.googleBtn);
        EditText etUserName = findViewById(R.id.et_userName);
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        TextView tvGotoLogin = findViewById(R.id.gotoLogin);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(AccountActivity.this, googleSignInOptions);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });




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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: inside acitivity result");
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> signInAccountTask;
            signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d("TAG", "onActivityResult: inside request code");
            // check condition
            if (signInAccountTask.isSuccessful()) {
                Log.d("TAG", "onActivityResult: inside task successful");
                // When google sign in successful initialize string
                String s = "Google sign in successful";
                displayToast(s);
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        Log.d("TAG", "onActivityResult: account is not null");
                        // When sign in account is not equal to null initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Check condition
                                if (task.isSuccessful()) {
                                    String uid = task.getResult().getUser().getUid();
                                    UID = uid;
                                    String email = mAuth.getCurrentUser().getEmail();
                                    String username = mAuth.getCurrentUser().getDisplayName();
                                    User user = new User(username,email);
                                    database.getReference().child("Users").child(uid).setValue(user);
                                    // When task is successful redirect to profile activity display Toast
                                    startActivity(new Intent(AccountActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    displayToast("Firebase authentication successful");
                                } else {
                                    // When task is unsuccessful display Toast
                                    displayToast("Authentication Failed :" + task.getException().getMessage());
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    Log.w("TAG", "onActivityResult: ", e.getCause());
                    e.printStackTrace();
                }
            }else{
                Log.d("TAG", "onActivityResult: task is not successful");
            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public void createProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");
    }
}