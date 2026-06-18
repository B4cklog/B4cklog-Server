package org.b4cklog.server.model.user

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FriendRequestRepository : JpaRepository<FriendRequest, Int> {
    @EntityGraph(attributePaths = ["sender", "receiver"])
    fun findByPairKey(pairKey: String): FriendRequest?

    @EntityGraph(attributePaths = ["sender", "receiver"])
    @Query("SELECT fr FROM FriendRequest fr WHERE ((fr.sender.id = :user1 AND fr.receiver.id = :user2) OR (fr.sender.id = :user2 AND fr.receiver.id = :user1))")
    fun findBetweenUsers(@Param("user1") user1: Int, @Param("user2") user2: Int): List<FriendRequest>

    @EntityGraph(attributePaths = ["sender", "receiver"])
    @Query("SELECT fr FROM FriendRequest fr WHERE (fr.sender.id = :userId OR fr.receiver.id = :userId) AND fr.status = :status")
    fun findAllByUserAndStatus(@Param("userId") userId: Int, @Param("status") status: FriendRequestStatus): List<FriendRequest>

    @EntityGraph(attributePaths = ["sender", "receiver"])
    fun findByReceiverIdAndStatus(receiverId: Int, status: FriendRequestStatus): List<FriendRequest>

    @EntityGraph(attributePaths = ["sender", "receiver"])
    fun findBySenderIdAndStatus(senderId: Int, status: FriendRequestStatus): List<FriendRequest>
}
