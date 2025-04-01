package com.example.prueba1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;

public class rutaTransporte extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ruta_transporte);

        //REFERENCIA A LOS TEXTVIEWS
        TextView txtUsuario = findViewById(R.id.nameView);
        // RECUPERAR EL NOMBRE DEL USUARIO DESDE SHAREDPREFERENCES
        String nombreUsuario = getSharedPreferences("usuerData", MODE_PRIVATE).getString("nombreUsuario", "");
        String apellidoUsuario = getSharedPreferences("usuerData", MODE_PRIVATE).getString("apellidoUsuario", "");
        txtUsuario.setText(nombreUsuario + " " + apellidoUsuario);


        // ENLACE PARA VOLVER A INCIO DE SESION
        ImageButton btnMenuPrincipal = findViewById(R.id.btnMenuPrincipal);

       btnMenuPrincipal.setOnClickListener(v ->{
        Intent intent = new Intent(rutaTransporte.this, MenuPrincipal.class);
        startActivity(intent);
       });


    }


    private GoogleMap mMap;






}
