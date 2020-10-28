package com.starchee.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class SpeedometerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {
    private val paint = Paint(ANTI_ALIAS_FLAG)
    private var count = 7f
    private var size = 500

    private var firstZoneColor: Int? = null
    private var secondZoneColor: Int? = null
    private var thirdZoneColor: Int? = null
    private var speedometerHandColor: Int? = null
    private var borderColor: Int? = null
    private var degreeTextColor: Int? = null
    private var degreeTextSize: Float? = null


    companion object {
        const val SUPER_STATE = "super_state"
        const val COUNT_STATE_KEY = "count_state"
    }

    init {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.SpeedometerView,
            defStyleAttr,
            R.style.SpeedometerViewStyle
        )

        try {
            firstZoneColor =
                typedArray.getColor(R.styleable.SpeedometerView_firstZoneColor, Color.GREEN)
            secondZoneColor =
                typedArray.getColor(R.styleable.SpeedometerView_secondZoneColor, Color.YELLOW)
            thirdZoneColor =
                typedArray.getColor(R.styleable.SpeedometerView_thirdZoneColor, Color.RED)
            speedometerHandColor =
                typedArray.getColor(R.styleable.SpeedometerView_speedometerHandColor, Color.BLUE)
            borderColor =
                typedArray.getColor(R.styleable.SpeedometerView_borderColor, Color.BLUE)
            degreeTextColor =
                typedArray.getColor(R.styleable.SpeedometerView_degreeTextColor, Color.BLUE)
            degreeTextSize =
                typedArray.getDimension(R.styleable.SpeedometerView_degreeTextSize, 35f)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = measureDimension(widthMeasureSpec)
        val height = measureDimension(heightMeasureSpec)

        size = min(width, height)

        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = size / 2f

        drawSpeedometerSections(canvas)
        drawBorder(canvas, radius)
        drawSpeedometerDegree(canvas, radius)
        drawSpeedometerHand(canvas, radius)
    }

    override fun onSaveInstanceState(): Parcelable? =
        Bundle().apply {
            putFloat(COUNT_STATE_KEY, count)
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
        }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState = state

        if (state is Bundle) {
            count = state.getFloat(COUNT_STATE_KEY)
            superState = state.getParcelable(SUPER_STATE)
        }
        super.onRestoreInstanceState(superState)
    }

    fun speedUp() {
        if (count > 1) {
            count -= 0.1f
            invalidate()
        }
    }

    fun speedDown() {
        if (count < 7) {
            count += 0.1f
            invalidate()
        }
    }

    private fun measureDimension(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> specSize
            MeasureSpec.UNSPECIFIED -> size
            else -> size
        }
    }

    private fun drawSpeedometerSections(canvas: Canvas) {
        paint.color = firstZoneColor!!
        paint.style = Paint.Style.FILL
        val oval = RectF()

        oval.set(0f, 0f, size.toFloat(), size.toFloat())
        canvas.drawArc(oval, 135f, 90f, true, paint)

        paint.color = secondZoneColor!!
        paint.style = Paint.Style.FILL

        canvas.drawArc(oval, 225f, 90f, true, paint)

        paint.color = thirdZoneColor!!
        paint.style = Paint.Style.FILL
        canvas.drawArc(oval, 315f, 90f, true, paint)

        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawArc(oval, 405f, 90f, true, paint)
    }

    private fun drawBorder(canvas: Canvas, radius: Float) {
        paint.color = borderColor!!
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE

        canvas.drawPoint(radius, radius, paint)
        canvas.drawCircle(radius, radius, radius, paint)

        for (step in 2..14) {
            canvas.drawLine(
                radius + sin((step * PI / 8).toFloat()) * radius,
                radius + cos((step * PI / 8).toFloat()) * radius,
                radius + sin((step * PI / 8).toFloat()) * radius / 1.1f,
                radius + cos((step * PI / 8).toFloat()) * radius / 1.1f,
                paint
            )
        }
    }

    private fun drawSpeedometerHand(canvas: Canvas, radius: Float) {
        paint.color = speedometerHandColor!!
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 7f


        canvas.drawLine(
            radius,
            radius,
            radius + sin((count * PI / 4).toFloat()) * radius,
            radius + cos((count * PI / 4).toFloat()) * radius,
            paint
        )
    }

    private fun drawSpeedometerDegree(canvas: Canvas, radius: Float) {
        var speedValue = 260

        paint.color = degreeTextColor!!
        paint.style = Paint.Style.STROKE
        paint.textSize = degreeTextSize!!

        for (step in 2..14) {
            paint.strokeWidth = 5f
            speedValue -= 20
            if (speedValue < 120) {
                canvas.drawText(
                    "$speedValue",
                    radius - degreeTextSize!! / 1.5f + sin((step * PI / 8).toFloat()) * radius / 1.3f,
                    radius + degreeTextSize!! / 4 + cos((step * PI / 8).toFloat()) * radius / 1.3f,
                    paint
                )
            } else {
                canvas.drawText(
                    "$speedValue",
                    radius - degreeTextSize!! + sin((step * PI / 8).toFloat()) * radius / 1.3f,
                    radius + degreeTextSize!! / 4 + cos((step * PI / 8).toFloat()) * radius / 1.3f,
                    paint
                )
            }
        }
    }
}