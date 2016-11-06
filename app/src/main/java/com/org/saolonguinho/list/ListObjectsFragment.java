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
import com.org.saolonguinho.object.ObjectActivity;
import com.org.saolonguinho.shared.models.Location;
import com.org.saolonguinho.shared.models.Objects;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ListObjectsFragment extends Fragment {
    private RecyclerView rv;
    View.OnClickListener onClickFloatingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(ObjectActivity.createIntent(getContext()));
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_objects, container, false);
        ;
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_btn);
        floatingActionButton.setOnClickListener(onClickFloatingListener);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        setRecyclerView();
        return view;
    }

    private void setRecyclerView() {
        ParseQuery<Objects> objectsParseQuery = ParseQuery.getQuery(Objects.class);
        objectsParseQuery.include(Objects.LOCATION);
        objectsParseQuery.whereEqualTo(Objects.USER, ParseUser.getCurrentUser());
        objectsParseQuery.findInBackground(new FindCallback<Objects>() {
            @Override
            public void done(List<Objects> objects, ParseException e) {
                if(e == null) {
                    ListObjectsAdapter listObjectsAdapter = new ListObjectsAdapter(objects);
                    rv.setAdapter(listObjectsAdapter);
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        });

    }

    private final ListObjectsFragment newInstance() {
        return new ListObjectsFragment();
    }
}
