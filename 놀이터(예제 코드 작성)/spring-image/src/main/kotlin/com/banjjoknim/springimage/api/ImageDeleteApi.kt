package com.banjjoknim.springimage.api

import com.banjjoknim.springimage.application.ImageDeleteService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/images")
@RestController
class ImageDeleteApi(
    private val imageDeleteService: ImageDeleteService
) {

    @DeleteMapping("/{imagePath}")
    fun deleteImage(@PathVariable imagePath: String): DeleteImageResponse {
        return imageDeleteService.deleteImage(imagePath)
    }
}
