package com.example.totalrecall

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.solver.widgets.Rectangle
import androidx.core.content.ContextCompat

class TagView(context: Context) : View(context) {
    private var tagText: String = "Null"
    private var mColor: Int = R.color.purple_500
    private var r = Rect()
    private val rectPaint = Paint()
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 10F
    }

    /*init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TagView,
            0, 0).apply {
            try {
                tagText = getString(R.styleable.TagView_tagText)!!
                mColor = getColor(R.styleable.TagView_color, ContextCompat.getColor(context, R.color.purple_500))
            } finally {
                recycle()
            }
        }
    }*/

    fun setTagText(s: String) {
        tagText = s
    }

    fun setColor(c: Int) {
        mColor = c
    }

    @Override
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            drawText(tagText, 0F, 0F, textPaint)
        }

    }

    @Override
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        var xpad = (paddingLeft + paddingRight).toFloat()
        val ypad = (paddingTop + paddingBottom).toFloat()

        val ww = w.toFloat() - xpad
        val hh = h.toFloat() - ypad

    }
}