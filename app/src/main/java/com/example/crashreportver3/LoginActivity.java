package com.example.crashreportver3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText lemail, lpass;
    Button mlogin;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences("com.example.crashreport", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        lemail = findViewById(R.id.user_email);
        lpass = findViewById(R.id.password);
        mlogin = findViewById(R.id.submit);



        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser!= null && mFirebaseUser.isEmailVerified()){
                    Toast.makeText(LoginActivity.this, "You are Logged in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, UpdateDetails.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        mlogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String emailid = lemail.getText().toString();
                String mpassword = lpass.getText().toString();
                if(emailid.isEmpty()){
                    lemail.setError("Please enter Email id");
                    lemail.requestFocus();
                }
                else if(mpassword.isEmpty()){
                    lpass.setError("Please enter password");
                    lpass.requestFocus();
                }
                else if(emailid.isEmpty() && mpassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
                }
                else if(!(emailid.isEmpty() && mpassword.isEmpty())){

                    editor.putString("email", emailid);
                    editor.putString("pass", mpassword);
                    editor.commit();

                    mFirebaseAuth.signInWithEmailAndPassword(emailid, mpassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Error Occurred, Please try again", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                    Intent in = new Intent(LoginActivity.this, UpdateDetails.class);
                                    startActivity(in);
                                    finish();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Please Verify your email adress.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListner);
    }
}
