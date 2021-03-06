package com.org.saolonguinho.list;

import android.app.Activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ItemBinding;
import com.org.saolonguinho.object.ObjectActivity;
import com.org.saolonguinho.shared.models.Objects;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by Felipe on 08/10/2016.
 */

public class ListObjectsAdapter extends RecyclerView.Adapter<ListObjectsAdapter.ViewHolder> {
    private List<Objects> mDataset;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBinding binding;

        public ViewHolder(final ItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
            itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ObjectActivity.createIntent(v.getContext(), mDataset.get(getAdapterPosition()).getObjectId());
                    v.getContext().startActivity(intent);
                }
            });
            itemBinding.sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    String name = mDataset.get(getAdapterPosition()).getNameObject();
                    String location = mDataset.get(getAdapterPosition()).getLocation().getDescription();
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Localização do objeto " + name + ": " + location + " - São Longuinho App");
                    sendIntent.setType("text/plain");
                    v.getContext().startActivity(sendIntent);
                    Toast.makeText(itemBinding.getRoot().getContext(),"Carregando...",Toast.LENGTH_LONG).show();
                }
            });
            itemBinding.objectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", mDataset.get(getAdapterPosition()).getObjectId());
                    Context context = v.getContext();
                    // FragmentManager fragmentManager = (FragmentManager) mActivity.getFragmentManager();
                    FragmentManager fm = ((Activity) context).getFragmentManager();
                    DialogImagePreview.newInstance(bundle).show(fm, "imagePreview");
                }
            });
        }
    }

    public ListObjectsAdapter(List<Objects> myDataset) {
        mDataset = myDataset;
    }

    public void setDataset(List<Objects> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ItemBinding itemBinding = DataBindingUtil.inflate(inflater, R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(itemBinding);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Context context = holder.binding.getRoot().getContext();
        final String id_object = mDataset.get(position).getObjectId();
        holder.binding.name.setText(mDataset.get(position).getNameObject());
        holder.binding.location.setText(mDataset.get(position).getLocation().getDescription());
        ImageLoader imageLoader = ImageLoader.getInstance();
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho", id_object + ".png");
        if ((mDataset.get(position).getImageObject() != null)) {
            if (!file.exists()) {
                imageLoader.displayImage(mDataset.get(position).getImageObject().getUrl(), holder.binding.objectImage, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        try {
                            holder.binding.noImage.setVisibility(View.GONE);
                            holder.binding.objectImage.setVisibility(View.VISIBLE);
                            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho", id_object + ".png");
                            FileOutputStream out = new FileOutputStream(file);
                            loadedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                holder.binding.noImage.setVisibility(View.GONE);
                holder.binding.objectImage.setVisibility(View.VISIBLE);
                imageLoader.displayImage("file://" + Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho" + File.separator + id_object + ".png", holder.binding.objectImage);
            }
        }else{
            holder.binding.noImage.setVisibility(View.VISIBLE);
            holder.binding.objectImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


