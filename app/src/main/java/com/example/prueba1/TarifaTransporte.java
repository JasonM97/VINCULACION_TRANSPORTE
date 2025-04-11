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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class TarifaTransporte extends AppCompatActivity {
    private View currentExpandedView = null;
    private List<List<BusStop>> savedRoutes = new ArrayList<>(); // Lista de rutas cargadas

    // Clase interna para representar una parada (copiada de rutaTransporte)
    private static class BusStop {
        LatLng location;
        String name;

        BusStop(LatLng location, String name) {
            this.location = location;
            this.name = name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tarifa_transporte);

        // REFERENCIA DEL IMAGEN
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView imgAvatar = findViewById(R.id.avatar);
        String generoUsuario = getSharedPreferences("userData", MODE_PRIVATE).getString("generoUsuario", "");
        if (generoUsuario != null) {
            if (generoUsuario.equalsIgnoreCase("Mujer")) {
                imgAvatar.setImageResource(R.drawable.icono_mujer);
            } else {
                imgAvatar.setImageResource(R.drawable.icono_hombre);
            }
        }

        // REFERENCIA A LOS TEXTVIEWS
        TextView txtUsuario = findViewById(R.id.nameView);
        String nombreUsuario = getSharedPreferences("userData", MODE_PRIVATE).getString("nombreUsuario", "");
        String apellidoUsuario = getSharedPreferences("userData", MODE_PRIVATE).getString("apellidoUsuario", "");
        txtUsuario.setText(nombreUsuario + " " + apellidoUsuario);

        // ENLACE PARA IR AL MENU PRINCIPAL DE LAS ACTIVIDADES
        ImageButton btnMenuPrincipal = findViewById(R.id.btnMenuPrincipal);
        btnMenuPrincipal.setOnClickListener(v -> {
            Intent intent = new Intent(TarifaTransporte.this, MenuPrincipal.class);
            startActivity(intent);
        });

        // Cargar las rutas guardadas desde routes.json
        loadRoutesFromFile();

        // DECLARACION DE LOS BOTONES PRINCIPALES
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);

        // Configurar clics para expandir/colapsar los contenidos
        btn1.setOnClickListener(v -> toggleContent(v, R.id.content1));
        btn2.setOnClickListener(v -> toggleContent(v, R.id.content2));
        btn3.setOnClickListener(v -> toggleContent(v, R.id.content3));
        btn4.setOnClickListener(v -> toggleContent(v, R.id.content4));
        btn5.setOnClickListener(v -> toggleContent(v, R.id.content5));

        // ENLACES PARA IR A LAS RUTAS
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnRuta = findViewById(R.id.btnIr);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnRuta2 = findViewById(R.id.btnIr2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnRuta3 = findViewById(R.id.btnIr3);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnRuta4 = findViewById(R.id.btnIr4);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnRuta5 = findViewById(R.id.btnIr5);

        // Configurar clics para abrir rutaTransporte con el índice correspondiente
        btnRuta.setOnClickListener(v -> openRoute(0));  // Ruta 1
        btnRuta2.setOnClickListener(v -> openRoute(1)); // Ruta 2
        btnRuta3.setOnClickListener(v -> openRoute(2)); // Ruta 3
        btnRuta4.setOnClickListener(v -> openRoute(3)); // Ruta 4
        btnRuta5.setOnClickListener(v -> openRoute(4)); // Ruta 5
    }

    // Método para abrir rutaTransporte con una ruta específica
    private void openRoute(int routeIndex) {
        if (routeIndex >= 0 && routeIndex < savedRoutes.size()) {
            Intent intent = new Intent(TarifaTransporte.this, rutaTransporte.class);
            intent.putExtra("routeIndex", routeIndex);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Ruta " + (routeIndex + 1) + " no está disponible. Crea la ruta primero.", Toast.LENGTH_SHORT).show();
        }
    }

    // Carga las rutas guardadas desde el archivo JSON
    private void loadRoutesFromFile() {
        try {
            File file = new File(getFilesDir(), "routes.json");
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();

                String jsonString = new String(data);
                JSONArray jsonRoutes = new JSONArray(jsonString);
                savedRoutes.clear();

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    JSONArray jsonRoute = jsonRoutes.getJSONArray(i);
                    List<BusStop> route = new ArrayList<>();
                    for (int j = 0; j < jsonRoute.length(); j++) {
                        JSONObject jsonPoint = jsonRoute.getJSONObject(j);
                        double lat = jsonPoint.getDouble("latitude");
                        double lng = jsonPoint.getDouble("longitude");
                        String name = jsonPoint.optString("name", "Parada " + (j + 1));
                        route.add(new BusStop(new LatLng(lat, lng), name));
                    }
                    savedRoutes.add(route);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar rutas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Método para alternar la visibilidad del contenido con animación
    private void toggleContent(View button, int contentId) {
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
            contentView.post(() -> scrollView.smoothScrollTo(0, contentView.getTop()));
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
