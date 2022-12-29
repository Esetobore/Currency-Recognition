package com.example.currencyrecognition.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.currencyrecognition.CameraActivity
import com.example.currencyrecognition.MainActivity
import com.example.currencyrecognition.R
import com.example.currencyrecognition.utils.Constants.Companion.SPLASHDELAY
import kotlinx.android.synthetic.main.activity_main_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainSplash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_splash)

        lottie.animate().translationY(1500f).setDuration(1000).startDelay = 5000

        lifecycleScope.launch(Dispatchers.Default){
            intent()
        }
    }
    private suspend fun intent(){
        delay(SPLASHDELAY)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}