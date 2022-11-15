package com.banjjoknim.soliddesignpatternsample.solid.dip.after

import com.banjjoknim.soliddesignpatternsample.solid.dip.common.ReadFileRequest
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.StorageFile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/storages")
@RestController
class StorageControllerAfter(
    private val storageService: StorageService
) {
    @GetMapping("/with-dip")
    fun readStorageFile(request: ReadFileRequest): StorageFile {
        return storageService.readStorageFile(request)
    }
}
