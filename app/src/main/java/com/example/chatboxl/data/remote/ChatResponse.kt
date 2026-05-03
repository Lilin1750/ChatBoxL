package com.example.chatboxl.data.remote

data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val created: Int,
    val model: String,
    val system_fingerprint: String? = null,
    val `object`: String,
    val usage: Usage? = null
)

data class Choice(
    val message: ResponseMessage,
    val finish_reason: String,
    val index: Int,
    val logprobs: LogProbs? = null
)

data class ResponseMessage(
    val role: String,
    val content: String,
    val tool_calls: List<ToolCall>? = null
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class LogProbs(
    val content: List<Any>? = null
)

data class ToolCall(
    val id: String,
    val type: String,
    val function: FunctionCall
)

data class FunctionCall(
    val name: String,
    val arguments: String
)