package com.cubilock.contactsLibrary.models

data class LibraryEmail(
    var email: String? = null,
    var category: String? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LibraryEmail

        if (email != other.email) return false
        if (category != other.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email?.hashCode() ?: 0
        result = 31 * result + (category?.hashCode() ?: 0)
        return result
    }
}
