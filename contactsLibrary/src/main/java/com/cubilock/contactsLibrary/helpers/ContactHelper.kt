package com.cubilock.contactsLibrary.helpers

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.RemoteException
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.*
import android.provider.ContactsContract.PhoneLookup
import android.util.Log
import com.cubilock.contactsLibrary.mapper.toEmailCategoryType
import com.cubilock.contactsLibrary.mapper.toEmailType
import com.cubilock.contactsLibrary.mapper.toPhoneCategoryType
import com.cubilock.contactsLibrary.mapper.toPhoneType
import com.cubilock.contactsLibrary.models.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


object ContactHelper {

    @SuppressLint("MissingPermission")
    fun addContact(context: Context, contact: LibraryContact): Long?{
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
                    .withValue(StructuredName.GIVEN_NAME, contact.name?.firstName)
                    .withValue(StructuredName.MIDDLE_NAME, contact.name?.middleName)
                    .withValue(StructuredName.FAMILY_NAME, contact.name?.lastName)
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

        contact.numbers.let {
            contact.numbers.forEach{item ->
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, item.number ?: "")
                    .withValue(Phone.TYPE, item.category?.toPhoneType() ?: Phone.TYPE_MOBILE)
                    .build())
            }
//            if(!it.home.isNullOrBlank()) {
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                        Phone.CONTENT_ITEM_TYPE)
//                    .withValue(Phone.NUMBER, it.home)
//                    .withValue(Phone.TYPE,
//                        Phone.TYPE_HOME)
//                    .build())
//            }
//            if(!it.work.isNullOrEmpty()) {
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                        Phone.CONTENT_ITEM_TYPE)
//                    .withValue(Phone.NUMBER, it.work)
//                    .withValue(Phone.TYPE,
//                        Phone.TYPE_WORK)
//                    .build())
//            }
//
//            if(!it.other.isNullOrEmpty()) {
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                        Phone.CONTENT_ITEM_TYPE)
//                    .withValue(Phone.NUMBER, it.other)
//                    .withValue(Phone.TYPE,
//                        Phone.TYPE_OTHER)
//                    .build())
//            }
        }


        //------------------------------------------------------ Email
        contact.emails?.let {
            contact.emails.forEach{item ->
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.DATA, item.email ?: "")
                    .withValue(Email.TYPE, item.category?.toEmailType() ?: Email.TYPE_MOBILE)
                    .build())
            }
