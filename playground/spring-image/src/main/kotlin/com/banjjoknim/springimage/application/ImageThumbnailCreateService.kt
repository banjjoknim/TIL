package com.banjjoknim.springimage.application

import com.banjjoknim.springimage.api.CreateImageThumbnailRequest
import com.banjjoknim.springimage.api.CreateThumbnailImageResponse
import com.banjjoknim.springimage.application.support.FilePath
import com.banjjoknim.springimage.application.support.ImageOrientation
import com.banjjoknim.springimage.application.support.ThumbnailGenerator
import com.banjjoknim.springimage.infrastructure.support.ImageMagickProcessor
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO

@Service
class ImageThumbnailCreateService {

    fun createThumbnailImage(request: CreateImageThumbnailRequest): CreateThumbnailImageResponse {
        // 이미지를 읽어옴
        val originImageFile = request.originImageFile
        val originImageFileBytes = originImageFile.bytes

        val originImageFilePath = originImageFile.originalFilename
            ?.let { FilePath(it) } ?: throw IllegalArgumentException("원본 파일 이름이 존재하지 않습니다.")

        val imageInputStream = when (originImageFilePath.isHeic()) {
            true -> ImageMagickProcessor.doConvert(
                ByteArrayInputStream(originImageFileBytes),
                FilePath("temp.heic"),
                FilePath("temp.${request.thumbnailType}"),
            )

            false -> ByteArrayInputStream(originImageFileBytes)
        }

        val originalImage = ImageIO.read(imageInputStream) // '.heic' 파일을 읽을 경우 null을 리턴한다.

        // 이미지 메타 데이터에서 방향, 배치 값 추출
        val orientation = ImageOrientation(ByteArrayInputStream(originImageFileBytes))

        // 썸네일을 생성함
        val thumbnailGenerator = ThumbnailGenerator(
            originalImage = originalImage,
            thumbnailType = request.thumbnailType,
            thumbnailMaxWidth = request.thumbnailMaxWidth,
            thumbnailMaxHeight = request.thumbnailMaxHeight,
            orientation = orientation,
        )
        val thumbnailBytes = thumbnailGenerator.generateThumbnail()

        // 새로운 파일에 썸네일 데이터를 씀
        val targetPath = request.targetPath
        val file = File(targetPath)
        file.createNewFile()
        file.writeBytes(thumbnailBytes)

        ImageMagickProcessor.doIdentify(FilePath(targetPath))

        return CreateThumbnailImageResponse(targetPath)
    }
}
