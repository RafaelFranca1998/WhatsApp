package com.cursoandroid.whatsapp.whatsapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cursoandroid.whatsapp.whatsapp.R;
import com.cursoandroid.whatsapp.whatsapp.model.Contato;
import com.cursoandroid.whatsapp.whatsapp.model.Conversa;

import java.util.ArrayList;

public class ConversaAdapter extends ArrayAdapter {


    private ArrayList<Conversa> conversas;
    private Context context;

    public ConversaAdapter(@NonNull Context c, @NonNull ArrayList<Conversa> objects) {
        super(c,0, objects);
        this.conversas = objects;
        this.context = c;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if (conversas != null){
            //inicializa objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Monta view a partir do xml
            view = inflater.inflate(R.layout.lista_conversas,parent,false);

            //recupera elemento para exibição
            TextView nomeContato = view.findViewById(R.id.tv_nome_conversas);
            TextView texto = view.findViewById(R.id.tv_texto_conversa);

            Conversa conversa = conversas.get(position);

            nomeContato.setText(conversa.getNome());
            texto.setText(conversa.getMensagem());

        }

        return view;
    }
}
