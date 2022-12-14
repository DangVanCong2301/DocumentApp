package com.example.bookapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bookapp.BooksUserFragment;
import com.example.bookapp.R;
import com.example.bookapp.models.ModelCategory;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashbroardUserActivity extends AppCompatActivity {

    private TextView tvTitle, tvSubTitle;
    private ImageButton btnLogout, btnMyProfile;

    private FirebaseAuth firebaseAuth;

    public ViewPagerAdapter viewPagerAdapter;

    private ArrayList<ModelCategory> categoryArrayList;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbroard_user);

        initUi();
        initFirebaseAuth();
        checkUser();
        setUpViewPagerAdapter(viewPager);
        setUpTabLayout();
        initListener();



    }

    private void initUi() {
        tvTitle = findViewById(R.id.tv_title);
        tvSubTitle = findViewById(R.id.tv_sub_title);
        btnLogout = findViewById(R.id.btn_logout);
        viewPager = findViewById(R.id.viewPager_dashboard_user);
        tabLayout = findViewById(R.id.tl_dashboard_user);
        btnMyProfile = findViewById(R.id.btn_my_profile_user);
    }

    private void setUpTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initListener() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(DashbroardUserActivity.this, MainActivity.class));
                finish();
            }
        });

        btnMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashbroardUserActivity.this, ProfileActivity.class));
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // not logged in
            tvSubTitle.setText("Not logged In");
        } else {
            //logged in, get user info
            String email = firebaseUser.getEmail();
            // set in textView of toolbar
            tvSubTitle.setText(email);

        }
    }

    private void setUpViewPagerAdapter(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);

        categoryArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();
                ModelCategory modelAll = new ModelCategory("01", "All", "", 1);
                ModelCategory modelMostViewed = new ModelCategory("02", "Most Viewed", "", 1);
                ModelCategory modelMostDownloaded = new ModelCategory("03", "Most Downloaded", "", 1);

                categoryArrayList.add(modelAll);
                categoryArrayList.add(modelMostViewed);
                categoryArrayList.add(modelMostDownloaded);

                // add data to viewpager adapter
                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        "" + modelAll.getId(),
                        "" + modelAll.getCategory(),
                        "" + modelAll.getUid()
                ), modelAll.getCategory());

                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        "" + modelMostViewed.getId(),
                        "" + modelMostViewed.getCategory(),
                        "" + modelMostViewed.getUid()
                ), modelMostViewed.getCategory());

                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        "" + modelMostDownloaded.getId(),
                        "" + modelMostDownloaded.getCategory(),
                        "" + modelMostDownloaded.getUid()
                ), modelMostDownloaded.getCategory());

                //refresh list
                viewPagerAdapter.notifyDataSetChanged();

                // now load from firebase
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelCategory modelCategory = ds.getValue(ModelCategory.class);
                    // add data to list
                    categoryArrayList.add(modelCategory);

                    // add data to viewpager adapter
                    viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                            "" + modelCategory.getId(),
                            "" + modelCategory.getCategory(),
                            "" + modelCategory.getUid()
                    ), modelCategory.getCategory());
                    //refresh list
                    viewPagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set adapter to view pager
        viewPager.setAdapter(viewPagerAdapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<BooksUserFragment> fragmentList = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
            super(fm, behavior);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        private void addFragment(BooksUserFragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}