package org.b4cklog.server.model.user

import jakarta.persistence.*

@Entity
@Table(name = "friend_requests")
data class FriendRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    val sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    val receiver: User,

    @Enumerated(EnumType.STRING)
    var status: FriendRequestStatus = FriendRequestStatus.PENDING
)

enum class FriendRequestStatus {
    PENDING, ACCEPTED, REJECTED
} 