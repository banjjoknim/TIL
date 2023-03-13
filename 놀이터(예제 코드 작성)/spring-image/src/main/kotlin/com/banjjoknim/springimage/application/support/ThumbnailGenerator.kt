package com.banjjoknim.springimage.application.support

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import java.awt.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ThumbnailGenerator(
    private val originalImage: Image,
    private val thumbnailType: String,
    private val thumbnailMaxWidth: Int,
    private val thumbnailMaxHeight: Int,
) {

    companion object {

    }

    fun generateThumbnail(): ByteArray {
        // 썸네일 크기를 계산
        val thumbnailSize = ThumbnailSize.of(
            originalImage = originalImage,
            thumbnailMaxWidth = thumbnailMaxWidth,
            thumbnailMaxHeight = thumbnailMaxHeight
        )

        // 원본 이미지를 썸네일 크기로 축소
        val thumbnail = originalImage.getScaledInstance(thumbnailSize.width, thumbnailSize.height, Image.SCALE_SMOOTH)

        // BufferedImage로 변환, 투명성 지원(알파 채널)
        val bufferedThumbnail = BufferedImage(thumbnailSize.width, thumbnailSize.height, BufferedImage.TYPE_INT_ARGB)
        val graphics = bufferedThumbnail.createGraphics()
        graphics.drawImage(thumbnail, 0, 0, null)
        graphics.dispose()

        // BufferedImage 의 Bytes 를 output에 씀
        val thumbnailByteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedThumbnail, thumbnailType, thumbnailByteArrayOutputStream)

        return thumbnailByteArrayOutputStream.toByteArray()
    }
}
