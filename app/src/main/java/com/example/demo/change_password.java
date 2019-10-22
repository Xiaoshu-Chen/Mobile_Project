package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Change password page.
public class change_password extends AppCompatActivity {

    EditText change_password;
    Button confirm;
    ImageView back;

    // Create the connections and listen to different event actions.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        change_password = findViewById(R.id.change_password);
        confirm = findViewById(R.id.confirm);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }

    // When confirm button is clicked, update user name to firebase.
    public void confirmBtn(View view) {
        String password = change_password.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Make change to the database.
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(change_password.this, "Update success!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), setting.class);
                            startActivity(intent);
                        }
                    }
                });
    }
}
