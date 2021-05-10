package com.wyre.trade.home.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.ContactUser;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AutoUserAdapter extends ArrayAdapter<ContactUser> {

    ArrayList<ContactUser> users,tempItems, suggestions = new ArrayList<>();
    Context context;
    int resourceId;

    public AutoUserAdapter(@NonNull Context context, int resourceId, ArrayList<ContactUser> users) {
        super(context, resourceId, users);
        this.users = users;
        this.context = context;
        this.resourceId = resourceId;
        this.tempItems = users;
        this.suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                view = inflater.inflate(resourceId, parent, false);
            }

            TextView textView = view.findViewById(R.id.name);
            textView.setText(getItem(position).getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    @Nullable
    @Override
    public ContactUser getItem(int position) {
        return users.get(position);
    }
    @Override
    public int getCount() {
        return users.size();
    }
    @NonNull
    @Override
    public Filter getFilter() {
        return userFilter;
    }
    private Filter userFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            ContactUser user = (ContactUser) resultValue;
            return user.getName();
        }
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (ContactUser user: tempItems) {
                    if (user.getName().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        suggestions.add(user);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            ArrayList<ContactUser> tempValues = (ArrayList<ContactUser>) filterResults.values;
            if (filterResults != null && filterResults.count > 0) {
                clear();
                for (ContactUser fruitObj : tempValues) {
                    add(fruitObj);
                }
            } else {
                clear();
            }
            notifyDataSetChanged();
        }
    };
}

