package com.example.chatboxl.data.remote

data class ChatRequest(
    val messages: List<ContextMessages>,
    val model: String,
    val thinking: Thinking? = null,
    val reasoning_effort: String? = null,
    val stream: Boolean = false,
    val stream_options: StreamOptions? = null,
    val temperature: Double? = null,
    val top_p: Double? = null,
    val max_tokens: Int? = null,
    val stop: List<String>? = null,
    val tools: List<Tool>? = null,
    val tool_choice: String? = null,
    val logprobs: Boolean? = null,
    val top_logprobs: Int? = null,
    val response_format: ResponseFormat? = null
)

data class ContextMessages(
    val role: String,
    val content: String,
    val name: String? = null
)

data class Thinking(
    val type: String
)

data class StreamOptions(
    val include_usage: Boolean = false
)

data class Tool(
    val type: String,
    val function: FunctionDefinition
)

data class FunctionDefinition(
    val name: String,
    val description: String? = null,
    val parameters: Map<String, Any>? = null
)

data class ResponseFormat(
    val type: String
)