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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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

        // ENLACE PARA DIRIGIRSE AL FORMULARIO DE REGISTRO
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtRegistrar = findViewById(R.id.txtRegistrar);
        txtRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroFormulario.class);
            startActivity(intent);
        });


    }

    private void login(){

        String emailLogin = usuario.getText().toString();
        String passLogin = contraseña.getText().toString();
        db = FirebaseFirestore.getInstance();

        // Validar campos vacíos
        if(emailLogin.isEmpty() || passLogin.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailLogin,passLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                  FirebaseUser user = mAuth.getCurrentUser(); //Obtener usuario actual iciio sesion
                  if (user != null) {
                      String userId = user.getUid(); // Obtener ID del usuario de firebase
                      // OBTENER EL NOMBRE y apellido DEL USUARIO DESDE FIRESTORE
                      db.collection("usuarios").document(userId)
                              .get()
                              .addOnCompleteListener(task2 -> {
                                  if (task2.isSuccessful())
                                  {
                                      DocumentSnapshot document = task2.getResult(); //Guarda la informacion obtenida en una variable doucment
                                          if (document.exists()) {
                                              String nombreUsuario = document.getString("nombre"); //Obtenemos el nombre usuario
                                              String apellidoUsuario = document.getString("apellido"); //Obtenemos el apellido usuario
                                              //GUARDAR EL NOMBRE EN SHAREDPREFERENCES
                                              getSharedPreferences("usuerData", MODE_PRIVATE)
                                                      .edit()
                                                      .putString("nombreUsuario", nombreUsuario)
                                                      .apply();

                                              getSharedPreferences("usuerData", MODE_PRIVATE)
                                                      .edit()
                                                      .putString("apellidoUsuario", apellidoUsuario)
                                                      .apply();

                                              Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                                              startActivity(intent);
                                              Toast.makeText(MainActivity.this, "INICIO EXISTOSO", Toast.LENGTH_SHORT).show();
                                              finish(); // Evitar volver atrás a la pantalla de login
                                          }
                                  } else
                                  {
                                      Toast.makeText(MainActivity.this, "DATOS NO ENCONTRADOS REGISTRESE " + task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                  }
                              });
                  } else {
                      // Si falla el inicio de sesión
                      Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();

                  }
              }
            }
        });
    }
}