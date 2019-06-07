package com.example.ainurbayanova.attendancesystem.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.fragments.DetailFragment;
import com.example.ainurbayanova.attendancesystem.fragments.OtherFragment;

import java.util.ArrayList;
import java.util.List;

public class DetailOfWidget extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_of_widget);
        initializeToolbar();
        initTabWithViewPager();
    }
    public void initializeToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void initTabWithViewPager(){
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        DetailOfWidget.SimplePageFragmentAdapter adapter = new SimplePageFragmentAdapter(getSupportFragmentManager());

        adapter.addFragment(new DetailFragment(), "Деталь");

        adapter.addFragment(new OtherFragment(), "Продается");

        adapter.addFragment(new OtherFragment(), "Места");

        adapter.addFragment(new OtherFragment(), "Похожие");

        adapter.addFragment(new OtherFragment(), "Другие");

        viewPager.setAdapter(adapter);
    }
    public class SimplePageFragmentAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> titles = new ArrayList<>();
        public SimplePageFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String one) {
            mFragmentList.add(fragment);
            titles.add(one);
        }
    }
}
