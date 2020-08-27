package com.example.prutc.autramkotlin.Beacon

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.prutc.autramkotlin.MainActivity
import com.kontakt.sdk.android.ble.configuration.ScanMode
import com.kontakt.sdk.android.ble.configuration.ScanPeriod
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener
import com.kontakt.sdk.android.common.KontaktSDK
import com.kontakt.sdk.android.common.profile.IEddystoneDevice
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace

class KontaktBeacon(private var context: Context) {

    var proximityManager : ProximityManager? = null
    companion object {
        var isFound = false
        var uniqueId = ""
    }
    fun setupProximityManager() {
        KontaktSDK.initialize(context)
        proximityManager = ProximityManagerFactory.create(context)
        proximityManager?.configuration()
                ?.scanPeriod(ScanPeriod.RANGING)
                ?.scanMode(ScanMode.LOW_LATENCY)
        proximityManager?.setEddystoneListener(createEddystoneListener())
        Toast.makeText(context, "SETUP PROXIMITY MANAGER", Toast.LENGTH_SHORT).show()
    }

    private fun createEddystoneListener() : EddystoneListener = object : SimpleEddystoneListener() {
        override fun onEddystonesUpdated(eddystones: MutableList<IEddystoneDevice>, namespace: IEddystoneNamespace?) {
            val nearestBeacon = findMin(eddystones)
            Log.d(MainActivity.TAG, "UID : ${eddystones[nearestBeacon].uniqueId}")
            Log.d(MainActivity.TAG, "Distance : ${eddystones[nearestBeacon].distance}")
            Log.d(MainActivity.TAG, "Proximity : ${eddystones[nearestBeacon].proximity}")
            Log.d(MainActivity.TAG, "Battery : ${eddystones[nearestBeacon].batteryPower}")
            uniqueId = eddystones[nearestBeacon].uniqueId

        }

        override fun onEddystoneDiscovered(eddystone: IEddystoneDevice, namespace: IEddystoneNamespace?) {
            Log.d(MainActivity.TAG, "FOUND BEACON")
            Toast.makeText(context, "FOUND BEACON", Toast.LENGTH_SHORT).show()
            isFound = true

        }

        override fun onEddystoneLost(eddystone: IEddystoneDevice?, namespace: IEddystoneNamespace?) {
            Log.d(MainActivity.TAG, "BEACON IS LOST")
            Toast.makeText(context, "BEACON IS LOST", Toast.LENGTH_SHORT).show()
            isFound = false
        }
    }

    fun startScanning() {
        proximityManager?.connect { proximityManager?.startScanning() }
        Toast.makeText(context, "START SCANNING", Toast.LENGTH_SHORT).show()
    }

    fun stopScanning() {
        proximityManager?.stopScanning()
        Toast.makeText(context, "STOP SCANNING", Toast.LENGTH_SHORT).show()
    }

    fun disconnect() {
        proximityManager?.disconnect()
        proximityManager = null
        Toast.makeText(context, "BEACON  IS DISCONNECTED", Toast.LENGTH_SHORT).show()
    }

    fun findMin(eddystones : MutableList<IEddystoneDevice>) : Int {
        var min = eddystones[0].distance
        var index = 0
        for (i : Int   in 0..eddystones.size - 1) {
            if (min > eddystones[i].distance) {
                min = eddystones[i].distance
                index = i
            }
        }
        return index

    }

}
