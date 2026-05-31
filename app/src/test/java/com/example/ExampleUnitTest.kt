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
    fun countingAdventureProgressTracksTouches() {
        val addition = GameState(num1 = 2, num2 = 2, operation = MathOperation.Addition)
        val subtraction = GameState(num1 = 5, num2 = 2, operation = MathOperation.Subtraction)

        assertEquals(0.5f, countingAdventureProgressFor(addition, countedCount = 2), 0.001f)
        assertEquals(1f, countingAdventureProgressFor(addition, countedCount = 8), 0.001f)
        assertEquals("Săpăm spre comoară", countingAdventureLabelFor(addition, countedCount = 1))
        assertEquals("Mutăm spre cufăr", countingAdventureLabelFor(subtraction, countedCount = 1))
        assertEquals("Numărăm ce rămâne", countingAdventureLabelFor(subtraction, countedCount = 3))
        assertEquals("Comoara e pregătită", countingAdventureLabelFor(addition, countedCount = 4))
    }

    @Test
    fun voyageSurprisesAndChestPileStayBounded() {
        assertEquals(0, visibleChestCoinCountFor(0))
        assertEquals(3, visibleChestCoinCountFor(5))
        assertEquals(9, visibleChestCoinCountFor(99))
        assertEquals(R.drawable.item_anchor, learningIslandDrawableFor(0))
        assertEquals(R.drawable.item_gold_coin, learningIslandDrawableFor(1))
        assertEquals(R.drawable.item_treasure_chest, learningIslandDrawableFor(2))
        assertEquals(R.drawable.item_pirate_flag, learningIslandDrawableFor(99))
        assertEquals(voyageSurpriseFor(0), voyageSurpriseFor(1))
        assertTrue(voyageSurpriseFor(4) != voyageSurpriseFor(0))
        assertTrue(!isVoyageSurpriseMoment(1))
        assertTrue(isVoyageSurpriseMoment(2))
        assertEquals(2, coinsToNextVoyageSurprise(0))
        assertEquals(1, coinsToNextVoyageSurprise(1))
        assertEquals(0, coinsToNextVoyageSurprise(2))
        assertEquals("descoperită acum", voyageSurpriseProgressTextFor(2))
        assertEquals("1 comoară până la surpriză", voyageSurpriseProgressTextFor(1))
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
    fun weeklyParentSummaryAggregatesSevenRecentSessions() {
        val history = listOf(
            SessionRecord(dayIndex = 17, minutes = 12, accuracy = 90, repairs = 0, coins = 8, difficulty = 3),
            SessionRecord(dayIndex = 16, minutes = 10, accuracy = 80, repairs = 1, coins = 8, difficulty = 2),
            SessionRecord(dayIndex = 15, minutes = 9, accuracy = 70, repairs = 2, coins = 7, difficulty = 2),
            SessionRecord(dayIndex = 14, minutes = 8, accuracy = 60, repairs = 2, coins = 6, difficulty = 2),
            SessionRecord(dayIndex = 13, minutes = 11, accuracy = 85, repairs = 1, coins = 8, difficulty = 3),
            SessionRecord(dayIndex = 12, minutes = 10, accuracy = 95, repairs = 0, coins = 8, difficulty = 3),
            SessionRecord(dayIndex = 11, minutes = 7, accuracy = 75, repairs = 1, coins = 6, difficulty = 2),
            SessionRecord(dayIndex = 10, minutes = 99, accuracy = 1, repairs = 9, coins = 99, difficulty = 5)
        )

        val summary = weeklyParentSummaryFor(history)

        assertEquals(7, summary.sessions)
        assertEquals(67, summary.minutes)
        assertEquals(79, summary.averageAccuracy)
        assertEquals(7, summary.repairs)
        assertEquals(51, summary.coins)
        assertEquals(3, summary.highestDifficulty)
        assertEquals("În urcare", summary.trend)
    }

    @Test
    fun weeklyParentNudgeHighlightsSupportOrStableProgress() {
        assertEquals(
            "Pornește cu presetul de 4 ani și urmărește primele 3 sesiuni.",
            weeklyParentNudgeFor(weeklyParentSummaryFor(emptyList()))
        )
        assertEquals(
            "Săptămâna cere suport: păstrează Port sigur și comori mici.",
            weeklyParentNudgeFor(
                WeeklyParentSummary(
                    sessions = 3,
                    minutes = 20,
                    averageAccuracy = 62,
                    repairs = 2,
                    coins = 12,
                    highestDifficulty = 2,
                    trend = "De sprijinit"
                )
            )
        )
        assertEquals(
            "Ritm bun: păstrează sesiunile scurte și lasă speed bump-ul să urce.",
            weeklyParentNudgeFor(
                WeeklyParentSummary(
                    sessions = 5,
                    minutes = 54,
                    averageAccuracy = 91,
                    repairs = 1,
                    coins = 40,
                    highestDifficulty = 3,
                    trend = "În urcare"
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
    fun parentAuditSignalsSummarizeTimeQualityAndSkillGaps() {
        val state = GameState(
            correctTotal = 7,
            attemptsTotal = 10,
            repairRounds = 2,
            consecutiveWrong = 1,
            difficultyLevel = 3,
            sessionSecondsElapsed = 9 * 60,
            sessionSecondsTotal = 10 * 60,
            additionCorrect = 5,
            additionAttempts = 5,
            subtractionCorrect = 1,
            subtractionAttempts = 3
        )
        val signals = parentAuditSignalsFor(state)

        assertEquals(3, signals.size)
        assertEquals("Timp de lucru", signals[0].title)
        assertEquals("9/10 min", signals[0].valueText)
        assertEquals(0.9f, signals[0].progress, 0.001f)
        assertEquals("Calitate", signals[1].title)
        assertEquals("70%", signals[1].valueText)
        assertEquals("Eficiență 42% · Risc de ghicit", signals[1].detail)
        assertEquals("Skill gap", signals[2].title)
        assertEquals("Scădere", signals[2].valueText)
        assertEquals("Adunare 100% · Scădere 33%", signals[2].detail)
    }

    @Test
    fun parentSkillGapCanBeBalancedOrCalibrating() {
        assertEquals(
            "Calibrare",
            parentSkillGapLabelFor(
                additionCorrect = 1,
                additionAttempts = 1,
                subtractionCorrect = 0,
                subtractionAttempts = 0
            )
        )
        assertEquals(
            "Echilibrat",
            parentSkillGapLabelFor(
                additionCorrect = 4,
                additionAttempts = 5,
                subtractionCorrect = 3,
                subtractionAttempts = 4
            )
        )
        assertEquals(
            "Adunare",
            parentSkillGapLabelFor(
                additionCorrect = 1,
                additionAttempts = 4,
                subtractionCorrect = 4,
                subtractionAttempts = 4
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
    fun rewardBurstShowsImmediateProgressTowardTheCollection() {
        val summary = rewardBurstSummaryFor(GameState(streak = 3, lifetimeCoins = 10))

        assertEquals("Comoară +1", summary.title)
        assertEquals("Streak 3. Colecția are 10 comori.", summary.detail)
        assertEquals("2 până la Busolă", summary.nextRewardText)
        assertEquals(0.333f, summary.progress, 0.01f)

        val completeSummary = rewardBurstSummaryFor(GameState(streak = 4, lifetimeCoins = 40))
        assertEquals("Colecția de bază este completă.", completeSummary.nextRewardText)
        assertEquals(1f, completeSummary.progress, 0.001f)
    }

    @Test
    fun onboardingPresetsStartWithTheAgeFourRecommendation() {
        val presets = onboardingPresetOptions()
        val recommended = recommendedOnboardingPresetForAge(4)

        assertEquals("age4", presets.first().id)
        assertEquals(true, presets.first().recommended)
        assertEquals("age4", recommended.id)
        assertEquals(8, recommended.dailyTarget)
        assertEquals(10, recommended.sessionMinutes)
        assertEquals(3, recommended.maxDifficulty)
        assertEquals("8 comori · 10 min · nivel 3", onboardingPresetSummary(recommended))
    }

    @Test
    fun onboardingPresetsScaleForOlderChildren() {
        val recommended = recommendedOnboardingPresetForAge(6)

        assertEquals("steady", recommended.id)
        assertEquals(12, recommended.dailyTarget)
        assertEquals(15, recommended.sessionMinutes)
        assertEquals(4, recommended.maxDifficulty)
    }

    @Test
    fun captainQuestsTranslateProgressIntoDailyMissions() {
        val state = GameState(
            correctTotal = 3,
            attemptsTotal = 4,
            repairRounds = 1,
            consecutiveWrong = 0,
            dailyTarget = 8,
            lifetimeCoins = 10
        )
        val quests = captainQuestsFor(state)

        assertEquals(3, quests.size)
        assertEquals("Ținta de azi", quests[0].title)
        assertEquals("3/8", quests[0].valueText)
        assertEquals(0.375f, quests[0].progress, 0.001f)
        assertEquals("Siguranță", quests[1].title)
        assertEquals("67%", quests[1].valueText)
        assertEquals(0.67f, quests[1].progress, 0.01f)
        assertEquals("Următoarea comoară", quests[2].title)
        assertEquals("Rar", quests[2].valueText)
        assertEquals(0.333f, quests[2].progress, 0.01f)
    }

    @Test
    fun captainQuestsShowCompleteStates() {
        val state = GameState(
            correctTotal = 8,
            attemptsTotal = 8,
            dailyTarget = 8,
            lifetimeCoins = 30
        )
        val quests = captainQuestsFor(state)

        assertEquals("Comoara zilei e gata.", quests[0].detail)
        assertEquals(1f, quests[0].progress, 0.001f)
        assertEquals("Colecție completă", quests[2].title)
        assertEquals("gata", quests[2].valueText)
        assertEquals(1f, quests[2].progress, 0.001f)
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
