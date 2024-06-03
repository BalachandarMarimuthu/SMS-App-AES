package com.smsapplication

import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smsapplication.adapter.SentAdapter
import com.smsapplication.data.AppDataBase
import com.smsapplication.databinding.ActivitySendMessagesBinding
import com.smsapplication.databinding.SendDialogBinding
import com.smsapplication.model.SentMessage
import com.smsapplication.utils.AES
import com.smsapplication.utils.mySMSPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendMessagesBinding
    private val sentList = mutableListOf<SentMessage>()
    private lateinit var database: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mySMSPermission(this, this)
        database = AppDataBase.getDatabaseClient(this)
        setupToolbar()
        binding.fab.setOnClickListener {
            openBottomSheet()
        }
        getMessages()
    }

    private fun setupToolbar() {
        binding.toolbar.toolTitle.text = getString(R.string.sent)
        binding.toolbar.toolBack.visibility = View.VISIBLE
        binding.toolbar.toolBack.setOnClickListener { finish() }
    }

    private fun openBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.send_dialog, null)
        val dialogBinding = SendDialogBinding.bind(view)
        dialogBinding.send.setOnClickListener {
            val number = dialogBinding.mobileNumberEdt.text.toString()
            val message = dialogBinding.messageEdt.text.toString()
            if (number.isNotEmpty() && message.isNotEmpty()) {
                val encryptedMessage = AES.encrypt(message)
                try {
                    val smsManager = getSmsManager()
                    smsManager.sendTextMessage(number, null, (encryptedMessage), null, null)
                    Toast.makeText(this, "Message Sent", Toast.LENGTH_LONG).show()
                    insertMessage(number, encryptedMessage!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Please enter all the data.." + e.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                dialog.dismiss()
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun getSmsManager(): SmsManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            applicationContext.getSystemService(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }
    }

    private fun insertMessage(sender: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = mutableListOf<SentMessage>()
            if (database.sentMessages().getSentMessages().isNotEmpty()) {
                list.addAll(database.sentMessages().getSentMessages())
                list.add(SentMessage(sender, message))
                database.sentMessages().insertAll(list)
            } else {
                list.add(SentMessage(sender, message))
                database.sentMessages().insertAll(list)
            }
            applyAdapter(list)
        }
    }

    private fun getMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            if (database.sentMessages().getSentMessages().isNotEmpty()) {
                sentList.addAll(database.sentMessages().getSentMessages())
                applyAdapter(sentList)
            } else noData()
        }
    }

    private fun noData() {
        runOnUiThread {
            binding.messageList.visibility = View.GONE
            binding.noData.noDataLt.visibility = View.VISIBLE
        }
    }

    private fun applyAdapter(list: MutableList<SentMessage>) {
        println(list)
        runOnUiThread {
            binding.messageList.layoutManager = LinearLayoutManager(this@SendMessageActivity)
            binding.messageList.adapter = SentAdapter(this@SendMessageActivity, list.reversed())
        }
    }
}
