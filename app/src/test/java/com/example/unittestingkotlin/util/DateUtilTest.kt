package com.example.unittestingkotlin.util

import com.example.unittestingkotlin.util.DateUtil.Companion.getMonthFromNumber
import com.example.unittestingkotlin.util.DateUtil.Companion.monthNumbers
import com.example.unittestingkotlin.util.DateUtil.Companion.months
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.random.Random

internal class DateUtilTest {

    private val today = "06-2021"

    @Test
    internal fun testGetCurrentTimestamp_returnTimestamp() {
        assertDoesNotThrow {
            assertEquals(today, DateUtil.getCurrentTimestamp())
            println("Timestamp is generated correctly")
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11])
    internal fun getMonthFromNumber_validNumber_returnTrue(monthNumber: Int, testInfo: TestInfo, testReporter: TestReporter) {
        assertEquals(months[monthNumber], getMonthFromNumber(monthNumbers[monthNumber]))
        println("$monthNumber : ${months[monthNumber]}")
    }

    @RepeatedTest(10)
    internal fun getMonthFromNumber_invalidNumber_returnError(testInfo: TestInfo, testReporter: TestReporter) {
        var randomInvalidNumber = Random.nextInt(-100, 100)
        while (randomInvalidNumber in 1..12) {
            randomInvalidNumber = Random.nextInt(-100, 100)
        }

        println("randomInvalidNumber: $randomInvalidNumber")
        assertEquals(getMonthFromNumber(randomInvalidNumber.toString()), "${Constants.GET_MONTH_ERROR}$randomInvalidNumber")
    }

}