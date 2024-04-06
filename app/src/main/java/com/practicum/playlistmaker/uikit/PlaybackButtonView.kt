package com.practicum.playlistmaker.uikit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.utils.LOG_TAG

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.playbackButtonStyle,
    @StyleRes defStyleRes: Int = R.style.DefaultPlaybackButtonStyle,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val minViewSize = resources.getDimensionPixelSize(R.dimen.playback_button_min_size)

    private var playing: Boolean = false

    private val playImageBitmap: Bitmap?
    private val pauseImageBitmap: Bitmap?
    private lateinit var imageRect: RectF

    private val paint = Paint().apply {
        colorFilter = PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP)
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playImageBitmap = getDrawable(R.styleable.PlaybackButtonView_playImageResId)?.toBitmap()
                pauseImageBitmap = getDrawable(R.styleable.PlaybackButtonView_pauseImageResId)?.toBitmap()
                paint.colorFilter =
                    PorterDuffColorFilter(getColor(R.styleable.PlaybackButtonView_fillColor, 0), PorterDuff.Mode.SRC_ATOP)

            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val contentWidth = when (val widthMode = MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> minViewSize
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> widthSize

            else -> Log.e(LOG_TAG, "Unknown widthMode $widthMode")
        }

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val contentHeight = when (val heightMode = MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> minViewSize
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> heightSize

            else -> Log.e(LOG_TAG, "Unknown heightMode $heightMode")
        }

        val size = Integer.min(contentWidth, contentHeight)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        if (playing) {
            if (pauseImageBitmap != null) {
                canvas.drawBitmap(pauseImageBitmap, null, imageRect, paint)
            }
        } else {
            if (playImageBitmap != null) {
                canvas.drawBitmap(playImageBitmap, null, imageRect, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (isEnabled) {
                    setPlaying(!playing)
                    performClick()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setPlaying(state: Boolean) {
        playing = state
        invalidate()
    }
}