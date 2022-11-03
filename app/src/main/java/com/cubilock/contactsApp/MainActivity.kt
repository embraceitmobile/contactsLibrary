package com.cubilock.contactsApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.cubilock.contactsLibrary.helpers.ContactHelper
import com.cubilock.contactsLibrary.models.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val name = LibraryName("Zubair", "Rehman", "Dar", "Zubair Rehman Dar")
        val email = LibraryEmail("", "", "")
        val libraryContactWorkInfo = LibraryContactWorkInfo("EmbraceIT", "Flutter Developer")
        val number = LibraryNumber("", "0333333333", "")
        val contact  = LibraryContact(name = name, email = email, libraryContactWorkInfo = libraryContactWorkInfo, number = number, isFavorite = false)
//        val conf = Bitmap.Config.ARGB_8888; // see other conf types
//        val bmp = Bitmap.createBitmap(100, 100, conf); // this creates a MUTABLE bitmap
//        val canvas =  Canvas(bmp)
//        val contact = Contact(name, phoneticName, number, email, workInfo, ContactPicture(bmp), address = address)
        val result = ContactHelper.addContact(this, contact)
        Log.e("MainActivity", "Result: $result")

//        val contactExist = ContactHelper.contactExists(this, "Bhatti Saab")
//        Log.e("MainActivity","contactExists: $contactExist")

        val list = ContactHelper.getContacts(this)
        Log.e("MainActivity", "list: $list")
    }
}