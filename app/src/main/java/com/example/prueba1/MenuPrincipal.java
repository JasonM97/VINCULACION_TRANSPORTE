package com.example.prueba1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MenuPrincipal extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu_principal); // Establecer el diseño de SecondActivity

        //REFERENCIA A LOS TEXTVIEWS
        TextView txtUsuario = findViewById(R.id.editText);
        // RECUPERAR EL NOMBRE DEL USUARIO DESDE SHAREDPREFERENCES
        String nombreUsuario = getSharedPreferences("usuerData", MODE_PRIVATE).getString("nombreUsuario", "");
        txtUsuario.setText(nombreUsuario);

        // -PARA FINALIZAR LA SESION DEL MENU PRINCIPAL

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton btnSalir = findViewById(R.id.btnSalir);

        btnSalir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Opción 1: Volver a MainActivity y limpiar el stack
                Intent intent = new Intent(MenuPrincipal.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        });


      // ACTIVIDADES DE INGRESARA LAS CONSULTAS DE TRASNPORTE : Ruta , tarifas, comentarios, informacion trasnporte

        //DECLARACION DE LAS VARIABLES TIPO BOTONES
       @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button  btnRuta = findViewById(R.id.btnRuta);
       @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnTarifaTransporte = findViewById(R.id.btnTarifaTransporte);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnComentarios = findViewById(R.id.btnComentarios);

        //DIRECCIONAR A LA PAGINA DE MENU PRINCIPAL
        btnRuta.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, rutaTransporte.class);
            startActivity(intent);
        });

        //DIRECCIONAR A LA PAGINA TARIFA TRASPORTE
        btnTarifaTransporte.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, TarifaTransporte.class);
            startActivity(intent);
        });

        //DIRECCIONAR A LA PAGINA TARIFA TRASPORTE
        btnComentarios.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, Comentarios.class);
            startActivity(intent);
        });
    }
}
