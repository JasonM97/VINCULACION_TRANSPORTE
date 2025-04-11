package com.example.prueba1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class rutaTransporte extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    // Declaración de variables de la interfaz y lógica
    EditText txtLatitud, txtLongitud; // Campos para mostrar las coordenadas actuales
    GoogleMap mMap; // Objeto del mapa de Google
    List<BusStop> busStops = new ArrayList<>(); // Lista de paradas marcadas en el mapa (ahora con nombre)
    Polyline currentRoute; // Polilínea que representa la ruta actual
    FusedLocationProviderClient fusedLocationClient; // Cliente para obtener la ubicación del dispositivo
    List<List<BusStop>> savedRoutes = new ArrayList<>(); // Lista de rutas guardadas
    Marker currentLocationMarker; // Marcador de la ubicación actual del usuario
    private List<Marker> stopMarkers = new ArrayList<>(); // Lista de marcadores de paradas
    ListView listSavedRoutes; // Lista para mostrar rutas guardadas
    ArrayAdapter<String> routesAdapter; // Adaptador para la lista de rutas
    private int lastNotifiedStopIndex = -1; // Índice de la última parada notificada para evitar repeticiones
    private static final String CHANNEL_ID = "BusStopsChannel"; // ID del canal de notificaciones
    private static final int NOTIFICATION_ID_BASE = 100; // Base para IDs de notificaciones únicas
    private int selectedRouteIndex = -1; // Índice de la ruta seleccionada en la lista

    // Clase interna para representar una parada con coordenadas y nombre
    private static class BusStop {
        LatLng location;
        String name;

        BusStop(LatLng location, String name) {
            this.location = location;
            this.name = name;
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ruta_transporte);

        //REFERENCIA DEL IMAGEN
        ImageView imgAvatar = findViewById(R.id.avatar);
        String generoUsuario = getSharedPreferences("userData", MODE_PRIVATE).getString("generoUsuario", ""); // "masculino" o "femenino"
        // Establecer la imagen según el género
        if (generoUsuario != null) {
            if (generoUsuario.equalsIgnoreCase("Mujer")) {
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

        // ENLACE PARA VOLVER A INICIO DE SESION
        ImageButton btnMenuPrincipal = findViewById(R.id.btnMenuPrincipal);

        btnMenuPrincipal.setOnClickListener(v -> {
            Intent intent = new Intent(rutaTransporte.this, MenuPrincipal.class);
            startActivity(intent);
        });

        // LATITUD Y LONGITUD MAPA

        // Vinculación de elementos de la interfaz de usuario
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        listSavedRoutes = findViewById(R.id.listSavedRoutes);

        // Inicialización del cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createNotificationChannel(); // Configura el canal de notificaciones
        loadRoutesFromFile(); // Carga rutas guardadas desde el archivo

        // * MODIFICACION PARA EL ÍNDICE *
        // Verificar si se pasó un índice de ruta desde TarifaTransporte
        Intent intent = getIntent();
        if (intent.hasExtra("routeIndex")) {
            int routeIndex = intent.getIntExtra("routeIndex", -1);
            if (routeIndex >= 0 && routeIndex < savedRoutes.size()) {
                selectedRouteIndex = routeIndex;
                // Mostrar la ruta automáticamente cuando el mapa esté listo
                if (mMap != null) {
                    showSavedRoute(routeIndex);
                } else {
                    // Si el mapa aún no está listo, esperar a que lo esté
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
                    mapFragment.getMapAsync(googleMap -> {
                        mMap = googleMap;
                        onMapReady(googleMap); // Llamar al método original
                        showSavedRoute(routeIndex); // Mostrar la ruta seleccionada
                    });
                }
            } else {
                Toast.makeText(this, "Ruta no encontrada", Toast.LENGTH_SHORT).show();
            }
        }
        // * FIN DEL CAMBIO *

        // Configuración del adaptador para la lista de rutas guardadas
        routesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getRouteNames());
        listSavedRoutes.setAdapter(routesAdapter);
        listSavedRoutes.setOnItemClickListener((parent, view, position, id) -> {
            selectedRouteIndex = position;
            showSavedRoute(position); // Muestra la ruta seleccionada en el mapa
            Toast.makeText(this, "Ruta " + (position + 1) + " seleccionada", Toast.LENGTH_SHORT).show();
        });

        // Inicialización del fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        // Solicitud de permisos de ubicación y notificaciones
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2);
            }
        }

        // Configuración del botón para guardar rutas
        Button btnSaveRoute = findViewById(R.id.btnSaveRoute);
        btnSaveRoute.setOnClickListener(v -> saveCurrentRoute());

        // Configuración del botón para eliminar rutas
        Button btnDeleteRoute = findViewById(R.id.btnDeleteRoute);
        btnDeleteRoute.setEnabled(false); //desahibilitar el boton para que no sea clikeable
        btnDeleteRoute.setOnClickListener(v -> {
            if (selectedRouteIndex != -1) {
                deleteSavedRoute(selectedRouteIndex);
            } else {
                Toast.makeText(this, "Selecciona una ruta para eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Crea el canal de notificaciones para Android 8.0 y superior
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Bus Stops Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notificaciones para paradas de autobús");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Envía una notificación push con un título, mensaje e ID único
    private void sendNotification(String title, String message, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono de la notificación
                .setContentTitle(title) // Título de la notificación
                .setContentText(message) // Mensaje de la notificación
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridad por defecto
                .setAutoCancel(true); // Se cierra al tocarla
        notificationManager.notify(notificationId, builder.build());
    }

    // Genera una lista de nombres de rutas para mostrar en el ListView
    private List<String> getRouteNames() {
        List<String> routeNames = new ArrayList<>();
        for (int i = 0; i < savedRoutes.size(); i++) {
            routeNames.add("Ruta " + (i + 1) + " (" + savedRoutes.get(i).size() + " paradas)");
        }
        return routeNames;
    }

    // Actualiza la lista de rutas en la interfaz de usuario
    private void updateRouteList() {
        routesAdapter.clear();
        routesAdapter.addAll(getRouteNames());
        routesAdapter.notifyDataSetChanged();
    }

    // Configura el mapa cuando está listo
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapLongClickListener(this); // Habilita clics largos para agregar paradas

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // Activa el botón "Mi Ubicación" en el mapa
        }

        LatLng ecuador = new LatLng(-0.0905468, -78.417745); // Punto inicial centrado en Ecuador
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ecuador, 10)); // Mueve la cámara al inicio
        checkLocationPeriodically(); // Inicia la verificación periódica de ubicación
    }

    // Agrega una parada al hacer clic largo en el mapa
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText(String.valueOf(latLng.latitude)); // Muestra la latitud en el campo
        txtLongitud.setText(String.valueOf(latLng.longitude)); // Muestra la longitud en el campo

        // Crear un diálogo para pedir el nombre de la parada
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre de la Parada");
        builder.setMessage("Ingresa un nombre para esta parada:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String stopName = input.getText().toString().trim();
            if (stopName.isEmpty()) {
                stopName = "Parada " + (busStops.size() + 1); // Nombre por defecto si no se ingresa nada
            }

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(stopName) // Usar el nombre personalizado
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)); // Marcador rojo
            Marker marker = mMap.addMarker(markerOptions);
            stopMarkers.add(marker); // Agrega el marcador a la lista

            busStops.add(new BusStop(latLng, stopName)); // Agrega la parada con su nombre

            if (busStops.size() >= 2) {
                drawBusRoute(); // Dibuja la ruta si hay al menos 2 paradas
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Dibuja la ruta entre las paradas usando una polilínea
    private void drawBusRoute() {
        if (currentRoute != null) {
            currentRoute.remove(); // Elimina la ruta anterior si existe
        }
        String url = getDirectionsUrl(); // Obtiene la URL para la API de Directions
        new FetchRouteTask().execute(url); // Ejecuta la tarea asíncrona para obtener la ruta
    }

    // Construye la URL para solicitar la ruta a la API de Google Directions
    private String getDirectionsUrl() {
        if (busStops.size() < 2) return ""; // No hay suficiente información para una ruta

        BusStop origin = busStops.get(0); // Punto de origen
        String strOrigin = "origin=" + origin.location.latitude + "," + origin.location.longitude;

        BusStop destination = busStops.get(busStops.size() - 1); // Punto de destino
        String strDest = "destination=" + destination.location.latitude + "," + destination.location.longitude;

        String waypoints = ""; // Puntos intermedios
        if (busStops.size() > 2) {
            waypoints = "waypoints=";
            for (int i = 1; i < busStops.size() - 1; i++) {
                BusStop point = busStops.get(i);
                waypoints += point.location.latitude + "," + point.location.longitude;
                if (i < busStops.size() - 2) waypoints += "|"; // Separador entre waypoints
            }
        }

        String mode = "mode=driving"; // Modo de transporte: conducción
        String key = "key=AIzaSyDfEc5cgk4zuh47rrsCV0d3NprpBWUmkS8"; // Clave de API
        return "https://maps.googleapis.com/maps/api/directions/json?" + strOrigin + "&" + strDest + "&" + waypoints + "&" + mode + "&" + key;
    }

    // Tarea asíncrona para obtener datos de la ruta desde la API
    private class FetchRouteTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                URL urlObject = new URL(url[0]);
                HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
                conn.connect();
                InputStream stream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                data = builder.toString();
                reader.close();
                stream.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            drawPolylineFromJson(result); // Dibuja la ruta en el mapa
        }
    }

    // Dibuja la polilínea en el mapa a partir del JSON de la API
    private void drawPolylineFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                String encodedPoints = overviewPolyline.getString("points");

                List<LatLng> points = decodePoly(encodedPoints); // Decodifica los puntos
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .width(10) // Grosor de la línea
                        .color(Color.BLUE); // Color azul
                currentRoute = mMap.addPolyline(polylineOptions);

                JSONArray legs = route.getJSONArray("legs");
                for (int i = 0; i < legs.length() && i < stopMarkers.size() - 1; i++) {
                    JSONObject leg = legs.getJSONObject(i);
                    JSONObject duration = leg.getJSONObject("duration");
                    String durationText = duration.getString("text");
                    Marker marker = stopMarkers.get(i + 1);
                    marker.setSnippet("ETA: " + durationText); // Muestra el tiempo estimado
                    marker.showInfoWindow();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al trazar ruta: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Decodifica los puntos codificados de la polilínea (formato Polyline de Google)
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
            poly.add(p);
        }
        return poly;
    }

    // Verifica la ubicación del usuario periódicamente cada 40 segundos
    @SuppressLint("MissingPermission")
    private void checkLocationPeriodically() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fusedLocationClient.getLastLocation().addOnSuccessListener(rutaTransporte.this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (currentLocationMarker != null) {
                            currentLocationMarker.remove(); // Elimina el marcador anterior
                        }
                        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("Mi Ubicación")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))); // Marcador azul
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15)); // Sigue al usuario
                        checkProximityToStops(currentLocation); // Verifica proximidad a paradas
                        updateETAFromCurrentLocation(currentLocation); // Actualiza ETAs
                        txtLatitud.setText(String.valueOf(currentLocation.latitude));
                        txtLongitud.setText(String.valueOf(currentLocation.longitude));
                    }
                });
                checkLocationPeriodically(); // Se llama recursivamente
            }
        }, 40000); // Intervalo de 40 segundos
    }

    // Verifica la proximidad a las paradas y envía notificaciones push con distancia y ETA
    private void checkProximityToStops(LatLng currentLocation) {
        if (busStops.isEmpty()) return; // No hace nada si no hay paradas

        for (int i = 0; i < busStops.size(); i++) {
            BusStop stop = busStops.get(i);
            float[] results = new float[1];
            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, stop.location.latitude, stop.location.longitude, results);
            float distance = results[0]; // Distancia en metros

            int notificationId = NOTIFICATION_ID_BASE + i; // ID único por parada

            if (distance < 5 && i != lastNotifiedStopIndex) { // Llegaste a la parada (< 5 metros)
                String message = "¡Has llegado a " + stop.name + "!";
                sendNotification("Llegada a Parada", message, notificationId);
                lastNotifiedStopIndex = i;
            } else if (distance < 100 && i != lastNotifiedStopIndex) { // Estás cerca (< 100 metros)
                String proximityMessage = "Estás a " + String.format("%.0f", distance) + " metros de " + stop.name;
                sendNotification("Proximidad a Parada", proximityMessage, notificationId); // Notificación inicial con distancia
                calculateETAForStop(currentLocation, stop, i, distance); // Calcula el ETA
                lastNotifiedStopIndex = i;
            } else if (distance >= 100 && i == lastNotifiedStopIndex) {
                lastNotifiedStopIndex = -1; // Resetea si te alejas
            }
        }
    }

    // Calcula el ETA desde la ubicación actual hasta una parada específica
    private void calculateETAForStop(LatLng currentLocation, BusStop stop, int stopIndex, float distance) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + currentLocation.latitude + "," + currentLocation.longitude +
                "&destination=" + stop.location.latitude + "," + stop.location.longitude +
                "&mode=driving&key=AIzaSyDfEc5cgk4zuh47rrsCV0d3NprpBWUmkS8";
        new FetchETAForNotificationTask(stopIndex, distance, stop.name).execute(url); // Ejecuta la tarea para obtener el ETA
    }

    // Tarea asíncrona para obtener el ETA y enviar una notificación con distancia y tiempo
    private class FetchETAForNotificationTask extends AsyncTask<String, Void, String> {
        private int stopIndex;
        private float distance;
        private String stopName;

        public FetchETAForNotificationTask(int stopIndex, float distance, String stopName) {
            this.stopIndex = stopIndex;
            this.distance = distance;
            this.stopName = stopName;
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                URL urlObject = new URL(url[0]);
                HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
                conn.connect();
                InputStream stream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                data = builder.toString();
                reader.close();
                stream.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray routes = jsonObject.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONArray legs = route.getJSONArray("legs");
                    if (legs.length() > 0) {
                        JSONObject leg = legs.getJSONObject(0);
                        JSONObject duration = leg.getJSONObject("duration");
                        String durationText = duration.getString("text");
                        // Envía notificación con distancia y ETA
                        String message = "Estás a " + String.format("%.0f", distance) + " metros de " + stopName +
                                " - ETA: " + durationText;
                        sendNotification("Proximidad a Parada", message, NOTIFICATION_ID_BASE + stopIndex);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(rutaTransporte.this, "Error al calcular ETA: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Actualiza el tiempo estimado (ETA) desde la ubicación actual a todas las paradas
    private void updateETAFromCurrentLocation(LatLng currentLocation) {
        if (busStops.isEmpty()) return;

        for (int i = 0; i < busStops.size(); i++) {
            BusStop stop = busStops.get(i);
            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + currentLocation.latitude + "," + currentLocation.longitude +
                    "&destination=" + stop.location.latitude + "," + stop.location.longitude +
                    "&mode=driving&key=AIzaSyDfEc5cgk4zuh47rrsCV0d3NprpBWUmkS8";
            new FetchETAFromCurrentTask(i).execute(url); // Calcula el ETA para cada parada
        }
    }

    // Tarea asíncrona para obtener el ETA desde la ubicación actual a una parada
    private class FetchETAFromCurrentTask extends AsyncTask<String, Void, String> {
        private int stopIndex;

        public FetchETAFromCurrentTask(int stopIndex) {
            this.stopIndex = stopIndex;
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                URL urlObject = new URL(url[0]);
                HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
                conn.connect();
                InputStream stream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                data = builder.toString();
                reader.close();
                stream.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray routes = jsonObject.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONArray legs = route.getJSONArray("legs");
                    if (legs.length() > 0) {
                        JSONObject leg = legs.getJSONObject(0);
                        JSONObject duration = leg.getJSONObject("duration");
                        String durationText = duration.getString("text");
                        Marker marker = stopMarkers.get(stopIndex);
                        marker.setSnippet("ETA desde tu ubicación: " + durationText); // Actualiza el ETA en el marcador
                        marker.showInfoWindow();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(rutaTransporte.this, "Error al calcular ETA: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Guarda la ruta actual en la lista de rutas guardadas
    private void saveCurrentRoute() {
        if (!busStops.isEmpty()) {
            savedRoutes.add(new ArrayList<>(busStops)); // Copia las paradas actuales
            saveRoutesToFile(); // Guarda en el archivo
            updateRouteList(); // Actualiza la lista en la UI
            Toast.makeText(this, "Ruta guardada en posición " + savedRoutes.size(), Toast.LENGTH_SHORT).show();
            busStops.clear(); // Limpia las paradas actuales
            for (Marker marker : stopMarkers) {
                marker.remove(); // Elimina los marcadores
            }
            stopMarkers.clear();
            if (currentRoute != null) {
                currentRoute.remove(); // Elimina la polilínea
                currentRoute = null;
            }
        } else {
            Toast.makeText(this, "No hay paradas para guardar", Toast.LENGTH_SHORT).show();
        }
    }

    // Muestra una ruta guardada en el mapa y ajusta la cámara para verla completa
    private void showSavedRoute(int routeIndex) {
        if (routeIndex >= 0 && routeIndex < savedRoutes.size()) {
            busStops.clear(); // Limpia las paradas actuales
            for (Marker marker : stopMarkers) {
                marker.remove(); // Elimina los marcadores existentes
            }
            stopMarkers.clear();
            busStops.addAll(savedRoutes.get(routeIndex)); // Carga las paradas de la ruta seleccionada
            for (int i = 0; i < busStops.size(); i++) {
                BusStop stop = busStops.get(i);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(stop.location)
                        .title(stop.name) // Usar el nombre personalizado
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                stopMarkers.add(marker); // Agrega los nuevos marcadores
            }
            if (busStops.size() >= 2) {
                drawBusRoute(); // Dibuja la ruta si hay al menos 2 paradas
            }

            // Ajusta la cámara para mostrar toda la ruta
            if (!busStops.isEmpty()) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (BusStop stop : busStops) {
                    builder.include(stop.location); // Incluye cada parada en los límites
                }
                LatLngBounds bounds = builder.build(); // Construye los límites
                int padding = 100; // Espacio en píxeles alrededor de la ruta
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding)); // Ajusta la cámara con animación
            }
        }
    }

    // Elimina una ruta guardada de la lista
    private void deleteSavedRoute(int routeIndex) {
        if (routeIndex >= 0 && routeIndex < savedRoutes.size()) {
            savedRoutes.remove(routeIndex); // Elimina la ruta
            saveRoutesToFile(); // Actualiza el archivo
            updateRouteList(); // Actualiza la lista en la UI
            Toast.makeText(this, "Ruta " + (routeIndex + 1) + " eliminada", Toast.LENGTH_SHORT).show();

            if (selectedRouteIndex == routeIndex) {
                busStops.clear(); // Limpia las paradas actuales
                for (Marker marker : stopMarkers) {
                    marker.remove(); // Elimina los marcadores
                }
                stopMarkers.clear();
                if (currentRoute != null) {
                    currentRoute.remove(); // Elimina la polilínea
                    currentRoute = null;
                }
                selectedRouteIndex = -1; // Resetea el índice seleccionado
            } else if (selectedRouteIndex > routeIndex) {
                selectedRouteIndex--; // Ajusta el índice si es necesario
            }
        } else {
            Toast.makeText(this, "Índice de ruta inválido", Toast.LENGTH_SHORT).show();
        }
    }

    // Guarda todas las rutas en un archivo JSON
    private void saveRoutesToFile() {
        try {
            JSONArray jsonRoutes = new JSONArray();
            for (List<BusStop> route : savedRoutes) {
                JSONArray jsonRoute = new JSONArray();
                for (BusStop stop : route) {
                    JSONObject jsonPoint = new JSONObject();
                    jsonPoint.put("latitude", stop.location.latitude);
                    jsonPoint.put("longitude", stop.location.longitude);
                    jsonPoint.put("name", stop.name); // Guardar el nombre de la parada
                    jsonRoute.put(jsonPoint);
                }
                jsonRoutes.put(jsonRoute);
            }

            File file = new File(getFilesDir(), "routes.json");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonRoutes.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar rutas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Carga las rutas guardadas desde un archivo JSON
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
                        String name = jsonPoint.optString("name", "Parada " + (j + 1)); // Nombre por defecto si no existe
                        route.add(new BusStop(new LatLng(lat, lng), name));
                    }
                    savedRoutes.add(route);
                }
                updateRouteList(); // Actualiza la lista en la UI
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar rutas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Maneja los resultados de las solicitudes de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true); // Activa el botón de ubicación
                checkLocationPeriodically(); // Inicia la verificación de ubicación
            }
        } else if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso de notificaciones otorgado", Toast.LENGTH_SHORT).show();
        }
    }
}