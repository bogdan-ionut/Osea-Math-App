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
    fun sessionJournalPrependsAndKeepsRecentRecords() {
        val oldHistory = listOf(
            SessionRecord(dayIndex = 10, minutes = 12, accuracy = 80, repairs = 1, coins = 8, difficulty = 2),
            SessionRecord(dayIndex = 9, minutes = 10, accuracy = 70, repairs = 2, coins = 8, difficulty = 2)
        )
        val newest = SessionRecord(dayIndex = 11, minutes = 13, accuracy = 90, repairs = 0, coins = 12, difficulty = 3)
        val history = appendSessionRecord(history = oldHistory, record = newest, limit = 2)

        assertEquals(listOf(newest, oldHistory.first()), history)
        assertEquals("În urcare", sessionJournalTrendLabel(history))
        assertEquals("Prima urmă", sessionJournalTrendLabel(listOf(newest)))
        assertEquals(
            "De sprijinit",
            sessionJournalTrendLabel(
                listOf(
                    SessionRecord(dayIndex = 12, minutes = 10, accuracy = 62, repairs = 2, coins = 8, difficulty = 2),
                    newest
                )
            )
        )
    }

    @Test
    fun parentNextStepHighlightsTheMostUsefulLearningAction() {
        assertEquals(
            "Mâine: 8 comori, nivel ușor și numărare ghidată, ca să reducem ghicitul.",
            parentNextStepFor(
                additionCorrect = 4,
                additionAttempts = 6,
                subtractionCorrect = 2,
                subtractionAttempts = 4,
                efficiencyScore = 62,
                difficultyLevel = 3
            )
        )
        assertEquals(
            "Mâine: introdu 3 scăderi scurte cu mutare în cufăr.",
            parentNextStepFor(
                additionCorrect = 5,
                additionAttempts = 5,
                subtractionCorrect = 0,
                subtractionAttempts = 0,
                efficiencyScore = 92,
                difficultyLevel = 3
            )
        )
        assertEquals(
            "Mâine: scăderi pe punte, mută în cufăr înainte de răspuns.",
            parentNextStepFor(
                additionCorrect = 5,
                additionAttempts = 5,
                subtractionCorrect = 2,
                subtractionAttempts = 4,
                efficiencyScore = 85,
                difficultyLevel = 4
            )
        )
    }

    @Test
    fun adaptiveOperationSelectorProtectsMasteryBeforeSubtraction() {
        assertEquals(
            MathOperation.Addition,
            selectAdaptiveOperationForNextGame(
                difficultyLevel = 4,
                maxDifficulty = 2,
                correctTotal = 8,
                consecutiveWrong = 0,
                additionCorrect = 8,
                additionAttempts = 8,
                subtractionCorrect = 0,
                subtractionAttempts = 0
            )
        )
        assertEquals(
            MathOperation.Addition,
            selectAdaptiveOperationForNextGame(
                difficultyLevel = 3,
                maxDifficulty = 5,
                correctTotal = 3,
                consecutiveWrong = 0,
                additionCorrect = 2,
                additionAttempts = 2,
                subtractionCorrect = 0,
                subtractionAttempts = 0
            )
        )
        assertEquals(
            MathOperation.Addition,
            selectAdaptiveOperationForNextGame(
                difficultyLevel = 3,
                maxDifficulty = 5,
                correctTotal = 5,
                consecutiveWrong = 1,
                additionCorrect = 5,
                additionAttempts = 5,
                subtractionCorrect = 0,
                subtractionAttempts = 0
            )
        )
    }

    @Test
    fun adaptiveOperationSelectorIntroducesAndRepairsSubtraction() {
        assertEquals(
            MathOperation.Subtraction,
            selectAdaptiveOperationForNextGame(
                difficultyLevel = 3,
                maxDifficulty = 5,
                correctTotal = 5,
                consecutiveWrong = 0,
                additionCorrect = 5,
                additionAttempts = 5,
                subtractionCorrect = 0,
                subtractionAttempts = 0
            )
        )
        assertEquals(
            MathOperation.Subtraction,
            selectAdaptiveOperationForNextGame(
                difficultyLevel = 4,
                maxDifficulty = 5,
                correctTotal = 9,
                consecutiveWrong = 0,
                additionCorrect = 8,
                additionAttempts = 9,
                subtractionCorrect = 2,
                subtractionAttempts = 4
            )
        )
        assertEquals(
            MathOperation.Subtraction,
            selectAdaptiveOperationForNextGame(
                difficultyLevel = 4,
                maxDifficulty = 5,
                correctTotal = 8,
                consecutiveWrong = 0,
                additionCorrect = 8,
                additionAttempts = 9,
                subtractionCorrect = 4,
                subtractionAttempts = 4
            )
        )
        assertEquals(
            MathOperation.Addition,
            selectAdaptiveOperationForNextGame(
                difficultyLevel = 4,
                maxDifficulty = 5,
                correctTotal = 9,
                consecutiveWrong = 0,
                additionCorrect = 8,
                additionAttempts = 9,
                subtractionCorrect = 4,
                subtractionAttempts = 4
            )
        )
    }

    @Test
    fun recoveryMissionReturnsToSmallConcreteBasics() {
        assertEquals(2..4, learningNumberRangeFor(difficulty = 5, recoveryMission = true))
        assertEquals(6..10, learningNumberRangeFor(difficulty = 4, recoveryMission = false))
        assertEquals(false, shouldTriggerStruggleSupport(consecutiveWrong = 1, recoveryMissionActive = false))
        assertEquals(true, shouldTriggerStruggleSupport(consecutiveWrong = 2, recoveryMissionActive = false))
        assertEquals(true, shouldTriggerStruggleSupport(consecutiveWrong = 1, recoveryMissionActive = true))
    }

    @Test
    fun recoveryMissionQueuesOnlyAfterSupportedRepair() {
        val supportedRepair = GameState(
            selectedWrongAnswer = 3,
            recoveryMissionQueued = true
        )
        val normalCorrect = GameState(recoveryMissionQueued = false)

        assertEquals(true, shouldQueueRecoveryAfterCorrect(supportedRepair, finishedDailyTarget = false))
        assertEquals(false, shouldQueueRecoveryAfterCorrect(supportedRepair, finishedDailyTarget = true))
        assertEquals(false, shouldQueueRecoveryAfterCorrect(normalCorrect, finishedDailyTarget = false))
    }

    @Test
    fun rewardCollectionTracksNextUnlockAndRarityProgress() {
        assertEquals(0, unlockedRewardCountFor(lifetimeCoins = 2))
        assertEquals(1, unlockedRewardCountFor(lifetimeCoins = 3))
        assertEquals(4, unlockedRewardCountFor(lifetimeCoins = 12))
        assertEquals(6, unlockedRewardCountFor(lifetimeCoins = 40))

        assertEquals("Monedă", nextRewardLabelFor(lifetimeCoins = 0))
        assertEquals("Hartă", nextRewardLabelFor(lifetimeCoins = 3))
        assertEquals("Colecție completă", nextRewardLabelFor(lifetimeCoins = 40))

        assertEquals(3, coinsToNextRewardFor(lifetimeCoins = 0))
        assertEquals(2, coinsToNextRewardFor(lifetimeCoins = 4))
        assertEquals(0, coinsToNextRewardFor(lifetimeCoins = 40))

        assertEquals(0f, rewardProgressToNextFor(lifetimeCoins = 0), 0.001f)
        assertEquals(0.333f, rewardProgressToNextFor(lifetimeCoins = 4), 0.01f)
        assertEquals(1f, rewardProgressToNextFor(lifetimeCoins = 40), 0.001f)
    }

    @Test
    fun roundFocusExplainsTheCurrentMasteryGoal() {
        assertEquals(
            RoundFocus(
                title = "Adunare până la 5",
                goal = "Unim două grupuri mici și citim ultimul număr atins."
            ),
            roundFocusFor(GameState(num1 = 2, num2 = 3, operation = MathOperation.Addition))
        )
        assertEquals(
            RoundFocus(
                title = "Minus concret",
                goal = "Mutăm în cufăr, apoi numărăm doar comorile rămase pe punte."
            ),
            roundFocusFor(GameState(num1 = 5, num2 = 2, operation = MathOperation.Subtraction))
        )
        assertEquals(
            "Reparare calmă",
            roundFocusFor(GameState(selectedWrongAnswer = 3)).title
        )
        assertEquals(
            "Sprijin pe punte",
            roundFocusFor(GameState(struggleSupportActive = true)).title
        )
        assertEquals(
            RoundFocus(
                title = "Port sigur",
                goal = "După o piedică, revenim la până la 4 comori și consolidăm baza."
            ),
            roundFocusFor(GameState(recoveryMissionActive = true))
        )
    }

    @Test
    fun roundStepCueGuidesAdditionWithoutExtraReading() {
        val state = GameState(num1 = 2, num2 = 3, operation = MathOperation.Addition)

        assertEquals("1", roundStepCueFor(state, countedCount = 0).badge)
        assertEquals("Atinge comoara", roundStepCueFor(state, countedCount = 0).title)
        assertEquals("5", roundStepCueFor(state, countedCount = 5).badge)
        assertEquals("Alege răspunsul", roundStepCueFor(state, countedCount = 5).title)
    }

    @Test
    fun roundStepCueMarksRecoveryMissionAsSafeHarbor() {
        val state = GameState(num1 = 2, num2 = 1, recoveryMissionActive = true)

        assertEquals("4", roundStepCueFor(state, countedCount = 0).badge)
        assertEquals("Port sigur", roundStepCueFor(state, countedCount = 0).title)
    }

    @Test
    fun roundStepCueShowsSubtractionAsMoveThenRemainder() {
        val state = GameState(num1 = 5, num2 = 2, operation = MathOperation.Subtraction)

        assertEquals("-1", roundStepCueFor(state, countedCount = 0).badge)
        assertEquals("Mută în cufăr", roundStepCueFor(state, countedCount = 0).title)
        assertEquals("=", roundStepCueFor(state, countedCount = 2).badge)
        assertEquals("Numără ce rămâne", roundStepCueFor(state, countedCount = 2).title)
        assertEquals("3", roundStepCueFor(state, countedCount = 5).badge)
        assertEquals("Alege răspunsul", roundStepCueFor(state, countedCount = 5).title)
    }

    @Test
    fun dailyRingProgressIsClampedAndSafe() {
        assertEquals(0f, dailyRingProgress(current = 5, total = 0), 0.001f)
        assertEquals(0f, dailyRingProgress(current = -2, total = 10), 0.001f)
        assertEquals(0.5f, dailyRingProgress(current = 5, total = 10), 0.001f)
        assertEquals(1f, dailyRingProgress(current = 14, total = 10), 0.001f)
    }

    @Test
    fun adventureMapTracksActiveIslandAndSegmentProgress() {
        assertEquals(0, activeLearningIslandIndexFor(correctTotal = 0))
        assertEquals(2, coinsToActiveLearningIsland(correctTotal = 0))
        assertEquals(0f, activeLearningIslandSegmentProgress(correctTotal = 0), 0.001f)

        assertEquals(0, activeLearningIslandIndexFor(correctTotal = 1))
        assertEquals(1, coinsToActiveLearningIsland(correctTotal = 1))
        assertEquals(0.5f, activeLearningIslandSegmentProgress(correctTotal = 1), 0.001f)

        assertEquals(1, activeLearningIslandIndexFor(correctTotal = 2))
        assertEquals(3, coinsToActiveLearningIsland(correctTotal = 2))
        assertEquals(0f, activeLearningIslandSegmentProgress(correctTotal = 2), 0.001f)

        assertEquals(1, activeLearningIslandIndexFor(correctTotal = 4))
        assertEquals(1, coinsToActiveLearningIsland(correctTotal = 4))
        assertEquals(0.666f, activeLearningIslandSegmentProgress(correctTotal = 4), 0.01f)

        assertEquals(3, activeLearningIslandIndexFor(correctTotal = 20))
        assertEquals(0, coinsToActiveLearningIsland(correctTotal = 20))
        assertEquals(1f, activeLearningIslandSegmentProgress(correctTotal = 20), 0.001f)
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
    fun subtractionTouchesStartFromTheDeckAndThenCountRemainders() {
        val state = GameState(num1 = 5, num2 = 2, operation = MathOperation.Subtraction)

        assertEquals(5, remainingTouchesFor(state, countedCount = 0))
        assertEquals(3, remainingTouchesFor(state, countedCount = 2))
        assertEquals(false, answerButtonsUnlocked(state, countedCount = 4))
        assertEquals(true, answerButtonsUnlocked(state, countedCount = 5))

        assertEquals(0, subtractionMovedTouchesFor(state, countedCount = 0))
        assertEquals(2, subtractionMovedTouchesFor(state, countedCount = 2))
        assertEquals(2, subtractionMovedTouchesFor(state, countedCount = 5))
        assertEquals(0, subtractionRemainingCountedFor(state, countedCount = 2))
        assertEquals(1, subtractionRemainingCountedFor(state, countedCount = 3))
        assertEquals(3, subtractionRemainingCountedFor(state, countedCount = 5))
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
    fun guidedSubtractionStaysOnTheDeckInsteadOfCountingASecondGroup() {
        val state = GameState(num1 = 4, num2 = 2, operation = MathOperation.Subtraction)

        assertEquals("left_0", nextGuidedItemId(state, emptySet()))
        assertEquals("left_1", nextGuidedItemId(state, setOf("left_0")))
        assertEquals("left_2", nextGuidedItemId(state, setOf("left_0", "left_1")))
        assertEquals("left_3", nextGuidedItemId(state, setOf("left_0", "left_1", "left_2")))
        assertEquals(null, nextGuidedItemId(state, setOf("left_0", "left_1", "left_2", "left_3")))
    }

    @Test
    fun guidedCountingOnlyAdvancesWhenTheHighlightedObjectIsTapped() {
        val empty = emptyMap<String, Int>()
        val first = nextCountedItemsAfterTap(
            countedItems = empty,
            tappedItemId = "right_0",
            guidedItemId = "left_0"
        )

        assertEquals(empty, first)

        val countedFirst = nextCountedItemsAfterTap(
            countedItems = empty,
            tappedItemId = "left_0",
            guidedItemId = "left_0"
        )

        assertEquals(mapOf("left_0" to 1), countedFirst)
        assertEquals(
            countedFirst,
            nextCountedItemsAfterTap(
                countedItems = countedFirst,
                tappedItemId = "left_0",
                guidedItemId = "left_1"
            )
        )
        assertEquals(
            mapOf("left_0" to 1, "left_1" to 2),
            nextCountedItemsAfterTap(
                countedItems = countedFirst,
                tappedItemId = "left_1",
                guidedItemId = "left_1"
            )
        )
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

    @Test
    fun expandedPirateCountingAssetsAreBundled() {
        val drawableDir = File("src/main/res/drawable")
        val expectedAssets = listOf(
            "item_ship_wheel.png",
            "item_gem_pouch.png",
            "item_cannonballs.png",
            "item_ship_lantern.png"
        )

        expectedAssets.forEach { fileName ->
            val assetFile = File(drawableDir, fileName)
            assertTrue("$fileName should exist in res/drawable", assetFile.exists())
            assertTrue("$fileName should not be empty", assetFile.length() > 0)
        }
    }
}
