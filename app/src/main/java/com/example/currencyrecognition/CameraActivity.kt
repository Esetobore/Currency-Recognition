package com.example.currencyrecognition

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
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


class CameraActivity : AppCompatActivity() {
    val imageSize = 224
    private var tts : TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        take_pic_btn.setOnClickListener {
            permissions()
        }
    }


    fun permissions(){
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

    @Deprecated("Deprecated in Kotlin")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == 1){
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
            Toast.makeText(this, "Error",Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun imgClassificationByModel(image: Bitmap) {
        val model = ModelUnquant.newInstance(applicationContext)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)

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
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }
        val classes = arrayOf("100", "200", "500", "1000")

        result.text = classes[maxPos]
        // likely to create a text to speech format of the applications result
      //  tts!!.speak(result.toString(), TextToSpeech.QUEUE_FLUSH, null,"")
        // Releases model resources if no longer used.
        model.close()
    }



}