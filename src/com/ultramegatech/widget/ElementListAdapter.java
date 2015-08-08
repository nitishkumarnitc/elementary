/*
 * The MIT License (MIT)
 * Copyright © 2012 Steve Guidetti
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ultramegatech.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.ultramegatech.ey.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This custom ListAdapter is for displaying a list of elements.
 *
 * @author Steve Guidetti
 */
public class ElementListAdapter extends BaseAdapter implements ListAdapter, Filterable {
    /* Sorting options */
    public static final int SORT_NUMBER = 0;
    public static final int SORT_NAME = 1;
    
    private final Context mContext;
    
    /* The original data set */
    private final ArrayList<ElementHolder> mListItems;
    
    /* The filter for this list adapter */
    private final Filter mFilter;
    
    /* The filtered and sorted data set */
    private final ArrayList<ElementHolder> mFiltered;

    /**
     * Constructor
     * 
     * @param context
     * @param listItems List of elements
     * @param filter Initial filter text
     * @param sortBy Initial sorting field
     */
    public ElementListAdapter(Context context, ArrayList<ElementHolder> listItems, String filter, int sortBy) {
        mContext = context;
        mListItems = listItems;
        
        mFiltered = getFilteredList(filter);
        sortList(sortBy);
        
        mFilter = new Filter() {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence cs) {
                final ArrayList<ElementHolder> filtered = getFilteredList(cs.toString());
                
                final FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                
                return results;
            }

            @Override
            protected void publishResults(CharSequence cs, Filter.FilterResults fr) {
                mFiltered.clear();
                mFiltered.addAll((ArrayList<ElementHolder>)fr.values);
                
                notifyDataSetChanged();
            }
        };
    }

    public int getCount() {
        return mFiltered.size();
    }

    public Object getItem(int i) {
        return mFiltered.get(i);
    }

    public long getItemId(int i) {
        return mFiltered.get(i).id;
    }

    public View getView(int i, View view, ViewGroup vg) {
        if(view == null) {
            view = View.inflate(mContext, R.layout.element_list_item, null);
        }
        
        final ElementHolder holder = mFiltered.get(i);
        
        ((TextView)view.findViewById(R.id.number)).setText(holder.number);
        ((TextView)view.findViewById(R.id.symbol)).setText(holder.symbol);
        ((TextView)view.findViewById(R.id.name)).setText(holder.name);
        view.findViewById(R.id.block).setBackgroundColor(holder.color);
        
        return view;
    }

    public Filter getFilter() {
        return mFilter;
    }
    
    /**
     * Set the field used to sort elements.
     * 
     * @param sortBy One of the SORT_ constants
     */
    public void setSort(int sortBy) {
        sortList(sortBy);
        notifyDataSetChanged();
    }
    
    /**
     * Get a filtered copy of the original data set
     * 
     * @param filter Text used to filter the elements
     * @return 
     */
    private ArrayList<ElementHolder> getFilteredList(String filter) {
        if(TextUtils.isEmpty(filter)) {
            return mListItems;
        }
        
        final ArrayList<ElementHolder> filtered = new ArrayList<ElementHolder>();

        for(ElementHolder element : mListItems) {
            if(element.symbol.toLowerCase().startsWith(filter.toLowerCase())
                    || element.name.toLowerCase().startsWith(filter.toLowerCase())) {
                filtered.add(element);
            }
        }
        
        return filtered;
    }
    
    /**
     * Sort the filtered list.
     * 
     * @param sortBy One of the SORT_ constants
     */
    private void sortList(int sortBy) {
        Collections.sort(mFiltered, new ElementComparator(sortBy));
    }
    
    /**
     * Class to hold data for a single element
     */
    public static class ElementHolder {
        public final long id;
        public final String number;
        public final String symbol;
        public final String name;
        public final int color;

        public ElementHolder(String number, String symbol, String name, int color) {
            this.id = Long.valueOf(number);
            this.number = number;
            this.symbol = symbol;
            this.name = name;
            this.color = color;
        }
        
    }
    
    /**
     * Comparator used for sorting elements
     */
    private static class ElementComparator implements Comparator<ElementHolder> {
        
        private final int mSortField;

        /**
         * Constructor
         * 
         * @param sortField One of the SORT_ constants
         */
        public ElementComparator(int sortField) {
            mSortField = sortField;
        }

        public int compare(ElementHolder l, ElementHolder r) {
            if(mSortField == SORT_NUMBER) {
                return Integer.valueOf(l.number) - Integer.valueOf(r.number);
            }
            
            return l.name.compareTo(r.name);
        }
        
    }
}