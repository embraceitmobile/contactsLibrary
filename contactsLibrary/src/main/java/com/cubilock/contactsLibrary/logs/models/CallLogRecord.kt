package com.cubilock.contactsLibrary.logs.models

data class CallLogRecord(
    var id: String? = "",
    var name: String? = "",
    var number: String? = "",
    var formattedNumber: String? = "",
    var callType: Int? = -1,
    var duration: Int? = 0,
    var timestamp: Long? = 0L,
    var cachedNumberType: Int? = -1,
    var cachedNumberLabel: String? = "",
    var cachedMatchedNumber: String? = "",
    var cachedNormalizedNumber: String? = "",
    var simDisplayName: String? = "",
    var phoneAccountId: String? = "",
    var contactId: String? = "",
)