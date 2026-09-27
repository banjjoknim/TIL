package com.banjjoknim.playground

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PlaygroundApplication

fun main(args: Array<String>) {
	runApplication<PlaygroundApplication>(*args)
}

/*
 * Complete the 'sockMerchant' function below.
 *
 * The function is expected to return an INTEGER.
 * The function accepts following parameters:
 *  1. INTEGER n
 *  2. INTEGER_ARRAY ar
 */

fun sockMerchant(n: Int, ar: Array<Int>): Int {
	// Write your code here
	val map = ar.groupBy { it }
	var result = 0
	for ((key, value) in map) {
		result += value.size / 2
	}
	return result
}
