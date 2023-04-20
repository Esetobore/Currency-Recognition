package com.example.currencyrecognition

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.currencyrecognition.utils.Constants.Companion.DESCRIPTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class Description : AppCompatActivity() {
    private lateinit var textToSpeech : TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { speak ->
            if (speak == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.UK
                textToSpeech.setSpeechRate(1.0f)
                textToSpeech.speak(DESCRIPTION,TextToSpeech.QUEUE_FLUSH,null)
            }
            if (speak == TextToSpeech.ERROR){
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            }
        })
        // another coroutine scope implementing a simple suspend function
        lifecycleScope.launch (Dispatchers.Default){
            intent()
        }
    }

    private suspend fun intent(){
       delay(22000L)
        startActivity(Intent(this,CameraActivity::class.java))
        finish()

    }
}