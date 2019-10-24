package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

//The event class on the map.
public class View_Event extends AppCompatActivity {

    String eventId, uid;
    TextView name, date, time, address, des, participant, etime;
    ImageView photo, back;
    Button join;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Initialize the view and connect the items.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__event);

        eventId = getIntent().getStringExtra("eventId");

        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        address = findViewById(R.id.address);
        des = findViewById(R.id.des);
        participant = findViewById(R.id.participant);
        photo = findViewById(R.id.photo);
        join = findViewById(R.id.join);
        back = findViewById(R.id.back);
        etime = findViewById(R.id.etime);

        uid = user.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference myRef = database.child("events/"+eventId);

        final DatabaseReference userRef = database.child("users_events");

        //When new
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("participant_uid")) {
                    if (snapshot.child("participant_uid").hasChild(uid)){
                        join.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //when data change on the firebase, update the view.
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    name.setText(dataSnapshot.child("name").getValue().toString());
                    String ddate = dataSnapshot.child("date").getValue().toString();
                    date.setText(ddate.substring(4, 6)+"/"+ddate.substring(2, 4)+"/"+ddate.substring(0, 2));
                    String dtime = dataSnapshot.child("start_time").getValue().toString();
                    time.setText(dtime.substring(0,2)+":"+dtime.substring(2,4));
                    String eetime = dataSnapshot.child("end_time").getValue().toString();
                    etime.setText(eetime.substring(0,2)+":"+eetime.substring(2,4));
                    String daddress = dataSnapshot.child("location").getValue().toString();
                    String[] ddaddress = daddress.split(",");
                    String full_address = getCompleteAddressString(Double.parseDouble(ddaddress[0]), Double.parseDouble(ddaddress[1]));
                    address.setText(full_address);
                    des.setText(dataSnapshot.child("description").getValue().toString());
                    participant.setText(dataSnapshot.child("participant").getValue().toString());
                    String photoId = dataSnapshot.getKey();

                StorageReference pathReference = FirebaseStorage.getInstance().getReference("images/"+ photoId+".jpg");

                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri downloadUrl)
                    {
                        Picasso.with(View_Event.this).load(downloadUrl).into(photo);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });

        //The join event listener.
        join.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                int num = Integer.parseInt(participant.getText().toString()) +1;
                myRef.child("participant").setValue(String.valueOf(num));
                Toast.makeText(View_Event.this, "Join successful!", Toast.LENGTH_LONG).show();
                myRef.child("participant_uid").child(uid).setValue("1");

                userRef.child(uid).child(eventId).setValue("1");
                finish();
                startActivity(getIntent());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //To get the complete address string from geocode.
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Address", strReturnedAddress.toString());
            } else {
                Log.w("Address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Address", "Canont get Address!");
        }
        return strAdd;
    }
}

