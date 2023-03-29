package com.cubilock.contactsApp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.cubilock.contactsLibrary.helpers.ContactHelper
import com.cubilock.contactsLibrary.logs.helper.LogsHelper
import com.cubilock.contactsLibrary.models.*
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*checking contacts saving region*/
//        val idsList = mutableListOf<Long>()
        /* val homeNumber = LibraryNumber("03262233433", CategoryType.HOME.value)
         val mobileNumber = LibraryNumber("03457693743", CategoryType.MOBILE.value)
         val workNumber = LibraryNumber("034937402232", CategoryType.WORK.value)
         val otherNumber = LibraryNumber("0323430243", CategoryType.OTHER.value)
         val numbers = listOf(workNumber, mobileNumber, otherNumber, homeNumber)

         val homeEmail = LibraryEmail("zee@zee.zee", CategoryType.HOME.value)
         val mobileEmail = LibraryEmail("zeed@zeed.zeed", CategoryType.MOBILE.value)
         val workEmail = LibraryEmail("zeef@zeef.zeef", CategoryType.WORK.value)
         val otherEmail = LibraryEmail("zeeg@zeeg.zeeg", CategoryType.OTHER.value)
         val emails = listOf(workEmail, mobileEmail, otherEmail, homeEmail)*/

        /*val contacts = ContactHelper.getContacts(this@MainActivity, false)

        if (contacts != null) {
            for(contact in contacts){
                if (contact.numbers.contains(LibraryNumber("15555", "mobile"))){
                     val numbers = listOf<LibraryNumber>(LibraryNumber("0320 5801273", "mobile"),LibraryNumber("03455916449", "work"),LibraryNumber("03185322814", "home"))
                    contact.numbers = numbers
                    ContactHelper.updateContact(this@MainActivity, contact, "1555")
                    Log.e("ContactLib", "contact name after update = "+contact?.numbers)
                }
            }
        }*/
        val dateFrom = SimpleDateFormat("dd-MM-yyyy").parse("09-01-2023")
        val dateTo = SimpleDateFormat("dd-MM-yyyy").parse("14-01-2023")
        LogsHelper.removeLogsByDate(
            this@MainActivity,
            dateFrom.time.toString(),
            dateTo.time.toString(),
            {})


//
//        for(i in 0..0) {
//            val name = LibraryName("Asad", middleName = "Bhai",)
//////            val email = LibraryEmail("", CategoryType.MOBILE.value,)
//            val libraryContactWorkInfo = LibraryContactWorkInfo("EmbraceIT", "Flutter Developer")
//////            val number = LibraryNumber("033333333", CategoryType.MOBILE.value)
//            val address = LibraryContactAddress("234", "city", "state", "postcode", "country")
//            val contact  = LibraryContact(name = name, emails = emails, libraryContactWorkInfo = libraryContactWorkInfo, numbers = numbers,  address = address, isFavorite = false, notes = "sheep")
//            val result  = ContactHelper.addContact(this, contact)
//        Log.e("MainActivity", "contactId: $result")


//        val conf = Bitmap.Config.ARGB_8888; // see other conf types
//        val bmp = Bitmap.createBitmap(100, 100, conf); // this creates a MUTABLE bitmap
//        val canvas =  Canvas(bmp)
//        val contact = Contact(name, phoneticName, number, email, workInfo, ContactPicture(bmp), address = address)
//            val result = ContactHelper.addContact(this, contact)
//            if(result != null) {
//                idsList.add(result)
//            }
//            Log.e("MainActivity", "Added contact Id: $result")
//        }

        /* get contact by Number */
//        val numbersToUpdate = listOf(LibraryNumber("365", CategoryType.MOBILE.value))
//        val contactToUpdate = LibraryContact(id = "19", name = LibraryName(displayName = "Test 21", firstName = "Test", lastName = "21"), numbers = numbersToUpdate)
//        val result = ContactHelper.getContactByPhoneNumber(this, "03431420420")
//        Log.e("MainActivity", "getContactByPhoneNumber: $result")

        /* update contact region */
//        val numbersToUpdate = listOf(LibraryNumber("365", CategoryType.MOBILE.value))
//        val contactToUpdate = LibraryContact(id = "19", name = LibraryName(displayName = "Test 21", firstName = "Test", lastName = "21"), numbers = numbersToUpdate)
//        val result = ContactHelper.updateContact(this, contactToUpdate)
//        Log.e("MainActivity", "contactId: $result")


        /* checking the delete contact region */
//        for(i in 2..3) {
//
//            val deleteResult = ContactHelper.deleteContactUsingId(this,"${idsList.get(i)}")
//            Log.e("MainActivity", "Deleted Contact: $deleteResult with id ${idsList.get(i)}")
//        }


        /* checking contact exists region */
//        val contactExist = ContactHelper.contactExists(this, "Bhatti Saab")
//        Log.e("MainActivity","contactExists: $contactExist")

        /* Getting contact list region */
        /*Log.e("MainActivity", "before Loading")
        val list = ContactHelper.getContacts(this)
        Log.e("MainActivity", "After loading list: ${list}")*/

        /* Getting single contact region*/
//        Log.e("MainActivity", "Before getting Single Contact")
//        val result = ContactHelper.getContactById(this, "1200")
//        val result = ContactHelper.getOrganizationDetails(this, "1200")
//        val result = ContactHelper.getOrganizationDetails(this, "30827")
//        Log.e("MainActivity", "Single Contact: $result")

        /* Getting picture of contact region*/
//        val result = ContactHelper.getPictureForContact(this, "6")
//        Log.e("MainActivity", "Single Contact: $result")


        callLogs()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun callLogs() {

        Log.e("MainActivity", "before callLogs")
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, -5)
        val previousDate = System.currentTimeMillis() - 5 * (24 * 60 * 60 * 1000)

        calendar.timeInMillis

        val map = mapOf(
            Pair("number","03431420420"),
            Pair("dateFrom", "$previousDate"),
            Pair("dateTo", "${System.currentTimeMillis()}")
        )
        val logs = LogsHelper.getSelectiveLogs(this, map)
        Log.e("MainActivity", "callLogs $logs")
    }
}

fun String.toBitmap(): Bitmap? {
    return try {
        val url = URL("$this")
        BitmapFactory.decodeStream(url.openConnection().getInputStream())
    } catch (e: IOException) {
        System.out.println(e)
        null
    }
}