//            if(!it.home.isNullOrBlank()) {
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                        Email.CONTENT_ITEM_TYPE)
//                    .withValue(Email.DATA, it.home)
//                    .withValue(Email.TYPE,
//                        Email.TYPE_HOME)
//                    .build())
//            }
//            if(!it.work.isNullOrBlank()) {
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                        Email.CONTENT_ITEM_TYPE)
//                    .withValue(Email.DATA, it.work)
//                    .withValue(Email.TYPE,
//                        Email.TYPE_WORK)
//                    .build())
//            }
//            if(!it.other.isNullOrBlank()) {
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                        Email.CONTENT_ITEM_TYPE)
//                    .withValue(Email.DATA, it.other)
//                    .withValue(Email.TYPE,
//                        Email.TYPE_OTHER)
//                    .build())
//            }
        }


        //------------------------------------------------------ Organization

        contact.libraryContactWorkInfo?.let {
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

        //------------------------------------------------------ Notes

        contact.notes?.let {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(Note.NOTE, contact.notes)
                .build())
        }

        //------------------------------------------------------ Label

        contact.label?.let {
            val accounts = AccountManager.get(context).accounts
            accounts.forEach {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Groups.CONTENT_URI)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.Groups.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.Groups.TITLE, it)
                    .withValue(ContactsContract.Groups.ACCOUNT_NAME, it.name)
                    .withValue(ContactsContract.Groups.ACCOUNT_TYPE, it.type)
                    .build()
                )
            }
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
                profilePicture.pictureBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
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

        //todo Need to update the for favorite contacts
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//            .withValueBackReference(ContactsContract.Data._ID, 0)
//            .withValue(ContactsContract.Contacts.STARRED, if (contact.isFavorite) 1 else 0)
//            .build())

        // Asking the Contact provider to create a new contact

        return try {
            val results = context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            val projection = arrayOf(ContactsContract.RawContacts.CONTACT_ID)
            val cursor: Cursor? = context.contentResolver.query(results[0].uri!!, projection, null, null, null)
            cursor?.moveToNext()
            val contactId = cursor?.getLong(0)
            Log.d("SavedContactId", "$contactId")
            cursor?.close()
            contactId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("MissingPermission")
    fun updateContact(context: Context, contact: LibraryContact): Boolean?{
        val ops = ArrayList<ContentProviderOperation>()

        var where = java.lang.String.format(
            "%s = ?",
            ContactsContract.Data.CONTACT_ID /*contactId*/
        )
        val args = arrayOf(contact.id)

        //------------------------------------------------------ Names

      /*  contact.name?.let { name ->
            if(!name.displayName.isNullOrBlank()) {
                ops.add(ContentProviderOperation.newUpdate(
                    ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, args)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.DISPLAY_NAME, name.displayName).build()
                )
            }
            if(name.firstName != null ||  name.middleName != null || name.lastName != null) {
                ops.add(ContentProviderOperation.newUpdate(
                    ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, args)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.GIVEN_NAME, contact.name?.firstName)
                    .withValue(StructuredName.MIDDLE_NAME, contact.name?.middleName)
                    .withValue(StructuredName.FAMILY_NAME, contact.name?.lastName)
                    .build())
            }
        }

        //------------------------------------------------------ Phonetic Names

        contact.phoneticName?.let { name ->
            if(!name.displayName.isNullOrBlank()) {
                ops.add(ContentProviderOperation.newUpdate(
                    ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, args)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.PHONETIC_NAME, name.displayName).build())
            }
            if(name.firstName != null ||  name.middleName != null || name.lastName != null) {
                ops.add(ContentProviderOperation.newUpdate(
                    ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, args)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.PHONETIC_GIVEN_NAME, name.firstName)
                    .withValue(StructuredName.PHONETIC_MIDDLE_NAME, name.middleName)
                    .withValue(StructuredName.PHONETIC_FAMILY_NAME, name.lastName)
                    .build())
            }
        }
*/
        //------------------------------------------------------ Mobile Number

        contact.numbers.let {
            contact.numbers.forEach{item ->
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, args)
                    .withValue(Phone.NUMBER, item.number ?: "")
                    .withValue(Phone.TYPE, item.category?.toPhoneType() ?: Phone.TYPE_MOBILE)
                    .build()
                )
            }

        }


     /*   //------------------------------------------------------ Email
        contact.emails?.let {
            contact.emails.forEach{item ->
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, args)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.DATA, item.email ?: "")
                    .withValue(Email.TYPE, item.category?.toEmailType() ?: Email.TYPE_MOBILE)
                    .build())
            }

        }


        //------------------------------------------------------ Organization

        contact.libraryContactWorkInfo?.let {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, args)
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

        //------------------------------------------------------ Notes

        contact.notes?.let {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, args)
                .withValue(ContactsContract.Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)
                .withValue(Note.NOTE, contact.notes)
                .build())
        }

        //------------------------------------------------------ Label

        contact.label?.let {
            val accounts = AccountManager.get(context).accounts
            accounts.forEach {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Groups.CONTENT_URI)
                    .withSelection(where, args)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.Groups.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.Groups.TITLE, it)
                    .withValue(ContactsContract.Groups.ACCOUNT_NAME, it.name)
                    .withValue(ContactsContract.Groups.ACCOUNT_TYPE, it.type)
                    .build()
                )
            }
        }

        //------------------------------------------------------ Address

        contact.address?.let { address ->
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, args)
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
                profilePicture.pictureBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, args)
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
*/
        //todo Need to update the for favorite contacts
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//            .withValueBackReference(ContactsContract.Data._ID, 0)
//            .withValue(ContactsContract.Contacts.STARRED, if (contact.isFavorite) 1 else 0)
//            .build())

        // Asking the Contact provider to create a new contact

        return try {
            val results = context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            for(result in results){
                Log.e("ContactLib", "result ${result.uri.toString()}")
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteAndAddContact(context: Context, contact: LibraryContact): Long? {
        deleteContactById(context, contact.id)
        return addContact(context, contact)
    }
    fun updateNameAndNumber(
        context: Context?,
        contact: LibraryContact,
        number: String?,
        newName: String?,
        newNumber: String?
    ): Boolean {
        var newNumber = newNumber
        if (context == null || number == null || number.trim { it <= ' ' }.isEmpty()) return false
        if (newNumber != null && newNumber.trim { it <= ' ' }.isEmpty()) newNumber = null
        if (newNumber == null) return false
        val contactId = getContactId(context, number) ?: return false

        //selection for name
        var where = java.lang.String.format(
            "%s = ?",
            ContactsContract.Data.CONTACT_ID /*contactId*/
        )
        val args = arrayOf(contactId)
        val operations: ArrayList<ContentProviderOperation> = ArrayList()
        operations.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, args)
                .withValue(StructuredName.GIVEN_NAME, newName)
                .build()
        )


        //change args for number
        args[0] = number
        operations.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, args)
                .withValue(ContactsContract.Data.DATA1 /*number*/, newNumber)
                .build()
        )
        try {
            val results = context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
            for (result in results) {
                Log.d("Update Result", result.toString())
            }
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }
    @SuppressLint("Range")
    fun getContactId(context: Context?, number: String): String? {
        if (context == null) return null
        val cursor = context.contentResolver.query(
            Phone.CONTENT_URI, arrayOf(Phone.CONTACT_ID, Phone.NUMBER),
            Phone.NORMALIZED_NUMBER + "=? OR " + Phone.NUMBER + "=?", arrayOf(number, number),
            null
        )
        if (cursor == null || cursor.count == 0) return null
        cursor.moveToFirst()
        val id = cursor.getString(cursor.getColumnIndex(Phone.CONTACT_ID))
        cursor.close()
        return id
    }

    fun getPictureForContact(ctx: Context, id: String): LibraryContactPicture {
        val contentResolver = ctx.contentResolver
        return getPictureFromContact(contentResolver, id)
    }

    @SuppressLint("Range")
    fun getContactById(ctx: Context, id: String, loadPicture: Boolean = false): LibraryContact? {
        val selection: String = ContactsContract.Contacts._ID + " = ? "
        val selectionArgs = arrayOf(id)
        var libraryContact : LibraryContact? = null
        val contentResolver = ctx.contentResolver
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, selection, selectionArgs, null)
        if(cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            while (cursor != null && !cursor.isAfterLast) {
                if (cursor.getColumnIndex(ContactsContract.Contacts._ID) >= 0) {
                    if (id == cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))) {
                        val name  = getNameFromContact(cursor, ContactsContract.Contacts.DISPLAY_NAME)
                        val phoneticName  = getNameFromContact(cursor, ContactsContract.Contacts.PHONETIC_NAME)
                        val number = getNumberFromContact(contentResolver, id)
                        val email = getEmailFromContact(contentResolver, id)
                        val address = getAddressFromContact(contentResolver, id)
                        val workInfo = getWorkInfoFromContact(contentResolver, id)
                        val profilePicture = if(loadPicture) {
                            getPictureFromContact(contentResolver, id)
                        } else {
                            LibraryContactPicture()
                        }
                        libraryContact = LibraryContact(
                            id = id,
                            name = name,
                            phoneticName = phoneticName,
                            numbers = number,
                            emails = email,
                            address = address,
                            libraryContactWorkInfo = workInfo,
                            profilePicture = profilePicture
                        )
                        break
                    }
                }
            }
        }
        cursor?.close()
        return libraryContact
    }


    //fetches all list with all data of each contact
    @SuppressLint("Range")
    fun getContacts(ctx: Context, loadPicture: Boolean = false): List<LibraryContact>? {
        val list: MutableList<LibraryContact> = ArrayList()
        val contentResolver = ctx.contentResolver
        val cursor =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val name  = getNameFromContact(cursor, ContactsContract.Contacts.DISPLAY_NAME)
//                    Log.e("name", "$name")
                    val phoneticName  = getNameFromContact(cursor, ContactsContract.Contacts.PHONETIC_NAME)
//                    Log.e("phoneticName", "$phoneticName")
                    val numbers = getNumberFromContact(contentResolver, id)
                    val emails = getEmailFromContact(contentResolver, id)
                    val address = getAddressFromContact(contentResolver, id)
                    val workInfo = getWorkInfoFromContact(contentResolver, id)
                    val profilePicture = if(loadPicture) {
                        getPictureFromContact(contentResolver, id)
                    } else {
                        LibraryContactPicture()
                    }
                    val contact = LibraryContact(
                        id = id,
                        name = name,
                        phoneticName = phoneticName,
                        numbers = numbers,
                        emails = emails,
                        address = address,
                        libraryContactWorkInfo = workInfo,
                        profilePicture = profilePicture
                    )
                    list.add(contact)
                }
            }
            cursor.close()
        }
        return list
    }

    private fun getPictureFromContact(contentResolver: ContentResolver, id: String): LibraryContactPicture {
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

        return LibraryContactPicture(photo)
    }

    // fetches all list with limited data of each contact
    @SuppressLint("Range")
    fun getContactsLight(context: Context): List<LibraryContact> {
        val list = ArrayList<LibraryContact>()
        val contentResolver = context.contentResolver
        val fieldListProjection = arrayOf(
            Phone.CONTACT_ID,
            Phone.DISPLAY_NAME_PRIMARY,
            Phone.NUMBER,
            Phone.NORMALIZED_NUMBER,
            ContactsContract.Contacts.PHOTO_URI, ContactsContract.Contacts.STARRED
        )
        val sort = Phone.DISPLAY_NAME_PRIMARY + " ASC"
        val cursor = contentResolver
            .query(Phone.CONTENT_URI, fieldListProjection, null, null, sort)
        val normalizedNumbersAlreadyFound: HashSet<String> = HashSet()
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val normalizedNumber = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME))
                if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                    val id = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val name = getNameFromContact(cursor, ContactsContract.Contacts.DISPLAY_NAME)
                    val phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER))
                    val uri = cursor.getString(cursor.getColumnIndex(Phone.PHOTO_URI))
                    if (uri != null) {
                        val profilePicture = LibraryContactPicture(uri = uri)
                        list.add(LibraryContact("$id", profilePicture = profilePicture, name = name, numbers = listOf(LibraryNumber(phoneNumber, CategoryType.MOBILE.value))))
                    } else {
                        list.add(LibraryContact("$id", name = name, numbers= listOf(LibraryNumber(phoneNumber, CategoryType.MOBILE.value))))
                    }
                }
            }
            cursor.close()
        }
        return list
    }

    @SuppressLint("Range")
    private fun getNameFromContact(cursor: Cursor, nameType: String): LibraryName {
        try {
            val name = cursor.getString(cursor.getColumnIndex(
                nameType))
            if(name.isNullOrBlank()){
                return LibraryName()
            }
            val splittedName = name.split(" ")
            if(splittedName.isNullOrEmpty()){
                return LibraryName()
            } else {
                when (splittedName.size) {
                    3 -> {
                        return LibraryName(firstName = splittedName[0], middleName = splittedName[1], lastName = splittedName[2])
                    }
                    2 -> {
                        return LibraryName(firstName = splittedName[0], lastName = splittedName[1])
                    }
                    1 -> {
                        return LibraryName(displayName = splittedName[0])
                    }
                }
            }
        }catch (ex: Exception) {
            ex.printStackTrace()
        }
        return LibraryName()
    }

    @SuppressLint("Range")
    private fun getNumberFromContact(contentResolver: ContentResolver, id: String): List<LibraryNumber> {
        var homeContact: String? = ""
        var workContact: String? = ""
        var otherContact: String? = ""
        var numbers = mutableListOf<LibraryNumber>()

        val cursorInfo =
            contentResolver.query(Phone.CONTENT_URI,
                null,
                Phone.CONTACT_ID + " = ?",
                arrayOf(id),
                null)

        while (cursorInfo!!.moveToNext()) {
            val phoneNumber = cursorInfo.getString(cursorInfo.getColumnIndex(Phone.NUMBER))
            val phoneType = cursorInfo.getInt(cursorInfo.getColumnIndex(Phone.TYPE))
            val categoryType = phoneType.toPhoneCategoryType()
//            try {

//                when (cursorInfo.getInt(cursorInfo.getColumnIndex(Phone.TYPE))) {
//                    Phone.TYPE_HOME -> {
//                        homeContact = phoneNumber
//                    }
//                    Phone.TYPE_WORK -> {
//                        workContact = phoneNumber
//                    }
//                    Phone.TYPE_OTHER -> {
//                        otherContact = phoneNumber
//                    }
//                    Phone.TYPE_MOBILE -> {
//                        otherContact = phoneNumber
//                    }
//                }
//            } catch (ex:Exception){
//                homeContact = phoneNumber
//            }
            val libraryNumber = LibraryNumber(phoneNumber, categoryType.value)
            if(!numbers.contains(libraryNumber))
                numbers.add(libraryNumber)
        }
        cursorInfo.close()
        return numbers
    }

    @SuppressLint("Range")
    private fun getEmailFromContact(contentResolver: ContentResolver, id: String): List<LibraryEmail> {
        var emails = mutableListOf<LibraryEmail>()

        val emailCursor =
            contentResolver.query(Email.CONTENT_URI,
                null,
                Email.CONTACT_ID + " = ?",
                arrayOf(id),
                null)

        while (emailCursor!!.moveToNext()) {
            var emailAddress = emailCursor.getString(emailCursor.getColumnIndex(Email.DATA))
            val emailType = emailCursor.getInt(emailCursor.getColumnIndex(Email.TYPE))
            val categoryType = emailType.toEmailCategoryType()
            val libraryEmail = LibraryEmail(emailAddress,categoryType.value)
            if(!emails.contains(libraryEmail))
                emails.add(libraryEmail)
        }
        emailCursor.close()
        return emails
    }

    @SuppressLint("Range")
    private fun getAddressFromContact(contentResolver: ContentResolver, id: String): LibraryContactAddress {

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
        return LibraryContactAddress(street, city, state, postCode, country)
    }

    @SuppressLint("Range")
    private fun getWorkInfoFromContact(contentResolver: ContentResolver, id: String): LibraryContactWorkInfo {
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
        return LibraryContactWorkInfo(companyName, jobTitle)
    }

    fun getContactByPhoneNumber(ctx: Context, phoneNumber: String?): LibraryContact {
        var id: String? = null
        val contentResolver = ctx.contentResolver
        if (phoneNumber != null && phoneNumber.isNotEmpty()) {

            val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
            val projection = arrayOf(Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER)
            val cursor = contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    id = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup._ID))
                    if (id != null) {
                        val name =
                            getNameFromContact(cursor, ContactsContract.Contacts.DISPLAY_NAME)
                        val phoneticName = LibraryName()
                        val numbers = getNumberFromContact(contentResolver, id)
                        val emails = getEmailFromContact(contentResolver, id)
                        val address = getAddressFromContact(contentResolver, id)
                        val workInfo = getWorkInfoFromContact(contentResolver, id)
                        val profilePicture = getPictureFromContact(contentResolver, id)
                        cursor.close()
                        return LibraryContact(
                            name = name,
                            phoneticName = phoneticName,
                            numbers = numbers,
                            emails = emails,
                            address = address,
                            libraryContactWorkInfo = workInfo,
                            profilePicture = profilePicture
                        )
                    }
                }
                cursor.close()
            }
        }
        return LibraryContact()
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
    fun deleteContactById(context: Context, id: String): Boolean {
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
                            return true
                        }

                    } while (it.moveToNext())
                }

            } catch (e: Exception) {
                println(e.stackTrace)
            } finally {
                it.close()
            }
        }
        return false
    }

    fun deleteContactUsingId(context: Context, id: String): Boolean{
        val ops = ArrayList<ContentProviderOperation>()
        val cr = context.contentResolver
        ops.add(ContentProviderOperation
            .newDelete(ContactsContract.RawContacts.CONTENT_URI)
            .withSelection(
                ContactsContract.RawContacts.CONTACT_ID
                        + " = ?",
                arrayOf(id))
            .build())

        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
            return true

        } catch (e: RemoteException) {
            e.printStackTrace()
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
        } finally {
            ops.clear()
        }

        return false
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