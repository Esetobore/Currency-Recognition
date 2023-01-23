package com.example.currencyrecognition

import android.R.attr
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.currencyrecognition.ml.ModelUnquant
import kotlinx.android.synthetic.main.activity_camera.*
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.Collections.min
import kotlin.math.min


class CameraActivity : AppCompatActivity() {
    val imageSize = 224
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

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
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun imgClassificationByModel(image: Bitmap) {

    }

}