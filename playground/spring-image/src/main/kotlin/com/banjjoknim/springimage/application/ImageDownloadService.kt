package com.banjjoknim.springimage.application

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.io.File

@Service
class ImageDownloadService {

    companion object {
        private const val CURRENT_PATH_PROPERTY = "user.dir"
    }

    private val currentPath = System.getProperty(CURRENT_PATH_PROPERTY)

    /**
     * @see ByteArray
     */
    fun downloadImageToByteArray(name: String): ByteArray {
        val file = File("$currentPath/$name")
        return file.readBytes()
    }

    /**
     * @see org.springframework.core.io.Resource
     */
    fun downloadImageToResource(name: String): Resource {
        val file = File("$currentPath/$name")
        return ByteArrayResource(file.readBytes())
    }
}
