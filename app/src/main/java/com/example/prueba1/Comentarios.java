package com.example.prueba1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Comentarios extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.enviar_comentarios);

        // -----------      ENLACE PARA IR AL MENU PRINCIPAL DE LAS ACTIVIDADES

        ImageButton btnMenuPrincipal = findViewById(R.id.btnMenuPrincipal);

        btnMenuPrincipal.setOnClickListener(v -> {
            Intent intent = new Intent(Comentarios.this, MenuPrincipal.class);
            startActivity(intent);
        });



    }

}
