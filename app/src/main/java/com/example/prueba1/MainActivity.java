package com.example.prueba1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.Loader;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //DECLARACION DE LAS VARAIBLES PARA CONEXXION BASE DE DATOS (FIREBASE)
    //FirebaseDatabase firebaseDatabase;
    //DatabaseReference databaseReference;

    private FirebaseAuth mAuth;


    //DECLARACION DE LAS VARAIBLES EDITEX
    private EditText usuario;
    private EditText contraseña;


    //@SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        //ENLAZAMIENTO CON LA INTERFAZ ACTIVITI MAIN - EDITEX
        usuario = findViewById(R.id.EtxtUsuario);
        contraseña = findViewById(R.id.EtxtContraseña);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        //DIRECCIONAR A LA PAGINA DE MENU PRINCIPAL : Validacion de datos login
        Button btnIniciar = findViewById(R.id.btnIniciar);
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

            }
        });

           /*btnIniciar.setOnClickListener(v -> {
              Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
              startActivity(intent);
            });*/


        // ENLACE PARA DIRIGIRSE AL FORMULARIO DE REGISTRO
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtRegistrar = findViewById(R.id.txtRegistrar);
        txtRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroFormulario.class);
            startActivity(intent);
        });


    }

  /*  private void inicializarFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference();

    }*/

    /*
    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si el usuario ya ha iniciado sesión
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Si ya está autenticado, redirigir al menú principal
            startActivity(new Intent(MainActivity.this, MenuPrincipal.class));
            finish();
        }
    }*/

    private void login(){

        String emailLogin = usuario.getText().toString();
        String passLogin = contraseña.getText().toString();

        // Validar campos vacíos
        if(emailLogin.isEmpty() || passLogin.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailLogin,passLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {

                  Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                  startActivity(intent);
                  Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                  finish(); // Evitar volver atrás a la pantalla de login
              } else{

                  // Si falla el inicio de sesión
                  Toast.makeText(MainActivity.this, "Error-- "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();

              }

            }

        });


    }


}