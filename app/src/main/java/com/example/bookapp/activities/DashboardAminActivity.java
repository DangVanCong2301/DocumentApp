package com.example.bookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.CategoryAdapter;
import com.example.bookapp.models.ModelCategory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardAminActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView tvTitle, tvSubTitle;
    private ImageButton btnLogOut, btnMyProfile;
    private Button btnAddCategory;
    private RecyclerView rcvCategory;
    private EditText edtSearch;
    private FloatingActionButton btnAddPdf;

    private ArrayList<ModelCategory> categoryArrayList;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_amin);

        initUi();
        initFirebaseAuth();
        checkUser();
        initListener();
        loadCategories();

    }

    private void initListener() {
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAminActivity.this, CategoryAddActivity.class));
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    categoryAdapter.getFilter().filter(charSequence);
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAddPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAminActivity.this, PdfAddActivity.class));
            }
        });

        btnMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAminActivity.this, ProfileActivity.class));
            }
        });
    }

    private void initFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initUi() {
        tvTitle = findViewById(R.id.tv_title);
        tvSubTitle = findViewById(R.id.tv_sub_title);
        btnLogOut = findViewById(R.id.btn_logout);
        btnAddCategory = findViewById(R.id.btn_add_category);
        rcvCategory = findViewById(R.id.rcv_category);
        edtSearch = findViewById(R.id.edt_search);
        btnAddPdf = findViewById(R.id.btn_add_pdf);
        btnMyProfile = findViewById(R.id.btn_my_profile_admin);
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // not logged in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            //logged in, get user info
            String email = firebaseUser.getEmail();
            // set in textView of toolbar
            tvSubTitle.setText(email);

        }
    }

    private void loadCategories() {
        //init arraylist
        categoryArrayList = new ArrayList<>();
        //get all category
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear arraylist
                categoryArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCategory modelCategory = ds.getValue(ModelCategory.class);
                    // add to array
                    categoryArrayList.add(modelCategory);
                }
                //set up
                categoryAdapter = new CategoryAdapter(DashboardAminActivity.this, categoryArrayList);
                rcvCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}