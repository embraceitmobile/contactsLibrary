package com.cubilock.contactslibrary.builder

import com.cubilock.contactslibrary.models.Email
import com.cubilock.contactslibrary.models.Name
import com.cubilock.contactslibrary.models.Number
import com.cubilock.contactslibrary.models.WorkInfo

class ContactsBuilder(
    val name: Name?,
    val number: Number?,
    val email: Email?,
    val workInfo: WorkInfo?,
    val profilePicture: String?,
    val label: String?
) {

    private constructor(builder: Builder):
            this(builder.name,
                builder.number,
                builder.email,
                builder.workInfo,
                builder.profilePicture,
                builder.label,
            )

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var name: Name? = null
            private set
        var number: Number? = null
            private set
        var email: Email? = null
            private set
        var workInfo: WorkInfo? = null
            private set
        var profilePicture: String? = null
            private set
        var label: String? = null
            private set

        fun build() = ContactsBuilder(this)
    }
}