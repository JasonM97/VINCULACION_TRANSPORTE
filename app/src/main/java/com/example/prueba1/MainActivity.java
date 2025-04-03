package com.example.prueba1;

import static android.provider.Settings.System.getString;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.Loader;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity
{
    //DECLARACION PARA FIREBASE
        private FirebaseAuth mAuth; //Autenticacion automatica
        private FirebaseFirestore db;
        private GoogleSignInClient mGoogleSignInClient;
        private static final int RC_SIGN_IN = 9001;


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

            // ENLACE PARA DIRIGIRSE AL FORMULARIO DE REGISTRO
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtRecupera = findViewById(R.id.txtRecupera);
            txtRecupera.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, recuperarContraseña.class);
                startActivity(intent);
            });


            //PARA LOGEARSE CON UNA CUENTA DE GMAIL
           /*
                // Configurar Google Sign-In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id)) // Obtén este ID desde google-services.json
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                mAuth = FirebaseAuth.getInstance();

                ImageButton btnGoogle = findViewById(R.id.btnGoogle);
                btnGoogle.setOnClickListener(v -> signInWithGoogle());*/




        } //Final Oncreate




        private void login()
        {

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
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                      if (task.isSuccessful())
                      {
                          //Obtener usuario actual iciio sesion
                          FirebaseUser user = mAuth.getCurrentUser();
                          if (user != null) {
                              // Obtener ID del usuario de firebase
                              String userId = user.getUid();

                              // OBTENER EL NOMBRE y apellido DEL USUARIO DESDE FIRESTORE
                              db.collection("usuarios").document(userId)
                                      .get()
                                      .addOnCompleteListener(task2 -> {
                                          if (task2.isSuccessful())
                                          {
                                              //Guarda la informacion obtenida en una variable doucment
                                              DocumentSnapshot document = task2.getResult();
                                                  if (document.exists()) {
                                                      String nombreUsuario = document.getString("nombre"); //Obtenemos el nombre usuario
                                                      String apellidoUsuario = document.getString("apellido"); //Obtenemos el apellido usuario
                                                      String generoUsuario = document.getString("genero");
                                                      // System.out.println("Genero del usuario: ---------------------- " + generoUsuario); //verifica que esta trayendo

                                                      //GUARDAR EL NOMBRE EN SHAREDPREFERENCES
                                                      getSharedPreferences("userData", MODE_PRIVATE)
                                                              .edit()
                                                              .putString("nombreUsuario", nombreUsuario)
                                                              .apply();

                                                      getSharedPreferences("userData", MODE_PRIVATE)
                                                              .edit()
                                                              .putString("apellidoUsuario", apellidoUsuario)
                                                              .apply();

                                                      getSharedPreferences("userData", MODE_PRIVATE)
                                                              .edit()
                                                              .putString("generoUsuario", generoUsuario)
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

                          }

                      }else {
                          // Si falla el inicio de sesión
                          Toast.makeText(MainActivity.this, "USUARIO O CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();

                      }

                }

            }); //Final singIntView

        } //Final Clase Login


    private void signInWithGoogle()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Error en Google Sign-In: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Bienvenido: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                    }
                });
    }

















} //FINAL CLASE PRINCIPAL