package com.example.firebasemlkit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.io.IOException
import java.io.InputStream
import java.security.AccessControlContext

fun getBitmap(context: Context) :Bitmap?{
    val assetManager = context.assets
    var istr: InputStream? = null
    var bitmap: Bitmap? = null
    try {
        istr = assetManager.open("liberty.jpg")
        bitmap = BitmapFactory.decodeStream(istr)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bitmap
}