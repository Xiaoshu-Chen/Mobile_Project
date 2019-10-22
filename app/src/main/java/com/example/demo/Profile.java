package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Profile extends AppCompatActivity {

    ImageView back;
    TextView name;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String name_text;
    final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    final String uid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back = findViewById(R.id.back);
        name = findViewById(R.id.name);

        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        name_text = user.getDisplayName();
        name.setText(name_text);

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot eventSnapShot = dataSnapshot;
                TextView upcoming_text = findViewById(R.id.upcoming);
                TextView past_text = findViewById(R.id.past);
                final HashMap<String, String> upcoming = new HashMap<>();
                final HashMap<String, String> past = new HashMap<>();
                for (DataSnapshot eventId : eventSnapShot.child("users_events").child(uid).getChildren()){
                    String event_id = eventId.getKey();
//                    Log.d("list1", event_id);
                    DataSnapshot event = eventSnapShot.child("events").child(event_id);

                    String name = (String) event.child("name").getValue();
                    String adate = (String) event.child("date").getValue();
                    String atime = (String) event.child("time").getValue();
//                    Log.d("list1", adate);
                    int date = Integer.parseInt(adate);
                    int time = Integer.parseInt(atime);

                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
                    String formattedDate = df.format(c);
                    SimpleDateFormat tf = new SimpleDateFormat("HHmm");
                    String formattedTime = tf.format(c);
                    int current_date = Integer.parseInt(formattedDate);
                    int current_time = Integer.parseInt(formattedTime);

                    if (date < current_date || (date==current_date && time<=current_time)){
                        past.put(name, event_id);
                    } else {
                       upcoming.put(name, event_id);
                    }
                }
                Fragment fragment = new Upcoming_fragment();
                Bundle data = new Bundle();
                data.putSerializable("event", upcoming);
                fragment.setArguments(data);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fragment_place, fragment);
                ft.commit();

                upcoming_text.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        Fragment fragment = new Upcoming_fragment();
                        Bundle data = new Bundle();
                        data.putSerializable("event", upcoming);
                        fragment.setArguments(data);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment_place, fragment);
                        ft.commit();
                    }
                });

                past_text.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        Fragment fragment = new Past_fragment();
                        Bundle data = new Bundle();
                        data.putSerializable("event", past);
                        fragment.setArguments(data);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment_place, fragment);
                        ft.commit();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }

    public void changFragment (View view) {
        Fragment fragment;

        if (view == findViewById(R.id.upcoming)) {

            fragment = new Upcoming_fragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_place, fragment);
            ft.commit();
        }
        if (view == findViewById(R.id.past)) {

            fragment = new Past_fragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_place, fragment);
            ft.commit();
        }
    }

}
