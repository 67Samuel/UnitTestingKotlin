package com.example.unittestingkotlin.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText


class LinedEditText(context: Context?, attrs: AttributeSet?) :
    AppCompatEditText(context!!, attrs) {
    private val mRect: Rect
    private val mPaint: Paint
    override fun onDraw(canvas: Canvas) {

        // get the height of the view
        val height = (this.parent as View).height
        val lineHeight = lineHeight
        val numberOfLines = height / lineHeight
        val r = mRect
        val paint = mPaint
        var baseline = getLineBounds(0, r)
        for (i in 0 until numberOfLines) {
            canvas.drawLine(r.left.toFloat(),
                (baseline + 1).toFloat(),
                r.right.toFloat(),
                (baseline + 1).toFloat(),
                paint)
            baseline += lineHeight
        }
        super.onDraw(canvas)
    }

    companion object {
        private const val TAG = "LinedEditText"
    }

    // we need this constructor for LayoutInflater
    init {
        mRect = Rect()
        mPaint = Paint()
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 2f
        mPaint.color = -0x269a // Color of the lines on paper
    }
}