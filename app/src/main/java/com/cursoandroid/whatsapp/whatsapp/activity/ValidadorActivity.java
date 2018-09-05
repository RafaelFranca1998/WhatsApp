package com.cursoandroid.whatsapp.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoandroid.whatsapp.whatsapp.R;
import com.cursoandroid.whatsapp.whatsapp.helper.Preferencias;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;

public class ValidadorActivity extends AppCompatActivity {


    private EditText codigoValidacao;
    private Button validar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codigoValidacao = findViewById(R.id.edit_code_validacao);
        validar = findViewById(R.id.bt_validar);

        SimpleMaskFormatter simpleMaskCodValidacao = new SimpleMaskFormatter( "NNNN" );
        MaskTextWatcher           maskCodValidacao = new MaskTextWatcher(codigoValidacao,simpleMaskCodValidacao);

        codigoValidacao.addTextChangedListener( maskCodValidacao );
/*
        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Preferencias preferencias = new Preferencias(ValidadorActivity.this);
                HashMap<String,String> usuario = preferencias.getDadosUsuario();

                String token = usuario.get("token");
                String tokendigitado = codigoValidacao.getText().toString();

                if (tokendigitado.equals(token)){
                    Toast.makeText(ValidadorActivity.this,"Token validado", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(ValidadorActivity.this,"Token nao validado", Toast.LENGTH_LONG);
                }

            }
        });
        */

    }
}
