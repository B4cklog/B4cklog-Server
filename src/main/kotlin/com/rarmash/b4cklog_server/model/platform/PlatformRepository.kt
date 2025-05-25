package com.rarmash.b4cklog_server.model.platform

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlatformRepository : CrudRepository<Platform, Int>