package com.example.bookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.PdfAdminAdapter;
import com.example.bookapp.models.ModelPdf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PdfListAdminActivity extends AppCompatActivity {

    private ArrayList<ModelPdf> pdfArrayList;
    private PdfAdminAdapter adminAdapter;
    private RecyclerView rcvPdfBook;
    private ImageButton btnBack;
    private EditText edtSearch;
    private TextView tvSubTitle;

    private String categoryId, categoryTitle;

    private static final String TAG = "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list_admin);

        initUi();
        getDataFromIntent();
        setPdfCategory();
        loadPdfList();
        initListener();
        searchPdfBook();

    }

    private void initUi() {
        rcvPdfBook = findViewById(R.id.rcv_book);
        btnBack = findViewById(R.id.btn_back_pdf_book);
        edtSearch = findViewById(R.id.edt_search_pdf_book);
        tvSubTitle = findViewById(R.id.tv_subtitle_book_pdf);
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadPdfList() {
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            // get data
                            ModelPdf modelPdf = ds.getValue(ModelPdf.class);
                            //add to list
                            pdfArrayList.add(modelPdf);
                            Log.d(TAG, "onDataChange:"+ modelPdf.getId() + " " + modelPdf.getTitle());
                        }
                        //set up
                        adminAdapter = new PdfAdminAdapter(PdfListAdminActivity.this, pdfArrayList);
                        rcvPdfBook.setAdapter(adminAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");
    }

    private void searchPdfBook() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //search as and when user each letter
                try {
                    adminAdapter.getFilter().filter(charSequence);
                } catch (Exception e) {
                    Log.d(TAG, "onTextChange:" + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setPdfCategory() {
        tvSubTitle.setText(categoryTitle);
    }
}