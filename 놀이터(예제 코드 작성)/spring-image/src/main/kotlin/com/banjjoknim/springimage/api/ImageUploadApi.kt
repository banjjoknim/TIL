package com.banjjoknim.springimage.api

import com.banjjoknim.springimage.application.ImageUploadService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/images")
@RestController
class ImageUploadApi(
    private val imageUploadService: ImageUploadService
) {

    @PostMapping("/upload")
    fun uploadImage(request: UploadImageRequest): UploadImageResponse {
        return imageUploadService.uploadImage(request)
    }

    @PostMapping("/multi-upload")
    fun uploadMultiImage(request: UploadMultiImageRequest): UploadMultiImageResponse {
        return imageUploadService.uploadMultiImage(request)
    }
}
