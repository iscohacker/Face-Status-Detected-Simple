package uz.iskandarbek.facedetection

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

class MainActivity : AppCompatActivity() {

    private lateinit var faceImageView: ImageView
    private lateinit var faceInfoTextView: TextView
    private lateinit var captureButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        faceImageView = findViewById(R.id.faceImageView)
        faceInfoTextView = findViewById(R.id.faceInfoTextView)
        captureButton = findViewById(R.id.captureButton)

        captureButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            faceImageView.setImageBitmap(imageBitmap)
            detectFaces(imageBitmap)
        }
    }

    private fun detectFaces(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        val detector = FaceDetection.getClient(options)

        detector.process(image)
            .addOnSuccessListener { faces ->
                faceInfoTextView.text = "" // Tozalash
                if (faces.isNotEmpty()) {
                    for (face in faces) {
                        val faceInfoBuilder = StringBuilder()

                        faceInfoBuilder.append("Yuz aniqlangan!\n")

                        val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                        leftEar?.let {
                            faceInfoBuilder.append("Chap quloq: ${it.position}\n")
                        }

                        face.smilingProbability?.let { smileProb ->
                            faceInfoBuilder.append("Tabassum ehtimoli: $smileProb\n")
                        }

                        face.rightEyeOpenProbability?.let { rightEyeOpenProb ->
                            faceInfoBuilder.append("O'ng ko'z ochiqlik ehtimoli: $rightEyeOpenProb\n")
                        }

                        face.trackingId?.let { id ->
                            faceInfoBuilder.append("Yuz ID: $id\n")
                        }

                        faceInfoTextView.text = faceInfoBuilder.toString()
                    }
                } else {
                    faceInfoTextView.text = "Yuz aniqlanmadi!"
                }
            }
            .addOnFailureListener { e ->
                faceInfoTextView.text = "Yuz aniqlashda xatolik: ${e.message}"
            }
    }
}
