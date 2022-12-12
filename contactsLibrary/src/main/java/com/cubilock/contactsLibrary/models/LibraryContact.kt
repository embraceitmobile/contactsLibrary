package com.cubilock.contactsLibrary.models



data class LibraryContact @JvmOverloads constructor(
    var id: String = "0",
    var name: LibraryName? = LibraryName(),
    var phoneticName: LibraryName? = LibraryName(),
    var numbers: List<LibraryNumber> = listOf<LibraryNumber>(),
    var emails: List<LibraryEmail> = listOf<LibraryEmail>(),
    var libraryContactWorkInfo: LibraryContactWorkInfo? = LibraryContactWorkInfo(),
    var profilePicture: LibraryContactPicture? = null,
    var notes: String? = null,
    var label: String? = null,
    var address: LibraryContactAddress? = LibraryContactAddress(),
    var isFavorite: Boolean = false
) {
    override fun hashCode(): Int {
        var result = id.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (numbers.hashCode() ?: 0)
        result = 31 * result + (profilePicture?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LibraryContact

        if (name != other.name) return false
        if (phoneticName != other.phoneticName) return false
        if (numbers != other.numbers) return false
        if (emails != other.emails) return false
        if (libraryContactWorkInfo != other.libraryContactWorkInfo) return false
        if (profilePicture != other.profilePicture) return false
        if (notes != other.notes) return false
        if (address != other.address) return false
        if (isFavorite != other.isFavorite) return false

        return true
    }
}
