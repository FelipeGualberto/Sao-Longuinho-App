package com.org.saolonguinho.object;

import android.app.Dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.org.saolonguinho.R;

/**
 * Created by Felipe on 13/11/2016.
 */

public class DialogTimePicker extends DialogFragment {

    public interface OnTimeSet {
        public void onSet(int minute, int hour);
    }

    private OnTimeSet onTimeSet;
    private int minute, hour;
    public void setOnTimeSet(OnTimeSet onTimeSet) {
        this.onTimeSet = onTimeSet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    public static DialogTimePicker newInstance() {
        Bundle args = new Bundle();
        DialogTimePicker fragment = new DialogTimePicker();
        fragment.setArguments(args);
        fragment.setCancelable(false);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        Button button = (Button) view.findViewById(R.id.ok_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeSet.onSet(minute,hour);
                dismiss();
            }
        });
        TimePicker timePicker = ((TimePicker) view.findViewById(R.id.tp));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                DialogTimePicker.this.minute = minute;
                DialogTimePicker.this.hour = hourOfDay;
            }
        });
        return view;
    }
}
