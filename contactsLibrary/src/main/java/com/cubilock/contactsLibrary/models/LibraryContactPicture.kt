package com.cubilock.contactsLibrary.models

import android.graphics.Bitmap

data class LibraryContactPicture(
    var pictureBitmap: Bitmap? = null,
    var pictureUrl: String? = "",
    var uri: String? = ""
)
