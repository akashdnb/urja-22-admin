package com.nitjsr.urja22admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitjsr.urja22admin.databinding.ActivityMainBinding;
import com.nitjsr.urja22admin.databinding.ActivityUpdateMatchesBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateMatchesActivity extends AppCompatActivity {

    private ActivityUpdateMatchesBinding binding;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Fixtures fixtures;
    String score1,score2,team1,team2,roundName,result,matchDate="01 JAN 2022 12:00 AM";
    boolean live= false, completed=false, upcoming= false;

    String myDateFormat="dd MMM yyyy", myTimeFormat="hh:mm aa", formatAll="dd MMM yyyy hh:mm aa";
    SimpleDateFormat dateFormat=new SimpleDateFormat(myDateFormat, Locale.US);
    SimpleDateFormat timeFormat=new SimpleDateFormat(myTimeFormat, Locale.US);
    SimpleDateFormat dateFormat1=new SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.US);
    Calendar myCalendar= Calendar.getInstance();
    String tabNames[] = {"CRICKET", "FOOTBALL", "BASKETBALL", "VOLLEYBALL", "BADMINTON", "CHESS", "HOCKEY", "TABLE TENNIS", "ATHLETICS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateMatchesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        int type = Integer.parseInt(intent.getStringExtra("type"));
        String uid = intent.getStringExtra("uid");
        getData(type,uid);
        setTextView();
        binding.matchTitle.setText(tabNames[type]);
        team1=binding.etTeam1.getText().toString();
        team2=binding.etTeam2.getText().toString();
        score1=binding.etT1s1.getText().toString();
        score2=binding.etT2s1.getText().toString();
        roundName=binding.etRoundName.getText().toString();
        result=binding.etResult.getText().toString();
        getDate();

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                team1=binding.etTeam1.getText().toString();
                team2=binding.etTeam2.getText().toString();
                score1=binding.etT1s1.getText().toString();
                score2=binding.etT2s1.getText().toString();
                roundName=binding.etRoundName.getText().toString();
                result=binding.etResult.getText().toString();
                matchDate=binding.etDate.getText().toString().trim()+" "+binding.etTime.getText().toString().trim();

                Fixtures fixture= new Fixtures(uid,type,team1,team2, roundName, result, score1,score2,
                        binding.rbLive.isChecked(),binding.rbCompleted.isChecked(),matchDate);
                myRef = database.getReference("matches").child(tabNames[type]).child("urja22").child(uid);
                if(isNetworkConnected()==false){
                    Toast.makeText(UpdateMatchesActivity.this, "No Internet Access !!", Toast.LENGTH_SHORT).show();
                }
                else try {
                    myRef.setValue(fixture).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UpdateMatchesActivity.this, "Successfully Updated !!", Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateMatchesActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(UpdateMatchesActivity.this, "Failed !!\n"+e, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void getDate() {

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
                new DatePickerDialog(UpdateMatchesActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });
        binding.etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(UpdateMatchesActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        matchDate= formatAll.format(dateFormat.format(myCalendar.getTime()).trim()+" "+timeFormat.format(myCalendar.getTime())).trim();

    }

    private void getData(int type, String uid) {
        myRef = database.getReference("matches").child(tabNames[type]).child("urja22").child(uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    fixtures = snapshot.getValue(Fixtures.class);
                    setData(fixtures);
                }catch (Exception e){
                    Toast.makeText(UpdateMatchesActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateMatchesActivity.this, "Unable to fetch previous data !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData(Fixtures fixturesNew) {
        if(this.fixtures !=null){
            binding.etRoundName.setText(fixturesNew.getRoundName());
            binding.etTeam1.setText(fixturesNew.getTeam1());
            binding.etTeam2.setText(fixturesNew.getTeam2());
            binding.etResult.setText(fixturesNew.getResult());
            binding.etDate.setText(fixturesNew.getMatchDate().substring(0,12));
            binding.etTime.setText(fixturesNew.getMatchDate().substring(12,20));
            if(fixturesNew.isLive()) binding.rbLive.setChecked(true);
            else if(fixturesNew.isCompleted()) binding.rbCompleted.setChecked(true);
            else binding.rbUpcoming.setChecked(true);
            setScores(fixturesNew);
        }
    }

    private void setScores(Fixtures fixturesNew) {
        String[] score1 = fixturesNew.getScore1().split("\n");
        String[] score2 = fixturesNew.getScore2().split("\n");
        binding.etT1s1.setText(fixturesNew.getScore1());
        binding.etT2s1.setText(fixturesNew.getScore2());
        binding.layoutSet2.setVisibility(View.GONE);
        binding.layoutSet3.setVisibility(View.GONE);
        binding.tvt1s1.setText("Score:");
        binding.tvt2s1.setText("Score:");

        switch (fixturesNew.getType()){
            case 0:
                binding.tvt1s1.setText("Runs:");
                binding.tvt2s1.setText("Runs:");
                binding.etT1s1.setHint("Runs/wicket");
                binding.etT2s1.setHint("Runs/wicket");
                binding.layoutSet3.setVisibility(View.GONE);
                break;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:
                binding.layoutSet2.setVisibility(View.GONE);
                binding.layoutSet3.setVisibility(View.GONE);
                break;
            case 6:

                break;
            case 7:

                break;
            case 8:
                binding.layoutSet2.setVisibility(View.GONE);
                binding.layoutSet3.setVisibility(View.GONE);
                break;
        }
    }

    private void setTextView(){
        binding.layoutSet3.setVisibility(View.GONE);
        binding.layoutSet2.setVisibility(View.GONE);
        binding.tvt1s1.setText("Score1:");
        binding.tvt2s1.setText("Score2:");
    }

    String [] seperate(String str){

        return null;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}