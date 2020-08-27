package com.example.prutc.autramkotlin

import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.CompoundButton
import com.example.prutc.autramkotlin.Beacon.KontaktBeacon
import com.example.prutc.autramkotlin.GPS.GPS
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    companion object {
        val TAG = "boss"
        var tramID = 0
        var isActive = false
        var roadID = 0
        var latlong = "0,0"
        var length = 0.0

    }

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val kontaktBeacon = KontaktBeacon(this)
    private val gps = GPS(this, this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kontaktBeacon.setupProximityManager()
        gps.setupGoogleApiClient()

        setBackgroundResource()

        onOffToggleButton.setOnCheckedChangeListener(this)
        roadOneButton.setOnClickListener(this)
        roadTwoButton.setOnClickListener(this)
        roadThreeButton.setOnClickListener(this)
        roadFourButton.setOnClickListener(this)
        roadFiveButton.setOnClickListener(this)
        roadSixButton.setOnClickListener(this)
        roadSevenButton.setOnClickListener(this)


    }

    override fun onDestroy() {
        if (kontaktBeacon.proximityManager != null) {
            kontaktBeacon.disconnect()
        }
        if (gps.googleApiClient != null) {
            gps.removeLocationUpdates()
        }
        super.onDestroy()
    }

    override fun onCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            isActive = true
            bluetoothAdapter.enable()
            kontaktBeacon.startScanning()
            gps.connect()
        } else {
            isActive = false
            bluetoothAdapter.disable()
            kontaktBeacon.stopScanning()
            gps.disconnect()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.roadOneButton -> MainActivity.roadID = 0
            R.id.roadTwoButton -> MainActivity.roadID = 1
            R.id.roadThreeButton -> MainActivity.roadID = 2
            R.id.roadFourButton -> MainActivity.roadID = 3
            R.id.roadFiveButton -> MainActivity.roadID = 4
            R.id.roadSixButton -> MainActivity.roadID = 5
            R.id.roadSevenButton -> MainActivity.roadID = 6

        }
        roadTextView.text = (MainActivity.roadID + 1).toString()
    }

    private fun setBackgroundResource() {
        if (tramID == 1) {
            tramIDTextView.text = "TRAM01"
            tramImageView.setBackgroundResource(R.drawable.tram_red)
        } else if (tramID == 2) {
            tramIDTextView.text = "TRAM02"
            tramImageView.setBackgroundColor(R.drawable.tram_blue)
        }
    }
}
