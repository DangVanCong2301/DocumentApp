package com.example.bookapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.activities.PdfListAdminActivity;
import com.example.bookapp.filters.FilterCategory;
import com.example.bookapp.models.ModelCategory;
import com.example.bookapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Filterable {

    private Context mContext;
    public ArrayList<ModelCategory> categoryArrayList, filterList;

    private FilterCategory filter;

    public CategoryAdapter(Context mContext, ArrayList<ModelCategory> categoryArrayList) {
        this.mContext = mContext;
        this.categoryArrayList = categoryArrayList;
        this.filterList = categoryArrayList;
    }

    // View holder class to hold UI views for row_category.xml
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ModelCategory modelCategory = categoryArrayList.get(position);
        if (modelCategory == null) {
            return;
        }
        String category = modelCategory.getCategory();
        String id = modelCategory.getId();
        String uid = modelCategory.getUid();
        long timestamp = modelCategory.getTimestamp();
        //set data
        holder.tvNameCategory.setText(category);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete")
                        .setMessage("Do you want to delete this category?")
                        .setPositiveButton("Dong y", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(mContext, "Deleting.....", Toast.LENGTH_SHORT).show();
                                deleteCategory(modelCategory);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        holder.layoutItemCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PdfListAdminActivity.class);
                intent.putExtra("categoryId",id);
                intent.putExtra("categoryTitle", category);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (categoryArrayList != null) {
            return categoryArrayList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterCategory(filterList, this);
        }
        return filter;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNameCategory;
        private ImageButton btnDelete;
        private LinearLayout layoutItemCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNameCategory = itemView.findViewById(R.id.tv_category_name);
            btnDelete = itemView.findViewById(R.id.btn_delete_category);
            layoutItemCategory = itemView.findViewById(R.id.layout_item_category);
        }
    }

    private void deleteCategory(ModelCategory modelCategory) {
        String id  = modelCategory.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mContext, "Xoa thanh cong....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
