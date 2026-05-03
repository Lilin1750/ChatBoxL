package com.example.chatboxl.presentation.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatboxl.databinding.ActivityChatBinding
import com.example.chatboxl.domain.model.Message
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatboxl.R

class ChatActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.linearLayout) { view , insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.setPadding(0,0,0,imeHeight)
            WindowInsetsCompat.CONSUMED
        }

        val chatAdapter = ChatAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }

        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages.toList())
        }

        binding.sentBtn.setOnClickListener {
            val message = binding.editText.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.editText.text?.clear()
            }
        }

        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                val message = binding.editText.text.toString().trim()
                if (message.isNotEmpty()) {
                    viewModel.sendMessage(message)
                    binding.editText.text?.clear()
                }
                true
            } else {
                false
            }
        }

        chatAdapter.onLongItemClick = { message, view ->
            showActionMenu(message, view)
            //就是在这里将anchorView传给了popupMenu，从这里拿的坐标
        }
    }

    private fun showActionMenu(message: Message,anchorView: View) {

        anchorView.post {
            /**
            val location1 = IntArray(2)
            anchorView.getLocationOnScreen(location1)
            Log.d("DEBUG", "创建 PopupMenu 时坐标: (${location1[0]}, ${location1[1]})")
             */

            val popup = PopupMenu(this, anchorView)

            popup.menuInflater.inflate(R.menu.menu_message_actions,popup.menu )
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        viewModel.removeMessage(message)
                        true
                    }
                    R.id.action_copy -> {
                        Toast.makeText( this, "模拟复制消息", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
            /**
             * val location2 = IntArray(2)
             * anchorView.getLocationOnScreen(location2)
             * Log.d("DEBUG", "Popup 显示时坐标: (${location2[0]}, ${location2[1]})")
             *
             * android:windowSoftInputMode="adjustPan"这个设置呼出软键盘时，视图不重新绘制，所以两次测量坐标一样
             * 但是又让view移动了，导致我菜单锚定不准
            */
        }
        /** popupMenu位置显示正确，重点在于和anchorView的锚定
         * popupMenu与anchorView的锚定的位置关系依靠gravity
         */
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}