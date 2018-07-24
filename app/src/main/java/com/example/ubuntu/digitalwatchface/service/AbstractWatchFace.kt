package com.example.ubuntu.digitalwatchface.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.view.SurfaceHolder
import android.view.WindowInsets
import android.widget.Toast
import com.example.ubuntu.digitalwatchface.R
import com.example.ubuntu.digitalwatchface.model.DigitalWatchFaceStyle

import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.TimeZone

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 *
 *
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */

private val NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

/**
 * Updates rate in milliseconds for interactive mode. We update once a second since seconds
 * are displayed in interactive mode.
 */
private const val INTERACTIVE_UPDATE_RATE_MS = 1000

/**
 * Handler message id for updating the time periodically in interactive mode.
 */
private const val MSG_UPDATE_TIME = 0


abstract class AbstractWatchFace: CanvasWatchFaceService() {

    private lateinit var customTypeface: Typeface

    //this will implements everything from our Models and the Dsl Builder "WatchFaceStyleDsl"
    //this will hold our default values and the values define in the Dsl Builder
    private lateinit var digitalWatchFaceStyle: DigitalWatchFaceStyle

    //we will need to override our digitalWatchFaceStyle from the Dsl to have our custom values
    //this will be called in our onCreate(SurfaceHolder) as an expression
    abstract fun getWatchFaceStyle(): DigitalWatchFaceStyle


    override fun onCreateEngine(): Engine {
        return Engine()
    }

    private class EngineHandler(reference: AbstractWatchFace.Engine) : Handler() {
        private val mWeakReference: WeakReference<AbstractWatchFace.Engine> = WeakReference(reference)

        override fun handleMessage(msg: Message) {
            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }

    inner class Engine : CanvasWatchFaceService.Engine() {

        private lateinit var mCalendar: Calendar

        private var mRegisteredTimeZoneReceiver = false

        private var mXOffset: Float = 0F
        private var mYOffset: Float = 0F

        //handy values to have
        private var centerX: Float = 0F
        private var centerY: Float = 0F


        private lateinit var mBackgroundPaint: Paint
        private lateinit var mTextPaint: Paint

        //our bitmap
        private lateinit var backgroundBitmap: Bitmap

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        private var mLowBitAmbient: Boolean = false
        private var mBurnInProtection: Boolean = false
        private var mAmbient: Boolean = false

        private val mUpdateTimeHandler: Handler = EngineHandler(this)

        private val mTimeZoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)

            digitalWatchFaceStyle = getWatchFaceStyle()


            setWatchFaceStyle(WatchFaceStyle.Builder(this@AbstractWatchFace)
                    .setAcceptsTapEvents(true)
                    .build())

            mCalendar = Calendar.getInstance()

            val resources = this@AbstractWatchFace.resources
            mYOffset = resources.getDimension(R.dimen.custom_digital_y_offset)

            // Initializes background.
            mBackgroundPaint = Paint().apply {
                color = ContextCompat.getColor( applicationContext, R.color.background)
            }

            // application context works in the onCreate but not outside of class...??
            customTypeface = ResourcesCompat.getFont(applicationContext, R.font.monoton_regular) as Typeface

            // Initializes Watch Face.
            mTextPaint = Paint().apply {
                typeface = customTypeface
                isAntiAlias = true
                color = digitalWatchFaceStyle.watchFaceColors.main //ContextCompat.getColor(applicationContext, R.color.digital_text)

                setShadowLayer(
                        digitalWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        digitalWatchFaceStyle.watchFaceColors.shadow
                )

                //added stroke width

                //TODO the two method doesn't work...WHY??
//                strokeWidth = digitalWatchFaceStyle.watchFaceDimensions.mainWidth
//                strokeCap = Paint.Cap.ROUND


            }


            initializeBackground()

        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)

            centerX = width / 2f
            centerY = height / 2f

