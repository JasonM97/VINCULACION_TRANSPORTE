package com.example.prueba1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        //REFERENCIA DEL IMAGEN
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView imgAvatar = findViewById(R.id.avatar);
        String generoUsuario = getSharedPreferences("userData", MODE_PRIVATE).getString("generoUsuario", ""); // "masculino" o "femenino"
        // Establecer la imagen según el género
        if (generoUsuario != null && generoUsuario.equalsIgnoreCase("Mujer")) {
            imgAvatar.setImageResource(R.drawable.icono_mujer); // Asegúrate de tener este drawable
        } else {
            imgAvatar.setImageResource(R.drawable.icono_hombre); // Asegúrate de tener este drawable
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
            Intent intent = new Intent(Comentarios.this, MenuPrincipal.class);
            startActivity(intent);
        });


        // -----------      ENLACE PARA IR AL WAHASP DEL CONTACTO

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton btnWashapt = findViewById(R.id.btnWashapt);

        btnWashapt.setOnClickListener(v -> {
            String phoneNumber = "593987724651"; // Reemplaza con el número de teléfono (incluye código de país)
            String url = "https://wa.me/" + phoneNumber;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });


        /*
        btnWashapt.setOnClickListener(v -> {
            WebView webView = new WebView(this);
            setContentView(webView);
            webView.loadUrl("https://tecnoecuatoriano.edu.ec/");
        });

         */

       // -----------      ENLACE PARA IR LLAMAR A UN NUMERO
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton btnLlamar = findViewById(R.id.btnLlamar);
        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "0987724651";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });





    }
}
