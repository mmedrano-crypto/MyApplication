package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
// Necesaria para crear una pantalla en la aplicación o actividad.

import android.os.Bundle
// Permite pasar información de una pantalla a otra.

import com.google.android.gms.maps.CameraUpdateFactory
// Proporciona herramientas para mover y cambiar la vista del mapa, como acercar o alejar la imagen.

import com.google.android.gms.maps.GoogleMap
// Motor del mapa.

import com.google.android.gms.maps.OnMapReadyCallback
// Indica si el mapa está listo para usar.

import com.google.android.gms.maps.SupportMapFragment
// Marco que contiene el mapa.

import com.google.android.gms.maps.model.LatLng
// Representa un punto en el mapa mediante latitud y longitud.

import com.google.android.gms.maps.model.MarkerOptions
// Permite poner marcadores en el mapa.

import com.example.myapplication.databinding.ActivityMapsBinding
// Facilita la conexión entre el diseño visual y el código.

import com.google.android.gms.location.FusedLocationProviderClient
// Ayuda a encontrar la ubicación actual del usuario de una manera más efectiva y precisa.

import com.google.android.gms.location.LocationServices
// Proporciona acceso a los servicios de localización de Google.

import com.google.android.gms.location.LocationRequest
// Especifica precisión/rapidez localización.

import com.google.android.gms.location.LocationCallback
// Notifica si la ubicación del usuario ha cambiado o se ha encontrado una nueva.

import com.google.android.gms.location.LocationResult
// Facilita la información de la ubicación actual.

import android.Manifest
// Contiene los nombres de permisos que la app puede necesitar.

import android.content.pm.PackageManager
// Ayuda a manejar y revisar los permisos.

import androidx.core.app.ActivityCompat
// Compatibilidad para versiones antiguas de Android, especialmente para permisos.

import androidx.core.content.ContextCompat
// Ayuda a manejar colores y aspectos visuales, y para verificar permisos.


/**
 * "AppCompatActivity" , tipo de pantalla básica en las aplicaciones de Android.
 * Permite que en la  pantalla del mapa pueda haber botones, textos así como el mapa.
 * "OnMapReadyCallback" avisa cuando el mapa esté listo para mostrarse.
 */


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    // Declaración de variables.

    // 'lateinit': Inicialización posterior, asegura no nulidad.
    private lateinit var mMap: GoogleMap
    // mMap: Instancia de GoogleMap utilizada para interactuar con el mapa en la UI.
    private lateinit var binding: ActivityMapsBinding
    // binding: Referencia a la clase generada para interactuar con los componentes de la UI.
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // fusedLocationClient: Cliente para acceder a la API de localización fusionada de Google.
    private lateinit var locationRequest: LocationRequest
    // locationRequest: Configuración para las solicitudes de actualización de ubicación.
    private lateinit var locationCallback: LocationCallback
    // locationCallback: Callback que se invoca con las actualizaciones de ubicación.

    /**
     * Inicializa la actividad, establece el contenido de la vista y prepara el fragmento del mapa y el cliente de localización.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad, si existe.
     */

    //Bundle es una clase en Android que proporciona una forma de pasar datos entre actividades o para guardar el estado de una actividad.
    //El símbolo ? en Bundle? en Kotlin indica que el parámetro savedInstanceState puede ser nulo (null).
    // override indica que estamos proporcionando nuestra propia implementación de una función de la clase base.

    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la versión del método 'onCreate' de la clase superior (AppCompatActivity).
        super.onCreate(savedInstanceState)


        // Inicializa 'binding' para interactuar con la UI, inflando la vista desde el archivo de diseño.
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Encuentra el fragmento del mapa en la UI y lo asigna a 'mapFragment'.
        // R es una clase generada automáticamente en Android que contiene referencias a todos los recursos de una app
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // Inicializa 'fusedLocationClient' para acceder a la localización del dispositivo.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Llama a un método para crear una solicitud de localización.
        createLocationRequest()
        // Llama a un método para verificar y solicitar permisos de localización.
        checkLocationPermission()
    }
    /**
     * Crea y configura la solicitud de localización, definiendo intervalos y prioridad.
     */

    // Uso de deprecated en atributos de la variable locationRequest y constante PRIORITY_HIGH_ACCURACY
    // el uso del elemento obsoleto no se recomienda porque puede eliminarse en versiones futuras
    // si bien es plenamente funcional
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            // interval define el intervalo  para las actualizaciones de ubicación en milisegundos.
            interval = 10000
            // fastestInterval o intervalo más rápido para actualizaciones de ubicación, en milisegundos.
            fastestInterval = 5000
            // priority establece la prioridad de la solicitud de ubicación.
            // PRIORITY_HIGH_ACCURACY obtiene la ubicación más precisa posible.
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Clase manejar actualizaciones de ubicación.
        locationCallback = object : LocationCallback() {

            // locationResult contiene los resultados de la ubicación.
            override fun onLocationResult(locationResult: LocationResult) {
                // Itera a través de todas las ubicaciones disponibles en locationResult.
                for (location in locationResult.locations){
                    // Llama a updateMapLocation actualizando el mapa con la nueva ubicación.
                    updateMapLocation(location)
                }
            }
        }

    }
    /**
     * Actualiza la posición del mapa basándose en la ubicación del usuario.
     *
     * @param location Ubicación actual del usuario.
     */
    private fun updateMapLocation(location: android.location.Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
    /**
     * Verifica si la aplicación tiene permisos de localización y, de no ser así, los solicita al usuario.
     */
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }
    /**
     * Maneja el resultado de la solicitud de permisos, activando la localización en el mapa si se conceden.
     *
     * @param requestCode Código de solicitud de permisos.
     * @param permissions Permisos solicitados.
     * @param grantResults Resultados de las solicitudes de permisos.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                mMap.isMyLocationEnabled = true
            }
        }
    }
    /**
     * Configura el mapa una vez que está listo, activando los controles de zoom y la localización del usuario.
     *
     * @param googleMap Instancia del mapa de Google.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        // Verifica si la aplicación tiene permisos acceso a ubicación.
        // ContextCompat es una clase auxiliar en Android que proporciona compatibilidd versiones anteriores.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            // Si se tiene el permiso, activa la capa de ubicación en el mapa, mostrando la ubicación actual del usuario.
            mMap.isMyLocationEnabled = true
            // También solicita actualizaciones periódicas de la ubicación.
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
    /**
     * Método onStart que solicita actualizaciones de localización al iniciar la actividad.
     */
    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
    /**
     * Método onStop que elimina las actualizaciones de localización al detener la actividad.
     */
    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
// Companion object incluye funciones y propiedades estáticas en una clase.
    companion object {
        /**
         * Código de solicitud de permiso de localización.
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
