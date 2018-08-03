package io.userfeeds.cryptocache.opensea

import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

interface OpenSeaRepository : JpaRepository<OpenSeaData, Asset>

@Entity
data class OpenSeaData(
        @EmbeddedId
        val asset: Asset,
        val backgroundColor: String?,
        val externalLink: String,
        val imageUrl: String,
        val name: String?
)

@Embeddable
data class Asset(
        val address: String,
        val token: String
) : Serializable