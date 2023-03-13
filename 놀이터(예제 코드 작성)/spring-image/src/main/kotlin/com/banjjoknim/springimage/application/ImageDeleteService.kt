package com.banjjoknim.springimage.application

import com.banjjoknim.springimage.api.DeleteImageResponse
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException

@Service
class ImageDeleteService {

    fun deleteImage(imagePath: String): DeleteImageResponse {
        val file = File(imagePath)
        if (file.exists()) {
            file.delete()
            return DeleteImageResponse(imagePath)
        }
        throw FileNotFoundException("'$imagePath'에 파일이 존재하지 않습니다.")
    }
}
