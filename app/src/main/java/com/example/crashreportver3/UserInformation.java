package com.example.crashreportver3;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class UserInformation {
    public String fname;
    public String lname;
    public String phoneNumber;
    public String blood_group;
    public String street;
    public String pin_code;
    public String city;
    public String state;
    public Location location;
    public String emergencyPhone1;
    public String emergencyPhone2;

    public UserInformation() {
        this.fname = "Josephine";
        this.lname = "Darakjy";
        this.phoneNumber = "08102929388";
        this.blood_group = "B+";
        this.street = "4 B Blue Ridge Blvd";
        this.pin_code = "48116";
        this.city = "Brighton";
        this.state = "Michigan";
        emergencyPhone1 = "07302200180";
        emergencyPhone2 = "07302200180";
        this.location = new Location("Sample");
        this.location.setLatitude(34.866215);
        this.location.setLongitude(-84.326248);
        this.location.setTime(Calendar.getInstance().getTimeInMillis());
    }

    public UserInformation(String fname, String lname, String phoneNumber, String blood_group, String street, String pin_code,
                           String city, String state, String emergencyPhone1, String emergencyPhone2) {
        this.fname = fname;
        this.lname = lname;
        this.phoneNumber = phoneNumber;
        this.blood_group = blood_group;
        this.street = street;
        this.pin_code = pin_code;
        this.city = city;
        this.state = state;
        this.emergencyPhone1 = emergencyPhone1;
        this.emergencyPhone2 = emergencyPhone2;
    }
}
