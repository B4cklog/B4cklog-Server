package org.b4cklog.server.model.platform

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlatformRepository : CrudRepository<Platform, Int>