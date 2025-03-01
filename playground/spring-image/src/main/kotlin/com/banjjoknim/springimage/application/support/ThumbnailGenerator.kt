package com.banjjoknim.springimage.application.support

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import java.awt.Graphics2D
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

data class ThumbnailGenerator(
    private val originalImage: Image,
    private val thumbnailType: String,
    private val thumbnailMaxWidth: Int,
    private val thumbnailMaxHeight: Int,
    private val orientation: ImageOrientation,
) {

    fun generateThumbnail(): ByteArray {
        // 썸네일 크기를 계산
        val thumbnailSize = ThumbnailSize.of(
            originalImage = originalImage,
            thumbnailMaxWidth = thumbnailMaxWidth,
            thumbnailMaxHeight = thumbnailMaxHeight
        )

        /**
         * BufferedImage로 변환, 원본 이미지를 썸네일 크기로 축소
         */
        val bufferedThumbnail = BufferedImage(thumbnailSize.width, thumbnailSize.height, thumbnailImageType())
        val graphics = bufferedThumbnail.createGraphics()
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        val thumbnail = originalImage.getScaledInstance(thumbnailSize.width, thumbnailSize.height, Image.SCALE_SMOOTH)
        graphics.drawImage(thumbnail, 0, 0, null)
        graphics.dispose()

        // 이미지의 회전, 배치 방향을 보정한다
        val correctedImage = doCorrectImageOrientation(bufferedThumbnail, orientation)

        // BufferedImage 의 Bytes 를 output에 씀
        val thumbnailByteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(correctedImage, thumbnailType, thumbnailByteArrayOutputStream)

        return thumbnailByteArrayOutputStream.toByteArray()
    }

    private fun doCorrectImageOrientation(image: BufferedImage, orientation: ImageOrientation): BufferedImage {
        val correctImageTransform = orientation.toImageTransform(image)

        // 새 크기의 BufferedImage 생성
        val correctedImage = BufferedImage(
            correctImageTransform.transformWidth,
            correctImageTransform.transformHeight,
            thumbnailImageType(),
        )
        val g2d: Graphics2D = correctedImage.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)

        // transform 적용 후 이미지 그리기
        g2d.drawImage(image, correctImageTransform.transform, null)
        g2d.dispose()

        return correctedImage
    }

    /**
     * jpg, jpeg는 투명도(알파 채널)을 지원하지 않는다.
     */
    private fun thumbnailImageType(): Int {
        return when (this.thumbnailType) {
            "jpg", "jpeg" -> BufferedImage.TYPE_INT_RGB
            else -> BufferedImage.TYPE_INT_ARGB // e.g. png, webp, ...
        }
    }
}
