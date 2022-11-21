package com.example.bookapp.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.MyApplication;
import com.example.bookapp.activities.PdfDetailActivity;
import com.example.bookapp.activities.PdfEditActivity;
import com.example.bookapp.R;
import com.example.bookapp.filters.FilterPdfAdmin;
import com.example.bookapp.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class PdfAdminAdapter extends RecyclerView.Adapter<PdfAdminAdapter.PdfAdminViewHolder> implements Filterable {

    private Context context;
    public ArrayList<ModelPdf> pdfArrayList, filterList;
    private static final String TAG = "PDF_ADAPTER_TAG";

    private FilterPdfAdmin filter;
    private ProgressDialog progressDialog;


    public PdfAdminAdapter(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        //init progress
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public PdfAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pdf_admin, parent, false);
        return new PdfAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfAdminViewHolder holder, int position) {
        ModelPdf modelPdf = pdfArrayList.get(position);
        if (modelPdf == null) {
            return;
        }
        String pdfId = modelPdf.getId();
        String categoryId = modelPdf.getCategoryId();
        String title = modelPdf.getTitle();
        String description = modelPdf.getDescription();
        long timestamp = modelPdf.getTimestamp();
        String formattedDate = MyApplication.formatTimestamp(timestamp);
        String pdfUrl = modelPdf.getUrl();

        holder.tvTitle.setText(title);
        holder.tvDescription.setText(description);
        holder.tvDate.setText(formattedDate);

        //load further
        MyApplication.loadCategory(
                "" + categoryId,
                holder.tvCategory
        );
        // we don't need page number in here
        MyApplication.loadPdfFromUrlSinglePage(
                "" + pdfUrl,
                "" + title,
                holder.pdfBook,
                holder.prgPdf
        );
        MyApplication.loadPdfSize(
                "" + pdfUrl,
                "" + title,
                holder.tvSize
        );

        holder.btnSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionDialog(modelPdf, holder);
            }
        });

        holder.layoutItemBookPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId", pdfId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (pdfArrayList != null) {
            return pdfArrayList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterPdfAdmin(filterList, this);
        }
        return filter;
    }

    public class PdfAdminViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDescription, tvCategory, tvSize, tvDate;
        private PDFView pdfBook;
        private ProgressBar prgPdf;
        private ImageButton btnSeeMore;
        private RelativeLayout layoutItemBookPdf;

        public PdfAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title_pdf);
            pdfBook = itemView.findViewById(R.id.pdfView);
            prgPdf = itemView.findViewById(R.id.prg_pdf);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvCategory = itemView.findViewById(R.id.tv_category_pdf);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvDate = itemView.findViewById(R.id.tv_date);
            btnSeeMore = itemView.findViewById(R.id.btn_see_more);
            layoutItemBookPdf = itemView.findViewById(R.id.layout_item_book_pdf);
        }
    }

    private void moreOptionDialog(ModelPdf modelPdf, PdfAdminViewHolder holder) {

        String bookId = modelPdf.getId();
        String bookUrl = modelPdf.getUrl();
        String bookTitle = modelPdf.getTitle();

        //options to show
        String[] options = {"Edit", "Delete"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //handle dialog
                        if (i == 0) {
                            //edit clicked, open new activity to edit book
                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("bookId", bookId);
                            context.startActivity(intent);
                        } else if (i == 1) {
                            //delete
                            MyApplication.deleteBook(
                                    context,
                                    "" + bookId,
                                    "" + bookUrl,
                                    "" + bookTitle);
                            //deleteBook(modelPdf, holder);
                        }
                    }
                })
                .show();
    }




}
