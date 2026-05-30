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
    fun resultObjectCountsMakeSubtractionConcrete() {
        val addition = GameState(num1 = 2, num2 = 3, operation = MathOperation.Addition)
        val subtraction = GameState(num1 = 5, num2 = 2, operation = MathOperation.Subtraction)

        assertEquals(5, remainingOnDeckCountFor(addition))
        assertEquals(0, movedToChestCountFor(addition))
        assertEquals(3, remainingOnDeckCountFor(subtraction))
        assertEquals(2, movedToChestCountFor(subtraction))
    }

    @Test
    fun skillAccuracyHandlesEmptyAndNormalSignals() {
        assertEquals(100, skillAccuracy(correct = 0, attempts = 0))
        assertEquals(75, skillAccuracy(correct = 3, attempts = 4))
        assertEquals(0, skillAccuracy(correct = 0, attempts = 5))
    }

    @Test
    fun learningEfficiencyCombinesAccuracyRepairsAndWrongStreaks() {
        assertEquals(100, learningEfficiencyScore(correct = 0, attempts = 0, repairRounds = 0, consecutiveWrong = 0))
        assertEquals(92, learningEfficiencyScore(correct = 3, attempts = 3, repairRounds = 1, consecutiveWrong = 0))
        assertEquals(40, learningEfficiencyScore(correct = 3, attempts = 5, repairRounds = 1, consecutiveWrong = 1))
        assertEquals("Calibrare", learningEfficiencyLabel(score = 100, attempts = 2))
        assertEquals("Eficient", learningEfficiencyLabel(score = 92, attempts = 3))
        assertEquals("Risc de ghicit", learningEfficiencyLabel(score = 40, attempts = 5))
    }

    @Test
    fun dailyRingProgressIsClampedAndSafe() {
        assertEquals(0f, dailyRingProgress(current = 5, total = 0), 0.001f)
        assertEquals(0f, dailyRingProgress(current = -2, total = 10), 0.001f)
        assertEquals(0.5f, dailyRingProgress(current = 5, total = 10), 0.001f)
        assertEquals(1f, dailyRingProgress(current = 14, total = 10), 0.001f)
    }

    @Test
    fun sessionTimeUpOnlyWhenTimeBoxEndsWithoutCelebration() {
        assertEquals(false, sessionTimeUp(GameState(sessionSecondsElapsed = 9, sessionSecondsTotal = 10)))
        assertEquals(true, sessionTimeUp(GameState(sessionSecondsElapsed = 10, sessionSecondsTotal = 10)))
        assertEquals(false, sessionTimeUp(GameState(sessionSecondsElapsed = 10, sessionSecondsTotal = 10, showCelebration = true)))
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
