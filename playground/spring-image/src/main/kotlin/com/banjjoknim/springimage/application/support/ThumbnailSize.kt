package com.banjjoknim.springimage.application.support

import java.awt.Image

data class ThumbnailSize(
    val width: Int,
    val height: Int,
) {

    companion object {
        private const val SQUARE_ASPECT_RATIO = 1.0

        fun of(originalImage: Image, thumbnailMaxWidth: Int, thumbnailMaxHeight: Int): ThumbnailSize {
            val originalWidth = originalImage.getWidth(null)
            val originalHeight = originalImage.getHeight(null)
            val aspectRatio = originalWidth.toDouble() / originalHeight.toDouble()
            when (originalWidth > thumbnailMaxWidth || originalHeight > thumbnailMaxHeight) {
                true -> {
                    if (aspectRatio > SQUARE_ASPECT_RATIO) {
                        return ThumbnailSize(thumbnailMaxWidth, (thumbnailMaxWidth / aspectRatio).toInt())
                    }
                    return ThumbnailSize((thumbnailMaxHeight * aspectRatio).toInt(), thumbnailMaxHeight)
                }
                else -> {
                    return ThumbnailSize(originalWidth, originalHeight)
                }
            }
        }
    }
}
