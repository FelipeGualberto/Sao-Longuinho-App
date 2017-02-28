package com.org.saolonguinho.list;

import android.app.ProgressDialog;
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
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ListObjectsFragment extends Fragment {
    private RecyclerView rv;
    private ProgressDialog progressDialog;
    View.OnClickListener onClickFloatingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(ObjectActivity.createIntent(getContext(),null));
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_objects, container, false);
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_btn);
        floatingActionButton.setOnClickListener(onClickFloatingListener);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.loading);
        progressDialog.setCancelable(false);
        if (isEmptyLocalDataStore()) {
            updateDataFromServer();
            progressDialog.show();
        } else {
            loadItens();
            updateDataFromServer();
        }
        return view;
    }

    private void updateDataFromServer() {
        final ParseQuery<Objects> query = ParseQuery.getQuery(Objects.class);
        query.include(Objects.LOCATION);
        query.whereEqualTo(Objects.USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Objects>() {
            public void done(final List<Objects> list, ParseException e) {
                if (e != null) { // Houve um erro
                    switch (e.getCode()) {
                        case ParseException.CONNECTION_FAILED:
                    }
                    return;
                } else {
                    Objects.unpinAllInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) return; // Houve um erro.
                            Objects.pinAllInBackground(list);
                            loadItens();
                        }
                    });
                }
            }
        });
    }

    private void loadItens() {
        ParseQuery<Objects> query = ParseQuery.getQuery(Objects.class);
        query.include(Objects.LOCATION);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Objects>() {
            @Override
            public void done(List<Objects> list, ParseException e) {
                if (e == null) {
                    ListObjectsAdapter listObjectsAdapter = new ListObjectsAdapter(list);
                    rv.setAdapter(listObjectsAdapter);
                    rv.getAdapter().notifyDataSetChanged();
                    progressDialog.dismiss();
                } else {
                }
            }
        });
    }

    public boolean isEmptyLocalDataStore() {
        ParseQuery<Objects> query = ParseQuery.getQuery(Objects.class);
        query.fromLocalDatastore();
        try {
            return query.count() == 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItens();
        updateDataFromServer();
    }

    /*    private void setRecyclerView() {
                ParseQuery<Objects> objectsParseQuery = ParseQuery.getQuery(Objects.class);
                objectsParseQuery.include(Objects.LOCATION);
                objectsParseQuery.whereEqualTo(Objects.USER, ParseUser.getCurrentUser());
                objectsParseQuery.findInBackground(new FindCallback<Objects>() {
                    @Override
                    public void done(List<Objects> objects, ParseException e) {
                        if (e == null) {
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        */
    private final ListObjectsFragment newInstance() {
        return new ListObjectsFragment();
    }
}
