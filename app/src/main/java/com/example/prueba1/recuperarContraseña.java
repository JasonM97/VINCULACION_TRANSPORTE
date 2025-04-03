package com.example.prueba1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class recuperarContraseña extends AppCompatActivity
{


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //DECLARACION DE LAS VARAIBLES EDITEX
    private EditText usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.recupera_password);

        //ENLAZAMIENTO CON LA INTERFAZ ACTIVITI MAIN - EDITEX
        usuario = findViewById(R.id.txtCorreo);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //BOTON RECUPERAR CONTRASEÑA
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnRecuperar = findViewById(R.id.btnRecuperar);
        btnRecuperar.setOnClickListener(v -> recuperarPassword());


        // ENLACE PARA DIRIGIRSE AL FORMULARIO DE REGISTRO
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtRegistrar = findViewById(R.id.txtRegistrar);
        txtRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(recuperarContraseña.this, RegistroFormulario.class);
            startActivity(intent);
        });

        // ENLACE PARA DIRIGIRSE AL FORMULARIO DE REGISTRO
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtVolver = findViewById(R.id.txtVolver);
        txtVolver.setOnClickListener(v -> {
            Intent intent = new Intent(recuperarContraseña.this, MainActivity.class);
            startActivity(intent);
        });


    }


    private void recuperarPassword()
    {
        String email = usuario.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(recuperarContraseña.this,
                                "Se ha enviado un enlace a tu correo para restablecer la contraseña",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(recuperarContraseña.this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}




