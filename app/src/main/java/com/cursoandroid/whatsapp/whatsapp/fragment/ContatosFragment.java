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
import com.cursoandroid.whatsapp.whatsapp.activity.MainActivity;
import com.cursoandroid.whatsapp.whatsapp.adapter.ContatoAdapter;
import com.cursoandroid.whatsapp.whatsapp.config.ConfiguracaoFirebase;
import com.cursoandroid.whatsapp.whatsapp.helper.Preferencias;
import com.cursoandroid.whatsapp.whatsapp.model.Contato;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;

    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Instanciar objetos
        contatos =  new ArrayList<>();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //monta listview we o adapter
        listView = view.findViewById(R.id.lv_contatos);
        adapter = new ContatoAdapter(getActivity(),contatos);


        listView.setAdapter(adapter);

        //recupera contatos firebase
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase()
                .child("contatos")
                .child(identificadorUsuarioLogado);

        //listener para recuperar contatos
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //limpar lista
                contatos.clear();
                //listar contatos
                for (DataSnapshot dados:dataSnapshot.getChildren()){

                    Contato contato =  dados.getValue(Contato.class);
                    contatos.add(contato);

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

                    Contato contato = contatos.get(i);

                    Intent intent =  new Intent(getActivity(), ConversaActivity.class);
                    intent.putExtra("nome", contato.getNome());
                    intent.putExtra("email", contato.getEmail());
                    startActivity(intent);

                }
            });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //inicia o listener
        firebase.addValueEventListener(valueEventListenerContatos);
    }

    @Override
    public void onStop() {
        super.onStop();
        //finaliza o listener
        firebase.removeEventListener(valueEventListenerContatos);
    }
}
