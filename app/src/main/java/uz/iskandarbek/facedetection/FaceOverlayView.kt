package uz.iskandarbek.facedetection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.face.Face

class FaceOverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private var faces: List<Face> = emptyList()

    fun setFaces(faces: List<Face>) {
        this.faces = faces
        invalidate()  // Repaint the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (face in faces) {
            // Yuz chegaralarini chizish
            val boundingBox = face.boundingBox
            canvas.drawRect(boundingBox, paint)
        }
    }
}
