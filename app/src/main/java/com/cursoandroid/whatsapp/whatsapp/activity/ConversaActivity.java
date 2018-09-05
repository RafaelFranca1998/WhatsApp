package com.cursoandroid.whatsapp.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.cursoandroid.whatsapp.whatsapp.R;
import com.cursoandroid.whatsapp.whatsapp.adapter.MensagemAdapter;
import com.cursoandroid.whatsapp.whatsapp.config.ConfiguracaoFirebase;
import com.cursoandroid.whatsapp.whatsapp.helper.Base64Custom;
import com.cursoandroid.whatsapp.whatsapp.helper.Preferencias;
import com.cursoandroid.whatsapp.whatsapp.model.Conversa;
import com.cursoandroid.whatsapp.whatsapp.model.Mensagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;

    // dados de destinatário
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    // dados remetente
    private String nomeUsuarioRemetente;
    private String idUsuarioRemetente;

    private EditText editMensagem;
    private ImageButton btMensagem;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = findViewById(R.id.tb_conversa);
        editMensagem = findViewById(R.id.edit_mensagem);
        btMensagem = findViewById(R.id.bt_enviar);
        listView =  findViewById(R.id.lv_conversas);

        // dados do usuário logado
        final Preferencias preferencias =  new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra = getIntent().getExtras();

        if (extra!= null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }

        // configurar toolbar
        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //monta listview e adapter
        mensagens =  new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this,mensagens);
        listView.setAdapter( adapter );

        //recupera mensagens do firebase
        firebase = ConfiguracaoFirebase.getFireBase()
                .child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        //cria listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //limpar mensagens
                mensagens.clear();
                //recupera mensagens
                for (DataSnapshot dados:dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        firebase.addValueEventListener( valueEventListenerMensagem );

        // enviar mensagem

        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoMensagem = editMensagem.getText().toString();
                if (textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this,"Digite uma mensagem",Toast.LENGTH_LONG).show();
                }else {
                    Mensagem mensagem =  new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,mensagem);

                    if (!retornoMensagemDestinatario){
                        Toast.makeText(ConversaActivity.this,
                                "Problema ao salvar mensagem, tente novamente!",
                                Toast.LENGTH_LONG).show();
                    }else{
                        boolean retornoMensagemRemetente = salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);
                        if (retornoMensagemRemetente){
                            Toast.makeText(ConversaActivity.this,
                                    "Problema ao enviar mensagem ao destinatário, tente novamente!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    Conversa conversa =  new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome( nomeUsuarioDestinatario );
                    conversa.setMensagem( textoMensagem );
                    boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente,idUsuarioDestinatario,conversa);
                    if (!retornoConversaRemetente){
                        Toast.makeText(ConversaActivity.this,
                                "Problema ao salvar conversa, tente novamente!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Conversa conversa1 = new Conversa();
                        conversa1.setIdUsuario(idUsuarioRemetente);
                        conversa1.setNome(preferencias.getNome());
                        conversa1.setMensagem(textoMensagem);
                        boolean retornoConversaDestinatario = salvarConversa(idUsuarioRemetente,idUsuarioDestinatario,conversa);
                        if (!retornoConversaDestinatario){
                            Toast.makeText(ConversaActivity.this,
                                    "Problema ao salvar conversa do destinatário, tente novamente!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    editMensagem.setText("");
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try {
            firebase = ConfiguracaoFirebase.getFireBase().child("mensagens");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente,String idDestinatario, Conversa conversa){
        try {
            firebase = ConfiguracaoFirebase.getFireBase().child("conversas");
            firebase.child(idRemetente).child(idDestinatario).setValue(conversa);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
