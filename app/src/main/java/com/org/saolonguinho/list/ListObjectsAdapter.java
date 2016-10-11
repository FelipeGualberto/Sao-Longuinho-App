package com.org.saolonguinho.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ItemBinding;

/**
 * Created by Felipe on 08/10/2016.
 */

    public class ListObjectsAdapter extends RecyclerView.Adapter<ListObjectsAdapter.ViewHolder> {
        private String[] mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(ItemBinding itemBinding) {
                super(itemBinding.getRoot());
            }
        }

        public ListObjectsAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            ItemBinding itemBinding = DataBindingUtil.inflate( inflater, R.layout.item, parent, false);
            ViewHolder vh = new ViewHolder(itemBinding);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //holder.mTextView.setText(mDataset[position]);

        }
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }


