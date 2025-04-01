package com.example.prueba1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class rutaTransporte extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener
{

   //DECLARACION VARAIBLES
    EditText txtLatitud, txtLongitud;
    GoogleMap mMap;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.ruta_transporte);


        //REFERENCIA DEL IMAGEN
        ImageView imgAvatar = findViewById(R.id.avatar);
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


            // ENLACE PARA VOLVER A INCIO DE SESION
            ImageButton btnMenuPrincipal = findViewById(R.id.btnMenuPrincipal);

           btnMenuPrincipal.setOnClickListener(v ->{
            Intent intent = new Intent(rutaTransporte.this, MenuPrincipal.class);
            startActivity(intent);
           });



           // LALITUTD Y LONGITUD MAPA

        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap =  googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        LatLng ecuador = new LatLng(-0.1862502,-78.595352);
        mMap.addMarker(new MarkerOptions().position(ecuador).title("Ecuador"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ecuador));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText("" + latLng.latitude);
        txtLongitud.setText("" + latLng.longitude);
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText("" + latLng.latitude);
        txtLongitud.setText("" + latLng.longitude);

    }



}
