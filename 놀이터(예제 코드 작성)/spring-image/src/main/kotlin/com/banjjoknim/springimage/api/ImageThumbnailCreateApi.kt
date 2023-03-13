package com.banjjoknim.springimage.api

import com.banjjoknim.springimage.application.ImageThumbnailCreateService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/images")
@RestController
class ImageThumbnailCreateApi(
    private val imageThumbnailCreateService: ImageThumbnailCreateService
) {

    @PostMapping("/thumbnail")
    fun createThumbnailImage(request: CreateImageThumbnailRequest): CreateThumbnailImageResponse {
        return imageThumbnailCreateService.createThumbnailImage(request)
    }
}
