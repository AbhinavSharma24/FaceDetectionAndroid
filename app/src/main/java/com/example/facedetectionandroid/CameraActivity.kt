package com.example.facedetectionandroid

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.facedetectionandroid.Helper.RectOverlay
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val alertDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please wait, processing...")
            .setCancelable(false)
            .build()

        cameraBtn.setOnClickListener {
            cameraView.start()
            cameraView.captureImage()
            graphic_overlay.clear()
        }

        cameraView.addCameraKitListener(object : CameraKitEventListener{
            override fun onVideo(p0: CameraKitVideo?) {
            }
            override fun onEvent(p0: CameraKitEvent?) {
            }
            override fun onImage(p0: CameraKitImage?) {
                alertDialog.show()
                var bitmap = p0?.bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap!!, cameraView.width, cameraView.height, false)
                cameraView.stop()

                val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)

                val firebaseVisionFaceDetectorOptions = FirebaseVisionFaceDetectorOptions.Builder().build()

                val firebaseVisionFaceDetector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(firebaseVisionFaceDetectorOptions)

                firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
                    .addOnSuccessListener {

                        var counter = 0
                        for(face in it){
                            val rect = face.boundingBox
                            val rectOverlay = RectOverlay(graphic_overlay, rect)

                            graphic_overlay.add(rectOverlay)
                            counter += 1
                        }
                        alertDialog.dismiss()

                    }
                    .addOnFailureListener {
                        Toast.makeText(this@CameraActivity, "Error: " + it.message, Toast.LENGTH_SHORT).show()
                    }


            }
            override fun onError(p0: CameraKitError?) {
            }

        })

        nextBtn.setOnClickListener {
            startActivity(Intent(this@CameraActivity, MainActivity::class.java))
        }

    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }
}
