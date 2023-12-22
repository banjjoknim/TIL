package com.banjjoknim.springimage.application

import com.banjjoknim.springimage.api.CreateImageThumbnailRequest
import com.banjjoknim.springimage.api.CreateThumbnailImageResponse
import com.banjjoknim.springimage.application.support.ThumbnailGenerator
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO

@Service
class ImageThumbnailCreateService {

    fun createThumbnailImage(request: CreateImageThumbnailRequest): CreateThumbnailImageResponse {
        // 이미지를 읽어옴
        val originImageFileBytes = request.originImageFile.bytes
        val imageInputStream = ByteArrayInputStream(originImageFileBytes)
        val originalImage = ImageIO.read(imageInputStream)

        // 썸네일을 생성함
        val thumbnailGenerator = ThumbnailGenerator(
            originalImage,
            request.thumbnailType,
            request.thumbnailMaxWidth,
            request.thumbnailMaxHeight
        )
        val thumbnailBytes = thumbnailGenerator.generateThumbnail()

        // 새로운 파일에 썸네일 데이터를 씀
        val file = File(request.targetPath)
        file.createNewFile()
        file.writeBytes(thumbnailBytes)

        return CreateThumbnailImageResponse(request.targetPath)
    }
}
