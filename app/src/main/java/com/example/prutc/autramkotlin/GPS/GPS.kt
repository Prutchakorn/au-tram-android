package com.example.prutc.autramkotlin.GPS

import android.Manifest
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.Language
import com.akexorcist.googledirection.constant.RequestResult
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.constant.Unit
import com.akexorcist.googledirection.model.Direction
import com.example.prutc.autramkotlin.Beacon.KontaktBeacon
import com.example.prutc.autramkotlin.Firebase.Firebase
import com.example.prutc.autramkotlin.MainActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class GPS(private var context: Context, private var activity: MainActivity) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    var googleApiClient: GoogleApiClient? = null
    private val firebase = Firebase()
    private val gpsPosition = GPSPosition()
    private var positionValue = 0
    private var isChanged = false

    private var time = ""

    companion object {
        var distanceValue = ""
    }

    private var uids = mutableListOf<String>("tG35", "Fm61", "I7iw", "8MgX" )

    //---------------------------------
    private var serverKey = "AIzaSyCBbvuhfK8hR9at5AJD8XqWW43_omeJEjA"
    private var origin: LatLng? = null


    private var waypoints = mutableListOf(gpsPosition.point1, gpsPosition.point2, gpsPosition.point3, gpsPosition.point4, gpsPosition.point5, gpsPosition.point6, gpsPosition.point7, gpsPosition.point8)
    fun setupGoogleApiClient() {
        if (googleApiClient == null) {
            this.googleApiClient = GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this@GPS)
                    .addOnConnectionFailedListener(this@GPS)
                    .build()
        }


        Toast.makeText(context, "SETUP GOOGLE API CLIENT", Toast.LENGTH_SHORT).show()
    }

    fun connect() {
        googleApiClient?.connect()
        firebase.writeFirebaseData(MainActivity.tramID, MainActivity.isActive, MainActivity.roadID, positionValue, 1)
        Toast.makeText(context, "GPS IS CONNECTED", Toast.LENGTH_SHORT).show()

    }

    fun disconnect() {
        if (googleApiClient!!.isConnected) {
            googleApiClient?.disconnect()
        }
        isChanged = false
        firebase.writeFirebaseData(MainActivity.tramID, MainActivity.isActive, MainActivity.roadID, positionValue, 1)
        Toast.makeText(context, "GPS IS DISCONNECTED", Toast.LENGTH_SHORT).show()
    }

    fun removeLocationUpdates() {
        if (googleApiClient!!.isConnected) {
            googleApiClient?.disconnect()
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this@GPS)
        }
        Toast.makeText(context, "REMOVE LOCA TION UPDATES", Toast.LENGTH_SHORT).show()
    }

    override fun onConnected(p0: Bundle?) {
        checkPermission()
        val locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient)
        if (locationAvailability.isLocationAvailable) {
            val locationRequest = LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(3000)
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this@GPS)

        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }


    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Toast.makeText(context, connectionResult.toString(), Toast.LENGTH_SHORT).show()
    }


    override fun onLocationChanged(location: Location) {
        isChanged = true
        origin = LatLng(location.latitude, location.longitude)

        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(waypoints[MainActivity.roadID + 1])
                .unit(Unit.METRIC)
                .language(Language.ENGLISH)
                .transitMode(TransportMode.DRIVING)
                .execute(object : DirectionCallback {
                    override fun onDirectionSuccess(direction: Direction, rawBody: String?) {
                        var status = direction.status

                        if (status == RequestResult.OK) {
                            showToast("OK")

                            time = direction.routeList[0].legList[0].duration.text
                            distanceValue = direction.routeList[0].legList[0].distance.value

                            var distance = distanceValue.toDouble()

                            //less or much
                            if (distance > gpsPosition.roadLength[MainActivity.roadID]) {
                                distance = gpsPosition.roadLength[MainActivity.roadID]
                            } else if (distance < 0) {
                                distance = 0.0
                            }
                            positionValue = 100 - ((distance / gpsPosition.roadLength[MainActivity.roadID]) * 100).toInt()
                            Log.d("BOSS", "Length: ${gpsPosition.roadLength[MainActivity.roadID]}")
                            Log.d("BOSS", "ROAD ID: ${MainActivity.roadID}")


                            //point
                            if (distance < 10) {
                                if (MainActivity.roadID == waypoints.size - 2) {
                                    MainActivity.roadID = 0
                                } else {
                                    MainActivity.roadID += 1
                                }
                                showToast("${MainActivity.roadID + 1}")
                            }

                            when (uids.indexOf(KontaktBeacon.uniqueId)) {
                                0 -> positionValue = 15
                                1 -> positionValue = 6
                                2 -> positionValue = 42
                                3 -> positionValue = 87
                            }
                            Log.d("BOSS", KontaktBeacon.uniqueId)
                            Log.d("BOSS", KontaktBeacon.isFound.toString())

                            firebase.writeFirebaseData(MainActivity.tramID, MainActivity.isActive, 1, positionValue, MainActivity.roadID + 1)
                            showToast(distanceValue)

                            Log.d("BOSS", "PositionValue: ${positionValue}")
                            Log.d("BOSS", "Distance: $distanceValue")
                            Log.d("BOSS", "Time: $time")
                            Log.d("BOSS", (MainActivity.roadID + 1).toString())

                        } else if (status == RequestResult.NOT_FOUND) {
                            showToast("Not Found")
                        }
                    }

                    override fun onDirectionFailure(t: Throwable?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

    fun checkPermission() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        //**************************
        builder.setAlwaysShow(true) //this is the key ingredient
        //**************************

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback {
            val status = it.status
            val state = it.locationSettingsStates
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Toast.makeText(context, "GPS is success", Toast.LENGTH_SHORT).show()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {

                        status.startResolutionForResult(
                                activity, 1000)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Toast.makeText(context, "GPS is unavailable", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}


