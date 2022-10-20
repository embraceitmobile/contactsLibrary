package com.cubilock.contactsLibrary.models


data class Contact(
    val id: String? = "",
    val name: Name? = Name(),
    val phoneticName: Name? = Name(),
    val number: Number? = Number(),
    val email: Email? = Email(),
    val workInfo: WorkInfo? = WorkInfo(),
    val profilePicture: ContactPicture? = null,
    val notes: String? = null,
    val address: Address? = Address()
) {

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (number?.hashCode() ?: 0)
        result = 31 * result + (profilePicture?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (name != other.name) return false
        if (phoneticName != other.phoneticName) return false
        if (number != other.number) return false
        if (email != other.email) return false
        if (workInfo != other.workInfo) return false
        if (profilePicture != other.profilePicture) return false
        if (notes != other.notes) return false
        if (address != other.address) return false

        return true
    }
}
