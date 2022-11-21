package com.example.bookapp.filters;

import android.widget.Filter;

import com.example.bookapp.adapters.CategoryAdapter;
import com.example.bookapp.models.ModelCategory;

import java.util.ArrayList;

public class FilterCategory extends Filter {
    private ArrayList<ModelCategory> filterList;

    private CategoryAdapter categoryAdapter;

    public FilterCategory(ArrayList<ModelCategory> filterList, CategoryAdapter categoryAdapter) {
        this.filterList = filterList;
        this.categoryAdapter = categoryAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0) {
            //change to upper
            charSequence = charSequence.toString().toLowerCase();
            ArrayList<ModelCategory> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if (filterList.get(i).getCategory().toUpperCase().contains(charSequence)) {
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
        categoryAdapter.categoryArrayList = (ArrayList<ModelCategory>)filterResults.values;

        //notify
        categoryAdapter.notifyDataSetChanged();
    }
}
