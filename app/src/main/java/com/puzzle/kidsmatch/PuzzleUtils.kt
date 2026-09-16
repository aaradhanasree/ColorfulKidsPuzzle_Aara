package com.puzzle.kidsmatch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileOutputStream

data class PuzzleTile(
    val originalIndex: Int,
    val image: ImageBitmap
)

object PuzzleUtils {

    fun saveCustomImage(context: Context, uri: Uri): File {
        val targetFile = File(context.filesDir, "custom_puzzle.png")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        return targetFile
    }

    fun loadBitmap(context: Context): Bitmap {
        val customFile = File(context.filesDir, "custom_puzzle.png")
        return if (customFile.exists()) {
            BitmapFactory.decodeFile(customFile.absolutePath)
        } else {
            createDefaultBitmap()
        }
    }

    private fun createDefaultBitmap(): Bitmap {
        val width = 600
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val bgPaint = Paint().apply { color = AndroidColor.parseColor("#FFD54F") }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val circlePaint = Paint().apply {
            color = AndroidColor.parseColor("#FF5252")
            isAntiAlias = true
        }
        canvas.drawCircle(300f, 320f, 180f, circlePaint)

        val eyePaint = Paint().apply {
            color = AndroidColor.WHITE
            isAntiAlias = true
        }
        canvas.drawCircle(230f, 270f, 40f, eyePaint)
        canvas.drawCircle(370f, 270f, 40f, eyePaint)

        val pupilPaint = Paint().apply {
            color = AndroidColor.parseColor("#212121")
            isAntiAlias = true
        }
        canvas.drawCircle(230f, 280f, 20f, pupilPaint)
        canvas.drawCircle(370f, 280f, 20f, pupilPaint)

        val smilePaint = Paint().apply {
            color = AndroidColor.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 24f
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
        canvas.drawArc(190f, 240f, 410f, 420f, 35f, 110f, false, smilePaint)

        val textPaint = Paint().apply {
            color = AndroidColor.parseColor("#3F51B5")
            textSize = 64f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Let's Play!", 300f, 620f, textPaint)

        return bitmap
    }

    fun sliceBitmap(source: Bitmap, rows: Int, cols: Int): List<PuzzleTile> {
        val tiles = mutableListOf<PuzzleTile>()
        val tileWidth = source.width / cols
        val tileHeight = source.height / rows

        var index = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val piece = Bitmap.createBitmap(
                    source,
                    c * tileWidth,
                    r * tileHeight,
                    tileWidth,
                    tileHeight
                )
                tiles.add(PuzzleTile(originalIndex = index++, image = piece.asImageBitmap()))
            }
        }
        return tiles
    }
}
