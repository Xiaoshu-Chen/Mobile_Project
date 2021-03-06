package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

// The setting page class.
public class setting extends AppCompatActivity {

    ImageView back;
    TextView change_name, change_password, sign_out;

    // initialize the class, make connections, and start listen to events.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        change_name = findViewById(R.id.change_name);
        change_password = findViewById(R.id.change_password);
        sign_out = findViewById(R.id.sign_out);
        back = findViewById(R.id.back);

        change_name.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), change_name.class);
                startActivity(intent);
            }
        });

        change_password.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), change_password.class);
                startActivity(intent);
            }
        });

        sign_out.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(setting.this, "Sign out!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }
}
