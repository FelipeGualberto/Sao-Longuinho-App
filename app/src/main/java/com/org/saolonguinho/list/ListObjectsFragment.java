package com.org.saolonguinho.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.org.saolonguinho.MainActivity;
import com.org.saolonguinho.R;
import com.org.saolonguinho.object.ObjectActivity;
import com.org.saolonguinho.shared.models.Objects;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ListObjectsFragment extends Fragment {
    private RecyclerView rv;
    private ProgressDialog progressDialog;
    private Boolean isVerified = false;
    private Boolean forceUpdate = false;
    View.OnClickListener onClickFloatingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ListObjectsAdapter adapter = (ListObjectsAdapter) rv.getAdapter();
            if(adapter.getItemCount() < 14) {
                if ((!isVerified) && (adapter.getItemCount() > 2)) {
                    Toast.makeText(getContext(), "Por favor verifique seu email para poder continuar adicionando mais itens (Reinicie a aplicação já tenha feito!)", Toast.LENGTH_LONG).show();
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            createDialogSendEmailConfirmAgain();
                        }
                    };
                    handler.postDelayed(r, 2000);
                } else {
                    startActivity(ObjectActivity.createIntent(getContext(), null));
                }
            }else{
                Toast.makeText(getContext(), "Estamos muito felizes por você está usando bastante o aplicativo!", Toast.LENGTH_LONG).show();
                createThanksDialog();
            }
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
        }
        verifyEmailStatus();
        ((MainActivity) getActivity()).setInterface(interfaceMain);
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

    private void loadItensLocalSpecific(String text) {
        ParseQuery<Objects> query = ParseQuery.getQuery(Objects.class);
        query.whereMatches(Objects.NAME, "(" + text + ")", "i");
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
        if(forceUpdate) {
            loadItens();
            updateDataFromServer();
        }
        else{
            forceUpdate = true;
        }
        verifyEmailStatus();
    }

    private final ListObjectsFragment newInstance() {
        return new ListObjectsFragment();
    }

    MainActivity.InterfaceMain interfaceMain = new MainActivity.InterfaceMain() {
        @Override
        public void onSearch(String text) {
            loadItensLocalSpecific(text);
        }

        @Override
        public void update() {
            updateDataFromServer();
        }
    };

    private void verifyEmailStatus() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean verified = sharedPref.getBoolean("emailVerified", false);
        if (verified) {
            isVerified = verified;
        } else {
            ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e != null) {
                        Toast.makeText(getContext(), "Sem internet", Toast.LENGTH_LONG).show();
                    } else {
                        isVerified = ParseUser.getCurrentUser().getBoolean("emailVerified");
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("emailVerified", isVerified);
                        editor.apply();
                    }
                }
            });
        }
    }

    private void createDialogSendEmailConfirmAgain() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Você deseja enviar outro email de confirmação?");
        builder.setCancelable(true);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ParseUser.getCurrentUser().setEmail(ParseUser.getCurrentUser().getEmail());
                        ParseUser.getCurrentUser().saveInBackground();
                        Toast.makeText(getContext(), "Enviado!", Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void createThanksDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Muito obrigado por está usando o São Longuinho! Para que você possa adicionar mais itens por favor mande um email para: fgualberto.santos@gmail.com");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
