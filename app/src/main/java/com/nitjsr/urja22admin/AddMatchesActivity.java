package com.nitjsr.urja22admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nitjsr.urja22admin.databinding.ActivityAddMatchesBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AddMatchesActivity extends AppCompatActivity {
    private ActivityAddMatchesBinding binding;
    String myDateFormat="dd MMM yyyy", myTimeFormat="hh:mm aa", formatAll="dd MMM yyyy hh:mm aa";
    SimpleDateFormat dateFormat=new SimpleDateFormat(myDateFormat, Locale.US);
    SimpleDateFormat timeFormat=new SimpleDateFormat(myTimeFormat, Locale.US);
    SimpleDateFormat dateFormat1=new SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.US);
    Calendar myCalendar= Calendar.getInstance();
    FirebaseDatabase database;
    DatabaseReference myRef;
    String roundName, team1, team2,date;
    int type=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddMatchesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        type = Integer.parseInt(intent.getStringExtra("tabPosition"));
        myCalendar= Calendar.getInstance();
        database = FirebaseDatabase.getInstance();
        setDateAndTime();
        String sports[] = {"CRICKET", "FOOTBALL", "BASKETBALL", "VOLLEYBALL", "BADMINTON", "CHESS", "HOCKEY", "TABLE TENNIS", "ATHLETICS"};
        binding.sportName.setText(sports[type]);
        binding.etRoundName.setHint(sports[type]+" R-1");

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                team1= String.valueOf(binding.etTeam1.getText());
                team2= String.valueOf(binding.etTeam2.getText());
                roundName=String.valueOf(binding.etRoundName.getText());
                if(isNetworkConnected()==false){
                    Toast.makeText(AddMatchesActivity.this, "No Internet ACCeSS !!", Toast.LENGTH_SHORT).show();
                }
                  else if(roundName.length()==0){
                    Toast.makeText(AddMatchesActivity.this, "Please enter Round name", Toast.LENGTH_SHORT).show();
                }
                else if(team1.length()==0){
                    Toast.makeText(AddMatchesActivity.this, "Please enter Team1 name", Toast.LENGTH_SHORT).show();
                }
                else if(team2.length()==0){
                    Toast.makeText(AddMatchesActivity.this, "Please enter Team2 name", Toast.LENGTH_SHORT).show();
                }else{
                    addMatch();
                }
            }
        });

    }

    private void addMatch() {
        String tabNames[] = {"CRICKET", "FOOTBALL", "BASKETBALL", "VOLLEYBALL", "BADMINTON", "CHESS", "HOCKEY", "TABLE TENNIS", "ATHLETICS"};

        String key = RandomKey();
        Fixtures fixtures= new Fixtures(key,type,team1,team2,roundName,"","","",binding.rbLive.isChecked(),binding.rbCompleted.isChecked(),date);
        myRef = database.getReference("matches").child(tabNames[type]).child("urja22").child(key);
        try {
            myRef.setValue(fixtures).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(AddMatchesActivity.this, "Successfully added !!", Toast.LENGTH_SHORT).show();
                    AddMatchesActivity.super.onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddMatchesActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void setDateAndTime(){
        Date dateAndTime = new Date();
        binding.etDate.setText(dateFormat.format(dateAndTime));
        binding.etTime.setText(timeFormat.format(dateAndTime));
        date= dateFormat1.format(dateAndTime);

        DatePickerDialog.OnDateSetListener date= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalendar.set(Calendar.YEAR, i);
                myCalendar.set(Calendar.MONTH,i1);
                myCalendar.set(Calendar.DAY_OF_MONTH,i2);
                updateLabel();
            }
        };
        binding.etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddMatchesActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });
        binding.etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddMatchesActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR,selectedHour);
                        myCalendar.set(Calendar.MINUTE,selectedMinute);
                        updateLabel();
                    }
                }, hour,minute,false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
    }

    private void updateLabel() {
        binding.etDate.setText(dateFormat.format(myCalendar.getTime()));
        binding.etTime.setText(timeFormat.format(myCalendar.getTime()));

        date= formatAll.format(dateFormat.format(myCalendar.getTime())+" "+timeFormat.format(myCalendar.getTime()));

    }

    String RandomKey(){
        char[] chars1 = "aAbBcCdDeEfFg0h1i2jGkHlImJnKoLp3q4r5sMtNuOvPwQxRy6z78STUVWXYZ9".toCharArray();
        StringBuilder sb1 = new StringBuilder();
        Random random1 = new Random();
        for (int i = 0; i < 18; i++)
        {
            char c1 = chars1[random1.nextInt(chars1.length)];
            sb1.append(c1);
        }
        return sb1.toString();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}