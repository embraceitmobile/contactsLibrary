package com.cubilock.contactsLibrary.models

import android.graphics.Bitmap

data class ContactPicture(
    val pictureBitmap: Bitmap? = null,
    val pictureUrl: String? = ""
)
