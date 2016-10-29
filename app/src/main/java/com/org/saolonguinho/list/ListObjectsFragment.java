package com.org.saolonguinho.list;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.org.saolonguinho.R;
import com.org.saolonguinho.edit.EditObjectActivity;

public class ListObjectsFragment extends Fragment {

   View.OnClickListener onClickFloatingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(EditObjectActivity.createIntent(getContext()));
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_objects, container, false);;
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        ListObjectsAdapter listObjectsAdapter = new ListObjectsAdapter( new String[3]);
        rv.setAdapter(listObjectsAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_btn);
        floatingActionButton.setOnClickListener(onClickFloatingListener);

        return view;
    }

    private final ListObjectsFragment newInstance(){
        return new ListObjectsFragment();
    }
}
