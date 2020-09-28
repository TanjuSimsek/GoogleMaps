package com.example.mapskotlin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManger: LocationManager
    private lateinit var locationListener: LocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(listener)

        // Latitude ->Enlem
        //Longitude -> Boylam

        /*  val sydney = LatLng(41.0286755,35.4958603)
          mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10f))*/

        //casting ->as
        locationManger = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {

                //location change call
                /* println(location!!.latitude)
                 println(location!!.longitude)*/
                mMap.clear()
                val updatedLocation = LatLng(location!!.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(updatedLocation).title("Your Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updatedLocation, 10f))

                //konumda adres almamızı sağlar geocoder

                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

                try {

                    val addressList =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addressList.size > 0) {

                        println(addressList.get(0).toString())

                    }


                } catch (e: Exception) {

                    e.printStackTrace()

                }


            }


            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("Not yet implemented")
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("Not yet implemented")
            }


        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            //izin al
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )


        } else {

            //izin verilmiş

            locationManger.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1,
                1f,
                locationListener
            )
            val latestLocation = locationManger.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (latestLocation != null) {

                val latestKnownLatLng = LatLng(latestLocation.latitude, latestLocation.longitude)
                mMap.addMarker(MarkerOptions().position(latestKnownLatLng).title("Latest Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latestKnownLatLng, 10f))


            }

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {

            if (grantResults.size > 0) {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    locationManger.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1,
                        1f,
                        locationListener
                    )

                }
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val listener = object : GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng?) {

            mMap.clear()

            val geoCoder = Geocoder(this@MapsActivity, Locale.getDefault())

            if (p0 != null) {

                var address = ""

                try {

                    val addressList = geoCoder.getFromLocation(p0.latitude, p0.longitude, 1)
                    if (addressList.size > 0) {


                        if (addressList.get(0).thoroughfare != null) {
                            address += addressList.get(0).thoroughfare

                            if (addressList.get(0).subThoroughfare != null) {

                                address += " " + addressList.get(0).subThoroughfare


                            }
                        }


                    }

                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(p0).title(address))

            }


        }


    }
}