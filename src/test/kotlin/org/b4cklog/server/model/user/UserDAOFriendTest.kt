package org.b4cklog.server.model.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.b4cklog.server.model.game.GameRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

class UserDAOFriendTest {
    private val userRepository = mockk<UserRepository>()
    private val gameRepository = mockk<GameRepository>()
    private val friendRequestRepository = mockk<FriendRequestRepository>(relaxed = true)
    private val dao = UserDAO(userRepository, gameRepository, friendRequestRepository)

    private val alice = User(id = 1, username = "alice")
    private val bob = User(id = 2, username = "bob")

    @Test
    fun `creates a pending request with canonical pair key`() {
        every { userRepository.findById(1) } returns Optional.of(alice)
        every { userRepository.findById(2) } returns Optional.of(bob)
        every { friendRequestRepository.findByPairKey("1:2") } returns null
        every { friendRequestRepository.saveAndFlush(any()) } answers { firstArg() }

        dao.sendFriendRequest(1, 2)

        verify {
            friendRequestRepository.saveAndFlush(match {
                it.sender == alice &&
                    it.receiver == bob &&
                    it.status == FriendRequestStatus.PENDING &&
                    it.pairKey == "1:2"
            })
        }
    }

    @Test
    fun `reuses a rejected request in the new direction`() {
        val request = FriendRequest(
            id = 10,
            sender = alice,
            receiver = bob,
            status = FriendRequestStatus.REJECTED
        )
        every { userRepository.findById(1) } returns Optional.of(alice)
        every { userRepository.findById(2) } returns Optional.of(bob)
        every { friendRequestRepository.findByPairKey("1:2") } returns request
        every { friendRequestRepository.save(request) } returns request

        dao.sendFriendRequest(2, 1)

        assertSame(bob, request.sender)
        assertSame(alice, request.receiver)
        assertEquals(FriendRequestStatus.PENDING, request.status)
    }

    @Test
    fun `cannot accept someone else's request`() {
        val request = FriendRequest(id = 10, sender = alice, receiver = bob)
        every { friendRequestRepository.findById(10) } returns Optional.of(request)

        val error = assertThrows<ResponseStatusException> {
            dao.acceptFriendRequest(10, alice.id)
        }

        assertEquals(HttpStatus.FORBIDDEN, error.statusCode)
    }

    @Test
    fun `cannot accept a request that is no longer pending`() {
        val request = FriendRequest(
            id = 10,
            sender = alice,
            receiver = bob,
            status = FriendRequestStatus.REJECTED
        )
        every { friendRequestRepository.findById(10) } returns Optional.of(request)

        val error = assertThrows<ResponseStatusException> {
            dao.acceptFriendRequest(10, bob.id)
        }

        assertEquals(HttpStatus.CONFLICT, error.statusCode)
    }

    @Test
    fun `rejects duplicate pending request`() {
        val request = FriendRequest(id = 10, sender = alice, receiver = bob)
        every { userRepository.findById(1) } returns Optional.of(alice)
        every { userRepository.findById(2) } returns Optional.of(bob)
        every { friendRequestRepository.findByPairKey("1:2") } returns request

        val error = assertThrows<ResponseStatusException> {
            dao.sendFriendRequest(1, 2)
        }

        assertEquals(HttpStatus.CONFLICT, error.statusCode)
    }

    @Test
    fun `removing a missing friendship returns not found`() {
        every { friendRequestRepository.findBetweenUsers(1, 2) } returns emptyList()

        val error = assertThrows<ResponseStatusException> {
            dao.removeFriend(1, 2)
        }

        assertEquals(HttpStatus.NOT_FOUND, error.statusCode)
    }
}
