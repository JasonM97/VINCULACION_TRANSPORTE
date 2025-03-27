package com.example.prueba1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class RegistroFormulario extends AppCompatActivity
{
    //DECLARACION DE LAS VARAIBLES PARA CONEXXION BASE DE DATOS (FIREBASE)
    // Variables para Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    //DECLARACION DE LAS VARAIBLES EDITEX
    private EditText nombre;
    private EditText apellido;
    private EditText celular;
    private EditText fecha;
    private EditText correo;
    private EditText contraseña;
    private EditText direccion;

    private ImageButton btnSubirImagen;
    private ImageView imagePreview;
    private Uri imageUri;


    private static final int PICK_IMAGE_REQUEST = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // ENLACE PARA VOLVER A INCIO DE SESION
        TextView txtVolver = findViewById(R.id.txtVolver);

        txtVolver.setOnClickListener(v ->{
            Intent intent = new Intent(RegistroFormulario.this, MainActivity.class);
            startActivity(intent);
        });



        // -----------------------DIRECCIONAR A LA PAGINA DE MENU PRINCIPAL

        //ENLAZAMIENTO CON LA INTERFAZ ACTIVITI MAIN - EDITEX
        nombre = findViewById(R.id.txtNombre);
        apellido = findViewById(R.id.txtApellido);
        celular = findViewById(R.id.txtCelular);
        fecha = findViewById(R.id.txtFechaNacimiento);
        correo = findViewById(R.id.txtCorreo);
        contraseña = findViewById(R.id.txtContraseña);
        direccion = findViewById(R.id.txtDireccion);
        btnSubirImagen = findViewById(R.id.btnSubirImagen);
        imagePreview = findViewById(R.id.imagePreview);

        // Configurar botón para subir imagen
        btnSubirImagen.setOnClickListener(v -> openFileChooser());




        // Boton para enviar el registro a la base de datos Firebase : uso metodo registar()
        Button btnRegistro = findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();

            }
        });

    }


//  METODO PARA ABRIR LA CARPETA
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            imagePreview.setImageURI(imageUri);
        }
    }



    // --------- METODO PARA REGISTARSE CON EL FORMULARIO
    private void registrar()
    {

                String nombreUsuario = nombre.getText().toString();
                String apellidoUsuario = apellido.getText().toString();
                String celularUsuario = celular.getText().toString();
                String fechaUsuario = fecha.getText().toString();
                String emailLogin = correo.getText().toString();
                String passLogin = contraseña.getText().toString();
                String direccionUsuario = direccion.getText().toString();


                // Validar campos vacíos
                if(  nombreUsuario.isEmpty() || apellidoUsuario.isEmpty() || celularUsuario.isEmpty() || fechaUsuario.isEmpty() || emailLogin.isEmpty()  || passLogin.isEmpty() || direccionUsuario.isEmpty() || imageUri == null) {
                    Toast.makeText(this, "Por favor complete todos los campos y seleccione una imagen ", Toast.LENGTH_SHORT).show();
                    return;
                }

            // Subir imagen a Firebase Storage
               // String filename = UUID.randomUUID().toString() + ".jpg";
               // StorageReference fileReference = storageRef.child("usuarios/" + filename);
               //StorageReference fileReference = storageRef.child("usuarios/" + System.currentTimeMillis() + ".jpg");
             StorageReference fileReference = FirebaseStorage.getInstance().getReference("usuarios/" + mAuth.getCurrentUser().getUid() + ".jpg");
                fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

                                // Obtener URL de la imagen subida
                                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    crearUsuarioEnFirebase(emailLogin, passLogin, nombreUsuario, apellidoUsuario, celularUsuario, fechaUsuario, direccionUsuario, imageUrl);
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                    }




    private void crearUsuarioEnFirebase(String email, String password, String nombre, String apellido, String celular, String fecha, String direccion, String imageUrl)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Map<String, Object> usuario = new HashMap<>();
                            usuario.put("nombre", nombre);
                            usuario.put("apellido", apellido);
                            usuario.put("celular", celular);
                            usuario.put("fechaNacimiento", fecha);
                            usuario.put("direccion", direccion);
                            usuario.put("fotoUrl", imageUrl);
                            usuario.put("email", email);

                            db.collection("usuarios").document(user.getUid())
                                    .set(usuario)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegistroFormulario.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegistroFormulario.this, MenuPrincipal.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegistroFormulario.this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(RegistroFormulario.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}



            /*mAuth.createUserWithEmailAndPassword(emailLogin,passLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(RegistroFormulario.this, MenuPrincipal.class);
                                startActivity(intent);
                                Toast.makeText(RegistroFormulario.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                finish(); // Evitar volver atrás a la pantalla de login
                            } else{

                                // Si falla el inicio de sesión
                                Toast.makeText(RegistroFormulario.this, "Error-- "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                            }

                        }

                    });*/