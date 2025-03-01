package com.banjjoknim.springimage.infrastructure.support

import com.banjjoknim.springimage.application.support.FilePath
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * - [ImageMagick](https://imagemagick.org/)
 */
object ImageMagickProcessor {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun doIdentify(filePath: FilePath) {
        // ImageMagick identify Format
        val command = "identify" // (linux 등 OS 환경에 맞춰 변경해줘야 함, from magick)
        val option = "-verbose"
        val process = ProcessBuilder(command, option, filePath.value).start()

        // 출력 스트림 읽기
        process.inputStream.bufferedReader().use { reader ->
            reader.lines().forEach { line ->
                logger.info(line) // 콘솔에 출력
            }
        }

        process.waitFor()
    }

    fun doConvert(inputStream: InputStream, inputFilePath: FilePath, outputFilePath: FilePath): InputStream {
        // 임시 입력 파일 경로 생성
        val inputTempFile =
            Files.createTempFile(inputFilePath.name(), ".${inputFilePath.extension()}") // e.g. temp.heic
        // 임시 출력 파일 경로 생성
        val outputTempFile =
            Files.createTempFile(outputFilePath.name(), ".${outputFilePath.extension()}") // e.g. temp.jpg

        return tryConvert(inputStream, inputTempFile, outputTempFile)
    }

    private fun tryConvert(
        inputStream: InputStream, inputTempFile: Path, outputTempFile: Path,
    ): InputStream {
        return try {
            inputStream.use { stream ->
                // 임시 입력 파일 생성
                Files.newOutputStream(inputTempFile).use { output -> stream.use { it.copyTo(output) } }

                // ImageMagick convert format
                val command = "convert" // (linux 등 OS 환경에 맞춰 변경해줘야 함, from magick)
                val process = ProcessBuilder(command, inputTempFile.toString(), outputTempFile.toString()).start()
                process.waitFor()

                if (!Files.exists(outputTempFile)) {
                    throw IOException("Image conversion failed: Output file does not exist")
                }

                // 임시 출력 파일 읽기
                Files.newInputStream(outputTempFile)
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Image conversion interrupted", e)
        } finally {
            // 임시 입력 파일 삭제
            Files.deleteIfExists(inputTempFile)
            // 임시 출력 파일 삭제
            Files.deleteIfExists(outputTempFile)
        }
    }
}
