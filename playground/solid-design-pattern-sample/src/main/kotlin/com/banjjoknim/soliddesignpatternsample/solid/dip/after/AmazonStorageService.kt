package com.banjjoknim.soliddesignpatternsample.solid.dip.after

import com.banjjoknim.soliddesignpatternsample.solid.dip.common.AmazonStorage
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.AmazonStorageReadFileRequest
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.ReadFileRequest
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.StorageFile
import org.springframework.stereotype.Service

@Service
class AmazonStorageService : StorageService {
    override fun readStorageFile(request: ReadFileRequest): StorageFile {
        val storage = AmazonStorage()
        val amazonStorageReadFileRequest = object : AmazonStorageReadFileRequest {
            override val fileName: String
                get() = request.fileInformation
        }
        return storage.provideStorageFile(amazonStorageReadFileRequest)
    }
}
