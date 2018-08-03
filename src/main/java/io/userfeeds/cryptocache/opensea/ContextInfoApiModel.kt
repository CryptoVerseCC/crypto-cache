package io.userfeeds.cryptocache.opensea

data class ContextInfoApiModel(
        val background_color: String?,
        val external_link: String,
        val image_url: String,
        val name: String?
) {
    constructor(openSeaData: OpenSeaData) :
            this(openSeaData.backgroundColor, openSeaData.externalLink, openSeaData.imageUrl, openSeaData.name)
}
