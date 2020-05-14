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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateDetails extends AppCompatActivity {

    EditText first_name, last_name, phone_number;
    EditText blood_group, street, pin_code, city, state;
    EditText enumber1, enumber2;
    Button update;
    TextView skip_text;
    FirebaseAuth nFirebaseAuth;
    SharedPreferences sharedPreferences;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        sharedPreferences = this.getSharedPreferences("com.example.crashreport", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "abcd@xyz.com");

        nFirebaseAuth = FirebaseAuth.getInstance();

        if (nFirebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(UpdateDetails.this, LoginActivity.class));
            finish();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();



        first_name = findViewById(R.id.fname);
        last_name = findViewById(R.id.lname);
        phone_number = findViewById(R.id.phone_number);
        blood_group = findViewById(R.id.blood_group);
        street = findViewById(R.id.street);
        pin_code = findViewById(R.id.pin_code);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        enumber1 = findViewById(R.id.emergency_number1);
        enumber2 = findViewById(R.id.emergency_number2);
        update = findViewById(R.id.update);
        skip_text = findViewById(R.id.skip);

        nFirebaseAuth = FirebaseAuth.getInstance();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser user = nFirebaseAuth.getCurrentUser();
                dataSnapshot = dataSnapshot.child(user.getUid());
                first_name.setText(dataSnapshot.child("fname").getValue().toString());
                last_name.setText(dataSnapshot.child("lname").getValue().toString());
                phone_number.setText(dataSnapshot.child("phoneNumber").getValue().toString());
                street.setText(dataSnapshot.child("street").getValue().toString());
                pin_code.setText(dataSnapshot.child("pin_code").getValue().toString());
                city.setText(dataSnapshot.child("city").getValue().toString());
                state.setText(dataSnapshot.child("state").getValue().toString());
                street.setText(dataSnapshot.child("street").getValue().toString());
                enumber1.setText(dataSnapshot.child("emergencyPhone1").getValue().toString());
                enumber2.setText(dataSnapshot.child("emergencyPhone2").getValue().toString());
                blood_group.setText(dataSnapshot.child("blood_group").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                String m_fname = first_name.getText().toString().trim();
                String m_lname = last_name.getText().toString().trim();
                String m_pNo = phone_number.getText().toString().trim();
                String m_bgrp = blood_group.getText().toString().trim();
                String m_street = street.getText().toString().trim();
                String m_pCode = pin_code.getText().toString().trim();
                String m_city = city.getText().toString().trim();
                String m_state = state.getText().toString().trim();
                String m_enumber1 = enumber1.getText().toString().trim();
                String m_enumber2 = enumber2.getText().toString().trim();


                UserInformation userInformation = new UserInformation(m_fname, m_lname, m_pNo, m_bgrp, m_street,
                        m_pCode, m_city, m_state, m_enumber1, m_enumber2);

                FirebaseUser user = nFirebaseAuth.getCurrentUser();
                databaseReference = databaseReference.child(user.getUid());
                databaseReference.child("fname").setValue(userInformation.fname);
                databaseReference.child("lname").setValue(userInformation.lname);
                databaseReference.child("phoneNumber").setValue(userInformation.phoneNumber);
                databaseReference.child("blood_group").setValue(userInformation.blood_group);
                databaseReference.child("street").setValue(userInformation.street);
                databaseReference.child("pin_code").setValue(userInformation.pin_code);
                databaseReference.child("city").setValue(userInformation.city);
                databaseReference.child("state").setValue(userInformation.state);
                databaseReference.child("emergencyPhone1").setValue(userInformation.emergencyPhone1);
                databaseReference.child("emergencyPhone2").setValue(userInformation.emergencyPhone2);

                Toast.makeText(UpdateDetails.this, "Information Saved...", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(UpdateDetails.this, Dashboard.class);
                startActivity(intent);
                finish();
        }});

        skip_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(UpdateDetails.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
