package com.example.contactsapp;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactsapp.models.Contact;

import java.util.List;
import java.util.stream.Collectors;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> implements Filterable {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    MyRecyclerViewAdapter(Context context, List<Contact> data) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = MainActivity.filteredContactList.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactNumber.setText(contact.getNumber());
    }

    @Override
    public int getItemCount() {
        return MainActivity.filteredContactList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final String filterQuery = constraint.toString();
                List<Contact> filteredResults = MainActivity.contactList.stream()
                        .filter(contact -> contact.getName().toLowerCase().contains(filterQuery.toLowerCase()) || contact.getNumber().contains(filterQuery))
                        .collect(Collectors.toList());
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredResults;
                MainActivity.filteredContactList = filteredResults;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView contactName, contactNumber;

        ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.tvContactName);
            contactNumber = itemView.findViewById(R.id.tvContactNumber);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
