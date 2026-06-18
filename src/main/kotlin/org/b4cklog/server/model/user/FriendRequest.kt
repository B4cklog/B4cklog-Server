package org.b4cklog.server.model.user

import jakarta.persistence.*

@Entity
@Table(
    name = "friend_requests",
    uniqueConstraints = [UniqueConstraint(name = "uk_friend_request_pair", columnNames = ["pair_key"])]
)
data class FriendRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    var sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    var receiver: User,

    @Enumerated(EnumType.STRING)
    var status: FriendRequestStatus = FriendRequestStatus.PENDING,

    @Column(name = "pair_key", nullable = false, length = 32)
    var pairKey: String = pairKey(sender.id, receiver.id)
) {
    @PrePersist
    @PreUpdate
    fun updatePairKey() {
        pairKey = pairKey(sender.id, receiver.id)
    }

    companion object {
        fun pairKey(user1: Int, user2: Int): String =
            "${minOf(user1, user2)}:${maxOf(user1, user2)}"
    }
}

enum class FriendRequestStatus {
    PENDING, ACCEPTED, REJECTED
}
