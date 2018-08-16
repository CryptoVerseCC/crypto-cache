package io.userfeeds.cryptocache.opensea

data class ContextInfoApiModel(
        val background_color: String?,
        val external_link: String,
        val image_preview_url: String?,
        val name: String?
) {
    constructor(openSeaData: OpenSeaData) :
            this(
                    background_color = openSeaData.backgroundColor,
                    external_link = openSeaData.externalLink,
                    image_preview_url = openSeaData.imageUrl,
                    name = openSeaData.name
            )

    companion object {
        val EMPTY = ContextInfoApiModel(
                background_color = null,
                external_link = "https://tokntalk.club/404",
                image_preview_url = null,
                name = null
        )
    }
}
