package com.mattverwey.m3uplayer.data.model

data class XtreamCredentials(
    val serverUrl: String,
    val username: String,
    val password: String
) {
    fun toApiUrl(): String {
        return "$serverUrl/player_api.php?username=$username&password=$password"
    }
}

data class XtreamAuthResponse(
    val user_info: UserInfo? = null,
    val server_info: ServerInfo? = null,
    val message: String? = null
)

data class UserInfo(
    val username: String? = null,
    val password: String? = null,
    val message: String? = null,
    val auth: Int? = null,
    val status: String? = null,
    val exp_date: String? = null,
    val is_trial: String? = null,
    val active_cons: String? = null,
    val created_at: String? = null,
    val max_connections: String? = null,
    val allowed_output_formats: List<String>? = null
)

data class ServerInfo(
    val url: String? = null,
    val port: String? = null,
    val https_port: String? = null,
    val server_protocol: String? = null,
    val rtmp_port: String? = null,
    val timestamp_now: Long? = null,
    val time_now: String? = null,
    val timezone: String? = null
)
