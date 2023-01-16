package com.cubilock.contactsLibrary.logs.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Looper
import android.provider.CallLog
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cubilock.contactsLibrary.extensions.ensureBackgroundThread
import com.cubilock.contactsLibrary.extensions.getQuestionMarks
import com.cubilock.contactsLibrary.extensions.times
import com.cubilock.contactsLibrary.helpers.ContactHelper
import com.cubilock.contactsLibrary.logs.models.CallLogRecord
import java.util.*
import kotlin.collections.ArrayList


object LogsHelper {

    private const val TAG = "LogsHelper"
    private const val ALREADY_RUNNING = "ALREADY_RUNNING"
    private const val PERMISSION_NOT_GRANTED = "PERMISSION_NOT_GRANTED"
    private const val INTERNAL_ERROR = "INTERNAL_ERROR"
    private const val METHOD_GET = "get"
    private const val METHOD_QUERY = "query"
    private const val OPERATOR_LIKE = "LIKE"
    private const val OPERATOR_GT = ">"
    private const val OPERATOR_BETWEEN = "BETWEEN"
    private const val OPERATOR_AND = "AND"
    private const val OPERATOR_LT = "<"
    private const val OPERATOR_EQUALS = "="

    private val CURSOR_PROJECTION = arrayOf(
        CallLog.Calls.CACHED_FORMATTED_NUMBER,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE,
        CallLog.Calls.DURATION,
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.CACHED_NUMBER_TYPE,
        CallLog.Calls.CACHED_NUMBER_LABEL,
        CallLog.Calls.CACHED_MATCHED_NUMBER,
        CallLog.Calls.PHONE_ACCOUNT_ID,
        CallLog.Calls._ID,
        CallLog.Calls.CACHED_NORMALIZED_NUMBER
    )

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun queryLogs(context: Context, query: String?): MutableList<CallLogRecord> {
        val entries: MutableList<CallLogRecord> = ArrayList()
        val subscriptionManager = ContextCompat.getSystemService(context, SubscriptionManager::class.java)
        var subscriptions: List<SubscriptionInfo>? = null
        if (subscriptionManager != null) {
            subscriptions = subscriptionManager.activeSubscriptionInfoList
        }
        try {
            context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                CURSOR_PROJECTION,
                query,
                null,
                CallLog.Calls.DATE + " DESC"
            ).use { cursor ->

                while (cursor != null && cursor.moveToNext()) {
                    var number = cursor.getString(11)
                    if(number ==  null){
                        number = cursor.getString(1)
                    }
                    val contactId = ContactHelper.getContactId(context, number)

                    val record = CallLogRecord(
                        id  = cursor.getString(10),
                        name = cursor.getString(5),
                        number = cursor.getString(1),
                        formattedNumber = cursor.getString(0),
                        callType = cursor.getInt(2),
                        duration = cursor.getInt(4),
                        timestamp = cursor.getLong(3),
                        cachedNumberType = cursor.getInt(6),
                        cachedNumberLabel = cursor.getString(7),
                        cachedMatchedNumber = cursor.getString(8),
                        cachedNormalizedNumber = cursor.getString(11),
                        simDisplayName = getSimDisplayName(subscriptions, cursor.getString(9)),
                        phoneAccountId = cursor.getString(9),
                        contactId = contactId
                    )
                    entries.add(record)
                }
                cursor?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return entries
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getSelectiveLogs(context: Context, map: Map<String, String>): MutableList<CallLogRecord> {
        val dateFrom: String? = map["dateFrom"]
        val dateTo: String? = map["dateTo"]
        val durationFrom: String? = map["durationFrom"]
        val durationTo: String? = map["durationTo"]
        val name: String? = map["name"]
        val number: String? = map["number"]
        val type: String? = map["type"]


        val predicates: MutableList<String> = ArrayList()
        generatePredicate(predicates, CallLog.Calls.DATE, OPERATOR_GT, dateFrom)
        generatePredicate(predicates, CallLog.Calls.DATE, OPERATOR_LT, dateTo)
        generatePredicate(predicates, CallLog.Calls.DURATION, OPERATOR_GT, durationFrom)
        generatePredicate(predicates, CallLog.Calls.DURATION, OPERATOR_LT, durationTo)
        generatePredicate(predicates, CallLog.Calls.CACHED_NAME, OPERATOR_LIKE, name)
        generatePredicate(predicates, CallLog.Calls.TYPE, OPERATOR_EQUALS, type)
        if (!number.isNullOrEmpty()) {
            val namePredicates: MutableList<String> = ArrayList()
            generatePredicate(namePredicates, CallLog.Calls.NUMBER, OPERATOR_LIKE, number)
            generatePredicate(namePredicates,
                CallLog.Calls.CACHED_MATCHED_NUMBER,
                OPERATOR_LIKE,
                number)
            generatePredicate(namePredicates, CallLog.Calls.PHONE_ACCOUNT_ID, OPERATOR_LIKE, number)
            predicates.add("(${namePredicates.joinToString( " OR ")}")
        }
        return queryLogs(context, "${predicates.joinToString(" AND ")}")
    }

    private fun generatePredicate(
        predicates: MutableList<String>,
        field: String,
        operator: String,
        value: String?,
    ) {
        if (value == null || value.isEmpty()) {
            return
        }
        val escapedValue: String = if (operator.equals(OPERATOR_LIKE, ignoreCase = true)) {
            "'%$value%'"
        } else {
            "'$value'"
        }
        predicates.add("$field $operator $escapedValue")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun getSimDisplayName(
        subscriptions: List<SubscriptionInfo>?,
        accountId: String?,
    ): String? {
        if (accountId != null && subscriptions != null) {
            for (info in subscriptions) {
                if (Integer.toString(info.subscriptionId) == accountId ||
                    accountId.contains(info.iccId)
                ) {
                    return info.displayName.toString()
                }
            }
        }
        return null
    }

    fun removeRecentCalls(context: Context, ids: ArrayList<Int>, callback: () -> Unit) {
        ensureBackgroundThread {
            val uri = CallLog.Calls.CONTENT_URI
            ids.chunked(30).forEach { chunk ->
                val selection = "${CallLog.Calls._ID} IN (${getQuestionMarks(chunk.size)})"
                val selectionArgs = chunk.map { it.toString() }.toTypedArray()
                context.contentResolver.delete(uri, selection, selectionArgs)
            }
            callback()
        }
    }

    fun removeAllRecentCalls(context: Context, callback: () -> Unit) {
        ensureBackgroundThread {
            val uri = CallLog.Calls.CONTENT_URI
            context.contentResolver.delete(uri, null, null)
            callback()
        }
    }
    fun removeLogsByDate(context: Context, dateFrom:String, dateTo:String, callback: () -> Unit) {
        ensureBackgroundThread {
            val uri = CallLog.Calls.CONTENT_URI
            val selection = "${CallLog.Calls.DATE} >= ${dateFrom} AND ${CallLog.Calls.DATE} <= ${dateTo}"
            context.contentResolver.delete(uri, selection, null)
            callback()
        }
    }
    fun removeLogsByNumber(context: Context, number: String, callback: () -> Unit) {
        ensureBackgroundThread {
            val uri = CallLog.Calls.CONTENT_URI
            val selection = "${CallLog.Calls.NUMBER} LIKE %${number}% OR ${CallLog.Calls.CACHED_MATCHED_NUMBER} LIKE %${number}%"
            context.contentResolver.delete(uri, selection, null)
            callback()
        }
    }
    fun removeLogsOfNumberAtDate(context: Context, number: String, dateFrom:String, dateTo:String, callback: () -> Unit) {
        ensureBackgroundThread {
            val uri = CallLog.Calls.CONTENT_URI
            val selection = "(${CallLog.Calls.NUMBER} LIKE '%${number}%' OR ${CallLog.Calls.CACHED_MATCHED_NUMBER} LIKE '%${number}%') AND ${CallLog.Calls.DATE} >= ${dateFrom} AND ${CallLog.Calls.DATE} <= ${dateTo}"
            context.contentResolver.delete(uri, selection, null)
            callback()
        }
    }
}