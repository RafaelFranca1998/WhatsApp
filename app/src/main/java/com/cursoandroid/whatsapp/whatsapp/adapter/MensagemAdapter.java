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
import com.cursoandroid.whatsapp.whatsapp.helper.Preferencias;
import com.cursoandroid.whatsapp.whatsapp.model.Mensagem;
import java.util.ArrayList;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context context;
    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter(@NonNull Context c, @NonNull ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        // Verifica se a lista está preenchida
        if (mensagens != null){
                //recupera preferencias do usuario remetente
                Preferencias preferencias = new Preferencias(context);
                String idUsuarioRemetente = preferencias.getIdentificador();
                // inicializa o layout
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

                // Recupera mensagem

                Mensagem mensagem = mensagens.get(position);

                //monta view a partir do xml
                if (idUsuarioRemetente.equals( mensagem.getIdUsuario() )){
                    view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);
                }else {
                    view = inflater.inflate(R.layout.item_mensagem_esquerda,parent,false);
                }
                // monta view


                //recuperar elemento para exibição
                TextView textoMensagem = view.findViewById(R.id.tv_mensagem);
                textoMensagem.setText( mensagem.getMensagem() );
            }
        return view;
    }
}
