package com.banjjoknim.springimage.application.support

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifIFD0Directory
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.InputStream

/**
 * 이미지의 회전 방향, 배치 방향 정보.
 */
data class ImageOrientation(
    private val value: Int = DEFAULT_VALUE,
) {

    companion object {
        private const val DEFAULT_VALUE = 1 // 1이 정방향이다.

        private fun extractValue(inputStream: InputStream): Int {
            return runCatching {
                inputStream.use {
                    val metadata = ImageMetadataReader.readMetadata(inputStream)
                    val directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
                    directory?.getInt(ExifIFD0Directory.TAG_ORIENTATION)
                }
            }.getOrNull() ?: DEFAULT_VALUE
        }
    }

    constructor(inputStream: InputStream) : this(
        value = extractValue(inputStream)
    )

    fun toImageTransform(image: BufferedImage): ImageTransform {
        val transform = AffineTransform()
        return when (value) {
            DEFAULT_VALUE -> {
                ImageTransform(transform, image.width, image.height)
            } // 정방향

            2 -> {
                transform.scale(-1.0, 1.0)
                ImageTransform(transform, image.width, image.height)
            } // 좌우 반전

            3 -> {
                transform.translate(image.width.toDouble(), image.height.toDouble())
                transform.rotate(Math.PI)
                ImageTransform(transform, image.width, image.height)
            } // 180도 회전

            4 -> {
                transform.scale(1.0, -1.0)
                ImageTransform(transform, image.width, image.height)
            } // 상하 반전

            5 -> {
                transform.translate(image.height.toDouble(), 0.0)
                transform.rotate(Math.PI / 2)
                transform.scale(-1.0, 1.0)
                ImageTransform(transform, image.height, image.width)
            } // -90도 회전 + 좌우 반전

            6 -> {
                transform.translate(image.height.toDouble(), 0.0)
                transform.rotate(Math.PI / 2)
                ImageTransform(transform, image.height, image.width)
            } // 90도 회전

            7 -> {
                transform.translate(image.height.toDouble(), 0.0)
                transform.rotate(Math.PI / 2)
                transform.scale(-1.0, 1.0)
                ImageTransform(transform, image.height, image.width)
            } // 90도 회전 + 좌우 반전

            8 -> {
                transform.translate(0.0, image.width.toDouble())
                transform.rotate(-Math.PI / 2)
                ImageTransform(transform, image.height, image.width)
            } // -90도 회전

            else -> throw IllegalArgumentException("정의되지 않은 방향값 입니다. 입력된 방향값: $value")
        }
    }
}