package com.smsapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smsapplication.databinding.MessageListItemBinding
import com.smsapplication.model.InboxMessage
import com.smsapplication.utils.AES

class InboxAdapter(private var context: Context, private var list: List<InboxMessage>) :
    RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    class ViewHolder(productList: MessageListItemBinding) :
        RecyclerView.ViewHolder(productList.root) {
        val number = productList.number
        val message = productList.message
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MessageListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.number.text = list[position].number
        val message = list[position].message
        holder.message.text = AES.decrypt(message)
    }

    override fun getItemCount() = list.size
}
