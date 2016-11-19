package com.org.saolonguinho.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ItemBinding;
import com.org.saolonguinho.shared.models.Objects;

import java.util.List;

/**
 * Created by Felipe on 08/10/2016.
 */

    public class ListObjectsAdapter extends RecyclerView.Adapter<ListObjectsAdapter.ViewHolder> {
        private List<Objects> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemBinding binding;
            public ViewHolder(ItemBinding itemBinding) {
                super(itemBinding.getRoot());
                binding = itemBinding;
            }
        }

        public ListObjectsAdapter(List<Objects> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            ItemBinding itemBinding = DataBindingUtil.inflate( inflater, R.layout.item, parent, false);
            ViewHolder vh = new ViewHolder(itemBinding);
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(itemBinding.getRoot().getContext()));
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.binding.name.setText(mDataset.get(position).getNameObject());
            holder.binding.location.setText(mDataset.get(position).getLocation().getDescription());
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(mDataset.get(position).getImageObject().getUrl(),holder.binding.profileImage);


        }
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


