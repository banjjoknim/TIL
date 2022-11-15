package com.banjjoknim.soliddesignpatternsample.solid.dip.before

import com.banjjoknim.soliddesignpatternsample.solid.dip.after.AmazonStorageService
import com.banjjoknim.soliddesignpatternsample.solid.dip.after.GoogleStorageService
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.ReadFileRequest
import com.banjjoknim.soliddesignpatternsample.solid.dip.common.StorageFile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/storages")
@RestController
class StorageControllerBefore(
    private val amazonStorageService: AmazonStorageService,
    private val googleStorageService: GoogleStorageService,
) {
    @GetMapping("/none-dip")
    fun readStorageFile(@RequestParam storageType: StorageType, @RequestParam fileInformation: String): StorageFile {
        val readFileRequest = ReadFileRequest(fileInformation)
        return when (storageType) {
            StorageType.AMAZON -> amazonStorageService.readStorageFile(readFileRequest)
            StorageType.GOOGLE -> googleStorageService.readStorageFile(readFileRequest)
        }
    }
}
