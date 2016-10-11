package com.org.saolonguinho.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.org.saolonguinho.R;


public class ListObjectsFragment extends Fragment {

    public ListObjectsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_objects, container, false);;
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
         ListObjectsAdapter listObjectsAdapter = new ListObjectsAdapter( new String[3]);
        rv.setAdapter(listObjectsAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private final ListObjectsFragment newInstance(){
        return new ListObjectsFragment();
    }
}
