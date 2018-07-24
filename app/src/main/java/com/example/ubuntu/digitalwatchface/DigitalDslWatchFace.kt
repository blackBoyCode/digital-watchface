package com.example.ubuntu.digitalwatchface

import android.graphics.Color
import com.example.ubuntu.digitalwatchface.model.DigitalWatchFaceStyle
import com.example.ubuntu.digitalwatchface.service.AbstractWatchFace
import com.example.ubuntu.digitalwatchface.service.digitalWatchFaceStyle

//those "()" mean it's extending a class not a interface
class DigitalDslWatchFace : AbstractWatchFace() {

    override fun getWatchFaceStyle(): DigitalWatchFaceStyle {





        //TODO the DSL could have fonts to define...?
        return digitalWatchFaceStyle {

            watchFaceColors {
                main = Color.BLUE

                shadow = Color.WHITE
            }

            watchFaceDimensions {
                mainWidth = 30f

                shadowRadius = 5f
            }

            watchFaceBackgroundImage {

                backgroundImageResource = R.drawable.download
            }



        }

    }
}