package com.starchee.customview

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
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
import android.view.animation.LinearInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
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
    private var speedometerSize = 500
    private var padding = 20
    private var radius = (speedometerSize - padding*2) / 2f
    private var currentSpeed = 0

    private var currentWarningColor = Color.GREEN

    private var speedUpAnimatorSet: AnimatorSet? = null
    private var speedDownAnimatorSet: AnimatorSet? = null

    private var speedometerHandColor: Int? = null
    private var borderColor: Int? = null

    private var backgroundColor: Int? = null

    private var degreeTextColor: Int? = null
    private var degreeTextSize: Float? = null


    companion object {
        private const val SUPER_STATE = "super_state"
        private const val CURRENT_SPEED_STATE_KEY = "current_speed_state"
        private const val ACCELERATION_SPEED_IN_MS = 14000L
        private const val MAX_SPEED = 240
        private const val SPEED_FACTOR = ((-7 * PI / 4 + 1 * PI / 4) / MAX_SPEED).toFloat()
        private const val SPEED_SHIFT = 1 * PI.toFloat() / 4
    }

    init {
        initParams(context, attributeSet, defStyleAttr)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureDimension(widthMeasureSpec)
        val height = measureDimension(heightMeasureSpec)

        speedometerSize = min(width, height)

        setMeasuredDimension(speedometerSize, speedometerSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)
        drawSpeedometerWarning(canvas)
        drawBorder(canvas)
        drawDegreeText(canvas)
        drawSpeedometerHand(canvas)
    }

    override fun onSaveInstanceState(): Parcelable? =
        Bundle().apply {
            putInt(CURRENT_SPEED_STATE_KEY, currentSpeed)
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
        }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState = state

        if (state is Bundle) {
            currentSpeed = state.getInt(CURRENT_SPEED_STATE_KEY)
            superState = state.getParcelable(SUPER_STATE)
        }
        super.onRestoreInstanceState(superState)
    }

    fun start() {
        speedDownAnimatorSet?.cancel()
        initSpeedUpAnimatorSet()
        speedUpAnimatorSet?.start()

    }

    fun stop() {
        speedUpAnimatorSet?.cancel()
        initSpeedDownAnimatorSet()
        speedDownAnimatorSet?.start()
    }

    private fun initParams(
        context: Context,
        attributeSet: AttributeSet?,
        defStyleAttr: Int
    ) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.SpeedometerView,
            defStyleAttr,
            R.style.SpeedometerViewStyle
        )

        try {
            speedometerHandColor =
                typedArray.getColor(R.styleable.SpeedometerView_speedometerHandColor, Color.BLUE)
            borderColor =
                typedArray.getColor(R.styleable.SpeedometerView_borderColor, Color.BLUE)
            backgroundColor =
                typedArray.getColor(R.styleable.SpeedometerView_backgroundColor, Color.WHITE)

            degreeTextColor =
                typedArray.getColor(R.styleable.SpeedometerView_degreeTextColor, Color.BLUE)
            degreeTextSize =
                typedArray.getDimension(R.styleable.SpeedometerView_degreeTextSize, 35f)
        } finally {
            typedArray.recycle()
        }
    }

    private fun initSpeedUpAnimatorSet() {
        val speedUpAnimator = ValueAnimator.ofInt(currentSpeed, MAX_SPEED).apply {
            duration = ACCELERATION_SPEED_IN_MS
            interpolator = LinearOutSlowInInterpolator()
            addUpdateListener {
                currentSpeed = it.animatedValue as Int
                invalidate()
            }
        }

        val colorUpAnimator = ValueAnimator.ofInt(Color.GREEN, Color.RED).apply {
            setEvaluator(ArgbEvaluator())
            duration = ACCELERATION_SPEED_IN_MS
            interpolator = LinearInterpolator()
            addUpdateListener {
                currentWarningColor = it.animatedValue as Int
                invalidate()
            }
        }
        speedUpAnimatorSet = AnimatorSet()
        speedUpAnimatorSet?.play(speedUpAnimator)?.with(colorUpAnimator)

    }


    private fun initSpeedDownAnimatorSet() {
        val speedDownAnimator = ValueAnimator.ofInt(currentSpeed, 0).apply {
            duration = 2 * ACCELERATION_SPEED_IN_MS / MAX_SPEED * currentSpeed
            interpolator = LinearInterpolator()
            addUpdateListener {
                currentSpeed = it.animatedValue as Int
                invalidate()
            }
        }

        val colorDownAnimator = ValueAnimator.ofInt(currentWarningColor, Color.GREEN).apply {
            setEvaluator(ArgbEvaluator())
            duration = 2 * ACCELERATION_SPEED_IN_MS / MAX_SPEED * currentSpeed
            interpolator = LinearInterpolator()
            addUpdateListener {
                currentWarningColor = it.animatedValue as Int
                invalidate()
            }
        }
        speedDownAnimatorSet = AnimatorSet()
        speedDownAnimatorSet?.play(speedDownAnimator)?.with(colorDownAnimator)
    }

    private fun measureDimension(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> speedometerSize.coerceAtMost(specSize)
            MeasureSpec.UNSPECIFIED -> speedometerSize
            else -> speedometerSize
        }
    }

    private fun drawSpeedometerWarning(canvas: Canvas) {
        paint.reset()
        paint.color = currentWarningColor
        paint.style = Paint.Style.FILL
        val oval = RectF()

        oval.set(padding.toFloat(), padding.toFloat(), radius * 2f + padding, radius * 2f + padding)
        canvas.drawArc(oval, 135f, currentSpeed * -SPEED_FACTOR * 180 / PI.toFloat(), true, paint)
    }

    private fun drawBackground(canvas: Canvas) {
        paint.reset()
        paint.color = backgroundColor!!
        paint.style = Paint.Style.FILL

        canvas.drawCircle(radius + padding, radius + padding, radius, paint)
    }

    private fun drawBorder(canvas: Canvas) {
        paint.reset()
        paint.color = borderColor!!
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE

        canvas.drawPoint(radius + padding, radius + padding, paint)
        canvas.drawCircle(radius + padding, radius + padding, radius, paint)

        for (speed in 0..240 step 20) {

            canvas.drawLine(
                radius + padding + sin(speed * SPEED_FACTOR - SPEED_SHIFT) * radius,
                radius + padding + cos(speed * SPEED_FACTOR - SPEED_SHIFT) * radius,
                radius + padding + sin(speed * SPEED_FACTOR - SPEED_SHIFT) * radius / 1.1f,
                radius + padding + cos(speed * SPEED_FACTOR - SPEED_SHIFT) * radius / 1.1f,
                paint
            )
        }
    }

    private fun drawSpeedometerHand(canvas: Canvas) {
        paint.reset()
        paint.color = speedometerHandColor!!
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 7f


        canvas.drawLine(
            radius + padding,
            radius + padding,
            radius + padding + sin(currentSpeed * SPEED_FACTOR - SPEED_SHIFT) * radius,
            radius + padding + cos(currentSpeed * SPEED_FACTOR - SPEED_SHIFT) * radius,
            paint
        )
    }

    private fun drawDegreeText(canvas: Canvas) {
        paint.reset()
        paint.color = degreeTextColor!!
        paint.style = Paint.Style.STROKE
        paint.textSize = degreeTextSize!!
        paint.strokeWidth = 5f

        for (speed in 0..240 step 20) {
            if (speed < 120) {
                canvas.drawText(
                    "$speed",
                    radius + padding - degreeTextSize!! / 1.5f + sin(speed * SPEED_FACTOR - SPEED_SHIFT) * radius / 1.3f,
                    radius + padding + degreeTextSize!! / 4 + cos(speed * SPEED_FACTOR - SPEED_SHIFT) * radius / 1.3f,
                    paint
                )
            } else {
                canvas.drawText(
                    "$speed",
                    radius + padding - degreeTextSize!! + sin(speed * SPEED_FACTOR - SPEED_SHIFT) * radius / 1.3f,
                    radius + padding + degreeTextSize!! / 4 + cos(speed * SPEED_FACTOR - SPEED_SHIFT) * radius / 1.3f,
                    paint
                )
            }
        }
    }
}