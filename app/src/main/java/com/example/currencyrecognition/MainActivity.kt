package com.example.currencyrecognition

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.currencyrecognition.splash.MainSplash
import com.example.currencyrecognition.utils.Constants
import com.example.currencyrecognition.utils.Constants.Companion.INTROTEXT
import com.example.currencyrecognition.utils.Constants.Companion.OPENDELAY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
   private lateinit var textToSpeech : TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { speak ->
            if (speak == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.ENGLISH
                textToSpeech.setSpeechRate(1.0f)
                textToSpeech.speak(INTROTEXT,TextToSpeech.QUEUE_FLUSH,null)
            }
            if (speak == TextToSpeech.ERROR){
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            }
        })
        // another use of coroutines to implement the suspend function below
        lifecycleScope.launch (Dispatchers.Default){
            intent()
        }
    }

    private suspend fun intent(){
        delay(OPENDELAY)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)==
            PackageManager.PERMISSION_GRANTED){
            startActivity(Intent(this, CameraActivity::class.java))
            finish()
        }
        else {
            startActivity(Intent(this, Description::class.java))
            finish()
        }

    }
}