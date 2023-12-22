package com.banjjoknim.soliddesignpatternsample.solid.dip.common

class AmazonStorage {
    fun provideStorageFile(request: AmazonStorageReadFileRequest): StorageFile {
        return StorageFile("${request.fileName} data from amazon")
    }
}
