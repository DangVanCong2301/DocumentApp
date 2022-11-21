package com.example.bookapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.MyApplication;
import com.example.bookapp.R;
import com.example.bookapp.activities.PdfDetailActivity;
import com.example.bookapp.filters.FilterPdfUser;
import com.example.bookapp.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class PdfUserAdapter extends RecyclerView.Adapter<PdfUserAdapter.PdfUserViewHolder> implements Filterable {

    private Context context;
    public ArrayList<ModelPdf> pdfArrayList, filterList;
    private FilterPdfUser filter;

    private static final String TAG = "ADAPTER_PDF_USER";

    public PdfUserAdapter(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public PdfUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pdf_user, parent, false);
        return new PdfUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfUserViewHolder holder, int position) {
        ModelPdf modelPdf = pdfArrayList.get(position);
        if (modelPdf == null) {
            return;
        }
        String bookId = modelPdf.getId();
        String title = modelPdf.getTitle();
        String description = modelPdf.getDescription();
        String pdfUrl = modelPdf.getUrl();
        String categoryId = modelPdf.getCategoryId();
        long timestamp = modelPdf.getTimestamp();

        //convert time
        String date = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.tvTitle.setText(title);
        holder.tvDescription.setText(description);
        holder.tvDate.setText(date);

        // we don't need page number here], pass null
        MyApplication.loadPdfFromUrlSinglePage(
                "" + pdfUrl,
                "" + title,
                holder.pdfView,
                holder.progressBar
        );

        MyApplication.loadCategory(
                "" + categoryId,
                holder.tvCategory
        );

        MyApplication.loadPdfSize(
                "" + pdfUrl,
                "" +title,
                holder.tvSize
        );

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId", bookId);
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
            filter = new FilterPdfUser(filterList, this);
        }
        return filter;
    }

    public class PdfUserViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDescription, tvDate, tvCategory, tvSize;
        private PDFView pdfView;
        private ProgressBar progressBar;
        private RelativeLayout layoutItem;

        public PdfUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title_pdf_user);
            tvDescription = itemView.findViewById(R.id.tv_description_user);
            tvCategory = itemView.findViewById(R.id.tv_category_pdf_user);
            tvSize = itemView.findViewById(R.id.tv_size_user);
            tvDate = itemView.findViewById(R.id.tv_date_user);
            pdfView = itemView.findViewById(R.id.pdfView_user);
            progressBar = itemView.findViewById(R.id.prg_pdf_user);
            layoutItem = itemView.findViewById(R.id.layout_item_book_pdf_user);
        }
    }
}
