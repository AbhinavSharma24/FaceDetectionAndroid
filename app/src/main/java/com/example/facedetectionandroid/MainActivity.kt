package com.example.facedetectionandroid

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.core.util.size
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.photo1)
        imageView.setImageBitmap(bitmap)

        val rectPaint = Paint()
        rectPaint.strokeWidth = 5.0F
        rectPaint.color = Color.WHITE
        rectPaint.style = Paint.Style.STROKE

        val tempBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(tempBitmap)
        canvas.drawBitmap(bitmap, 0F, 0F, null)

        processBtn.setOnClickListener {
            val faceDetector= FaceDetector.Builder(applicationContext)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build()

            if(!faceDetector.isOperational){
                Toast.makeText(this, "Face Detector could not be set up in your device!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                Toast.makeText(this, "Faces detected successfully", Toast.LENGTH_SHORT).show()
            }

            val frame : Frame = Frame.Builder().setBitmap(bitmap).build()
            Log.d("FrameTest", frame.toString())
            val sparseArray : SparseArray<Face> = faceDetector.detect(frame)

            for(i in 0 until sparseArray.size)
            {
                val face = sparseArray.valueAt(i)
                val x1 = face.position.x
                val y1 = face.position.y
                val x2 = x1 + face.width
                val y2 = y1 + face.height

                val rectF = RectF(x1, y1, x2, y2)
                canvas.drawRoundRect(rectF, 2.0F, 2.0F, rectPaint)
            }

            imageView.setImageDrawable(BitmapDrawable(resources, tempBitmap))
        }

        backBtn.setOnClickListener {

            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }
}
