package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEvent extends AppCompatActivity {

    final Calendar myCalendar = Calendar.getInstance();
    final Calendar cldr = Calendar.getInstance();
    TextView date_text, time_text, address_text;
    TimePickerDialog picker;
    ImageView add_photo, add_address, photo, back;
    Button submit;
    String location, address, name, date, time, des, initiator, uid;
    EditText name_text, des_text;
    private static final int PICK_IMAGE_REQUEST = 102;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference userRef;
    private StorageTask mUploadTask;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        date_text = findViewById(R.id.date);
        time_text = findViewById(R.id.time);
        add_address = findViewById(R.id.add_address);
        add_photo = findViewById(R.id.add_photo);
        address_text = findViewById(R.id.address);
        name_text = findViewById(R.id.name);
        des_text = findViewById(R.id.des);
        photo = findViewById(R.id.photo);
        submit = findViewById(R.id.submit);
        back = findViewById(R.id.back);
        mRelativeLayout = findViewById(R.id.rl);

        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("events");

        userRef = FirebaseDatabase.getInstance().getReference("users_events");

        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

//        time_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                int hour = cldr.get(Calendar.HOUR_OF_DAY);
//                int minutes = cldr.get(Calendar.MINUTE);
//                picker = new TimePickerDialog(AddEvent.this,AlertDialog.THEME_HOLO_LIGHT,
//                        new TimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
//                                String hh, mm;
//                                if (String.valueOf(sHour).length() == 1){
//                                    hh = "0"+sHour;
//                                } else {
//                                    hh = ""+sHour;
//                                }
//                                if (String.valueOf(sMinute).length() == 1){
//                                    mm = "0"+sMinute;
//                                } else {
//                                    mm = ""+sMinute;
//                                }
//                                time_text.setText(hh + ":" + mm);
//                                time = hh+mm;
//                            }
//                        }, hour, minutes, true);
//                picker.show();
//            }
//        });

        final TimePickerDialog.OnTimeSetListener mtime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                // TODO Auto-generated method stub
                cldr.set(Calendar.HOUR_OF_DAY, sHour);
                cldr.set(Calendar.MINUTE, sMinute);

                String myFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String myFormat1 = "HHmm";
                SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1, Locale.US);
                time_text.setText(sdf.format(cldr.getTime()));
                time = sdf1.format(cldr.getTime());
            }
        };

        time_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddEvent.this, AlertDialog.THEME_HOLO_LIGHT, mtime, cldr
                        .get(Calendar.HOUR_OF_DAY), cldr.get(Calendar.MINUTE), true).show();
            }
        });

         final DatePickerDialog.OnDateSetListener mdate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String myFormat1 = "yyMMdd";
                SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1, Locale.US);
                date_text.setText(sdf.format(myCalendar.getTime()));
                date = sdf1.format(myCalendar.getTime());
            }

        };


        date_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddEvent.this, AlertDialog.THEME_HOLO_LIGHT, mdate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        add_address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), choose_location.class);
                startActivityForResult(i, 1);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            initiator = user.getDisplayName();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                location = data.getStringExtra("result");
                address = data.getStringExtra("address");

                address_text.setText(address);
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(photo);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadFile() {
        name = name_text.getText().toString();
        des = des_text.getText().toString();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        String formattedDate = df.format(c);
        SimpleDateFormat tf = new SimpleDateFormat("HHmm");
        String formattedTime = tf.format(c);
        int current_date = Integer.parseInt(formattedDate);
        int current_time = Integer.parseInt(formattedTime);
//        Log.d("time2", current_date+" "+current_time);
        if(Integer.parseInt(date)<current_date || (Integer.parseInt(date)==current_date && Integer.parseInt(time)<=current_time)){
            Toast.makeText(AddEvent.this, "Invalid date or time!", Toast.LENGTH_LONG).show();
        } else {
            final String uploadId = mDatabaseRef.push().getKey();
            if (mImageUri != null) {
                StorageReference fileReference = mStorageRef.child(uploadId
                        + "." + getFileExtension(mImageUri));

                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                mDatabaseRef.child(uploadId).child("name").setValue(name);
                                mDatabaseRef.child(uploadId).child("date").setValue(date);
                                mDatabaseRef.child(uploadId).child("time").setValue(time);
                                mDatabaseRef.child(uploadId).child("location").setValue(location);
                                mDatabaseRef.child(uploadId).child("description").setValue(des);
                                mDatabaseRef.child(uploadId).child("initiator").setValue(initiator);
                                mDatabaseRef.child(uploadId).child("participant").setValue("1");
                                mDatabaseRef.child(uploadId).child("participant_uid").child(uid).setValue("0");

                                userRef.child(uid).child(uploadId).setValue("0");

                                Toast.makeText(AddEvent.this, "Upload successful", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                startActivity(intent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddEvent.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                            }
                        });
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

//    private void showPopUp() {
//        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        View customView = inflater.inflate(R.layout.pop_up,null);
//
//        mPopupWindow = new PopupWindow(
//                customView,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                380
//        );
//        mPopupWindow.setElevation(5.0f);
//
//        customView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mPopupWindow.dismiss();
//                return true;
//            }
//        });
//
//
//        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
//
//    }

}
