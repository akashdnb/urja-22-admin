package com.nitjsr.urja22admin.ui.score;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nitjsr.urja22admin.adapter.TabPagerAdapter;
import com.nitjsr.urja22admin.databinding.FragmentScoreBinding;

public class ScoreFragment extends Fragment {

    private FragmentScoreBinding binding;
    TabPagerAdapter tabPagerAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;

    public ScoreFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScoreViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ScoreViewModel.class);

        binding = FragmentScoreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String tabNames[]={"CRICKET","CHESS","BADMINTON","HOCKEY","FOOTBALL","BASKETBALL","LAWN TENNIS"};
        tabLayout = binding.tabs;
        viewPager = binding.viewpager;
        tabLayout.setupWithViewPager(viewPager);
        tabPagerAdapter= new TabPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);

        if(isNetworkConnected()==false)
            Toast.makeText(getContext(), "No Internet Access !!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}