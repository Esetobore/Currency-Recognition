package com.example.currencyrecognition

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        lifecycleScope.launch (Dispatchers.Default){
            intent()
        }
    }
    private suspend fun intent(){
        delay(OPENDELAY)
        startActivity(Intent(this,CameraActivity::class.java))
        finish()
    }
}