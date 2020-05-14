package com.example.crashreportver3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Dash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Dashboard extends AppCompatActivity {

    static TrackingOrNot trackingOrNot = new TrackingOrNot();

    static private class TrackingOrNot {
        boolean flag;
        TrackingOrNot() {
            this.flag = false;
        }
    }

    static private class Flag {
        boolean flag;
        Flag() {
            this.flag = false;
        }
    }

    private static final int PERMISSION_REQUEST = 100;
    private static final int PERMISSIONS_REQUEST_SEND_SMS =0 ;

    Button logout;
    Button drivingToggle;
    Button raiseAlarm;
    Button map;
    MediaPlayer mp;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    String fname, lname, pNo, enumber1, enumber2, latitude, longitude, blood_group, timestamp;
    TextView emergencyPhone1, emergencyPhone2, latitudeView, longitudeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        emergencyPhone1 = findViewById(R.id.emergencyNum1);
        emergencyPhone2 = findViewById(R.id.emergencyNum2);
        latitudeView = findViewById(R.id.latitude);
        longitudeView = findViewById(R.id.longitude);
        map = findViewById(R.id.map);

        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (user != null &&  user.getUid() != null) {

                        enumber1 = dataSnapshot.child(user.getUid()).child("emergencyPhone1").getValue().toString().trim();
                        enumber2 = dataSnapshot.child(user.getUid()).child("emergencyPhone2").getValue().toString().trim();
                        latitude = dataSnapshot.child(user.getUid()).child("location").child("latitude").getValue().toString().trim();
                        longitude = dataSnapshot.child(user.getUid()).child("location").child("longitude").getValue().toString().trim();
                        fname = dataSnapshot.child(user.getUid()).child("fname").getValue().toString().trim();
                        lname = dataSnapshot.child(user.getUid()).child("lname").getValue().toString().trim();
                        pNo = dataSnapshot.child(user.getUid()).child("phoneNumber").getValue().toString().trim();
                        blood_group = dataSnapshot.child(user.getUid()).child("blood_group").getValue().toString().trim();
                        timestamp = dataSnapshot.child(user.getUid()).child("location").child("time").getValue().toString().trim();

                        if (latitude != null && longitude != null && emergencyPhone1 != null && emergencyPhone2 != null) {

                            emergencyPhone1.setText(enumber1);
                            emergencyPhone2.setText(enumber2);
                            latitudeView.setText(latitude);
                            longitudeView.setText(longitude);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        mp = MediaPlayer.create(this, R.raw.alarm);
        mp.setLooping(true);
        logout = findViewById(R.id.logout);
        drivingToggle = findViewById(R.id.trackingToggle);
        raiseAlarm = findViewById(R.id.raiseAlarm);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (trackingOrNot.flag == true) {
                    stopService(new Intent(Dashboard.this, TrackingService.class));
                    Toast.makeText(Dashboard.this, "GPS tracking stopped", Toast.LENGTH_SHORT).show();
                    trackingOrNot.flag = false;
                }
                mp.release();
                firebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, MapsActivity.class));
            }
        });

       drivingToggle.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (trackingOrNot.flag == false) {
                   LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                   if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                       Toast.makeText(Dashboard.this, "Please enable GPS", Toast.LENGTH_SHORT).show();
                   } else {
                       int permission = ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION);
                       if (permission == PackageManager.PERMISSION_GRANTED) {
                           startService(new Intent(Dashboard.this, TrackingService.class));
                           Toast.makeText(Dashboard.this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
                           drivingToggle.setText("Stop Driving");
                           trackingOrNot.flag = true;
                       } else {
                           ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                       }
                   }
               } else {
                   stopService(new Intent(Dashboard.this, TrackingService.class));
                   Toast.makeText(Dashboard.this, "GPS tracking stopped", Toast.LENGTH_SHORT).show();
                   drivingToggle.setText("Start Driving");
                   trackingOrNot.flag = false;
               }
           }
       });

       raiseAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raiseAlert(v);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                trackingOrNot.flag = true;
                startService(new Intent(Dashboard.this, TrackingService.class));
                drivingToggle.setText("Stop Driving");
            } else {
                Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSIONS_REQUEST_SEND_SMS ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendSms();
            } else {
                Toast.makeText(getApplicationContext(),
                        "SOS SMS failed, please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendSms() {
        String date;
        if (timestamp != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timestamp));
            date = DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar).toString();
        } else {
            date = "Not Available";
        }
        String msg = "SOS ALERT!\n" +
                fname + " " + lname + " met an accident at " + date +
                "\nlatitude: " + latitude + "\n" +
                "longitude: " + longitude + "\n" +
                "Blood Group: " + blood_group + "\n" +
                "Phone Number: " + pNo;
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(enumber1, null, msg, null, null);
            sms.sendTextMessage(enumber2, null, msg, null, null);
            Toast.makeText(Dashboard.this, "SOS SMS Sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void raiseAlert(View v) {
        final Flag flag = new Flag();
        mp.setVolume(1.0f, 1.0f);
        mp.start();
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Raise Alarm");
        builder.setMessage("After 10 seconds, Alarm will be raised\nSMS will be sent to Concerned!!");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flag.flag = true;
                mp.pause();
            }
        });
        final AlertDialog dlg = builder.create();
        dlg.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dlg.dismiss();
                mp.pause();
                if (flag.flag != true) {
                        int permission = ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.SEND_SMS);
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            sendSms();
                        } else {
                            ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
                        }
                }
            }
        }, 1000*10);
    }
}
