package com.emotion.detector.activity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.mlkit.vision.face.Face

class FaceBox(
    overlay: FaceBoxOverlay,
    private val face: Face,
    private val imageRect: Rect,
    private val emotionLabel: String
) : FaceBoxOverlay.FaceBox(overlay) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 6.0f
        textSize = 150f  // Set the text size to make the label larger
        isAntiAlias = true  // Enable anti-aliasing for smoother text edges
    }

    override fun draw(canvas: Canvas?) {
        val rect = getBoxRect(
            imageRectWidth = imageRect.width().toFloat(),
            imageRectHeight = imageRect.height().toFloat(),
            faceBoundingBox = face.boundingBox
        )
        canvas?.drawRect(rect, paint)

        // Adjust the position of the text so it appears above the face box
        val textWidth = paint.measureText(emotionLabel)
        val xPos = rect.centerX() - textWidth / 2
        val yPos = rect.top - 10f  // Position the text above the bounding box

        // Draw the emotion label with the larger text size
        canvas?.drawText(emotionLabel, xPos, yPos, paint)
    }
}
