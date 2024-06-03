package com.smsapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.smsapplication.data.AppDataBase
import com.smsapplication.model.InboxMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SMSReceiver : BroadcastReceiver() {

    lateinit var database: AppDataBase

    override fun onReceive(context: Context?, intent: Intent?) {
        database = AppDataBase.getDatabaseClient(context!!)
        val bundle = intent?.extras
        if (bundle != null) {
            val smsObj = bundle.get("pdus") as Array<*>
            for (i in smsObj.indices) {
                val smsMessage = SmsMessage.createFromPdu(smsObj[i] as ByteArray)
                val sender = smsMessage.originatingAddress
                val message = smsMessage.messageBody
                insertMessage(sender!!, message)
            }
        }
    }

    private fun insertMessage(sender: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = mutableListOf<InboxMessage>()
            if (database.inbox().getInboxMessages().isNotEmpty()) {
                list.addAll(database.inbox().getInboxMessages())
                list.add(InboxMessage(sender, message))
                database.inbox().insertAll(list)
            } else {
                list.add(InboxMessage(sender, message))
                database.inbox().insertAll(list)
            }
        }
    }
}
