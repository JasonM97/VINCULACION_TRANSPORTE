package com.example.prueba1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class infoApp extends AppCompatActivity

{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.info_app); // Establecer el diseño de SecondActivity


        // -----------      ENLACE PARA IR AL MENU PRINCIPAL DE LAS ACTIVIDADES

        ImageButton btnMenuPrincipal = findViewById(R.id.btnMenuPrincipal);

        btnMenuPrincipal.setOnClickListener(v -> {
            Intent intent = new Intent(infoApp.this, MenuPrincipal.class);
            startActivity(intent);
        });



    }
}
