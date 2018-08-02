package io.userfeeds.cryptocache.opensea

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Entity
import javax.persistence.Id

interface OpenSeaRepository : JpaRepository<OpenSeaData, String>

@Entity
data class OpenSeaData(
        @Id
        val asset: String,
        val backgroundColor: String?,
        val imageUrl: String,
        val name: String
)