package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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


public class Upcoming_fragment extends ListFragment implements AdapterView.OnItemClickListener{

    HashMap<String, String> event = new HashMap<>();
    String[] name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upcoming_fragment, container,false);

        event = (HashMap) getArguments().getSerializable("event");
//        Log.d("test1",String.valueOf(event.size()));

        name = new String[event.size()];
        int index = 0;
        for (Map.Entry<String, String> mapEntry : event.entrySet()) {
            name[index] = mapEntry.getKey();
            index++;
        }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, name);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) getListView().getItemAtPosition(position);
        String eventid = event.get(item);

        Intent intent1 = new Intent(getActivity(), View_Event.class);
        intent1.putExtra("eventId", eventid);
        startActivity(intent1);
    }


}
