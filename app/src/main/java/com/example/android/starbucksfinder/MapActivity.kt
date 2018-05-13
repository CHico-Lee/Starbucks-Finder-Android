package com.example.android.starbucksfinder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
/**
 * Created by leech on 5/6/2018.
 */
/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
class MapActivity :
        AppCompatActivity(),
        OnMapReadyCallback {

    var location = LatLng(42.359584, -71.059844)
    val ZOOM_LEVEL = 15f
    var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_map)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val mapFragment: SupportMapFragment? =
                supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        val intent = getIntent()
        val pos = intent.getIntExtra("position", 0)
        val nearbyStarbucks = NearbyStarbucks.instance
        location = LatLng(nearbyStarbucks.storeList[pos].lat, nearbyStarbucks.storeList[pos].lng)
        title = nearbyStarbucks.storeList[pos].name
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just move the camera to Sydney and add a marker in Sydney.
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL))
            addMarker(MarkerOptions().position(location).title(title))
        }
    }
}