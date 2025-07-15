package org.b4cklog.server.dto

import org.b4cklog.server.model.user.FriendRequestStatus

data class FriendRequestDto(
    val id: Int,
    val senderId: Int,
    val senderUsername: String,
    val receiverId: Int,
    val receiverUsername: String,
    val status: FriendRequestStatus
) 