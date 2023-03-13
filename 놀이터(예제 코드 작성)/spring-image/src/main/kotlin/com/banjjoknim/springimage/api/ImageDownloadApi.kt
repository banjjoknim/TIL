package com.banjjoknim.springimage.api

import com.banjjoknim.springimage.application.ImageDownloadService
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/images")
@RestController
class ImageDownloadApi(
    private val imageDownloadService: ImageDownloadService
) {

    @GetMapping("/bytes")
    fun downloadImageToByteArray(@RequestParam name: String): ResponseEntity<ByteArray> {
        val image = imageDownloadService.downloadImageToByteArray(name)
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(image)
    }

    @GetMapping("/resource")
    fun downloadImageToResource(@RequestParam name: String): ResponseEntity<Resource> {
        val image = imageDownloadService.downloadImageToResource(name)
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(image)
    }
}
