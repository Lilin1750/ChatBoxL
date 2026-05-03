package com.example.chatboxl.presentation.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatboxl.BuildConfig
import com.example.chatboxl.data.remote.ChatRequest
import com.example.chatboxl.data.remote.ChatResponse
import com.example.chatboxl.data.remote.ContextMessages
import com.example.chatboxl.data.remote.RetrofitClient
import com.example.chatboxl.domain.model.Message
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatViewModel : ViewModel() {

    companion object {
        private const val TAG = "ChatViewModel"
    }

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val disposable = CompositeDisposable()
    private var messageIdCounter = 0

    /*
     * 时间格式化器，格式：yyyy-MM-dd HH:mm:ss
     */
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        _messages.value = mutableListOf()
    }

    fun sendMessage(userInput: String) {
        //如果用户输入为空，则不处理并打印日志
        if (userInput.isBlank()){
            Log.w(TAG, "用户输入为空")
             return
        }

        val currentMessages = _messages.value ?: mutableListOf()

        /*
         * 创建用户消息（Domain Model）
         */
        val userMessage = Message(
            content = userInput,
            timestamp = LocalDateTime.now().format(formatter),
            isSentByMe = true,
            senderName = "您",
            senderAvatar = null,
            id = messageIdCounter++
        )

        // 添加用户消息到列表
        addMessage(userMessage)

        /*
         * 将 Domain Model 转换为 Data Layer 的 DTO
         * 只转换需要的消息历史用于 API 请求
         */
        val contextMessages = concatMessages(currentMessages + userMessage)

        /*
         * 构建网络请求对象（Data Layer DTO）
         */
        val chatRequest = ChatRequest(
            model = "deepseek-chat",
            messages = contextMessages,
            stream = false
        )

        /*
         * 执行网络请求
         */
        val apiDisposable = RetrofitClient.apiService.getChatResponse(
            token = "Bearer ${BuildConfig.DEEPSEEK_API_KEY}",
            chatRequest = chatRequest
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    /*
                     * 将网络响应（Data Layer）转换为 Domain Model
                     */
                    val reply = response.choices.firstOrNull()?.message?.content
                    val aiMessage = Message(
                        content = reply ?: "无回复",
                        timestamp = LocalDateTime.now().format(formatter),
                        isSentByMe = false,
                        senderName = "DeepseekV4",
                        senderAvatar = null,
                        id = messageIdCounter++
                    )
                    addMessage(aiMessage)
                },
                { throwable ->
                    /*
                     * 处理网络请求异常，创建错误消息（Domain Model）
                     */
                    Log.e(TAG, "网络请求失败: ${throwable.message}")
                    val errorMessage = Message(
                        content = "请求失败: ${throwable.localizedMessage}",
                        timestamp = LocalDateTime.now().format(formatter),
                        isSentByMe = false,
                        senderName = "系统",
                        senderAvatar = null,
                        id = messageIdCounter++
                    )
                    addMessage(errorMessage)
                }
            )

        disposable.add(apiDisposable)
    }

    /*
     * 将 Domain Model（Message）转换为 Data Layer DTO（RequestMessage）
     * ViewModel 负责两层之间的数据转换
     */
    private fun concatMessages(messages: List<Message>): List<ContextMessages> {
        return messages.map { message ->
            ContextMessages(
                role = if (message.isSentByMe) "user" else "assistant",
                content = message.content
            )
        }
    }

    /*
     * 添加消息到列表
     */
    private fun addMessage(message: Message) {
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(message)
        _messages.value = currentMessages
    }

    override fun onCleared() {
        super.onCleared()
        //释放资源
        disposable.clear()
    }
}