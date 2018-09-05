package com.cursoandroid.whatsapp.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cursoandroid.whatsapp.whatsapp.R;
import com.cursoandroid.whatsapp.whatsapp.activity.ConversaActivity;
import com.cursoandroid.whatsapp.whatsapp.adapter.ConversaAdapter;
import com.cursoandroid.whatsapp.whatsapp.config.ConfiguracaoFirebase;
import com.cursoandroid.whatsapp.whatsapp.helper.Base64Custom;
import com.cursoandroid.whatsapp.whatsapp.helper.Preferencias;
import com.cursoandroid.whatsapp.whatsapp.model.Contato;
import com.cursoandroid.whatsapp.whatsapp.model.Conversa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Conversa> adapter;
    private ArrayList<Conversa> conversas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListener;

    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversas, container, false);
        listView = view.findViewById(R.id.lv_coversa);
        conversas = new ArrayList<>();
        adapter = new ConversaAdapter(getActivity(),conversas);
        listView.setAdapter(adapter);

        Preferencias preferencias =  new Preferencias(getActivity());
        String idUsuarioLogado =  preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFireBase().child("conversas").child(idUsuarioLogado);

        valueEventListener =  new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conversas.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Conversa conversa =  dados.getValue(Conversa.class);
                    conversas.add(conversa);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Conversa conversa = conversas.get(i);

                String email = Base64Custom.decodificarBase64(conversa.getIdUsuario());
                Intent intent =  new Intent(getActivity(), ConversaActivity.class);
                intent.putExtra("nome", conversa.getNome());
                intent.putExtra("email", email  );
                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListener);
    }
}
