package com.example.currencyrecognition.utils


// this package we have some of the global variables that are used across activities rather than creating it individually for every activity
// hence the companion object tag
class Constants {
    companion object{
        const val INTROTEXT = "Welcome to the Application for the visually impaired people. To detect the currency with high accuracy , place the note over a flat surface with good lighting and scan it using the camera."
        const val OPENDELAY = 12000L
        const val DESCRIPTION = "Steps to Follow\n" +
                "One: Click on the take Picture button in the bright yellow color then select yes to allow the app be able to use the camera\n" +
                "Two:Click the take picture button in the bright yellow color to open your camera and click the camera button to snap\n" +
                "Three: CLick ok after the image is captured then watch the magic"
        const val SPLASHDELAY = 6000L
    }
}