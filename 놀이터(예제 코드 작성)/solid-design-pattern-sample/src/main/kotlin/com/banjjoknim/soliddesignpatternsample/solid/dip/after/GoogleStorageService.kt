package com.banjjoknim.soliddesignpatternsample.solid.dip.after

import com.banjjoknim.soliddesignpatternsample.solid.dip.common.GoogleStorage
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.GoogleStorageReadFileRequest
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.ReadFileRequest
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.StorageFile
import org.springframework.stereotype.Component

@Component
class GoogleStorageService : StorageService {
    override fun readStorageFile(request: ReadFileRequest): StorageFile {
        val storage = GoogleStorage()
        val googleStorageReadFileRequest = object : GoogleStorageReadFileRequest {
            override val fileNumber: Int
                get() = request.fileInformation.toInt()
        }
        return storage.provideStorageFile(googleStorageReadFileRequest)
    }
}
