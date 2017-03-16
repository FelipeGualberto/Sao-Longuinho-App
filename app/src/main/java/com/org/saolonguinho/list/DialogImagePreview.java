package com.org.saolonguinho.list;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.org.saolonguinho.R;

import java.io.File;

/**
 * Created by Felipe on 01/03/2017.
 */

public class DialogImagePreview extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    public static DialogImagePreview newInstance(Bundle arg) {
        DialogImagePreview dialogImagePreview = new DialogImagePreview();
        dialogImagePreview.setArguments(arg);
        dialogImagePreview.setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, 0);
        return dialogImagePreview;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_preview_image, container, false);
        String id_object = getArguments().getString("Id");
        ImageView imageView = ((ImageView) view.findViewById(R.id.image_preview));
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage("file://" + Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho" + File.separator + id_object + ".png", imageView);
        return view;
    }
}
