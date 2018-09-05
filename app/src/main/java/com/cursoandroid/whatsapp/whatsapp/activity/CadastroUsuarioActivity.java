package com.cursoandroid.whatsapp.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText        nome;
    private EditText        email;
    private EditText        senha;
    private Button          botaoCadastrar;
    private Usuario         usuario;
    private FirebaseAuth    autenticacao;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome           = findViewById(R.id.edit_cadastro_nome);
        email          = findViewById(R.id.edit_cadastro_email);
        senha          = findViewById(R.id.edit_cadastro_senha);
        botaoCadastrar = findViewById(R.id.bt_cadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = new Usuario();
                usuario.setNome(nome.getText().toString());
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());

                cadastrarUsuario();
            }
        });
    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CadastroUsuarioActivity.this,"Sucesso ao cadastrar usuário",Toast.LENGTH_LONG);

                    String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    FirebaseUser user =  task.getResult().getUser();
                    usuario.setId( identificadorUsuario );
                    usuario.salvar();
                    abrirUsuarioLogado();

                    Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                    preferencias.salvarDados( identificadorUsuario , usuario.getNome());
                }else {
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte, contendo letras e numeros";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "O emais digitado é inválido, digite outro email";
                    } catch (FirebaseAuthUserCollisionException e ){
                        erroExcecao = "já existe outra conta com este e-mail";
                    }catch (Exception e){
                        erroExcecao = "Erro a o efetuar cadastro";
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroUsuarioActivity.this,erroExcecao,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void  abrirUsuarioLogado(){
        Intent intent =  new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
