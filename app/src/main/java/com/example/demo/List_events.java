package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// The list of events obj, handling all the events of one user.
public class List_events extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ListView listView;
    ImageView back;

    // initialize the obj, link the item to the code.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("events/");
        listView = findViewById(R.id.myList);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Listen to any change on the event
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final HashMap<String, String> event= new HashMap<>();
                final String[] event_name;
                for (DataSnapshot eventSnapShot : dataSnapshot.getChildren()) {
                    String adate = (String) eventSnapShot.child("date").getValue();
                    String atime = (String) eventSnapShot.child("time").getValue();
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

                    } else {
                        String eventId = (String) eventSnapShot.getKey();
                        String name = (String) eventSnapShot.child("name").getValue();

                        event.put(name, eventId);
//                    Log.d(TAG, "added Marker of event " + name);
                    }
                }

                event_name = new String[event.size()];
                int index = 0;
                for (Map.Entry<String, String> mapEntry : event.entrySet()) {
                    event_name[index] = mapEntry.getKey();
                    index++;
                }

                ArrayAdapter adapter = new ArrayAdapter(List_events.this,android.R.layout.simple_list_item_1, event_name);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String item = (String) listView.getItemAtPosition(i);
                        String eventid = event.get(item);

                        Intent intent1 = new Intent(List_events.this, View_Event.class);
                        intent1.putExtra("eventId", eventid);
                        startActivity(intent1);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
