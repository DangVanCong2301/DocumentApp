package com.example.bookapp.filters;

import android.widget.Filter;

import com.example.bookapp.adapters.PdfUserAdapter;
import com.example.bookapp.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfUser extends Filter {

    private ArrayList<ModelPdf> filterList;
    private PdfUserAdapter pdfUserAdapter;

    public FilterPdfUser(ArrayList<ModelPdf> filterList, PdfUserAdapter pdfUserAdapter) {
        this.filterList = filterList;
        this.pdfUserAdapter = pdfUserAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null || charSequence.length() > 0) {
            //not null empty
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence)) {
                    // search matches
                    filterModels.add(filterList.get(i));
                }
            }
            results.count = filterModels.size();
            results.values = filterModels;
        } else {
            // empty or null, make
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        // apply filter changes
        pdfUserAdapter.pdfArrayList = (ArrayList<ModelPdf>)filterResults.values;

        //notify changes
        pdfUserAdapter.notifyDataSetChanged();
    }
}
