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

data class CreateImageThumbnailRequest(
    val originImageFile: MultipartFile,
    /**
     * jpg, jpeg, png, webp
     */
    val thumbnailType: String,
    val thumbnailMaxWidth: Int,
    val thumbnailMaxHeight: Int,
    val targetPath: String,
)

data class CreateThumbnailImageResponse(
    val path: String,
)
