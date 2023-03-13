package com.banjjoknim.springimage.api

import org.springframework.web.multipart.MultipartFile

data class UploadImageRequest(
    val files: MultipartFile,
)

data class UploadImageResponse(
    val name: String,
    val path: String,
)

data class UploadMultiImageRequest(
    val files: List<MultipartFile>,
)

data class UploadMultiImageResponse(
    val nameAndPaths: List<UploadImageResponse>
)

data class DeleteImageResponse(
    val path: String,
)
