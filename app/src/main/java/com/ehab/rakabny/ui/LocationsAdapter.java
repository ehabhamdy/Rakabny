package com.ehab.rakabny.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ehab.rakabny.R;

import java.util.List;

/**
 * Created by ehabhamdy on 1/6/18.
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {

    List<String> mData;
    Context mContext;

    ItemClickListener mItemClickListener;

    public interface ItemClickListener{
        void onClick(String location);
    }

    public LocationsAdapter(List<String> data, Context mContext) {
        this.mData = data;
        this.mContext = mContext;
        mItemClickListener = (ItemClickListener) mContext;
    }

    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.location_list_item, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        String item = mData.get(position);
        holder.location_textview.setText(item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView location_textview;

        public LocationViewHolder(View itemView) {
            super(itemView);
            location_textview = itemView.findViewById(R.id.location_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String location = mData.get(getAdapterPosition());
            mItemClickListener.onClick(location);
        }
    }
}
