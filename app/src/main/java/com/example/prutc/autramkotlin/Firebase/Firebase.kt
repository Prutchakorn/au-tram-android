package com.example.prutc.autramkotlin.Firebase

import com.example.prutc.autramkotlin.TramDetail
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class Firebase {

    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("tram-tracking")

    fun writeFirebaseData(tramID : Int, isActive : Boolean, pointID : Int, positionValue : Int, roadID : Int) {
        val tramDetail = TramDetail()
        tramDetail.run {
            this.tramID = tramID
            this.isActive = isActive
            this.pointID = pointID
            this.positionValue = positionValue
            this.roadID = roadID
        }

        val tramIDReference = databaseReference.child("tram0" + tramID)

        tramIDReference.run {
            child("isActive").setValue(isActive)
            child("pointID").setValue(pointID)
            child("positionValue").setValue(positionValue)
            child("roadID").setValue(roadID)
        }

        databaseReference.run {
            orderByChild("tram-tracking")
            setPriority(ServerValue.TIMESTAMP)
        }

    }
}
