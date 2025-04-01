package com.example.prueba1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class informacionBus extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.informacion_bus);


        //REFERENCIA DEL IMAGEN
        ImageView imgAvatar = findViewById(R.id.avatar);
        String generoUsuario = getSharedPreferences("userData", MODE_PRIVATE).getString("generoUsuario", ""); // "masculino" o "femenino"
        // Establecer la imagen según el género
        if (generoUsuario != null)
        {
            if (generoUsuario.equalsIgnoreCase("Mujer"))
            {
                imgAvatar.setImageResource(R.drawable.icono_mujer);
            } else {
                imgAvatar.setImageResource(R.drawable.icono_hombre);
            }
        }



        //REFERENCIA A LOS TEXTVIEWS
        TextView txtUsuario = findViewById(R.id.nameView);
        // RECUPERAR EL NOMBRE DEL USUARIO DESDE SHAREDPREFERENCES
        String nombreUsuario = getSharedPreferences("usuerData", MODE_PRIVATE).getString("nombreUsuario", "");
        String apellidoUsuario = getSharedPreferences("usuerData", MODE_PRIVATE).getString("apellidoUsuario", "");
        txtUsuario.setText(nombreUsuario + " " + apellidoUsuario);



        // -----------      ENLACE PARA IR AL MENU PRINCIPAL DE LAS ACTIVIDADES

        ImageButton btnMenuPrincipal = findViewById(R.id.btnMenuPrincipal);

        btnMenuPrincipal.setOnClickListener(v -> {
            Intent intent = new Intent(informacionBus.this, MenuPrincipal.class);
            startActivity(intent);
        });





    }
}
