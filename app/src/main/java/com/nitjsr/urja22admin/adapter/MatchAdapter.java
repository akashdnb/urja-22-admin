package com.nitjsr.urja22admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nitjsr.urja22admin.Fixtures;
import com.nitjsr.urja22admin.R;
import com.nitjsr.urja22admin.UpdateMatchesActivity;

import java.text.SimpleDateFormat;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.viewHolder>{

    Context context;
    List<Fixtures> fixtureList;

    public MatchAdapter(Context context, List<Fixtures> fixtureList) {
        this.context = context;
        this.fixtureList = fixtureList;
    }

    @NonNull
    @Override
    public MatchAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.match_list_item, parent, false);
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchAdapter.viewHolder holder, int pos) {
        int position= pos;
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("DD-MM-YYYY HH:MM:SS");
        String result= fixtureList.get(position).getResult();
        if(position==fixtureList.size()-1) {
            holder.itemView.setVisibility(View.INVISIBLE);
            return;
        }else{
            holder.itemView.setVisibility(View.VISIBLE);
        }
        if(fixtureList.get(position).isLive()){
            result="LIVE";
        }
        else if(fixtureList.get(position).isCompleted()){
            result= fixtureList.get(position).getResult();
        }
        else result= fixtureList.get(position).getMatchDate();

        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, UpdateMatchesActivity.class);
                String type= String.valueOf(fixtureList.get(position).getType());
                intent.putExtra("type",type);
                intent.putExtra("uid",fixtureList.get(position).getUid());
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle("DELETE !!")
                        .setMessage("Do you want to delete "+ fixtureList.get(position).getRoundName()
                        +" "+fixtureList.get(position).getTeam1()+" vs "+fixtureList.get(position).getTeam2())
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(isNetworkConnected()==false){
                                    Toast.makeText(context, "No Internet access !!", Toast.LENGTH_SHORT).show();
                                    return;
                                }else{
                                    holder.deleteMatch(fixtureList.get(position).getType(),fixtureList.get(position).getUid());
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();
                return false;
            }
        });

        holder.setData(fixtureList.get(position).getRoundName(),
                fixtureList.get(position).getTeam1(),
                fixtureList.get(position).getTeam2(),
                fixtureList.get(position).getScore1().replaceAll("\\\\n", "\\\n"),
                fixtureList.get(position).getScore2().replaceAll("\\\\n", "\\\n"),
                result, fixtureList.get(position).isCompleted(),
                fixtureList.get(position).getMatchDate());
    }

    @Override
    public int getItemCount() {
        return fixtureList.size() ;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView matchName, matchTeams, matchScore1, matchScore2, matchResult, matchDate;
        private Button updateBtn;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            matchName = itemView.findViewById(R.id.match_name);
            matchTeams = itemView.findViewById(R.id.match_teams);
            matchScore1 = itemView.findViewById(R.id.match_score_1);
            matchScore2 = itemView.findViewById(R.id.match_score_2);
            matchResult = itemView.findViewById(R.id.match_result);
            matchDate = itemView.findViewById(R.id.match_date);
            updateBtn=itemView.findViewById(R.id.Update_btn);
        }

        public void setData(String name, String team1, String team2, String s1, String s2, String res, boolean completed, String date) {
            matchResult.setVisibility(View.VISIBLE);
            this.matchDate.setVisibility(View.VISIBLE);

            if(res=="LIVE"){
                matchResult.setTextColor(Color.RED);
                matchDate.setVisibility(View.INVISIBLE);
            }
            else if(completed) {
                matchResult.setTextColor(Color.GREEN);
            }
            else matchResult.setVisibility(View.INVISIBLE);

            matchName.setText(name);
            matchTeams.setText(team1 + " vs " + team2);
            matchScore1.setText(s1);
            matchScore2.setText(s2);
            matchResult.setText(res);
            matchDate.setText(date);
        }

        public void deleteMatch(int type, String uid) {
            String tabNames[] = {"CRICKET", "FOOTBALL", "BASKETBALL", "VOLLEYBALL", "BADMINTON", "CHESS", "HOCKEY", "TABLE TENNIS", "ATHLETICS"};
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef= database.getReference("matches").child(tabNames[type]).child("urja22").child(uid);

            try {
                myRef.removeValue();
                Toast.makeText(context, "Successfully Deleted !!", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(context, "Failed!!\n"+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