            //arrange bitmap scaling value to adjust to screen
            val scale = width.toFloat()/backgroundBitmap.width.toFloat()

            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap,
                    (backgroundBitmap.width * scale).toInt(),
                    (backgroundBitmap.height * scale).toInt(), true)
        }

        private fun initializeBackground() {

            backgroundBitmap = BitmapFactory.decodeResource(
                    resources,
                    digitalWatchFaceStyle.watchFaceBackgroundImage.backgroundImageResource
            )

        }

        override fun onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        override fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)
            mLowBitAmbient = properties.getBoolean(
                    WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false)
            mBurnInProtection = properties.getBoolean(
                    WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false)
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }







        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            mAmbient = inAmbientMode

            if (mLowBitAmbient) {
                mTextPaint.isAntiAlias = !inAmbientMode
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer()
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            when (tapType) {
                WatchFaceService.TAP_TYPE_TOUCH -> {
                    // The user has started touching the screen.
                }
                WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                    // The user has started a different gesture or otherwise cancelled the tap.
                }
                WatchFaceService.TAP_TYPE_TAP ->
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(applicationContext, R.string.message, Toast.LENGTH_SHORT)
                            .show()
            }
            invalidate()
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            // Draw the background.
            if (mAmbient) {
                canvas.drawColor(Color.BLACK)
            } else {
                canvas.drawRect(
                        0f, 0f, bounds.width().toFloat(), bounds.height().toFloat(), mBackgroundPaint)

                //TESTING bitmap .....
                canvas.drawBitmap(backgroundBitmap,0f,0f, mBackgroundPaint)
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            val now = System.currentTimeMillis()
            mCalendar.timeInMillis = now

            val text = if (mAmbient)
                String.format("%d:%02d", mCalendar.get(Calendar.HOUR),
                        mCalendar.get(Calendar.MINUTE))
            else
                String.format("%d:%02d:%02d", mCalendar.get(Calendar.HOUR),
                        mCalendar.get(Calendar.MINUTE), mCalendar.get(Calendar.SECOND))
            //canvas.drawText(text, mXOffset, mYOffset, mTextPaint)


            if (mAmbient){

                mTextPaint.color = Color.WHITE
                mTextPaint.isAntiAlias = false

                canvas.drawText(text, mXOffset, mYOffset, mTextPaint)

                canvas.drawText("d:${mCalendar.get(Calendar.DATE)}" +
                        " m:${mCalendar.get(Calendar.MONTH) + 1 }" +//January = 0
                        " y:${mCalendar.get(Calendar.YEAR)}",mXOffset-80f,mYOffset-40f,mTextPaint)

            }else{
                mTextPaint.color = digitalWatchFaceStyle.watchFaceColors.main
                mTextPaint.isAntiAlias = true

                canvas.drawText(text, mXOffset, mYOffset, mTextPaint)
                canvas.drawText("d:${mCalendar.get(Calendar.DATE)}" +
                        " m:${mCalendar.get(Calendar.MONTH) + 1 }" +//January = 0
                        " y:${mCalendar.get(Calendar.YEAR)}",mXOffset-80f,mYOffset-40f,mTextPaint)
            }

        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()

                // Update time zone in case it changed while we weren't visible.
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            } else {
                unregisterReceiver()
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer()
        }

        private fun registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@AbstractWatchFace.registerReceiver(mTimeZoneReceiver, filter)
        }

        private fun unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = false
            this@AbstractWatchFace.unregisterReceiver(mTimeZoneReceiver)
        }

        override fun onApplyWindowInsets(insets: WindowInsets) {
            super.onApplyWindowInsets(insets)

            // Load resources that have alternate values for round watches.
            val resources = this@AbstractWatchFace.resources
            val isRound = insets.isRound
            mXOffset = resources.getDimension(
                    if (isRound)
                        R.dimen.digital_x_offset_round
                    else
                        R.dimen.custom_digital_x_offset
            )

            val textSize = resources.getDimension(
                    if (isRound)
                        R.dimen.digital_text_size_round
                    else
                        R.dimen.custom_digital_text_size //R.id.digital_text_size
            )

            mTextPaint.textSize = textSize
        }

        /**
         * Starts the [.mUpdateTimeHandler] timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private fun updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Returns whether the [.mUpdateTimeHandler] timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !isInAmbientMode
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }
    }


}
