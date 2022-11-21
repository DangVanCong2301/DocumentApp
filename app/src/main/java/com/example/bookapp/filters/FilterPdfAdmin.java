package com.example.bookapp.filters;

import android.widget.Filter;

import com.example.bookapp.adapters.CategoryAdapter;
import com.example.bookapp.adapters.PdfAdminAdapter;
import com.example.bookapp.models.ModelCategory;
import com.example.bookapp.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {
    private ArrayList<ModelPdf> filterList;

    private PdfAdminAdapter adminAdapter;

    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, PdfAdminAdapter adminAdapter) {
        this.filterList = filterList;
        this.adminAdapter = adminAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0) {
            //change to upper
            charSequence = charSequence.toString().toLowerCase();
            ArrayList<ModelPdf> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence)) {
                    // add to filter
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        // apply filter
        adminAdapter.pdfArrayList = (ArrayList<ModelPdf>)filterResults.values;

        //notify
        adminAdapter.notifyDataSetChanged();
    }
}
