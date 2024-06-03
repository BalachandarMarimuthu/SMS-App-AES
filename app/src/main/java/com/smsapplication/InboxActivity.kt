package com.smsapplication

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.smsapplication.adapter.InboxAdapter
import com.smsapplication.data.AppDataBase
import com.smsapplication.databinding.ActivityInboxBinding
import com.smsapplication.model.InboxMessage
import com.smsapplication.utils.mySMSPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InboxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInboxBinding
    private lateinit var database: AppDataBase
    private val list = mutableListOf<InboxMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInboxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mySMSPermission(this, this)
        setupToolbar()
        database = AppDataBase.getDatabaseClient(this)
        getMessages()
    }

    private fun setupToolbar() {
        binding.toolbar.toolTitle.text = getString(R.string.inbox)
        binding.toolbar.toolBack.visibility = VISIBLE
        binding.toolbar.toolBack.setOnClickListener { finish() }
    }

    private fun getMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            if (database.inbox().getInboxMessages().isNotEmpty()) {
                list.addAll(database.inbox().getInboxMessages())
                applyAdapter(list)
            } else noData()
        }
    }

    private fun noData() {
        runOnUiThread {
            binding.messageList.visibility = View.GONE
            binding.noData.noDataLt.visibility = VISIBLE
        }
    }

    private fun applyAdapter(list: MutableList<InboxMessage>) {
        println(list)
        runOnUiThread {
            binding.messageList.layoutManager = LinearLayoutManager(this@InboxActivity)
            binding.messageList.adapter = InboxAdapter(this@InboxActivity, list.reversed())
        }
    }
}
