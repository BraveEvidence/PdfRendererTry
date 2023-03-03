package com.example.androiddeleteme

import android.graphics.Bitmap

interface ReadQuraanInterface {

    fun downloadFile()
    fun downloadFinished(bitmap: Bitmap, swipe: Direction) //0 for none,1 for left ,2 for right
    fun downloadFailed(msg: String)

}

enum class Direction {
    Left, Right, None
}