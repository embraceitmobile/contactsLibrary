package com.cubilock.contactsLibrary.helpers

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.*
import android.provider.ContactsContract.PhoneLookup
import android.util.Log
import com.cubilock.contactsLibrary.models.*
import com.cubilock.contactsLibrary.models.Email
import com.cubilock.contactsLibrary.models.Number
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


object ContactHelper {

    fun addContact(context: Context, contact: Contact): Boolean{
        val ops = ArrayList<ContentProviderOperation>()

        ops.add(ContentProviderOperation.newInsert(
            ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            .build())

        //------------------------------------------------------ Names

        contact.name?.let { name ->
            if(!name.displayName.isNullOrBlank()) {
                ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.DISPLAY_NAME, name.displayName).build())
            }
            if(name.firstName != null ||  name.middleName != null || name.lastName != null) {
                ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.GIVEN_NAME, contact.name.firstName)// contact.name?.displayName
                    .withValue(StructuredName.MIDDLE_NAME, contact.name.middleName)// contact.name?.displayName
                    .withValue(StructuredName.FAMILY_NAME, contact.name.lastName)
                    .build())
            }
        }

        //------------------------------------------------------ Phonetic Names

        contact.phoneticName?.let { name ->
            if(!name.displayName.isNullOrBlank()) {
                ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.PHONETIC_NAME, name.displayName).build())
            }
            if(name.firstName != null ||  name.middleName != null || name.lastName != null) {
                ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.PHONETIC_GIVEN_NAME, name.firstName)
                    .withValue(StructuredName.PHONETIC_MIDDLE_NAME, name.middleName)
                    .withValue(StructuredName.PHONETIC_FAMILY_NAME, name.lastName)
                    .build())
            }
        }

        //------------------------------------------------------ Mobile Number

        contact.number?.let {
            if(!it.home.isNullOrBlank()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, it.home)
                    .withValue(Phone.TYPE,
                        Phone.TYPE_MOBILE)
                    .build())
            }
            if(!it.work.isNullOrEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, it.work)
                    .withValue(Phone.TYPE,
                        Phone.TYPE_WORK)
                    .build())
            }

            if(!it.other.isNullOrEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, it.other)
                    .withValue(Phone.TYPE,
                        Phone.TYPE_OTHER)
                    .build())
            }
        }


        //------------------------------------------------------ Email
        contact.email?.let {
            if(!it.home.isNullOrBlank()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, it.home)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                    .build())
            }
            if(!it.work.isNullOrBlank()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, it.work)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build())
            }
            if(!it.other.isNullOrBlank()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, it.other)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_OTHER)
                    .build())
            }
        }


        //------------------------------------------------------ Organization

        contact.workInfo?.let {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                    Organization.CONTENT_ITEM_TYPE)
                .withValue(Organization.COMPANY, it.company)
                .withValue(Organization.TYPE,
                    Organization.TYPE_WORK)
                .withValue(Organization.TITLE, it.jobTitle)
                .withValue(Organization.TYPE,
                    Organization.TYPE_WORK)
                .build())
        }

        //------------------------------------------------------ Address

        contact.address?.let { address ->
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                    StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(StructuredPostal.CITY, address.city)
                .withValue(StructuredPostal.STREET, address.street)
                .withValue(StructuredPostal.POSTCODE, address.postcode)
                .withValue(StructuredPostal.REGION, address.state)
                .withValue(StructuredPostal.COUNTRY, address.country)
                .build())
        }

        //------------------------------------------------------ Photo

        contact.profilePicture?.let { profilePicture ->
            val stream = ByteArrayOutputStream()
            if(profilePicture.pictureBitmap != null) {
                profilePicture.pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
                    .withValue(Photo.PHOTO, stream.toByteArray())
                    .build())
            }
            try {
                stream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Asking the Contact provider to create a new contact

        try {
            val results = context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            Log.d("ContactHelper", "$results")
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }


    @SuppressLint("Range")
    fun getContacts(ctx: Context): List<Contact>? {
        val list: MutableList<Contact> = ArrayList()
        val contentResolver = ctx.contentResolver
        val cursor =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val name  = getNameFromContact(cursor, ContactsContract.Contacts.DISPLAY_NAME)
//                    Log.e("name", "$name")
                    val phoneticName  = getNameFromContact(cursor, ContactsContract.Contacts.PHONETIC_NAME)
//                    Log.e("phoneticName", "$phoneticName")
                    val number = getNumberFromContact(contentResolver, id)
                    val email = getEmailFromContact(contentResolver, id)
                    val address = getAddressFromContact(contentResolver, id)
                    val workInfo = getWorkInfoFromContact(contentResolver, id)
                    val profilePicture = getPictureFromContact(contentResolver, id)
                    val contact = Contact(
                        name = name,
                        phoneticName = phoneticName,
                        number = number,
                        email = email,
                        address = address,
                        workInfo = workInfo,
                        profilePicture = profilePicture
                    )
                    list.add(contact)
                }
            }
            cursor.close()
        }
        return list
    }

    private fun getPictureFromContact(contentResolver: ContentResolver, id: String): ContactPicture {
        val inputStream: InputStream? =
            ContactsContract.Contacts.openContactPhotoInputStream(contentResolver,
                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong()))
        val person =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
        val pURI = Uri.withAppendedPath(person,
            ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
        var photo: Bitmap? = null
        if (inputStream != null) {
            photo = BitmapFactory.decodeStream(inputStream)
        }

        return ContactPicture(photo)
    }

    @SuppressLint("Range")
    private fun getNameFromContact(cursor: Cursor, nameType: String): Name {
        val name = cursor.getString(cursor.getColumnIndex(
            nameType))
        if(name.isNullOrBlank()){
            return Name()
        }
        val splittedName = name.split(" ")
        if(splittedName.isNullOrEmpty()){
            return Name()
        } else {
            when (splittedName.size) {
                3 -> {
                    return Name(firstName = splittedName[0], middleName = splittedName[1], lastName = splittedName[2])
                }
                2 -> {
                    return Name(firstName = splittedName[0], lastName = splittedName[1])
                }
                1 -> {
                    return Name(displayName = splittedName[0])
                }
            }
        }
        return Name()
    }

    @SuppressLint("Range")
    private fun getNumberFromContact(contentResolver: ContentResolver, id: String): Number {
        var homeContact: String? = ""
        var workContact: String? = ""
        var otherContact: String? = ""

        val cursorInfo =
            contentResolver.query(Phone.CONTENT_URI,
                null,
                Phone.CONTACT_ID + " = ?",
                arrayOf(id),
                null)

        while (cursorInfo!!.moveToNext()) {
            val phoneNumber = cursorInfo.getString(cursorInfo.getColumnIndex(Phone.NUMBER))
            try {
                when (cursorInfo.getInt(cursorInfo.getColumnIndex(Phone.TYPE))) {
                    Phone.TYPE_MOBILE -> {
                        homeContact = phoneNumber
                    }
                    Phone.TYPE_WORK -> {
                        workContact = phoneNumber
                    }
                    Phone.TYPE_OTHER -> {
                        otherContact = phoneNumber
                    }
                }
            } catch (ex:Exception){
                homeContact = phoneNumber
            }
        }
        cursorInfo.close()
        return Number(homeContact, workContact, otherContact)
    }

    @SuppressLint("Range")
    private fun getEmailFromContact(contentResolver: ContentResolver, id: String): Email {
        var homeEmail: String? = ""
        var workEmail: String? = ""
        var otherEmail: String? = ""

        val emailCursor =
            contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                arrayOf(id),
                null)

        while (emailCursor!!.moveToNext()) {
            var emailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
//            Log.e("getEmailFromContact", "$emailAddress")
            try {
                when (emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE))) {
                    Phone.TYPE_MOBILE -> {
                        homeEmail = emailAddress
                    }
                    Phone.TYPE_WORK -> {
                        workEmail = emailAddress
                    }
                    Phone.TYPE_OTHER -> {
                        otherEmail = emailAddress
                    }
                }
            } catch (ex:Exception){
                homeEmail = emailAddress
            }
        }
        emailCursor.close()
        return Email(homeEmail,workEmail,otherEmail)
    }

    @SuppressLint("Range")
    private fun getAddressFromContact(contentResolver: ContentResolver, id: String): Address{

        var where = StructuredPostal.CONTACT_ID + " = " + id
        val addressCursor = contentResolver.query(
            StructuredPostal.CONTENT_URI,
            null,
            where,
            null,
            null
        )
        var street  =""
        var city  =""
        var state  =""
        var country  =""
        var postCode  =""
        while (addressCursor!!.moveToNext()) {
            street = addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.STREET)) ?: ""
            city = addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.CITY)) ?: ""
            state = addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.REGION)) ?: ""
            country = addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.COUNTRY)) ?: ""
            postCode = addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.POSTCODE)) ?: ""
        }
        addressCursor.close()
        return Address(street, city, state, postCode, country)
    }

    @SuppressLint("Range")
    private fun getWorkInfoFromContact(contentResolver: ContentResolver, id: String): WorkInfo {
        val workInfoCursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
            arrayOf(id, Organization.CONTENT_ITEM_TYPE),
            null
        )
        var companyName  = ""
        var jobTitle  = ""
        while (workInfoCursor!!.moveToNext()){
            companyName = workInfoCursor.getString(workInfoCursor.getColumnIndex(Organization.COMPANY)) ?: ""
            jobTitle = workInfoCursor.getString(workInfoCursor.getColumnIndex(Organization.TITLE)) ?: ""
        }
        return WorkInfo(companyName, jobTitle)
    }

    fun getContactByPhoneNumber(ctx: Context, phoneNumber: String?): Contact? {
        var id: String? = null
        val contentResolver = ctx.contentResolver
        if (phoneNumber != null && phoneNumber.isNotEmpty()) {

            val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
            val projection = arrayOf(PhoneLookup._ID)
            val cursor = contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    id = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup._ID))
                }
                cursor.close()

                if(id != null) {
                    val name = getNameFromContact(cursor, ContactsContract.Contacts.DISPLAY_NAME)
                    val phoneticName =
                        getNameFromContact(cursor, ContactsContract.Contacts.PHONETIC_NAME)
                    val number = getNumberFromContact(contentResolver, id)
                    val email = getEmailFromContact(contentResolver, id)
                    val address = getAddressFromContact(contentResolver, id)
                    val workInfo = getWorkInfoFromContact(contentResolver, id)
                    val profilePicture = getPictureFromContact(contentResolver, id)
                    return Contact(
                        name = name,
                        phoneticName = phoneticName,
                        number = number,
                        email = email,
                        address = address,
                        workInfo = workInfo,
                        profilePicture = profilePicture
                    )
                }
            }
        }
        return null
    }

    @SuppressLint("Range")
    fun deleteContactByName(context: Context, displayName: String): Boolean {
        val cr = context.contentResolver
        val cur: Cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)!!
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equals(displayName, ignoreCase = true)) {
                        val lookupKey: String =
                            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                        val uri: Uri =
                            Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                                lookupKey)
                        cr.delete(uri, null, null)
                        return true
                    }
                } while (cur.moveToNext())
            }
        } catch (e: Exception) {
            println(e.stackTrace)
        } finally {
            cur.close()
        }
        return false
    }

    @SuppressLint("Range")
    fun deleteContactByNumber(context: Context, phone: String?): Boolean {
        val cr = context.contentResolver
        val contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone))
        val cur = cr.query(contactUri, null, null, null, null)
        try {
            if (cur!!.moveToFirst()) {
                do {
                    val lookupKey =
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                    val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                        lookupKey)
                    cr.delete(uri, null, null)
                        return true
                } while (cur.moveToNext())
            }
        } catch (e: java.lang.Exception) {
            println(e.stackTrace)
        } finally {
            cur!!.close()
        }
        return false
    }

    @SuppressLint("Range")
    fun deleteContactById(context: Context, id: String) {
        val cr = context.contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null)
        cur?.let {
            try {
                if (it.moveToFirst()) {
                    do {
                        if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID)) == id) {
                            val lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                            val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey)
                            cr.delete(uri, null, null)
                            break
                        }

                    } while (it.moveToNext())
                }

            } catch (e: Exception) {
                println(e.stackTrace)
            } finally {
                it.close()
            }
        }
    }

    fun contactExists(context: Context, name: String?): Boolean {
        val lookupUri = Uri.withAppendedPath(
            PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(name))
        val mPhoneNumberProjection = arrayOf(PhoneLookup._ID,
            PhoneLookup.NUMBER,
            PhoneLookup.DISPLAY_NAME
        )
        val cur = context.contentResolver.query(lookupUri, mPhoneNumberProjection, null, null, null)
        try {
            if (cur!!.moveToFirst()) {
                return true
            }
        } finally {
            cur?.close()
        }
        return false
    }
}