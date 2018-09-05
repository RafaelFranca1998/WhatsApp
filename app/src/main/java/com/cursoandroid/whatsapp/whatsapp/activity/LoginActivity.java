package com.cursoandroid.whatsapp.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoandroid.whatsapp.whatsapp.R;
import com.cursoandroid.whatsapp.whatsapp.config.ConfiguracaoFirebase;
import com.cursoandroid.whatsapp.whatsapp.helper.Base64Custom;
import com.cursoandroid.whatsapp.whatsapp.helper.Preferencias;
import com.cursoandroid.whatsapp.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button   botaoLogar;
    private Usuario usuario;
    private FirebaseAuth auntenticacao;
    private ValueEventListener valueEventListenerUsuario;
    private DatabaseReference firebase;
    private String identificadorUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();


        email = findViewById(R.id.edit_login_email);
        senha = findViewById(R.id.edit_login_senha);
        botaoLogar = findViewById(R.id.bt_logar);

        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = new Usuario();
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                validarLogin();
            }
        });

    }

    public void validarLogin(){
        auntenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        auntenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());

                firebase = ConfiguracaoFirebase.getFireBase()
                        .child("usuarios")
                        .child(identificadorUsuarioLogado);
                valueEventListenerUsuario = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usuarioRecuperado = dataSnapshot.getValue( Usuario.class );

                        Preferencias preferencias = new Preferencias(LoginActivity.this);
                        preferencias.salvarDados( identificadorUsuarioLogado,usuarioRecuperado.getNome() );

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);


                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this,"Sucesso ao fazer login!",Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();
                } else {
                    Toast.makeText(LoginActivity.this,"Erro ao fazer login!",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void verificarUsuarioLogado(){
        auntenticacao =  ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (auntenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }

    private void abrirTelaPrincipal(){
        Intent intent =  new Intent(LoginActivity.this,MainActivity.class);
        startActivity( intent );
        finish();
    }

    public void abrirCadastroUsuario(View view){
        Intent intent =  new Intent(LoginActivity.this,CadastroUsuarioActivity.class);
        startActivity(intent);

    }

}
