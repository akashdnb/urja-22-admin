package com.nitjsr.urja22admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitjsr.urja22admin.databinding.ActivityLoginBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    FirebaseDatabase database;
    DatabaseReference myRef;
    HashMap<String,String> user;
    List<String> masterEmail= new ArrayList<>();
    List<String> masterPassword= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binding.email.setInputType(InputType.TYPE_CLASS_TEXT);
                if(binding.checkBox.isChecked()){
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else{
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        user=new HashMap<String,String>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("matches").child("admin");

        fetch();

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= binding.email.getText().toString().trim();
                String password= binding.password.getText().toString().trim();

                if(isNetworkConnected()==false){
                    Toast.makeText(LoginActivity.this, "Not connected to the Internet !!", Toast.LENGTH_SHORT).show();
                }
                else if(email.length()==0)
                    Toast.makeText(LoginActivity.this, "Please enter email !!", Toast.LENGTH_SHORT).show();
                else if(password.length()==0)
                    Toast.makeText(LoginActivity.this, "Please enter password !!", Toast.LENGTH_SHORT).show();
                else{
                     boolean flag= true;
                    for(int i=0;i<masterEmail.size();i++){
                        if(email.equals(masterEmail.get(i))&& password.equals(masterPassword.get(i))){
                            flag =false;
                            changeActivity();
                        }
                    }
                    if(flag){
                        Toast.makeText(LoginActivity.this, "Invalid email or password !!", Toast.LENGTH_SHORT).show();
                    }
                    fetch();
                }
            }
        });

    }

    private void fetch() {
        if(isNetworkConnected()==false){
            Toast.makeText(this, "No network Access !!", Toast.LENGTH_SHORT).show();
        }
        else{
            binding.progressBar2.setVisibility(View.VISIBLE);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    masterEmail.clear();
                    masterPassword.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Users user= dataSnapshot.getValue(Users.class);
                        masterEmail.add(user.getEmail());
                        masterPassword.add(user.getPassword());
                    }
                    binding.progressBar2.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                    binding.progressBar2.setVisibility(View.GONE);
                }
            });
        }
    }

    private void changeActivity() {
        Intent intent= new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}