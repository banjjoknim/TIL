package com.banjjoknim.springimage.application

import com.banjjoknim.springimage.api.UploadImageRequest
import com.banjjoknim.springimage.api.UploadImageResponse
import com.banjjoknim.springimage.api.UploadMultiImageRequest
import com.banjjoknim.springimage.api.UploadMultiImageResponse
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.File

@Service
class ImageUploadService {

    companion object {
        private const val CURRENT_PATH_PROPERTY = "user.dir"
    }

    private val currentPath = System.getProperty(CURRENT_PATH_PROPERTY)

    fun uploadImage(request: UploadImageRequest): UploadImageResponse {
        val originalFileExtension = StringUtils.getFilenameExtension(request.files.originalFilename)
        val newFile = File("$currentPath/new-file.${originalFileExtension}")
        request.files.transferTo(newFile)
        return UploadImageResponse(newFile.name, newFile.path)
    }

    fun uploadMultiImage(request: UploadMultiImageRequest): UploadMultiImageResponse {
        val uploadedImageNameAndPaths = request.files.mapIndexed { index, file ->
            val originalFileExtension = StringUtils.getFilenameExtension(file.originalFilename)
            val newFile = File("$currentPath/new-file-$index.${originalFileExtension}")
            file.transferTo(newFile)
            UploadImageResponse(newFile.name, newFile.path)
        }
        return UploadMultiImageResponse(uploadedImageNameAndPaths)
    }
}
