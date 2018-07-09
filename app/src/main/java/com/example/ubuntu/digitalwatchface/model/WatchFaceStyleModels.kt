package com.example.ubuntu.digitalwatchface.model

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
        // main stroke width of the clock text Paint Object in pixel
        val mainWidth:Float,
        //Length in pixels of the shadow radius
        //(range from 0.0 to 1.0)
        val shadowRadius:Float



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