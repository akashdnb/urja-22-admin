package com.nitjsr.urja22admin.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitjsr.urja22admin.AddMatchesActivity;
import com.nitjsr.urja22admin.Fixtures;
import com.nitjsr.urja22admin.MainActivity;
import com.nitjsr.urja22admin.R;
import com.nitjsr.urja22admin.adapter.MatchAdapter;
import com.nitjsr.urja22admin.databinding.FragmentScoreBinding;
import com.nitjsr.urja22admin.databinding.FragmentSportsBinding;
import com.nitjsr.urja22admin.ui.score.ScoreViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class SportsFragment extends Fragment {

    private FragmentSportsBinding binding;
    MatchAdapter adapter;
    List<Fixtures> fixtureList= new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference myRef;

    public SportsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSportsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        database = FirebaseDatabase.getInstance();
        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvUpcomings.setLayoutManager(new LinearLayoutManager(getContext()));
        Bundle args = getArguments();
        binding.tvUpcoming.setVisibility(View.GONE);
        binding.upcomingProgressBar.setVisibility(View.VISIBLE);
        FetchAll(args.getInt("position"));

        adapter = new MatchAdapter(getContext(), fixtureList);
        binding.rvUpcomings.setAdapter(adapter);

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getContext(), AddMatchesActivity.class);
                myIntent.putExtra("tabPosition",args.get("position").toString());
                getContext().startActivity(myIntent);
            }
        });
    }

    private void FetchAll(int position) {
        String tabNames[] = {"CRICKET", "FOOTBALL", "BASKETBALL", "VOLLEYBALL", "BADMINTON", "CHESS", "HOCKEY", "TABLE TENNIS", "ATHLETICS"};

        myRef = database.getReference("matches").child(tabNames[position]).child("urja22");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fixtureList.clear();
                binding.upcomingProgressBar.setVisibility(View.GONE);
                binding.tvUpcoming.setVisibility(View.GONE);

                List<Fixtures> livefixtures = new ArrayList<>();
                List<Fixtures> completedfixtures = new ArrayList<>();
                List<Fixtures> upcomingfixtures = new ArrayList<>();

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    try {
                        Fixtures fixture = singleSnapshot.getValue(Fixtures.class);
                        if(fixture.isLive()){
                            livefixtures.add(fixture);
                        }else if(fixture.isCompleted()){
                            completedfixtures.add(fixture);
                        }
                        else upcomingfixtures.add(fixture);
                    }catch (Exception e){
                        Log.d("tag",""+e);
                    }
                }
                sortFixtures(livefixtures);
                sortFixtures(completedfixtures);
                sortFixtures(upcomingfixtures);

                for(Fixtures fixtures : livefixtures){
                    fixtureList.add(fixtures);
                }
                for(Fixtures fixtures : upcomingfixtures){
                    fixtureList.add(fixtures);
                }
                for(Fixtures fixtures : completedfixtures){
                    fixtureList.add(fixtures);
                }
                if(fixtureList.size()==0){
                    binding.tvUpcoming.setVisibility(View.VISIBLE);
                }
                fixtureList.add((new Fixtures("",-1,"","","","","","",false,false,"")));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("tag", "Failed to read value.", error.toException());
                binding.tvUpcoming.setVisibility(View.VISIBLE);
                binding.upcomingProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Something went wrong !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void sortFixtures(List<Fixtures> fixtures){
        Collections.sort(fixtures, new Comparator<Fixtures>() {
            SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            @Override
            public int compare(Fixtures fixturesNew, Fixtures t1) {
                try {
                    Date d1=sdfDateTime.parse(fixturesNew.getMatchDate());
                    Date d2 =sdfDateTime.parse(fixturesNew.getMatchDate());
                    if(d1.compareTo(d2)<0) return -1;
                    else return 1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}