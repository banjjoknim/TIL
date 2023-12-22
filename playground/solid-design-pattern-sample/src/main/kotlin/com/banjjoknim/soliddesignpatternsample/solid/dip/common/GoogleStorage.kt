package com.banjjoknim.soliddesignpatternsample.solid.dip.common

class GoogleStorage {
    fun provideStorageFile(request: GoogleStorageReadFileRequest): StorageFile {
        return StorageFile("${request.fileNumber} data from amazon")
    }
}
