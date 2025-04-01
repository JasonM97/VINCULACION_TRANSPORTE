package com.example.prueba1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class TarifaTransporte extends AppCompatActivity
{
    private View currentExpandedView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tarifa_transporte);

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
            Intent intent = new Intent(TarifaTransporte.this, MenuPrincipal.class);
            startActivity(intent);
        });



        // --------------- FUNCIONALIDAD DE LOS BOTONES DE TARIFAS


        // DECLARACION DE LOS BOTONES
        Button btn1 = findViewById(R.id.btn1);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btn2 = findViewById(R.id.btn2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btn3 = findViewById(R.id.btn3);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btn4 = findViewById(R.id.btn4);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContent(v, R.id.content1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContent(v, R.id.content2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContent(v, R.id.content3);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContent(v, R.id.content4);
            }
        });


    }
/*
    private void toggleContent(View button, int contentId) {
        View contentView = findViewById(contentId);

        if (contentView.getVisibility() == View.VISIBLE) {
            contentView.setVisibility(View.GONE);
        } else {
            // Oculta todos primero
            findViewById(R.id.content1).setVisibility(View.GONE);
            findViewById(R.id.content2).setVisibility(View.GONE);


            // Muestra el seleccionado
            contentView.setVisibility(View.VISIBLE);
        }
    }
*/




    private void toggleContent(View button, int contentId)
    {
        final View contentView = findViewById(contentId);

        if (contentView == currentExpandedView) {
            // Si ya está expandido, contraerlo
            collapseContent(contentView);
            currentExpandedView = null;
        } else {
            // Contraer el contenido actual (si hay uno expandido)
            if (currentExpandedView != null) {
                collapseContent(currentExpandedView);
            }

            // Expandir el nuevo contenido
            expandContent(contentView);
            currentExpandedView = contentView;

            // Desplazar el ScrollView para mostrar el contenido
            final ScrollView scrollView = findViewById(R.id.scrollView);
            contentView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, contentView.getTop());
                }
            });
        }
    }

    private void expandContent(final View view) {
        view.setVisibility(View.VISIBLE);
        Animation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(300);
        view.startAnimation(anim);
    }

    private void collapseContent(final View view) {
        Animation anim = new AlphaAnimation(1f, 0f);
        anim.setDuration(300);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(anim);
    }



}
