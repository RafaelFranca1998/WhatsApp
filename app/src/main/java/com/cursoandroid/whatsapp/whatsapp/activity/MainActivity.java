package com.cursoandroid.whatsapp.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoandroid.whatsapp.whatsapp.R;
import com.cursoandroid.whatsapp.whatsapp.adapter.TabAdapter;
import com.cursoandroid.whatsapp.whatsapp.config.ConfiguracaoFirebase;
import com.cursoandroid.whatsapp.whatsapp.helper.Base64Custom;
import com.cursoandroid.whatsapp.whatsapp.helper.Preferencias;
import com.cursoandroid.whatsapp.whatsapp.helper.SlidingTabLayout;
import com.cursoandroid.whatsapp.whatsapp.model.Contato;
import com.cursoandroid.whatsapp.whatsapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth usuarioFirebase;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorDoContato;
    private DatabaseReference firebase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();

        slidingTabLayout = findViewById(R.id.stl_tabs);
        viewPager        = findViewById(R.id.vp_pagina);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("WhatsApp");

        toolbar.setTitleTextColor(getResources().getColor(R.color.branco));

        slidingTabLayout.setDistributeEvenly(true);

        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.collorAccente));


        setSupportActionBar(toolbar);

        // configurar adapter
        TabAdapter tabAdapter =  new TabAdapter( getSupportFragmentManager() );

        viewPager.setAdapter( tabAdapter );

        slidingTabLayout.setViewPager( viewPager );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_sair: deslogarUsuario();
                return true;

            case R.id.item_onfiguracoes:
                return true;

            case R.id.item_adicionar:
                abrirCadastroContato();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void abrirCadastroContato(){
        AlertDialog.Builder alertDialog =  new AlertDialog.Builder(this);

        //Configurações do dialog
        alertDialog.setTitle("Novo Contato");
        alertDialog.setMessage("E-mail do usuário");
        alertDialog.setCancelable(false);

        final EditText editText =  new EditText(MainActivity.this);

        alertDialog.setView( editText );

        //configura botões
        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String emailContato = editText.getText().toString();

                // valida
                if (emailContato.isEmpty()){
                    Toast.makeText(MainActivity.this,"Preencha o e-mail",Toast.LENGTH_SHORT).show();
                }else {
                    //verifica se já existe cadastro
                    identificadorDoContato = Base64Custom.codificarBase64(emailContato);

                    //recuperar instacia
                    firebase = ConfiguracaoFirebase.getFireBase().child("usuarios").child(identificadorDoContato);

                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if( dataSnapshot.getValue() != null ){

                                //Recuperar dados do contato a ser adicionado
                                Usuario usuarioContato = dataSnapshot.getValue( Usuario.class );

                                //Recuperar identificador usuario logado (base64)
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado = preferencias.getIdentificador();

                                firebase = ConfiguracaoFirebase.getFireBase();
                                firebase = firebase.child("contatos")
                                        .child( identificadorUsuarioLogado )
                                        .child( identificadorDoContato );

                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario( identificadorDoContato );
                                contato.setEmail( usuarioContato.getEmail() );
                                contato.setNome( usuarioContato.getNome() );

                                firebase.setValue( contato );
                            }else {
                                Toast.makeText(MainActivity.this,"Usuário não possui cadastro",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog.create();

        alertDialog.show();
    }

    public void deslogarUsuario(){
        //desloga
        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarioFirebase.signOut();
        //volta a tela de login
        Intent intent =  new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
