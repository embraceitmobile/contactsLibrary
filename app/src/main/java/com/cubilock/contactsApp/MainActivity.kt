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
        val name = LibraryName("Muhammad", "Ali", "Haider")
        val email = LibraryEmail("ali@gmail.com", "ali@work.com", "ali@other.com")
        val libraryContactWorkInfo = LibraryContactWorkInfo("EmbraceIT", "Flutter Developer")
        val number = LibraryNumber("03348556301", "03348556300", "03348556302")
        val address = LibraryContactAddress("Main Street", "Mandra", "Punjab", "44000", "Pakistan")
        val phoneticName = LibraryName("Phonetic1", "Phonetic2", "phonetic3")

//        val conf = Bitmap.Config.ARGB_8888; // see other conf types
//        val bmp = Bitmap.createBitmap(100, 100, conf); // this creates a MUTABLE bitmap
//        val canvas =  Canvas(bmp)
//        val contact = Contact(name, phoneticName, number, email, workInfo, ContactPicture(bmp), address = address)
//        val result = ContactHelper.addContact(this, contact)
//        Log.e("MainActivity", "Result: $result")

//        val contactExist = ContactHelper.contactExists(this, "Bhatti Saab")
//        Log.e("MainActivity","contactExists: $contactExist")

        val list = ContactHelper.getContacts(this)
        Log.e("MainActivity", "list: $list")
    }
}