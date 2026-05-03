package com.example.chatboxl.presentation.chat

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatboxl.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ChatActivity"
    }

    private val viewModel: ChatViewModel by viewModels()
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatAdapter = ChatAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }

        chatAdapter.registerAdapterDataObserver(object : androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
            /**
             * 当RecyclerView中插入新项时回调，自动滚动到最新插入的项
             * 用于确保新消息插入后能够自动显示在可视区域底部
             *
             * @param positionStart 插入项的起始位置
             * @param itemCount 插入的项数量
             * 这一段实现了RecyclerView滚动到最新插入项的功能
             */
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(positionStart + itemCount - 1)
            }
        })

        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages.toList())
        }

        binding.sentBtn.setOnClickListener {
            val message = binding.editText.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.editText.text.clear()
            }
        }

        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                val message = binding.editText.text.toString().trim()
                if (message.isNotEmpty()) {
                    viewModel.sendMessage(message)
                    binding.editText.text.clear()
                }
                true
            } else {
                false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}