package com.example.bookapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookapp.MyApplication;
import com.example.bookapp.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PdfDetailActivity extends AppCompatActivity {

    private TextView tvCategory, tvSizeDetail, tvTitle, tvDescription, tvViews, tvDownloads, tvDate;
    private PDFView pdfViewDetail;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private Button btnReadBookPdf, btnDownloadsBook;

    private String bookId, bookTitle, bookUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_detail);

        initUi();
        getDataFromIntent();
        loadBookDetails();
        incrementViews();
        initListener();

    }

    private void initUi() {
        tvCategory = findViewById(R.id.tv_category_pdf_detail);
        pdfViewDetail = findViewById(R.id.pdf_view_pdf_detail);
        progressBar = findViewById(R.id.progressBar_detail);
        tvSizeDetail = findViewById(R.id.tv_size_pdf_detail);
        tvTitle = findViewById(R.id.tv_title_pdf_detail);
        tvDescription = findViewById(R.id.tv_description_pdf_detail);
        tvViews = findViewById(R.id.tv_views_pdf_detail);
        tvDownloads = findViewById(R.id.tv_downloads_pdf_detail);
        tvDate = findViewById(R.id.tv_date_pdf_detail);
        btnBack = findViewById(R.id.btn_back_pdf_detail);
        btnReadBookPdf = findViewById(R.id.btn_read_book_pdf);
        btnDownloadsBook = findViewById(R.id.btn_download_book_pdf);
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnReadBookPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PdfDetailActivity.this, PdfViewActivity.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            }
        });

        btnDownloadsBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG_DOWNLOAD, "onClick: Check permission");
                if (ContextCompat.checkSelfPermission(PdfDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG_DOWNLOAD, "onClick: Permission already granted");
                    MyApplication.downloadBook(PdfDetailActivity.this, "" + bookId, "" + bookTitle, "" + bookUrl);
                } else {
                    Log.d(TAG_DOWNLOAD, "onClick:Permission was granted, request permission");
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        btnDownloadsBook.setVisibility(View.GONE);
    }

    private void loadBookDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get data
                        bookTitle = "" + snapshot.child("title").getValue();
                        String description = "" + snapshot.child("description").getValue();
                        String categoryId = "" + snapshot.child("categoryId").getValue();
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();
                        bookUrl = "" + snapshot.child("url").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();

                        //required data
                        btnDownloadsBook.setVisibility(View.VISIBLE);
                        //format date
                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadCategory(
                                "" + categoryId,
                                tvCategory
                        );
                        MyApplication.loadPdfFromUrlSinglePage(
                                "" + bookUrl,
                                "" + bookTitle,
                                pdfViewDetail,
                                progressBar
                        );
                        MyApplication.loadPdfSize(
                                "" + bookUrl,
                                "" + bookTitle,
                                tvSizeDetail
                        );

                        //set data
                        tvTitle.setText(bookTitle);
                        tvDescription.setText(description);
                        tvViews.setText(viewsCount.replace("null", "N/A"));
                        tvDownloads.setText(downloadsCount.replace("null", "N/A"));
                        tvDate.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Log.d(TAG_DOWNLOAD, "Permission Granted ");
            MyApplication.downloadBook(this, "" + bookId, "" + bookTitle, "" + bookUrl);

        } else {
            Log.d(TAG_DOWNLOAD, "Permission was dined... ");
            Toast.makeText(this, "Permission was dined....", Toast.LENGTH_SHORT).show();
        }
    });

    private void incrementViews() {
        MyApplication.incrementBookViewCount(bookId);
    }

}