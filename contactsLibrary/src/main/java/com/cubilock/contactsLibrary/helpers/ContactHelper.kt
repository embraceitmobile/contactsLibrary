package com.cubilock.contactsLibrary.helpers

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.RemoteException
import android.provider.BaseColumns
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
    fun addContact(context: Context, contact: LibraryContact): Long? {
        val ops = ArrayList<ContentProviderOperation>()

        ops.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI
            )
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        //------------------------------------------------------ Names

        contact.name?.let { name ->
            if (!name.displayName.isNullOrBlank()) {
                ops.add(
                    ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI
                    )
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(StructuredName.DISPLAY_NAME, name.displayName).build()
                )
            }
            if (name.firstName != null || name.middleName != null || name.lastName != null) {
                ops.add(
                    ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI
                    )
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(StructuredName.GIVEN_NAME, contact.name?.firstName)
                        .withValue(StructuredName.MIDDLE_NAME, contact.name?.middleName)
                        .withValue(StructuredName.FAMILY_NAME, contact.name?.lastName)
                        .build()
                )
            }
        }

        //------------------------------------------------------ Phonetic Names

        contact.phoneticName?.let { name ->
            if (!name.displayName.isNullOrBlank()) {
                ops.add(
                    ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI
                    )
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(StructuredName.PHONETIC_NAME, name.displayName).build()
                )
            }
            if (name.firstName != null || name.middleName != null || name.lastName != null) {
                ops.add(
                    ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI
                    )
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(StructuredName.PHONETIC_GIVEN_NAME, name.firstName)
                        .withValue(StructuredName.PHONETIC_MIDDLE_NAME, name.middleName)
                        .withValue(StructuredName.PHONETIC_FAMILY_NAME, name.lastName)
                        .build()
                )
            }
        }

        //------------------------------------------------------ Mobile Number

        contact.numbers.let {
            contact.numbers.forEach { item ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            Phone.CONTENT_ITEM_TYPE
                        )
                        .withValue(Phone.NUMBER, item.number ?: "")
                        .withValue(Phone.TYPE, item.category?.toPhoneType() ?: Phone.TYPE_MOBILE)
                        .build()
                )
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
        contact.emails.let {
            contact.emails.forEach { item ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            Email.CONTENT_ITEM_TYPE
                        )
                        .withValue(Email.DATA, item.email ?: "")
                        .withValue(Email.TYPE, item.category?.toEmailType() ?: Email.TYPE_MOBILE)
                        .build()
                )
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
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        Organization.CONTENT_ITEM_TYPE
                    )
                    .withValue(Organization.COMPANY, it.company)
                    .withValue(
                        Organization.TYPE,
                        Organization.TYPE_WORK
                    )
                    .withValue(Organization.TITLE, it.jobTitle)
                    .withValue(
                        Organization.TYPE,
                        Organization.TYPE_WORK
                    )
                    .build()
            )
        }

        //------------------------------------------------------ Notes

        contact.notes?.let {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(Note.NOTE, contact.notes)
                    .build()
            )
        }

        //------------------------------------------------------ Label

        contact.label?.let {
            val accounts = AccountManager.get(context).accounts
            accounts.forEach {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Groups.CONTENT_URI)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.Groups.CONTENT_ITEM_TYPE
                        )
                        .withValue(ContactsContract.Groups.TITLE, it)
                        .withValue(ContactsContract.Groups.ACCOUNT_NAME, it.name)
                        .withValue(ContactsContract.Groups.ACCOUNT_TYPE, it.type)
                        .build()
                )
            }
        }

        //------------------------------------------------------ Address

        contact.address?.let { address ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        StructuredPostal.CONTENT_ITEM_TYPE
                    )
                    .withValue(StructuredPostal.CITY, address.city)
                    .withValue(StructuredPostal.STREET, address.street)
                    .withValue(StructuredPostal.POSTCODE, address.postcode)
                    .withValue(StructuredPostal.REGION, address.state)
                    .withValue(StructuredPostal.COUNTRY, address.country)
                    .build()
            )
        }

        //------------------------------------------------------ Photo

        contact.profilePicture?.let { profilePicture ->
            val stream = ByteArrayOutputStream()
            if (profilePicture.pictureBitmap != null) {
                profilePicture.pictureBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                        .withValue(ContactsContract.Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
                        .withValue(Photo.PHOTO, stream.toByteArray())
                        .build()
                )
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
            val cursor: Cursor? =
                context.contentResolver.query(results[0].uri!!, projection, null, null, null)
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


    fun deleteAndAddContact(context: Context, contact: LibraryContact): Long? {
        deleteContactById(context, contact.id)
        return addContact(context, contact)
    }


    @SuppressLint("MissingPermission")
    fun updateContact(context: Context, id: String?, contact: LibraryContact) {

        val ops = ArrayList<ContentProviderOperation>()
        // Name
        contact.name.let { name ->
            if (!name?.displayName.isNullOrBlank()) {
                var map = mutableMapOf<String, String>()
                map.put(StructuredName.DISPLAY_NAME, name?.displayName.toString())
                var builder = createBuilder(id.toString(), StructuredName.CONTENT_ITEM_TYPE, map)
                ops.add(builder.build())
            }
            if (!name?.lastName.isNullOrEmpty() || !name?.firstName.isNullOrEmpty() || !name?.middleName.isNullOrEmpty()) {
                var map = mutableMapOf<String, String>()
                map.put(StructuredName.FAMILY_NAME, name?.lastName.toString())
                map.put(StructuredName.MIDDLE_NAME, name?.middleName.toString())
                map.put(StructuredName.GIVEN_NAME, name?.firstName.toString())
                var builder = createBuilder(id.toString(), StructuredName.CONTENT_ITEM_TYPE, map)
                ops.add(builder.build())
            }
        }
        contact.phoneticName.let { name ->
            if (!name?.displayName.isNullOrBlank()) {
                var map = mutableMapOf<String, String>()
                map.put(StructuredName.PHONETIC_NAME, name?.displayName.toString())
                var builder = createBuilder(id.toString(), StructuredName.CONTENT_ITEM_TYPE, map)
                ops.add(builder.build())
            }
            if (!name?.lastName.isNullOrEmpty() || !name?.firstName.isNullOrEmpty() || !name?.middleName.isNullOrEmpty()) {
                var map = mutableMapOf<String, String>()
                map.put(StructuredName.PHONETIC_FAMILY_NAME, name?.lastName.toString())
                map.put(StructuredName.PHONETIC_MIDDLE_NAME, name?.middleName.toString())
                map.put(StructuredName.PHONETIC_GIVEN_NAME, name?.firstName.toString())
                var builder = createBuilder(id.toString(), StructuredName.CONTENT_ITEM_TYPE, map)
                ops.add(builder.build())
            }
        }
        contact.numbers.let {
            contact.numbers.forEach { item ->

                var map = mutableMapOf<String, String>()
                map.put(Phone.NUMBER, item.number.toString())
                map.put(Phone.TYPE, (item.category?.toPhoneType() ?: Phone.TYPE_MOBILE).toString())
                var builder = createBuilder(id.toString(), Phone.CONTENT_ITEM_TYPE, map)
                ops.add(builder.build())
            }
        }

        contact.emails.let {
            contact.emails.forEach { item ->
                var map = mutableMapOf<String, String>()
                map.put(Email.DATA, item.email ?: "")
                map.put(
                    Email.TYPE, (item.category?.toEmailType() ?: Email.TYPE_MOBILE).toString()
                )
                var builder = createBuilder(id.toString(), Email.CONTENT_ITEM_TYPE, map)
                ops.add(builder.build())
            }
        }
        contact.libraryContactWorkInfo?.let {
            var map = mutableMapOf<String, String>()
            map.put(Organization.COMPANY, it.company.toString())
            map.put(
                Organization.TYPE,
                Organization.TYPE_WORK.toString()
            )
            map.put(Organization.TITLE, it.jobTitle.toString())
            map.put(
                Organization.TYPE,
                Organization.TYPE_WORK.toString()
            )
            var builder = createBuilder(id.toString(), Organization.CONTENT_ITEM_TYPE, map)
            ops.add(builder.build())
        }

        contact.notes?.let {
            var map = mutableMapOf<String, String>()
            map.put(Note.NOTE, contact.notes.toString())
            var builder = createBuilder(id.toString(), Note.CONTENT_ITEM_TYPE, map)
            ops.add(builder.build())
        }

        //------------------------------------------------------ Label

        contact.label?.let {
            val accounts = AccountManager.get(context).accounts
            accounts.forEach {
                var map = mutableMapOf<String, String>()
                map.put(ContactsContract.Groups.TITLE, it.toString())
                map.put(ContactsContract.Groups.ACCOUNT_NAME, it.name.toString())
                map.put(ContactsContract.Groups.ACCOUNT_TYPE, it.type.toString())
                var builder = createBuilder(id.toString(), ContactsContract.Groups.CONTENT_ITEM_TYPE, map)
                ops.add(builder.build())
            }
        }

        //------------------------------------------------------ Address

        contact.address?.let { address ->
            var map = mutableMapOf<String, String>()
            map.put(StructuredPostal.CITY, address.city.toString())
            map.put(StructuredPostal.STREET, address.street.toString())
            map.put(StructuredPostal.POSTCODE, address.postcode.toString())
            map.put(StructuredPostal.REGION, address.state.toString())
            map.put(StructuredPostal.COUNTRY, address.country.toString())
            var builder = createBuilder(id.toString(), StructuredPostal.CONTENT_ITEM_TYPE, map)
            ops.add(builder.build())
        }

        //------------------------------------------------------ Photo

        contact.profilePicture?.let { profilePicture ->
            val stream = ByteArrayOutputStream()
            if (profilePicture.pictureBitmap != null) {
                profilePicture.pictureBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                var builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                builder.withSelection(
                    ContactsContract.Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?",
                    arrayOf(id.toString(), Photo.CONTENT_ITEM_TYPE)
                )
//                builder.withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                builder.withValue(Photo.PHOTO, stream.toByteArray())
                ops.add(builder.build())
            }
            try {
                stream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Update
        try {
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun createBuilder(
        id: String,
        contentType: String,
        keyValue: Map<String, String>
    ): ContentProviderOperation.Builder {
        var builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
        builder.withSelection(
            ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?",
            arrayOf(id, contentType)
        )
        keyValue.forEach {
            builder.withValue(it.key, it.value)
        }
        return builder
    }

    @SuppressLint("Range")
    fun getContactId(context: Context?, number: String): String? {
        if (context == null) return null
//        testId(number,context)
//        getContactName(number, context)

//        val cursor = context.contentResolver.query(
//            Phone.CONTENT_URI, arrayOf(Phone.CONTACT_ID, Phone.NUMBER),
//            Phone.NORMALIZED_NUMBER + " = ? OR " + Phone.NUMBER + " = ?", arrayOf("$number", "$number"),
//            null
//        )
//        if (cursor == null || cursor.count == 0) return null
//        cursor.moveToFirst()
//        val id = cursor.getString(cursor.getColumnIndex(Phone.CONTACT_ID))
//        cursor.close()
        return getContactIdTest(context, number)
    }

    @SuppressLint("Range")
    fun getContactName(number: String, context: Context) {

        // // define the columns I want the query to return
        val projection = arrayOf(
            PhoneLookup.DISPLAY_NAME,
            PhoneLookup.NUMBER,
            PhoneLookup.HAS_PHONE_NUMBER
        )

        // encode the phone number and build the filter URI
        val contactUri = Uri.withAppendedPath(
            PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )

        // query time
        val cursor = context.contentResolver.query(
            contactUri,
            projection, null, null, null
        )
        // querying all contacts = Cursor cursor =
        // context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
        // projection, null, null, null);
        if (cursor!!.moveToFirst()) {
            var contactName = cursor.getString(
                cursor
                    .getColumnIndex(PhoneLookup.DISPLAY_NAME)
            )

            Log.i("Contact Name", "Contact Name: $contactName")

        }
        cursor.close()
    }


    fun getPictureForContact(ctx: Context, id: String): LibraryContactPicture {
        val contentResolver = ctx.contentResolver
        return getPictureFromContact(contentResolver, id)
    }

    @SuppressLint("Range")
    fun getContactById(ctx: Context, id: String, loadPicture: Boolean = false): LibraryContact? {
        val selection: String = ContactsContract.Contacts._ID + " = ? "
        val selectionArgs = arrayOf(id)
        var libraryContact: LibraryContact? = null
        val contentResolver = ctx.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            null
        )
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            while (cursor != null && !cursor.isAfterLast) {
                if (cursor.getColumnIndex(ContactsContract.Contacts._ID) >= 0) {
                    if (id == cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))) {
                        val name =
                            getNameFromContact(cursor, ContactsContract.Contacts.DISPLAY_NAME)
                        val phoneticName =
                            getNameFromContact(cursor, ContactsContract.Contacts.PHONETIC_NAME)
                        val number = getNumberFromContact(contentResolver, id)
                        val email = getEmailFromContact(contentResolver, id)
                        val address = getAddressFromContact(contentResolver, id)
                        val workInfo = getWorkInfoFromContact(contentResolver, id)
                        val profilePicture = if (loadPicture) {
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
                    val name = getNameFromContact(cursor, ContactsContract.Contacts.DISPLAY_NAME)
//                    Log.e("name", "$name")
                    val phoneticName =
                        getNameFromContact(cursor, ContactsContract.Contacts.PHONETIC_NAME)
//                    Log.e("phoneticName", "$phoneticName")
                    val numbers = getNumberFromContact(contentResolver, id)
                    val emails = getEmailFromContact(contentResolver, id)
                    val address = getAddressFromContact(contentResolver, id)
                    val workInfo = getWorkInfoFromContact(contentResolver, id)
                    val profilePicture = if (loadPicture) {
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

    private fun getPictureFromContact(
        contentResolver: ContentResolver,
        id: String
    ): LibraryContactPicture {
        val inputStream: InputStream? =
            ContactsContract.Contacts.openContactPhotoInputStream(
                contentResolver,
                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
            )
        val person =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
        val pURI = Uri.withAppendedPath(
            person,
            ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
        )

        var photo: Bitmap? = null
        if (inputStream != null) {
            photo = BitmapFactory.decodeStream(inputStream)
        }

        return LibraryContactPicture(photo, uri = pURI.toString())
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
                    val id =
                        cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val name = getNameFromContact(cursor, ContactsContract.Contacts.DISPLAY_NAME)
                    val phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER))
                    val uri = cursor.getString(cursor.getColumnIndex(Phone.PHOTO_URI))
                    if (uri != null) {
                        val profilePicture = LibraryContactPicture(uri = uri)
                        list.add(
                            LibraryContact(
                                "$id",
                                profilePicture = profilePicture,
                                name = name,
                                numbers = listOf(
                                    LibraryNumber(
                                        phoneNumber,
                                        CategoryType.MOBILE.value
                                    )
                                )
                            )
                        )
                    } else {
                        list.add(
                            LibraryContact(
                                "$id",
                                name = name,
                                numbers = listOf(
                                    LibraryNumber(
                                        phoneNumber,
                                        CategoryType.MOBILE.value
                                    )
                                )
                            )
                        )
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
            val name = cursor.getString(
                cursor.getColumnIndex(
                    nameType
                )
            )
            if (name.isNullOrBlank()) {
                return LibraryName()
            }
            val splittedName = name.split(" ")
            if (splittedName.isNullOrEmpty()) {
                return LibraryName()
            } else {
                when (splittedName.size) {
                    3 -> {
                        return LibraryName(
                            firstName = splittedName[0],
                            middleName = splittedName[1],
                            lastName = splittedName[2]
                        )
                    }
                    2 -> {
                        return LibraryName(firstName = splittedName[0], lastName = splittedName[1])
                    }
                    1 -> {
                        return LibraryName(displayName = splittedName[0])
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return LibraryName()
    }

    @SuppressLint("Range")
    private fun getNumberFromContact(
        contentResolver: ContentResolver,
        id: String
    ): List<LibraryNumber> {
        var homeContact: String? = ""
        var workContact: String? = ""
        var otherContact: String? = ""
        var numbers = mutableListOf<LibraryNumber>()

        val cursorInfo =
            contentResolver.query(
                Phone.CONTENT_URI,
                null,
                Phone.CONTACT_ID + " = ?",
                arrayOf(id),
                null
            )

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
            if (!numbers.contains(libraryNumber))
                numbers.add(libraryNumber)
        }
        cursorInfo.close()
        return numbers
    }

    @SuppressLint("Range")
    private fun getEmailFromContact(
        contentResolver: ContentResolver,
        id: String
    ): List<LibraryEmail> {
        var emails = mutableListOf<LibraryEmail>()

        val emailCursor =
            contentResolver.query(
                Email.CONTENT_URI,
                null,
                Email.CONTACT_ID + " = ?",
                arrayOf(id),
                null
            )

        while (emailCursor!!.moveToNext()) {
            var emailAddress = emailCursor.getString(emailCursor.getColumnIndex(Email.DATA))
            val emailType = emailCursor.getInt(emailCursor.getColumnIndex(Email.TYPE))
            val categoryType = emailType.toEmailCategoryType()
            val libraryEmail = LibraryEmail(emailAddress, categoryType.value)
            if (!emails.contains(libraryEmail))
                emails.add(libraryEmail)
        }
        emailCursor.close()
        return emails
    }

    @SuppressLint("Range")
    private fun getAddressFromContact(
        contentResolver: ContentResolver,
        id: String
    ): LibraryContactAddress {

        var where = StructuredPostal.CONTACT_ID + " = " + id
        val addressCursor = contentResolver.query(
            StructuredPostal.CONTENT_URI,
            null,
            where,
            null,
            null
        )
        var street = ""
        var city = ""
        var state = ""
        var country = ""
        var postCode = ""
        while (addressCursor!!.moveToNext()) {
            street =
                addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.STREET)) ?: ""
            city =
                addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.CITY)) ?: ""
            state =
                addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.REGION)) ?: ""
            country =
                addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.COUNTRY))
                    ?: ""
            postCode =
                addressCursor.getString(addressCursor.getColumnIndex(StructuredPostal.POSTCODE))
                    ?: ""
        }
        addressCursor.close()
        return LibraryContactAddress(street, city, state, postCode, country)
    }

    @SuppressLint("Range")
    private fun getWorkInfoFromContact(
        contentResolver: ContentResolver,
        id: String
    ): LibraryContactWorkInfo {
        val workInfoCursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
            arrayOf(id, Organization.CONTENT_ITEM_TYPE),
            null
        )
        var companyName = ""
        var jobTitle = ""
        while (workInfoCursor!!.moveToNext()) {
            companyName =
                workInfoCursor.getString(workInfoCursor.getColumnIndex(Organization.COMPANY)) ?: ""
            jobTitle =
                workInfoCursor.getString(workInfoCursor.getColumnIndex(Organization.TITLE)) ?: ""
        }
        return LibraryContactWorkInfo(companyName, jobTitle)
    }

    fun contactIdByPhoneNumber(ctx: Context, phoneNumber: String?): String? {
        var contactId:String = ""
        val contentResolver: ContentResolver = ctx.getContentResolver()

        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))

        val projection = arrayOf(PhoneLookup.DISPLAY_NAME, PhoneLookup._ID)

        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val contactName =
                    cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME))
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup._ID))
                Log.d("LOGTAG", "contactMatch name: $contactName")
                Log.d("LOGTAG", "contactMatch id: $contactId")
            }
            cursor.close()
        }
        return contactId
    }
    fun getContactForPhone(ctx: Context, phone: String): LibraryContact? {
        if (phone.isEmpty()) return null
        val contentResolver = ctx.contentResolver
        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone))
        val projection = arrayOf(BaseColumns._ID)
        val contactIds: ArrayList<String> = ArrayList()
        val phoneCursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        while (phoneCursor != null && phoneCursor.moveToNext()) {
            contactIds.add(phoneCursor.getString(phoneCursor.getColumnIndex(BaseColumns._ID)))
        }
        if (phoneCursor != null) phoneCursor.close()
        if (!contactIds.isEmpty()) {
            val contactIdsListString = contactIds.toString().replace("[", "(").replace("]", ")")
            val contactSelection = ContactsContract.Data.CONTACT_ID + " IN " + contactIdsListString
            var cursor =  contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                contactSelection,
                null,
                null
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup._ID))
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

        return null
    }
    fun getContactByPhoneNumber(ctx: Context, phoneNumber: String?): LibraryContact? {
        var contact:LibraryContact? = null
        val contactId = phoneNumber?.let { getContactId(ctx, it) }
        contactId?.let { contact = getContactById(ctx, it, false) }
        return contact
        /*var id: String? = null
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
        return LibraryContact()*/
    }

    @SuppressLint("Range")
    fun deleteContactByName(context: Context, displayName: String): Boolean {
        val cr = context.contentResolver
        val cur: Cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)!!
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                            .equals(displayName, ignoreCase = true)
                    ) {
                        val lookupKey: String =
                            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                        val uri: Uri =
                            Uri.withAppendedPath(
                                ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                                lookupKey
                            )
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
                    val uri = Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                        lookupKey
                    )
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
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        cur?.let {
            try {
                if (it.moveToFirst()) {
                    do {
                        if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID)) == id) {
                            val lookupKey =
                                cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                            val uri = Uri.withAppendedPath(
                                ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                                lookupKey
                            )
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

    fun deleteContactUsingId(context: Context, id: String): Boolean {
        val ops = ArrayList<ContentProviderOperation>()
        val cr = context.contentResolver
        ops.add(
            ContentProviderOperation
                .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(
                    ContactsContract.RawContacts.CONTACT_ID
                            + " = ?",
                    arrayOf(id)
                )
                .build()
        )

        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops)
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
            Uri.encode(name)
        )
        val mPhoneNumberProjection = arrayOf(
            PhoneLookup._ID,
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

    // test functions:------------------------------------------------------------------------------
    @SuppressLint("Range")
    fun getContactIdTest(context: Context?, number: String): String? {
        val lookupUri = Uri.withAppendedPath(
            PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )

        val projection = arrayOf( ///as we need only this colum from a table...
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts._ID
        )

        var id: String? = null
        val sortOrder = StructuredPostal.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        val cursor = context?.contentResolver?.query(lookupUri, projection, null, null, sortOrder)
        while (cursor?.moveToNext() == true) {
            val name = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                .let { cursor.getString(it) }
            id = cursor.getColumnIndex(ContactsContract.Contacts._ID).let { cursor.getString(it) }

        }
        cursor?.close()

        return id
    }

    @SuppressLint("Range")
    fun getOrganizationDetails(context: Context?, contactId: String) {

        val projection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            Organization.COMPANY,
            Organization.DATA2,
            Organization.TITLE,
            Organization.JOB_DESCRIPTION,
            Organization.DEPARTMENT,
            Organization.PHONETIC_NAME,
        )

        val selection = ContactsContract.Data.MIMETYPE + " = ? AND " +
                ContactsContract.Data.CONTACT_ID + " = ? "
        val selectionArgs = arrayOf(
            Organization.CONTENT_ITEM_TYPE,
            contactId
        )

        val cursor = context?.contentResolver?.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val company = cursor.getString(cursor.getColumnIndex(Organization.COMPANY))
            val title = cursor.getString(cursor.getColumnIndex(Organization.TITLE))
            val jobDescription = cursor.getString(cursor.getColumnIndex(Organization.JOB_DESCRIPTION))
            val department = cursor.getString(cursor.getColumnIndex(Organization.DEPARTMENT))
            val pName = cursor.getString(cursor.getColumnIndex(Organization.PHONETIC_NAME))
            val data2 = cursor.getString(cursor.getColumnIndex(Organization.DATA2))
            Log.d("Contact Info", "Company: $company, Title: $title")
            cursor.close()
        }
    }

}