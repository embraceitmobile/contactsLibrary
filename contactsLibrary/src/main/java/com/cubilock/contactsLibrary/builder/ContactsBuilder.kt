package com.cubilock.contactsLibrary.builder

import com.cubilock.contactsLibrary.models.LibararyEmail
import com.cubilock.contactsLibrary.models.LibraryName
import com.cubilock.contactsLibrary.models.LibraryNumber
import com.cubilock.contactsLibrary.models.LibraryContactWorkInfo

class ContactsBuilder(
    val name: LibraryName?,
    val number: LibraryNumber?,
    val email: LibararyEmail?,
    val libraryContactWorkInfo: LibraryContactWorkInfo?,
    val profilePicture: String?,
    val label: String?
) {

    private constructor(builder: Builder):
            this(builder.name,
                builder.number,
                builder.email,
                builder.libraryContactWorkInfo,
                builder.profilePicture,
                builder.label,
            )

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var name: LibraryName? = null
            private set
        var number: LibraryNumber? = null
            private set
        var email: LibararyEmail? = null
            private set
        var libraryContactWorkInfo: LibraryContactWorkInfo? = null
            private set
        var profilePicture: String? = null
            private set
        var label: String? = null
            private set

        fun build() = ContactsBuilder(this)
    }
}