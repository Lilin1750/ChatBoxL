package com.example.chatboxl.presentation.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatboxl.databinding.ItemMessageReceivedBinding
import com.example.chatboxl.databinding.ItemMessageSentBinding
import com.example.chatboxl.domain.model.Message

class ChatAdapter: ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {
    var onLongItemClick:((Message,android.view.View)-> Unit) ?= null

    companion object{
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    inner class ReceivedViewHolder(internal val binding: ItemMessageReceivedBinding): RecyclerView.ViewHolder(binding.root){

        /**
         *ViewBinding为每一个布局文件生成一个Binding类
         *每一个ViewHolder需要持有对应的Binding对象
         *由此我们就可以通过获取对应的Binding对象来操作布局
         * @param binding.root 就是对应的布局文件
         */

        init {
            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    /**
                     * 将 messageText 作为锚点传给 Activity
                     */
                    onLongItemClick?.invoke(getItem(position), binding.messageText)
                }
                true
            }
        }
    }

    inner class SentViewHolder(internal val binding: ItemMessageSentBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onLongItemClick?.invoke(getItem(position), binding.messageText)
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return when(p1){
            VIEW_TYPE_SENT -> {
                val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(p0.context), p0, false)
                SentViewHolder(binding)
            }
            VIEW_TYPE_RECEIVED -> {
                val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(p0.context), p0, false)
                ReceivedViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val msg = getItem(p1)
        fullBind(p0, msg)
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull() as? Bundle

        if (payload != null){
            partialBind(p0, getItem(p1), payload)
        }else{
            fullBind(p0, getItem(p1))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.isSentByMe) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    private fun fullBind(holder: RecyclerView.ViewHolder, message: Message) {
        /**
         * 创建fullBind方法，用于完整更新
         * 通过判断message的isSentByMe字段，将message对象传递给对应的ViewHolder进行绑定
        */
        when (holder) {
            is SentViewHolder -> {
                holder.binding.messageText.text = message.content
                holder.binding.nameText.text = message.senderName
                holder.binding.timeText.text = message.timestamp
            }
            is ReceivedViewHolder -> {
                holder.binding.messageText.text = message.content
                holder.binding.nameText.text = message.senderName
                holder.binding.timeText.text = message.timestamp
            }
        }
    }

    /**
     * 创建partialBind方法，用于部分更新
     * 通过遍历payload中的key，根据key更新对应的字段
     */
    private fun partialBind(holder: RecyclerView.ViewHolder, message: Message, payload: Bundle) {
        when (holder) {
            is SentViewHolder -> {
                payload.keySet().forEach { key ->
                    when (key) {
                        "KEY_CONTENT" -> holder.binding.messageText.text = payload.getString(key)
                        "KEY_SENDER_NAME" -> holder.binding.nameText.text = payload.getString(key)
                        "KEY_TIMESTAMP" -> holder.binding.timeText.text = payload.getString(key)
                    }
                }
            }
            is ReceivedViewHolder -> {
                payload.keySet().forEach { key ->
                    when (key) {
                        "KEY_CONTENT" -> holder.binding.messageText.text = payload.getString(key)
                        "KEY_SENDER_NAME" -> holder.binding.nameText.text = payload.getString(key)
                        "KEY_TIMESTAMP" -> holder.binding.timeText.text = payload.getString(key)
                    }
                }
            }
        }
    }

    class MessageDiffCallback: DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Message, newItem: Message): Any? {

            val diffBundle = Bundle()

            if (oldItem.content != newItem.content) diffBundle.putString("KEY_CONTENT", newItem.content)
            if (oldItem.senderName != newItem.senderName) diffBundle.putString("KEY_SENDER_NAME", newItem.senderName)
            if (oldItem.timestamp != newItem.timestamp) diffBundle.putString("KEY_TIMESTAMP", newItem.timestamp)

            return if (diffBundle.size()>0) diffBundle else null
        }
    }
}