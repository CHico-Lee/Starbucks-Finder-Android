package com.example.android.starbucksfinder

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * Created by leech on 5/7/2018.
 */

class ListFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener, Toolbar.OnMenuItemClickListener {

    private lateinit var toolbar: Toolbar
    private lateinit var loadingLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private var lastLocation: Location? = null
    private var googleApiClient: GoogleApiClient? = null
    private val nearbyStarbucks = NearbyStarbucks.instance
    private var searchLatLng : LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                if (nearbyStarbucks.storeList.isEmpty())
                    refreshStoreList(searchLatLng);
            }

        }
        createLocationRequest()
        googleApiClient = GoogleApiClient.Builder(context!!)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity()!!, this)
                .build()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.recycler_view_frag,
                container, false).apply { tag = TAG }

        toolbar = activity!!.findViewById<View>(R.id.my_toolbar) as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(rootView.findViewById(R.id.my_toolbar))
        toolbar.setTitle(R.string.app_name)
        toolbar.inflateMenu(R.menu.main)
        toolbar.setOnMenuItemClickListener(this)

        loadingLayout = rootView.findViewById(R.id.layout_loading)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(activity)
        with(recyclerView) {
            layoutManager = this@ListFragment.layoutManager
            scrollToPosition(0)
        }
        return rootView
    }

    override fun onResume() {
        super.onResume()

            if (!locationUpdateState) {
                startLocationUpdates()
            }
            if (nearbyStarbucks.storeList.isEmpty())
                refreshStoreList(searchLatLng);
            else
                ShowCafeResults()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null)
            android.util.Log.e(TAG, "press the back button on the PlacePicker will cause null Intent returned, fixed by using Intent?")
            if (requestCode == ListFragment.REQUEST_CHECK_SETTINGS) {
                if (resultCode == Activity.RESULT_OK) {
                    locationUpdateState = true
                    startLocationUpdates()
                }
            }
            if (requestCode == ListFragment.PLACE_PICKER_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    nearbyStarbucks.storeList.clear()
                    val place = PlacePicker.getPlace(context, data)
                    searchLatLng = place.latLng
                    refreshStoreList(searchLatLng)
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == ListFragment.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val id = item!!.itemId

        when (id) {

            R.id.menu_edit_location -> {
                val builder = PlacePicker.IntentBuilder()
                startActivityForResult(builder.build(activity!!), PLACE_PICKER_REQUEST);
                return true
            }


            else -> return false
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        android.util.Log.e(TAG, "onConnectionFailed: " + p0.toString())
    }

    companion object {
        private val TAG = "ListFragment"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 11
        private const val REQUEST_CHECK_SETTINGS = 12
        private const val PLACE_PICKER_REQUEST = 13
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {

        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(activity!!)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(activity!!, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    android.util.Log.e(TAG, "sendEx: IntentSender.SendIntentException: ")
                }
            }
        }


    }

    fun listItemClicked(pos: Int) {
        var intent = Intent(context, MapActivity::class.java)
        intent.putExtra("position", pos)
        startActivity(intent)
    }


    private fun refreshStoreList(newLocation: LatLng?) {
        recyclerView.visibility = GONE
        loadingLayout.visibility = VISIBLE

        thread() {
            try {
                if (newLocation == null) {
                    while (lastLocation == null) {
                        Thread.sleep(500)
                    }
                    startUrlRequest(lastLocation!!.latitude, lastLocation!!.longitude)
                } else
                    startUrlRequest(newLocation.latitude, newLocation.longitude)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "catch")
                showErrorDialog()
            }

        }

    }

    private fun startUrlRequest(lat: Double, long: Double) {
        val apiKey = getString(R.string.google_places_web_key)
        val link = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${lat},${long}&" +
                "rankby=distance&type=cafe&keyword=Starbucks&key=${apiKey}"
        val result = URL(link).readText()
        postUrlRequest(result)
    }

    private fun postUrlRequest(json: String) {
        val jsonObj = JSONObject(json)
        parseCafeData(jsonObj)
        ShowCafeResults()
    }

    private fun parseCafeData(jsonObj: JSONObject) {
        val resultsArray = jsonObj.getJSONArray("results")
        nearbyStarbucks.storeList.clear()
        for (i in 0 until resultsArray.length() - 1) {
            try {
                val resultObj = resultsArray.getJSONObject(i)
                val name = resultObj.getString("name")
                if (name != "Starbucks")
                    continue
                val vicinity = resultObj.getString("vicinity")
                val geometryObj = resultObj.getJSONObject("geometry")
                val locationObj = geometryObj.getJSONObject("location")
                val lat = locationObj.getDouble("lat")
                val lng = locationObj.getDouble("lng")
                val openingObj = resultObj.getJSONObject("opening_hours")
                val openNow = (openingObj.getString("open_now") == "true")
                val newStore = Store(name, vicinity, lat, lng, openNow)
                nearbyStarbucks.storeList.add(newStore)
            } catch (e: JSONException) {
                android.util.Log.e(TAG, "parseCafeData Error: " + e.toString())
            }
        }
    }

    private fun ShowCafeResults() {
        this@ListFragment.activity?.runOnUiThread(java.lang.Runnable {
            recyclerView.adapter = ListItemAdapter(nearbyStarbucks.storeList, this)
            recyclerView.visibility = VISIBLE
            loadingLayout.visibility = GONE
        })

    }

    private fun showErrorDialog() {
        this@ListFragment.activity?.runOnUiThread(java.lang.Runnable {
            showAlertDialog {
                setTitle("No internet connection")
                setMessage("Please connection to internet.")
                positiveButton("Retry") {
                    refreshStoreList(null)
                }
                negativeButton {
                    exitProcess(0)
                }
            }
        })

    }

    private fun showAlertDialog(dialogBuilder: AlertDialog.Builder.() -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.dialogBuilder()
        val dialog = builder.create()

        dialog.show()
    }

    private fun AlertDialog.Builder.positiveButton(text: String = "Okay", handleClick: (which: Int) -> Unit = {}) {
        this.setPositiveButton(text, { _, which-> handleClick(which) })
    }

    private fun AlertDialog.Builder.negativeButton(text: String = "Cancel", handleClick: (which: Int) -> Unit = {}) {
        this.setNegativeButton(text, { _, which-> handleClick(which) })
    }


}
