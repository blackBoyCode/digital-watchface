package com.example.ubuntu.digitalwatchface.model

import android.support.v4.content.res.ResourcesCompat

//no background image so resource id = 0
const val EMPTY_IMAGE_RESOURCE = 0

data class DigitalWatchFaceStyle(
        val watchFaceColors: WatchFaceColors,
        val watchFaceDimensions: WatchFaceDimensions,
        val watchFaceBackgroundImage: WatchFaceBackgroundImage

)


data class WatchFaceColors(
        //main color for clock text
        val main:Int,
        //background color
        val background:Int,
        // shadow beneath the clock text
        val shadow:Int
)

data class WatchFaceDimensions(
        //TODO remove the mainWidth as it doesn't apply to digital watch
        // main stroke width of the clock text Paint Object in pixel
        val mainWidth:Float,
        //Length in pixels of the shadow radius
        //(range from 0.0 to 1.0)
        val shadowRadius:Float// will use it with the paint Object "setShadowLayer"

)
// background image, 0 for no image

data class WatchFaceBackgroundImage(

        val backgroundImageResource:Int

){

    //a static final object to be use anywhere
    companion object {
        const val EMPTY_IMAGE_RESOURCE = 0
    }

}


//TODO should I add fonts as a data class in the DSL??
//data class WatchFaceFonts(val font_1:Int, val font_2:Int){
//
//
//
//}