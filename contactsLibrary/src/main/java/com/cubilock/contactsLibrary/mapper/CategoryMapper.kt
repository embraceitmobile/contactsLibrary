package com.cubilock.contactsLibrary.mapper

import android.provider.ContactsContract.CommonDataKinds.*
import com.cubilock.contactsLibrary.models.CategoryType

fun CategoryType.toPhoneType(): Int{
    return when(this) {
        CategoryType.MOBILE -> Phone.TYPE_MOBILE
        CategoryType.HOME -> Phone.TYPE_HOME
        CategoryType.WORK -> Phone.TYPE_WORK
        CategoryType.OTHER -> Phone.TYPE_OTHER
    }
}

fun Int.toPhoneCategoryType(): CategoryType {
    return when(this){
        Phone.TYPE_MOBILE -> CategoryType.MOBILE
        Phone.TYPE_HOME -> CategoryType.HOME
        Phone.TYPE_WORK -> CategoryType.WORK
        Phone.TYPE_OTHER -> CategoryType.OTHER
        else -> CategoryType.MOBILE
    }
}

fun CategoryType.toEmailType(): Int{
    return when(this) {
        CategoryType.MOBILE -> Email.TYPE_MOBILE
        CategoryType.HOME -> Email.TYPE_HOME
        CategoryType.WORK -> Email.TYPE_WORK
        CategoryType.OTHER -> Email.TYPE_OTHER
    }
}

fun Int.toEmailCategoryType(): CategoryType {
    return when(this){
        Email.TYPE_MOBILE -> CategoryType.MOBILE
        Email.TYPE_HOME -> CategoryType.HOME
        Email.TYPE_WORK -> CategoryType.WORK
        Email.TYPE_OTHER -> CategoryType.OTHER
        else -> CategoryType.MOBILE
    }
}

fun String.toPhoneType(): Int{
    return when(this) {
        CategoryType.MOBILE.value -> Phone.TYPE_MOBILE
        CategoryType.HOME.value -> Phone.TYPE_HOME
        CategoryType.WORK.value -> Phone.TYPE_WORK
        CategoryType.OTHER.value -> Phone.TYPE_OTHER
        else -> Phone.TYPE_MOBILE
    }
}

fun String.toEmailType(): Int{
    return when(this) {
        CategoryType.MOBILE.value -> Email.TYPE_MOBILE
        CategoryType.HOME.value -> Email.TYPE_HOME
        CategoryType.WORK.value -> Email.TYPE_WORK
        CategoryType.OTHER.value -> Email.TYPE_OTHER
        else -> Email.TYPE_MOBILE
    }
}