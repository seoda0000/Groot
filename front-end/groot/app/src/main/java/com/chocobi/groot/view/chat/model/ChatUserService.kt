package com.chocobi.groot.view.chat.model

import com.chocobi.groot.data.BasicResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class ChatUser(
    var uId: String,
    var nickname: String,
    var profile: String,
    var userPK: Int
) {
    constructor() : this("", "", "", 0)
}

interface ChatUserListService {
    @GET("/api/chattings/list")
    fun requestChatUserList(

    ): Call<ChatUserListResponse>
}

interface AddChatRoomService {
    @POST("/api/chattings")
    fun requestAddChatRoom(
        @Body params: ChatRoomRequest
    ): Call<BasicResponse>
}

class ChatRoomRequest internal constructor(
    val userPK: String?,
    val roomId: String
)

data class ChatMessage(
    var message: String?,
    var sendId: String?,
    var saveTime: String?
){
    constructor():this("", "", "")
}