package com.example.currencyrecognition

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.currencyrecognition.ml.ModelUnquant
import kotlinx.android.synthetic.main.activity_camera.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


class CameraActivity : AppCompatActivity() {
    private val imageSize = 224
    private lateinit var tts : TextToSpeech
    private val noResult = "0%\n"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                // set language for TextToSpeech
                tts.language = Locale.UK
            }
        }

        // attaching the function to the "take picture" cardview onclicklistener
        take_pic_btn.setOnClickListener {
            permissions()
        }

    }


   private fun permissions(){
        // Check camera permission if we have it.
        val checkSelfPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)==
                PackageManager.PERMISSION_GRANTED
        if (checkSelfPermission) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 1)
        } else {
            //Request camera permission if we don't have it.
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        }
    }

    //@Deprecated("Deprecated in Kotlin")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK){
            var image: Bitmap = data?.extras?.get("data") as Bitmap
            // crop the image
            val imgDimensions = Math.min(image.width, image.height)
            // centre the image
            image = ThumbnailUtils.extractThumbnail(image, imgDimensions, imgDimensions)
            //captured image is now set on the imageview
            imageView.setImageBitmap(image)

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)

            imgClassificationByModel(image)

        }
        else{
            Toast.makeText(this, "Error Please Retry",Toast.LENGTH_LONG).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("DefaultLocale")
    private fun imgClassificationByModel(image: Bitmap) {

           val model = ModelUnquant.newInstance(applicationContext)

           // Creates inputs for reference.
           val inputFeature0 =
               TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)

           // bitmap image to bytebuffer
           val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
           byteBuffer.order(ByteOrder.nativeOrder())


           // get the array of 224 * 224 pixels in the captured image
           val intValues = IntArray(imageSize * imageSize)
           image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)

           // iterate over pixels and extract R, G, and B values. Add to bytebuffer. as seen from the modelTFLite
           var pixel = 0
           for (i in 0 until imageSize) {
               for (j in 0 until imageSize) {
                   val `val` = intValues[pixel++] // RGB
                   byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 255f))
                   byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255f))
                   byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
               }
           }
           inputFeature0.loadBuffer(byteBuffer)

           // Runs model inference and gets result.
           val outputs = model.process(inputFeature0)
           val outputFeature0 = outputs.outputFeature0AsTensorBuffer
           val confidences = outputFeature0.floatArray
           // find the index of the class with the biggest confidence.
           // find the index of the class with the biggest confidence.
           var maxPos = 0
           var maxConfidence = 0F
           for (i in confidences.indices) {
               if (confidences[i] > maxConfidence) {
                   maxConfidence = confidences[i]
                   maxPos = i
               }
           }



           val classes = arrayOf("100", "200", "500", "1000")
           result.text = classes[maxPos]

            val unknown = "Unknown Currency"
            val noMatchFound = "No match Found"
        if(maxConfidence < 78){
            result.text = unknown
            confidence.text = noMatchFound
            tts.speak("Unknown Currency", TextToSpeech.QUEUE_FLUSH, null, null)
        }
           else {
            val speechText = "The currency is ${classes[maxPos]} Naira"
            tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null)


            // likely to create a text to speech format of the applications result

            // confidence percentage of other currencies
            var confidenceResult: String? = ""
            for (i in classes.indices) {
                confidenceResult += java.lang.String.format(
                    "%s: %.1f%%\n",
                    classes[i],
                    confidences[i] * 100
                )
            }
            confidence.text = confidenceResult

        }
           // Releases model resources if no longer used.
           model.close()

    }



}