package com.example.crashreportver3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    EditText username, password;
    Button signin;
    TextView login_text;
    FirebaseAuth nFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signin = findViewById(R.id.submit);
        login_text = findViewById(R.id.login);


        nFirebaseAuth = FirebaseAuth.getInstance();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String musername = username.getText().toString();
                String mpassword = password.getText().toString();
                if(musername.isEmpty()){
                    username.setError("Please enter username");
                    username.requestFocus();
                }
                else if(mpassword.isEmpty()){
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else if(musername.isEmpty() && mpassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
                }
                else if(!(musername.isEmpty() && mpassword.isEmpty())){


                    nFirebaseAuth.createUserWithEmailAndPassword(musername, mpassword).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            else {

                                nFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(RegistrationActivity.this, "Verification Mail has been sent. Please check your email.", Toast.LENGTH_SHORT).show();
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                                    .child(nFirebaseAuth.getCurrentUser().getUid());
                                            databaseReference.setValue(new UserInformation());
                                        }
                                        else{
                                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(in);
                finish();
            }
        });
    }

    private void addDummyData() {

    }
}
