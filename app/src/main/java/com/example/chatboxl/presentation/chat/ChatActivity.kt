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

    private val TAG = "ChatActivity"

    private val viewModel: ChatViewModel by viewModels()
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: 开始创建")

        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: 布局已设置")

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            Log.d(TAG, "WindowInsets 集中处理: IME=$imeHeight, StatusBar=$statusBarHeight, NavBar=$navigationBarHeight")

            binding.appBar.setPadding(0, statusBarHeight, 0, 0)

            if (imeHeight > 0) {
                // 软键盘弹出，让输入框避开键盘
                binding.linearLayout.setPadding(0, 0, 0, imeHeight)
                Log.d(TAG, "软键盘弹出，底部 padding=$imeHeight")
            } else {
                // 软键盘收起，让输入框避开底部导航栏 (小白条)
                binding.linearLayout.setPadding(0, 0, 0, navigationBarHeight)
                Log.d(TAG, "软键盘收起，底部 padding=$navigationBarHeight")
            }

            binding.recyclerView.setPadding(0, 0, 0, 0)
            
            v.setPadding(0, 0, 0, 0)

            WindowInsetsCompat.CONSUMED
        }

        val chatAdapter = ChatAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                //列表从底部开始
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
        Log.d(TAG, "onCreate: RecyclerView 已初始化")

        viewModel.messages.observe(this) { messages ->
            Log.d(TAG, "messages observe: 收到 ${messages.size} 条消息")
            chatAdapter.submitList(messages.toList())
        }

        binding.toolbar.setNavigationOnClickListener {
            Log.d(TAG, "toolbar: 返回按钮点击")
            onBackPressedDispatcher.onBackPressed()
        }

        binding.sentBtn.setOnClickListener {
            val message = binding.editText.text.toString().trim()
            Log.d(TAG, "sentBtn: 发送消息='$message'")
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.editText.text?.clear()
            }
        }

        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                val message = binding.editText.text.toString().trim()
                Log.d(TAG, "editText: IME_ACTION_SEND, 消息='$message'")
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
            Log.d(TAG, "onLongItemClick: 长按消息")
            showActionMenu(message, view)
            //就是在这里将anchorView传给了popupMenu，从这里拿的坐标
        }

        Log.d(TAG, "onCreate: 完成")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: 调用")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: 调用")
        Log.d(TAG, "onResume: 根布局宽度=${binding.main.width}, 高度=${binding.main.height}")
        Log.d(TAG, "onResume: 根布局可见性=${binding.main.visibility}")
    }

    private fun showActionMenu(message: Message, anchorView: View) {

        anchorView.post {

            /*
            val location1 = IntArray(2)
            anchorView.getLocationOnScreen(location1)
            Log.d("DEBUG", "创建 PopupMenu 时坐标: (${location1[0]}, ${location1[1]})")
             */

            val popup = PopupMenu(this, anchorView)

            popup.menuInflater.inflate(R.menu.menu_message_actions, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        viewModel.removeMessage(message)
                        true
                    }
                    R.id.action_copy -> {
                        Toast.makeText(this, "模拟复制消息", Toast.LENGTH_SHORT).show()
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
        Log.d(TAG, "onDestroy: 调用")
        _binding = null
    }
}