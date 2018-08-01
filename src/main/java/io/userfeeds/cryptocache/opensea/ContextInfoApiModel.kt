package io.userfeeds.cryptocache.opensea

data class ContextInfoApiModel(
        val background_color: String?,
        val owner: String,
        val image_url: String,
        val name: String
) {
    constructor(openSeaData: OpenSeaData) :
            this(openSeaData.backgroundColor, openSeaData.owner, openSeaData.imageUrl, openSeaData.name)
}