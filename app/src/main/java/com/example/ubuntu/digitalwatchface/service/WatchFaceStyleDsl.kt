package com.example.ubuntu.digitalwatchface.service

import android.graphics.Color
import com.example.ubuntu.digitalwatchface.model.DigitalWatchFaceStyle
import com.example.ubuntu.digitalwatchface.model.WatchFaceBackgroundImage
import com.example.ubuntu.digitalwatchface.model.WatchFaceColors
import com.example.ubuntu.digitalwatchface.model.WatchFaceDimensions


/**
 * this creates the watch face style DSL so developers can customize the watchface
 * without dealing with complexe implementation
 */

// a class that set default value for WatchFaceColors
class WatchFaceColorsBuilder{

    //TODO I need to better understand this piece of Code a bit better
    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(
        "main" to Color.WHITE,
        "background" to Color.RED,
        "shadow" to Color.BLACK
    )

    //TODO does attributesMaps hold our values? if so how?
    var main:Int by attributesMap
    var background:Int by attributesMap
    var shadow:Int by attributesMap


    // the build function add new value assign by mutableMap
    fun build(): WatchFaceColors {
        return WatchFaceColors(
                main, background, shadow
        )
    }




}

//TODO this class has no constructor When did it initialise the values?
class WatchFaceDimensionsBuilder{

    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(
            "mainWidth" to 5f,
            "shadowRadius" to 2f


    )

    var mainWidth:Float by attributesMap
    var shadowRadius:Float by attributesMap


    fun build(): WatchFaceDimensions {

        return WatchFaceDimensions(mainWidth, shadowRadius)
    }

}


class WatchFaceBackgroundImageBuilder {


    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(

            "backgroundImageResource" to WatchFaceBackgroundImage.EMPTY_IMAGE_RESOURCE

    )

    var backgroundImageResource:Int by attributesMap

    fun build(): WatchFaceBackgroundImage{

        return WatchFaceBackgroundImage(backgroundImageResource)
    }


}


// build the watchface  using all three classes
// (watchFaceColors,watchFaceDimensions,watchFaceBackgroundImage)

class DigitalWatchFaceStyleBuilder {

    //TODO could we use lateinit intead of instantiate to null?
    private var watchFaceColors: WatchFaceColors? = null
    private var watchFaceDimensions: WatchFaceDimensions? = null
    private var watchFaceBackgroundImage: WatchFaceBackgroundImage =
            WatchFaceBackgroundImageBuilder().build()

    //TODO part of the code that I need to understand better

    fun watchFaceColors(setup: WatchFaceColorsBuilder.() -> Unit) {

        val watchFaceColorsBuilder = WatchFaceColorsBuilder()
        watchFaceColorsBuilder.setup()
        watchFaceColors = watchFaceColorsBuilder.build()

    }

    fun watchFaceDimensions(setup: WatchFaceDimensionsBuilder.() -> Unit) {

        val digitalWatchFaceDimensionsBuilder = WatchFaceDimensionsBuilder()
        digitalWatchFaceDimensionsBuilder.setup()
        watchFaceDimensions = digitalWatchFaceDimensionsBuilder.build()

    }

    fun watchFaceBackgroundImage(setup: WatchFaceBackgroundImageBuilder.() -> Unit) {

        val digitalWatchFaceBackgroundImageBuilder = WatchFaceBackgroundImageBuilder()
        digitalWatchFaceBackgroundImageBuilder.setup()
        watchFaceBackgroundImage = digitalWatchFaceBackgroundImageBuilder.build()
    }


    fun build(): DigitalWatchFaceStyle {

        return DigitalWatchFaceStyle(
                watchFaceColors ?:
                throw InstantiationException("Must define watch face styles in DSL."),
                watchFaceDimensions ?:
                throw InstantiationException("Must define watch face dimensions in DSL."),
                watchFaceBackgroundImage
        )

    }


    //TODO need to understand this better (supress, deprecated)
    /**
     * This method shadows the [digitalWatchFaceStyle] method when inside the scope
     * of a [DigitalWatchFaceStyleBuilder], so that watch faces can't be nested.
     */
    @Suppress("UNUSED_PARAMETER")
    @Deprecated(level = DeprecationLevel.ERROR, message = "WatchFaceStyles can't be nested.")
    fun analogWatchFaceStyle(param: () -> Unit = {}) {
    }


}

// this will be called to build our WatchFaceStyle(Colors,Dimensions,Background)
fun digitalWatchFaceStyle (setup: DigitalWatchFaceStyleBuilder.() -> Unit): DigitalWatchFaceStyle {

    val digitalWatchFaceStyleBuilder = DigitalWatchFaceStyleBuilder()

    digitalWatchFaceStyleBuilder.setup()

    return digitalWatchFaceStyleBuilder.build()

}
