package com.example.prutc.autramkotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.prutc.autramkotlin.GPS.GPS
import kotlinx.android.synthetic.main.activity_choose_tram.*

class ChooseTramActivity : AppCompatActivity(), View.OnClickListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_tram)
        tramOneButton.setOnClickListener(this)
        tramTwoButton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent = Intent(this, MainActivity::class.java)

        when (view.id) {
            R.id.tramOneButton -> MainActivity.tramID = 1
            R.id.tramTwoButton -> MainActivity.tramID = 2

         }
        startActivity(intent)
    }
}
