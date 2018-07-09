package com.example.ubuntu.digitalwatchface.service

import android.graphics.Color
import com.example.ubuntu.digitalwatchface.model.WatchFaceColors


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