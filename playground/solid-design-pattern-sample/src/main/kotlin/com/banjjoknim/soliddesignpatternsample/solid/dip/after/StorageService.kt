package com.banjjoknim.soliddesignpatternsample.solid.dip.after

import com.banjjoknim.soliddesignpatternsample.solid.dip.common.ReadFileRequest
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.StorageFile

interface StorageService {
    fun readStorageFile(request: ReadFileRequest): StorageFile
}
