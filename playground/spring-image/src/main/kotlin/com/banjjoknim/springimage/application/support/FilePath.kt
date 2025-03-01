package com.banjjoknim.springimage.application.support

data class FilePath(
    val value: String, // temp.heic, temp.jpg
) {

    fun name(): String {
        return this.value.split(".").dropLast(1).joinToString(".")
    }

    fun extension(): String {
        return this.value.split(".").last()
    }

    fun isHeic(): Boolean {
        return this.value.split(".").last().lowercase() == "heic"
    }
}
