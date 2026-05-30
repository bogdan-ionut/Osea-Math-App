package com.example

import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class ExampleUnitTest {
    @Test
    fun correctAnswerSupportsAdditionAndSubtraction() {
        assertEquals(7, correctAnswerFor(3, 4, MathOperation.Addition))
        assertEquals(2, correctAnswerFor(5, 3, MathOperation.Subtraction))
    }

    @Test
    fun skillAccuracyHandlesEmptyAndNormalSignals() {
        assertEquals(100, skillAccuracy(correct = 0, attempts = 0))
        assertEquals(75, skillAccuracy(correct = 3, attempts = 4))
        assertEquals(0, skillAccuracy(correct = 0, attempts = 5))
    }

    @Test
    fun dailyStreakContinuesOnlyAcrossConsecutiveDays() {
        assertEquals(1, nextDailyStreak(lastCompletionDay = 0, currentStreak = 0, today = 100))
        assertEquals(4, nextDailyStreak(lastCompletionDay = 99, currentStreak = 3, today = 100))
        assertEquals(3, nextDailyStreak(lastCompletionDay = 100, currentStreak = 3, today = 100))
        assertEquals(1, nextDailyStreak(lastCompletionDay = 95, currentStreak = 3, today = 100))
    }

    @Test
    fun answersUnlockOnlyAfterEveryVisibleObjectIsTouched() {
        val state = GameState(num1 = 3, num2 = 2)

        assertEquals(5, remainingTouchesFor(state, countedCount = 0))
        assertEquals(2, remainingTouchesFor(state, countedCount = 3))
        assertEquals(0, remainingTouchesFor(state, countedCount = 5))
        assertEquals(0, remainingTouchesFor(state, countedCount = 9))
        assertEquals(false, answerButtonsUnlocked(state, countedCount = 4))
        assertEquals(true, answerButtonsUnlocked(state, countedCount = 5))
    }

    @Test
    fun guidedCountingMovesLeftToRightAcrossVisibleObjects() {
        val state = GameState(num1 = 2, num2 = 1)

        assertEquals("left_0", nextGuidedItemId(state, emptySet()))
        assertEquals("left_1", nextGuidedItemId(state, setOf("left_0")))
        assertEquals("right_0", nextGuidedItemId(state, setOf("left_0", "left_1")))
        assertEquals(null, nextGuidedItemId(state, setOf("left_0", "left_1", "right_0")))
    }

    @Test
    fun audioAssetsAreBundledForOfflinePlay() {
        val rawDir = File("src/main/res/raw")
        val expectedAudio = listOf(
            "correct_1.mp3",
            "correct_2.mp3",
            "correct_3.mp3",
            "correct_4.mp3",
            "wrong_1.mp3",
            "wrong_2.mp3",
            "wrong_3.mp3",
            "victory_sound.mp3"
        )

        expectedAudio.forEach { fileName ->
            val audioFile = File(rawDir, fileName)
            assertTrue("$fileName should exist in res/raw", audioFile.exists())
            assertTrue("$fileName should not be empty", audioFile.length() > 0)
        }
    }
}
