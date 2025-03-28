package com.example.prueba1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;


public class RegistroFormulario extends AppCompatActivity {
    //DECLARACION DE LAS VARAIBLES PARA CONEXXION BASE DE DATOS (FIREBASE)
    // Variables para Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //DECLARACION DE LAS VARAIBLES EDITEX
    private EditText nombre, apellido, celular, fecha, correo, contraseña, direccion;

    // Declaracion de la variable para el spinner
    private Spinner spinnerGenero;
    private ImageView imagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        // CONFIGURACION DEL SPINNER
        spinnerGenero = findViewById(R.id.spinnerGenero);
        imagePreview = findViewById(R.id.imagePreview);

        // CONFIGURACION DEL SPINNER PARA SELECCIONAR EL GÉNERO
        String[] generos = {"Hombre", "Mujer", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapter);

        // MOSTRAR LA IMAGEN PREDETERMINADA SEGUN EL GENERO SELECCIONADO
        spinnerGenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String generoSeleccionado = parent.getItemAtPosition(position).toString();
                switch (generoSeleccionado) {
                    case "Hombre":
                        imagePreview.setImageResource(R.drawable.icono_hombre);
                        break;
                    case "Mujer":
                        imagePreview.setImageResource(R.drawable.icono_mujer);
                        break;
                    case "Otro":
                        imagePreview.setImageResource(R.drawable.icono_otro);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // Boton para enviar el registro a la base de datos Firebase : uso metodo registar()
        Button btnRegistro = findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
            }
        });
    }
    // --------- METODO PARA REGISTARSE CON EL FORMULARIO
    private void registrar() {
        String nombreUsuario = nombre.getText().toString();
        String apellidoUsuario = apellido.getText().toString();
        String celularUsuario = celular.getText().toString();
        String fechaUsuario = fecha.getText().toString();
        String emailLogin = correo.getText().toString();
        String passLogin = contraseña.getText().toString();
        String direccionUsuario = direccion.getText().toString();
        String genero = spinnerGenero.getSelectedItem().toString();

        // Validar campos vacíos
        if(  nombreUsuario.isEmpty() || apellidoUsuario.isEmpty() || celularUsuario.isEmpty() || fechaUsuario.isEmpty() || emailLogin.isEmpty()  || passLogin.isEmpty() || direccionUsuario.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // CREAR USUARIO EN FIREBASE
        mAuth.createUserWithEmailAndPassword(emailLogin,passLogin).addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("nombre", nombreUsuario);
                    usuario.put("apellido", apellidoUsuario);
                    usuario.put("celular", celularUsuario);
                    usuario.put("fechaNacimiento", fechaUsuario);
                    usuario.put("direccion", direccionUsuario);
                    usuario.put("email", emailLogin);
                    usuario.put("genero", genero);

                    // GUARDAR DATOS EN FIRESTORE
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
    };
}