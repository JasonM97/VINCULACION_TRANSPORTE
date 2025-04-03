package com.example.prueba1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Comentarios extends AppCompatActivity
{

    //DECLARACION VARIABLES DE LOS DATOS DE LOS COMENTARIOS

    private EditText txtNombre, txtApellido, txtCelular, txtCorreo, txtDescripcion;




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
        if (generoUsuario != null)
        {
            if (generoUsuario.equalsIgnoreCase("Mujer"))
            {
                imgAvatar.setImageResource(R.drawable.icono_mujer);
            } else {
                imgAvatar.setImageResource(R.drawable.icono_hombre);
            }
        }

        //REFERENCIA A LOS TEXTVIEWS
        TextView txtUsuario = findViewById(R.id.nameView);
        // RECUPERAR EL NOMBRE DEL USUARIO DESDE SHAREDPREFERENCES
        String nombreUsuario = getSharedPreferences("userData", MODE_PRIVATE).getString("nombreUsuario", "");
        String apellidoUsuario = getSharedPreferences("userData", MODE_PRIVATE).getString("apellidoUsuario", "");
        txtUsuario.setText(nombreUsuario + " " + apellidoUsuario);


        // PARA ENVIAR LOS COMENTARIOS O SUGERENCIAS

        // Vincular vistas
        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtCelular = findViewById(R.id.txtCelular);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtDescripcion = findViewById(R.id.txtDescripcion);

        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(v -> enviarCorreo()); //Envia el correo tomando en cuenta la clase



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


    } //Final Oncreate

    private void enviarCorreo() {
        // Obtener datos del formulario
        String nombre = txtNombre.getText().toString();
        String apellido = txtApellido.getText().toString();
        String celular = txtCelular.getText().toString();
        String correoUsuario = txtCorreo.getText().toString();
        String descripcion = txtDescripcion.getText().toString();

        // Validar campos vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || celular.isEmpty() || correoUsuario.isEmpty()|| descripcion.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el cuerpo del correo
        String asunto = "Nuevo formulario de: " + nombre + " " + apellido;
        String cuerpo = "Nombre: " + nombre + "\n"
                + "Apellido: " + apellido + "\n"
                + "Celular: " + celular + "\n"
                + "Correo: " + correoUsuario + "\n"
                + "Descripción: " + descripcion;

        // Intent para enviar correo
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // Tipo MIME para correo
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jason97gts@gmail.com"}); // Correo destino
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, cuerpo);

        // Verificar si hay apps de correo instaladas
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Enviar correo con:"));
        } else {
            Toast.makeText(this, "No hay aplicaciones de correo instaladas", Toast.LENGTH_SHORT).show();
        }
    }






} //Final Clasee
