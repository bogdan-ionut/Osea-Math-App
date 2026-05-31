package com.example

import android.app.Application
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.CardBg
import com.example.ui.theme.CoralBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OceanBg
import com.example.ui.theme.RubyRed
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextSandy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.TimeZone

data class PirateItem(
    val nameSingular: String,
    val namePlural: String,
    val description: String,
    val color: Color,
    val shape: TreasureShape,
    val drawableRes: Int? = null
)

enum class TreasureShape {
    Boat,
    Coin,
    Gem,
    Compass,
    Shell,
    Map,
    Anchor,
    Spyglass,
    Island,
    Key
}

enum class MathOperation(val symbol: String) {
    Addition("+"),
    Subtraction("-")
}

data class LearningIsland(
    val title: String,
    val focus: String,
    val targetCoins: Int,
    val color: Color,
    val icon: String
)

data class RewardDefinition(
    val label: String,
    val detail: String,
    val drawableRes: Int,
    val color: Color,
    val unlockCoins: Int,
    val rarity: RewardRarity
)

data class RewardRarity(
    val label: String,
    val color: Color
)

data class SessionRecord(
    val dayIndex: Int,
    val minutes: Int,
    val accuracy: Int,
    val repairs: Int,
    val coins: Int,
    val difficulty: Int
)

data class RoundFocus(
    val title: String,
    val goal: String
)

data class RoundStepCue(
    val badge: String,
    val title: String,
    val detail: String,
    val color: Color
)

data class OnboardingPreset(
    val id: String,
    val title: String,
    val detail: String,
    val dailyTarget: Int,
    val sessionMinutes: Int,
    val maxDifficulty: Int,
    val color: Color,
    val recommended: Boolean = false
)

data class CaptainQuest(
    val title: String,
    val detail: String,
    val valueText: String,
    val progress: Float,
    val color: Color,
    val drawableRes: Int
)

data class ParentAuditSignal(
    val title: String,
    val valueText: String,
    val detail: String,
    val progress: Float,
    val color: Color
)

private val treasureItems = listOf(
    PirateItem("corabie", "corăbii", "cu pânze de aventură", Color(0xFF54C6F4), TreasureShape.Boat, R.drawable.item_ship),
    PirateItem("cufăr", "cufere", "pline cu bogății", Color(0xFFFFB74D), TreasureShape.Key, R.drawable.item_treasure_chest),
    PirateItem("bănuț", "bănuți", "de aur", Color(0xFFFFD54F), TreasureShape.Coin, R.drawable.item_gold_coin),
    PirateItem("tun", "tunuri", "de corabie veche", Color(0xFFB0BEC5), TreasureShape.Anchor, R.drawable.item_cannon),
    PirateItem("sabie", "săbii", "de căpitan din 1500", Color(0xFF5DADE2), TreasureShape.Spyglass, R.drawable.item_cutlass),
    PirateItem("hartă", "hărți", "de comoară", Color(0xFFFFE082), TreasureShape.Map, R.drawable.item_treasure_map),
    PirateItem("lunetă", "lunete", "de căpitan", Color(0xFFFFCA28), TreasureShape.Spyglass, R.drawable.item_spyglass),
    PirateItem("busolă", "busole", "de navigator", Color(0xFFFFB74D), TreasureShape.Compass, R.drawable.item_compass),
    PirateItem("ancoră", "ancore", "de pe punte", Color(0xFFB0BEC5), TreasureShape.Anchor, R.drawable.item_anchor),
    PirateItem("lopată", "lopeți", "pentru comori", Color(0xFFFFCC80), TreasureShape.Key, R.drawable.item_shovel),
    PirateItem("cârmă", "cârme", "de corabie", Color(0xFFD7A64A), TreasureShape.Compass, R.drawable.item_ship_wheel),
    PirateItem("săculeț", "săculeți", "cu nestemate", Color(0xFFE57373), TreasureShape.Gem, R.drawable.item_gem_pouch),
    PirateItem("ghiulea", "ghiulele", "de tun vechi", Color(0xFFB0BEC5), TreasureShape.Coin, R.drawable.item_cannonballs),
    PirateItem("felinar", "felinare", "de corabie", Color(0xFFFFD180), TreasureShape.Key, R.drawable.item_ship_lantern)
)

private val learningIslands = listOf(
    LearningIsland("Portul Numărării", "atinge fiecare obiect", 2, Color(0xFF4DD0E1), "1"),
    LearningIsland("Golful lui 5", "adunări până la 5", 5, Color(0xFFFFD54F), "5"),
    LearningIsland("Insula Scăderii", "rămân până la 10", 8, Color(0xFFFF8A65), "10"),
    LearningIsland("Comoara Mastery", "răspunsuri sigure", 12, Color(0xFF81C784), "★")
)

private val commonReward = RewardRarity("Comun", Color(0xFF8FD8FF))
private val rareReward = RewardRarity("Rar", Color(0xFFFFD54F))
private val legendaryReward = RewardRarity("Legendar", Color(0xFFFF8A65))

private val onboardingPresets = listOf(
    OnboardingPreset(
        id = "age4",
        title = "4 ani",
        detail = "8 comori, 10 minute, minus mic mai târziu",
        dailyTarget = 8,
        sessionMinutes = 10,
        maxDifficulty = 3,
        color = EmeraldGreen,
        recommended = true
    ),
    OnboardingPreset(
        id = "steady",
        title = "Ritm stabil",
        detail = "12 comori, 15 minute, scăderi ghidate",
        dailyTarget = 12,
        sessionMinutes = 15,
        maxDifficulty = 4,
        color = CoralBlue
    ),
    OnboardingPreset(
        id = "bold",
        title = "Aventură mare",
        detail = "16 comori, 25 minute, provocări complete",
        dailyTarget = 16,
        sessionMinutes = 25,
        maxDifficulty = 5,
        color = StarGold
    )
)

fun onboardingPresetOptions(): List<OnboardingPreset> = onboardingPresets

fun recommendedOnboardingPresetForAge(age: Int): OnboardingPreset {
    return if (age <= 4) onboardingPresets.first { it.id == "age4" } else onboardingPresets.first { it.id == "steady" }
}

fun onboardingPresetSummary(preset: OnboardingPreset): String {
    return "${preset.dailyTarget} comori · ${preset.sessionMinutes} min · nivel ${preset.maxDifficulty}"
}

fun activeLearningIslandIndexFor(correctTotal: Int): Int {
    val nextIslandIndex = learningIslands.indexOfFirst { correctTotal < it.targetCoins }
    return if (nextIslandIndex == -1) learningIslands.lastIndex else nextIslandIndex
}

fun coinsToActiveLearningIsland(correctTotal: Int): Int {
    val island = learningIslands[activeLearningIslandIndexFor(correctTotal)]
    return (island.targetCoins - correctTotal).coerceAtLeast(0)
}

fun activeLearningIslandSegmentProgress(correctTotal: Int): Float {
    val islandIndex = activeLearningIslandIndexFor(correctTotal)
    val previousTarget = if (islandIndex == 0) 0 else learningIslands[islandIndex - 1].targetCoins
    val target = learningIslands[islandIndex].targetCoins
    val distance = (target - previousTarget).coerceAtLeast(1)

    return ((correctTotal - previousTarget).toFloat() / distance.toFloat()).coerceIn(0f, 1f)
}

private val rewardDefinitions = listOf(
    RewardDefinition("Monedă", "prima pradă", R.drawable.item_gold_coin, StarGold, 3, commonReward),
    RewardDefinition("Hartă", "drum secret", R.drawable.item_treasure_map, Color(0xFFFFE082), 6, commonReward),
    RewardDefinition("Lunetă", "ochi de căpitan", R.drawable.item_spyglass, Color(0xFF64B5F6), 9, commonReward),
    RewardDefinition("Busolă", "direcție bună", R.drawable.item_compass, Color(0xFFFFB74D), 12, rareReward),
    RewardDefinition("Ancoră", "port sigur", R.drawable.item_anchor, Color(0xFFB0BEC5), 18, rareReward),
    RewardDefinition("Cufăr", "comoară mare", R.drawable.item_treasure_chest, Color(0xFFFF8A65), 24, legendaryReward)
)

fun unlockedRewardCountFor(lifetimeCoins: Int): Int {
    return rewardDefinitions.count { lifetimeCoins >= it.unlockCoins }
}

private fun nextRewardDefinitionFor(lifetimeCoins: Int): RewardDefinition? {
    return rewardDefinitions.firstOrNull { lifetimeCoins < it.unlockCoins }
}

fun nextRewardLabelFor(lifetimeCoins: Int): String {
    return nextRewardDefinitionFor(lifetimeCoins)?.label ?: "Colecție completă"
}

fun coinsToNextRewardFor(lifetimeCoins: Int): Int {
    val nextReward = nextRewardDefinitionFor(lifetimeCoins) ?: return 0
    return (nextReward.unlockCoins - lifetimeCoins).coerceAtLeast(0)
}

fun rewardProgressToNextFor(lifetimeCoins: Int): Float {
    val nextReward = nextRewardDefinitionFor(lifetimeCoins) ?: return 1f
    val previousUnlock = rewardDefinitions
        .filter { it.unlockCoins < nextReward.unlockCoins }
        .maxOfOrNull { it.unlockCoins } ?: 0
    val span = (nextReward.unlockCoins - previousUnlock).coerceAtLeast(1)
    return ((lifetimeCoins - previousUnlock).toFloat() / span.toFloat()).coerceIn(0f, 1f)
}

fun captainQuestsFor(state: GameState): List<CaptainQuest> {
    val accuracy = if (state.attemptsTotal == 0) 100 else (state.correctTotal * 100 / state.attemptsTotal).coerceIn(0, 100)
    val efficiency = learningEfficiencyScore(
        correct = state.correctTotal,
        attempts = state.attemptsTotal,
        repairRounds = state.repairRounds,
        consecutiveWrong = state.consecutiveWrong
    )
    val nextReward = nextRewardDefinitionFor(state.lifetimeCoins)
    val remainingDaily = (state.dailyTarget - state.correctTotal).coerceAtLeast(0)
    val efficiencyColor = when {
        state.attemptsTotal < 3 -> CoralBlue
        efficiency >= 85 -> EmeraldGreen
        efficiency >= 70 -> StarGold
        else -> RubyRed
    }

    return listOf(
        CaptainQuest(
            title = "Ținta de azi",
            detail = if (remainingDaily == 0) "Comoara zilei e gata." else "$remainingDaily comori rămase",
            valueText = "${state.correctTotal}/${state.dailyTarget}",
            progress = dailyRingProgress(current = state.correctTotal, total = state.dailyTarget),
            color = StarGold,
            drawableRes = R.drawable.item_gold_coin
        ),
        CaptainQuest(
            title = "Siguranță",
            detail = if (state.attemptsTotal < 3) "Calibrăm primele răspunsuri." else "$accuracy% răspunsuri corecte",
            valueText = "$efficiency%",
            progress = dailyRingProgress(current = efficiency, total = 100),
            color = efficiencyColor,
            drawableRes = R.drawable.item_compass
        ),
        CaptainQuest(
            title = if (nextReward == null) "Colecție completă" else "Următoarea comoară",
            detail = if (nextReward == null) {
                "Toate obiectele de bază sunt în port."
            } else {
                "${coinsToNextRewardFor(state.lifetimeCoins)} comori până la ${nextReward.label}"
            },
            valueText = if (nextReward == null) "gata" else nextReward.rarity.label,
            progress = rewardProgressToNextFor(state.lifetimeCoins),
            color = nextReward?.rarity?.color ?: EmeraldGreen,
            drawableRes = nextReward?.drawableRes ?: R.drawable.item_treasure_chest
        )
    )
}

fun correctAnswerFor(num1: Int, num2: Int, operation: MathOperation): Int {
    return when (operation) {
        MathOperation.Addition -> num1 + num2
        MathOperation.Subtraction -> num1 - num2
    }
}

private fun correctAnswerFor(state: GameState): Int {
    return correctAnswerFor(state.num1, state.num2, state.operation)
}

private fun visibleObjectCountFor(state: GameState): Int {
    return if (state.operation == MathOperation.Subtraction) {
        state.num1
    } else {
        state.num1 + state.num2
    }
}

fun remainingTouchesFor(state: GameState, countedCount: Int): Int {
    return (visibleObjectCountFor(state) - countedCount).coerceAtLeast(0)
}

fun answerButtonsUnlocked(state: GameState, countedCount: Int): Boolean {
    return remainingTouchesFor(state, countedCount) == 0
}

fun remainingOnDeckCountFor(state: GameState): Int {
    return if (state.operation == MathOperation.Subtraction) {
        correctAnswerFor(state)
    } else {
        visibleObjectCountFor(state)
    }
}

fun movedToChestCountFor(state: GameState): Int {
    return if (state.operation == MathOperation.Subtraction) state.num2 else 0
}

fun subtractionMovedTouchesFor(state: GameState, countedCount: Int): Int {
    return if (state.operation == MathOperation.Subtraction) {
        countedCount.coerceIn(0, state.num2)
    } else {
        0
    }
}

fun subtractionRemainingCountedFor(state: GameState, countedCount: Int): Int {
    return if (state.operation == MathOperation.Subtraction) {
        (countedCount - state.num2).coerceIn(0, correctAnswerFor(state))
    } else {
        countedCount
    }
}

fun roundStepCueFor(state: GameState, countedCount: Int): RoundStepCue {
    val totalItems = visibleObjectCountFor(state)
    val answer = correctAnswerFor(state)
    val remainingTouches = remainingTouchesFor(state, countedCount)

    return when {
        state.isCorrecting -> RoundStepCue(
            badge = "+1",
            title = "Comoară câștigată",
            detail = "Bravo. Urmează o nouă hartă scurtă.",
            color = EmeraldGreen
        )
        state.selectedWrongAnswer != null && remainingTouches > 0 -> {
            if (state.operation == MathOperation.Subtraction) {
                val movedCount = subtractionMovedTouchesFor(state, countedCount)
                if (movedCount < state.num2) {
                    RoundStepCue(
                        badge = "-1",
                        title = "Reparăm: în cufăr",
                        detail = "Atinge doar comoara luminoasă cu -1: $movedCount/${state.num2}.",
                        color = RubyRed
                    )
                } else {
                    RoundStepCue(
                        badge = "=",
                        title = "Reparăm: ce rămâne",
                        detail = "Comoara din cufăr stă deoparte. Numărăm puntea.",
                        color = CoralBlue
                    )
                }
            } else {
                RoundStepCue(
                    badge = (countedCount + 1).toString(),
                    title = "Reparăm numărarea",
                    detail = "Atinge comoara luminoasă și mergem încet până la $totalItems.",
                    color = CoralBlue
                )
            }
        }
        state.selectedWrongAnswer != null -> RoundStepCue(
            badge = answer.toString(),
            title = "Alege răspunsul",
            detail = "Numărarea este refăcută. Ultimul număr e ancora.",
            color = StarGold
        )
        state.recoveryMissionActive && remainingTouches > 0 -> RoundStepCue(
            badge = "4",
            title = "Port sigur",
            detail = "Misiune ușoară: atingem încet comori mici, fără grabă.",
            color = EmeraldGreen
        )
        state.operation == MathOperation.Subtraction -> {
            val movedCount = subtractionMovedTouchesFor(state, countedCount)
            val remainingCounted = subtractionRemainingCountedFor(state, countedCount)
            when {
                movedCount < state.num2 -> RoundStepCue(
                    badge = "-1",
                    title = "Mută în cufăr",
                    detail = "Atinge comoara luminoasă cu -1: $movedCount/${state.num2}.",
                    color = RubyRed
                )
                remainingCounted < answer -> RoundStepCue(
                    badge = "=",
                    title = "Numără ce rămâne",
                    detail = "Cele din cufăr nu se mai numără. Pe punte: $remainingCounted/$answer.",
                    color = EmeraldGreen
                )
                else -> RoundStepCue(
                    badge = answer.toString(),
                    title = "Alege răspunsul",
                    detail = "${state.num1} minus ${state.num2}: pe punte rămân $answer.",
                    color = StarGold
                )
            }
        }
        remainingTouches > 0 -> RoundStepCue(
            badge = (countedCount + 1).toString(),
            title = "Atinge comoara",
            detail = "Numărăm în ordine: $countedCount/$totalItems atinse.",
            color = CoralBlue
        )
        else -> RoundStepCue(
            badge = answer.toString(),
            title = "Alege răspunsul",
            detail = "Ultimul număr citit este totalul sigur.",
            color = StarGold
        )
    }
}

fun nextGuidedItemId(state: GameState, countedItemIds: Set<String>): String? {
    val orderedIds = buildList {
        repeat(state.num1) { index -> add("left_$index") }
        if (state.operation == MathOperation.Addition) {
            repeat(state.num2) { index -> add("right_$index") }
        }
    }
    return orderedIds.firstOrNull { it !in countedItemIds }
}

fun nextCountedItemsAfterTap(
    countedItems: Map<String, Int>,
    tappedItemId: String,
    guidedItemId: String?
): Map<String, Int> {
    return if (guidedItemId != null && tappedItemId == guidedItemId && tappedItemId !in countedItems) {
        countedItems + (tappedItemId to (countedItems.size + 1))
    } else {
        countedItems
    }
}

private fun buildCoachNarration(state: GameState, countedCount: Int): String {
    val visibleItems = visibleObjectCountFor(state)
    val answer = correctAnswerFor(state)
    val leftGroup = "${state.num1} ${if (state.num1 == 1) state.item1.nameSingular else state.item1.namePlural}"
    val rightGroup = "${state.num2} ${if (state.num2 == 1) state.item2.nameSingular else state.item2.namePlural}"
    return when {
        state.isCorrecting -> {
            "Bravo, Osea. Comoară plus unu. Ai numărat cu grijă."
        }
        state.selectedWrongAnswer != null && countedCount < visibleItems -> {
            if (state.operation == MathOperation.Addition) {
                "Reparăm împreună. Avem $leftGroup și $rightGroup. Atinge fiecare obiect din nou, de la unu până la $visibleItems."
            } else {
                val movedCount = subtractionMovedTouchesFor(state, countedCount)
                if (movedCount < state.num2) {
                    "Reparăm împreună. Pornim cu $leftGroup și mutăm ${state.num2} în cufăr. Caută comoara luminoasă și dă-o deoparte."
                } else {
                    "Bun. Am mutat ${state.num2} în cufăr. Acum numărăm doar comorile rămase pe punte."
                }
            }
        }
        state.selectedWrongAnswer != null -> {
            if (state.operation == MathOperation.Addition) {
                "Ai refăcut numărarea. ${state.num1} plus ${state.num2} fac $answer. Alege $answer."
            } else {
                "Ai refăcut numărarea. ${state.num1} minus ${state.num2} fac $answer. Alege $answer."
            }
        }
        countedCount < visibleItems -> {
            val nextNumber = countedCount + 1
            if (state.operation == MathOperation.Addition) {
                "Căpitane Osea, avem $leftGroup și $rightGroup. Caută comoara luminoasă pentru numărul $nextNumber. Ai numărat $countedCount din $visibleItems."
            } else {
                val movedCount = subtractionMovedTouchesFor(state, countedCount)
                val remainingCounted = subtractionRemainingCountedFor(state, countedCount)
                if (movedCount < state.num2) {
                    "Căpitane Osea, pornim cu $leftGroup. Mutăm ${state.num2} în cufăr. Ai mutat $movedCount din ${state.num2}."
                } else {
                    "Acum scădem. Cele din cufăr nu se mai numără. Ai numărat $remainingCounted comori rămase pe punte."
                }
            }
        }
        else -> {
            if (state.operation == MathOperation.Addition) {
                "Toate obiectele sunt numărate. ${state.num1} plus ${state.num2} fac $answer. Alege răspunsul $answer."
            } else {
                "Toate obiectele sunt numărate. ${state.num1} minus ${state.num2} fac $answer. Alege răspunsul $answer."
            }
        }
    }
}

fun nextDailyStreak(lastCompletionDay: Int, currentStreak: Int, today: Int): Int {
    return when (lastCompletionDay) {
        today -> maxOf(1, currentStreak)
        today - 1 -> currentStreak + 1
        else -> 1
    }
}

fun skillAccuracy(correct: Int, attempts: Int): Int {
    return if (attempts == 0) 100 else (correct * 100 / attempts).coerceIn(0, 100)
}

fun learningEfficiencyScore(
    correct: Int,
    attempts: Int,
    repairRounds: Int,
    consecutiveWrong: Int
): Int {
    val accuracy = skillAccuracy(correct = correct, attempts = attempts)
    val repairPenalty = (repairRounds * 8).coerceAtMost(28)
    val wrongStreakPenalty = (consecutiveWrong * 12).coerceAtMost(24)
    return (accuracy - repairPenalty - wrongStreakPenalty).coerceIn(0, 100)
}

fun learningEfficiencyLabel(score: Int, attempts: Int): String {
    return when {
        attempts < 3 -> "Calibrare"
        score >= 85 -> "Eficient"
        score >= 70 -> "De urmărit"
        else -> "Risc de ghicit"
    }
}

fun appendSessionRecord(
    history: List<SessionRecord>,
    record: SessionRecord,
    limit: Int = 4
): List<SessionRecord> {
    return (listOf(record) + history).take(limit.coerceAtLeast(1))
}

fun sessionJournalTrendLabel(history: List<SessionRecord>): String {
    if (history.size < 2) return "Prima urmă"
    val latest = history.first().accuracy
    val previousAverage = history.drop(1).map { it.accuracy }.average()

    return when {
        latest >= previousAverage + 5 -> "În urcare"
        latest + 5 < previousAverage -> "De sprijinit"
        else -> "Stabil"
    }
}

fun parentNextStepFor(
    additionCorrect: Int,
    additionAttempts: Int,
    subtractionCorrect: Int,
    subtractionAttempts: Int,
    efficiencyScore: Int,
    difficultyLevel: Int
): String {
    val additionAccuracy = skillAccuracy(correct = additionCorrect, attempts = additionAttempts)
    val subtractionAccuracy = skillAccuracy(correct = subtractionCorrect, attempts = subtractionAttempts)

    return when {
        efficiencyScore < 70 -> "Mâine: 8 comori, nivel ușor și numărare ghidată, ca să reducem ghicitul."
        additionAttempts < 3 -> "Mâine: încălzire cu adunări mici până la 5, fără grabă."
        additionAccuracy < 80 -> "Mâine: repetă adunări concrete cu obiecte până la 5."
        difficultyLevel >= 3 && subtractionAttempts < 3 -> "Mâine: introdu 3 scăderi scurte cu mutare în cufăr."
        subtractionAttempts >= 3 && subtractionAccuracy < 75 -> "Mâine: scăderi pe punte, mută în cufăr înainte de răspuns."
        else -> "Mâine: păstrează sesiunea scurtă și lasă speed bump-ul să ridice nivelul."
    }
}

fun parentSkillGapLabelFor(
    additionCorrect: Int,
    additionAttempts: Int,
    subtractionCorrect: Int,
    subtractionAttempts: Int
): String {
    val additionReady = additionAttempts >= 3
    val subtractionReady = subtractionAttempts >= 3
    if (!additionReady && !subtractionReady) return "Calibrare"
    if (!additionReady) return "Adunare"
    if (!subtractionReady) return "Scădere"

    val additionAccuracy = skillAccuracy(correct = additionCorrect, attempts = additionAttempts)
    val subtractionAccuracy = skillAccuracy(correct = subtractionCorrect, attempts = subtractionAttempts)
    return when {
        additionAccuracy + 15 < subtractionAccuracy -> "Adunare"
        subtractionAccuracy + 15 < additionAccuracy -> "Scădere"
        else -> "Echilibrat"
    }
}

fun parentAuditSignalsFor(state: GameState): List<ParentAuditSignal> {
    val minutesUsed = state.sessionSecondsElapsed / 60
    val minutesTotal = (state.sessionSecondsTotal / 60).coerceAtLeast(1)
    val accuracy = if (state.attemptsTotal == 0) 100 else (state.correctTotal * 100 / state.attemptsTotal).coerceIn(0, 100)
    val efficiencyScore = learningEfficiencyScore(
        correct = state.correctTotal,
        attempts = state.attemptsTotal,
        repairRounds = state.repairRounds,
        consecutiveWrong = state.consecutiveWrong
    )
    val skillGap = parentSkillGapLabelFor(
        additionCorrect = state.additionCorrect,
        additionAttempts = state.additionAttempts,
        subtractionCorrect = state.subtractionCorrect,
        subtractionAttempts = state.subtractionAttempts
    )
    val additionAccuracy = skillAccuracy(correct = state.additionCorrect, attempts = state.additionAttempts)
    val subtractionAccuracy = skillAccuracy(correct = state.subtractionCorrect, attempts = state.subtractionAttempts)
    val qualityColor = when {
        state.attemptsTotal < 3 -> TextSandy
        accuracy < 70 || efficiencyScore < 70 -> RubyRed
        accuracy >= 95 && state.difficultyLevel <= 2 -> StarGold
        else -> EmeraldGreen
    }
    val gapColor = when (skillGap) {
        "Calibrare", "Echilibrat" -> EmeraldGreen
        else -> StarGold
    }

    return listOf(
        ParentAuditSignal(
            title = "Timp de lucru",
            valueText = "$minutesUsed/$minutesTotal min",
            detail = if (minutesUsed >= minutesTotal) "Time-box complet." else "În ritmul ales la onboarding/Parent Dash.",
            progress = dailyRingProgress(current = minutesUsed, total = minutesTotal),
            color = CoralBlue
        ),
        ParentAuditSignal(
            title = "Calitate",
            valueText = "$accuracy%",
            detail = "Eficiență $efficiencyScore% · ${learningEfficiencyLabel(score = efficiencyScore, attempts = state.attemptsTotal)}",
            progress = dailyRingProgress(current = efficiencyScore, total = 100),
            color = qualityColor
        ),
        ParentAuditSignal(
            title = "Skill gap",
            valueText = skillGap,
            detail = if (skillGap == "Calibrare") {
                "Mai trebuie semnal pe adunare și scădere."
            } else {
                "Adunare $additionAccuracy% · Scădere $subtractionAccuracy%"
            },
            progress = when (skillGap) {
                "Calibrare" -> 0.18f
                "Echilibrat" -> 1f
                else -> 0.55f
            },
            color = gapColor
        )
    )
}

fun selectAdaptiveOperationForNextGame(
    difficultyLevel: Int,
    maxDifficulty: Int,
    correctTotal: Int,
    consecutiveWrong: Int,
    additionCorrect: Int,
    additionAttempts: Int,
    subtractionCorrect: Int,
    subtractionAttempts: Int
): MathOperation {
    if (maxDifficulty < 3 || difficultyLevel < 3) return MathOperation.Addition
    if (consecutiveWrong > 0) return MathOperation.Addition

    val additionAccuracy = skillAccuracy(correct = additionCorrect, attempts = additionAttempts)
    if (additionAttempts < 3 || additionAccuracy < 80) return MathOperation.Addition

    if (subtractionAttempts < 2) return MathOperation.Subtraction

    val subtractionAccuracy = skillAccuracy(correct = subtractionCorrect, attempts = subtractionAttempts)
    if (subtractionAttempts >= 3 && subtractionAccuracy < 75) return MathOperation.Subtraction

    return if (correctTotal % 3 == 2) MathOperation.Subtraction else MathOperation.Addition
}

fun learningNumberRangeFor(difficulty: Int, recoveryMission: Boolean): IntRange {
    return if (recoveryMission) {
        2..4
    } else {
        when (difficulty) {
            1 -> 2..4
            2 -> 3..5
            3 -> 4..7
            4 -> 6..10
            else -> 8..12
        }
    }
}

fun shouldTriggerStruggleSupport(consecutiveWrong: Int, recoveryMissionActive: Boolean): Boolean {
    return consecutiveWrong >= 2 || recoveryMissionActive
}

fun shouldQueueRecoveryAfterCorrect(state: GameState, finishedDailyTarget: Boolean): Boolean {
    return !finishedDailyTarget && state.recoveryMissionQueued && state.selectedWrongAnswer != null
}

fun roundFocusFor(state: GameState): RoundFocus {
    return when {
        state.selectedWrongAnswer != null -> RoundFocus(
            title = "Reparare calmă",
            goal = "Refacem numărarea și alegem numai după ce toate comorile sunt clare."
        )
        state.recoveryMissionActive -> RoundFocus(
            title = "Port sigur",
            goal = "După o piedică, revenim la până la 4 comori și consolidăm baza."
        )
        state.struggleSupportActive -> RoundFocus(
            title = "Sprijin pe punte",
            goal = "Runda rămâne mică, cu obiectul luminos care conduce fiecare pas."
        )
        state.speedBumpActive -> RoundFocus(
            title = "Speed bump",
            goal = "Creștem puțin nivelul doar pentru răspunsuri sigure, nu pentru viteză."
        )
        state.operation == MathOperation.Subtraction -> RoundFocus(
            title = "Minus concret",
            goal = "Mutăm în cufăr, apoi numărăm doar comorile rămase pe punte."
        )
        correctAnswerFor(state) <= 5 -> RoundFocus(
            title = "Adunare până la 5",
            goal = "Unim două grupuri mici și citim ultimul număr atins."
        )
        else -> RoundFocus(
            title = "Adunare sigură",
            goal = "Ținem ordinea numărării până când răspunsul se deblochează."
        )
    }
}

fun dailyRingProgress(current: Int, total: Int): Float {
    return if (total <= 0) 0f else (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
}

fun sessionTimeUp(state: GameState): Boolean {
    return !state.showCelebration && state.sessionSecondsElapsed >= state.sessionSecondsTotal
}

private fun currentLocalDayIndex(): Int {
    val now = System.currentTimeMillis()
    val localOffsetMillis = TimeZone.getDefault().getOffset(now)
    return ((now + localOffsetMillis) / 86_400_000L).toInt()
}

data class GameState(
    val num1: Int = 1,
    val num2: Int = 1,
    val operation: MathOperation = MathOperation.Addition,
    val item1: PirateItem = treasureItems[0],
    val item2: PirateItem = treasureItems[1],
    val options: List<Int> = listOf(2, 3, 4, 5),
    val streak: Int = 0,
    val correctTotal: Int = 0,
    val attemptsTotal: Int = 0,
    val difficultyLevel: Int = 1,
    val consecutiveCorrect: Int = 0,
    val consecutiveWrong: Int = 0,
    val selectedWrongAnswer: Int? = null,
    val repairRound: Int = 0,
    val repairRounds: Int = 0,
    val isCorrecting: Boolean = false,
    val showCelebration: Boolean = false,
    val speedBumpActive: Boolean = false,
    val struggleSupportActive: Boolean = false,
    val recoveryMissionQueued: Boolean = false,
    val recoveryMissionActive: Boolean = false,
    val showOnboarding: Boolean = false,
    val sessionSecondsTotal: Int = 25 * 60,
    val sessionSecondsElapsed: Int = 0,
    val dailyTarget: Int = 12,
    val maxDifficulty: Int = 5,
    val lifetimeCoins: Int = 0,
    val completedSessions: Int = 0,
    val bestStreak: Int = 0,
    val dailyStreak: Int = 0,
    val lastCompletionDay: Int = 0,
    val lastSessionMinutes: Int = 0,
    val lastSessionAccuracy: Int = 100,
    val lastSessionRepairs: Int = 0,
    val additionCorrect: Int = 0,
    val additionAttempts: Int = 0,
    val subtractionCorrect: Int = 0,
    val subtractionAttempts: Int = 0,
    val sessionHistory: List<SessionRecord> = emptyList(),
    val coachMessage: String = "Bun venit, Căpitane Oséa. Numărăm încet și sigur.",
    val missionTitle: String = "Atinge comorile, apoi alege răspunsul."
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val progressStore = application.getSharedPreferences("osea_learning_progress", android.content.Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow(
        generateGame(1).copy(
            lifetimeCoins = progressStore.getInt("lifetimeCoins", 0),
            completedSessions = progressStore.getInt("completedSessions", 0),
            bestStreak = progressStore.getInt("bestStreak", 0),
            dailyStreak = progressStore.getInt("dailyStreak", 0),
            lastCompletionDay = progressStore.getInt("lastCompletionDay", 0),
            lastSessionMinutes = progressStore.getInt("lastSessionMinutes", 0),
            lastSessionAccuracy = progressStore.getInt("lastSessionAccuracy", 100),
            lastSessionRepairs = progressStore.getInt("lastSessionRepairs", 0),
            additionCorrect = progressStore.getInt("additionCorrect", 0),
            additionAttempts = progressStore.getInt("additionAttempts", 0),
            subtractionCorrect = progressStore.getInt("subtractionCorrect", 0),
            subtractionAttempts = progressStore.getInt("subtractionAttempts", 0),
            dailyTarget = progressStore.getInt("dailyTarget", 12),
            sessionSecondsTotal = progressStore.getInt("sessionSecondsTotal", 25 * 60),
            maxDifficulty = progressStore.getInt("maxDifficulty", 5),
            sessionHistory = loadSessionHistory(),
            showOnboarding = !progressStore.getBoolean("onboardingComplete", false)
        )
    )
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                _uiState.update { state ->
                    if (state.sessionSecondsElapsed < state.sessionSecondsTotal && !state.showCelebration && !state.showOnboarding) {
                        state.copy(sessionSecondsElapsed = state.sessionSecondsElapsed + 1)
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun onAnswer(answer: Int) {
        _uiState.update { current ->
            if (current.showCelebration || current.isCorrecting || sessionTimeUp(current)) return@update current

            val correctAnswer = correctAnswerFor(current)
            val attempts = current.attemptsTotal + 1
            val nextAdditionAttempts = current.additionAttempts + if (current.operation == MathOperation.Addition) 1 else 0
            val nextSubtractionAttempts = current.subtractionAttempts + if (current.operation == MathOperation.Subtraction) 1 else 0

            if (answer == correctAnswer) {
                val correctTotal = current.correctTotal + 1
                val consecutiveCorrect = current.consecutiveCorrect + 1
                val leveledUp = consecutiveCorrect >= 3
                val nextDifficulty = if (leveledUp) minOf(current.maxDifficulty, current.difficultyLevel + 1) else current.difficultyLevel
                val finishedDailyTarget = correctTotal >= current.dailyTarget
                val nextLifetimeCoins = current.lifetimeCoins + 1
                val nextCompletedSessions = if (finishedDailyTarget) current.completedSessions + 1 else current.completedSessions
                val nextBestStreak = maxOf(current.bestStreak, current.streak + 1)
                val nextAdditionCorrect = current.additionCorrect + if (current.operation == MathOperation.Addition) 1 else 0
                val nextSubtractionCorrect = current.subtractionCorrect + if (current.operation == MathOperation.Subtraction) 1 else 0
                val sessionAccuracy = (correctTotal * 100 / attempts).coerceIn(0, 100)
                val sessionMinutes = maxOf(1, current.sessionSecondsElapsed / 60)
                val today = if (finishedDailyTarget) currentLocalDayIndex() else current.lastCompletionDay
                val nextDailyStreak = if (finishedDailyTarget) {
                    nextDailyStreak(
                        lastCompletionDay = current.lastCompletionDay,
                        currentStreak = current.dailyStreak,
                        today = today
                    )
                } else {
                    current.dailyStreak
                }
                val nextSessionHistory = if (finishedDailyTarget) {
                    appendSessionRecord(
                        history = current.sessionHistory,
                        record = SessionRecord(
                            dayIndex = today,
                            minutes = sessionMinutes,
                            accuracy = sessionAccuracy,
                            repairs = current.repairRounds,
                            coins = correctTotal,
                            difficulty = nextDifficulty
                        )
                    )
                } else {
                    current.sessionHistory
                }
                val nextRecoveryQueued = shouldQueueRecoveryAfterCorrect(
                    state = current,
                    finishedDailyTarget = finishedDailyTarget
                )

                saveProgress(
                    lifetimeCoins = nextLifetimeCoins,
                    completedSessions = nextCompletedSessions,
                    bestStreak = nextBestStreak,
                    dailyStreak = nextDailyStreak,
                    lastCompletionDay = today,
                    lastSessionMinutes = if (finishedDailyTarget) sessionMinutes else current.lastSessionMinutes,
                    lastSessionAccuracy = if (finishedDailyTarget) sessionAccuracy else current.lastSessionAccuracy,
                    lastSessionRepairs = if (finishedDailyTarget) current.repairRounds else current.lastSessionRepairs,
                    additionCorrect = nextAdditionCorrect,
                    additionAttempts = nextAdditionAttempts,
                    subtractionCorrect = nextSubtractionCorrect,
                    subtractionAttempts = nextSubtractionAttempts
                )
                if (finishedDailyTarget) {
                    saveSessionHistory(nextSessionHistory)
                }

                current.copy(
                    streak = current.streak + 1,
                    correctTotal = correctTotal,
                    attemptsTotal = attempts,
                    difficultyLevel = nextDifficulty,
                    consecutiveCorrect = consecutiveCorrect,
                    consecutiveWrong = 0,
                    selectedWrongAnswer = null,
                    repairRound = current.repairRound,
                    repairRounds = current.repairRounds,
                    isCorrecting = !finishedDailyTarget,
                    showCelebration = finishedDailyTarget,
                    speedBumpActive = leveledUp && !nextRecoveryQueued,
                    struggleSupportActive = nextRecoveryQueued,
                    recoveryMissionQueued = nextRecoveryQueued,
                    recoveryMissionActive = false,
                    lifetimeCoins = nextLifetimeCoins,
                    completedSessions = nextCompletedSessions,
                    bestStreak = nextBestStreak,
                    dailyStreak = nextDailyStreak,
                    lastCompletionDay = today,
                    lastSessionMinutes = if (finishedDailyTarget) sessionMinutes else current.lastSessionMinutes,
                    lastSessionAccuracy = if (finishedDailyTarget) sessionAccuracy else current.lastSessionAccuracy,
                    lastSessionRepairs = if (finishedDailyTarget) current.repairRounds else current.lastSessionRepairs,
                    additionCorrect = nextAdditionCorrect,
                    additionAttempts = nextAdditionAttempts,
                    subtractionCorrect = nextSubtractionCorrect,
                    subtractionAttempts = nextSubtractionAttempts,
                    sessionHistory = nextSessionHistory,
                    coachMessage = if (nextRecoveryQueued) {
                        "Port sigur pregătit: următoarea rundă revine la comori mici."
                    } else if (leveledUp) {
                        "Speed bump trecut. Urcăm puțin nivelul, dar rămânem la obiecte mici."
                    } else {
                        "Perfect, Oséa. Ai numărat cu grijă."
                    }
                )
            } else {
                val consecutiveWrong = current.consecutiveWrong + 1
                val needsSupport = shouldTriggerStruggleSupport(
                    consecutiveWrong = consecutiveWrong,
                    recoveryMissionActive = current.recoveryMissionActive
                )
                val nextDifficulty = if (needsSupport) maxOf(1, current.difficultyLevel - 1) else current.difficultyLevel

                saveProgress(
                    lifetimeCoins = current.lifetimeCoins,
                    completedSessions = current.completedSessions,
                    bestStreak = current.bestStreak,
                    dailyStreak = current.dailyStreak,
                    lastCompletionDay = current.lastCompletionDay,
                    lastSessionMinutes = current.lastSessionMinutes,
                    lastSessionAccuracy = current.lastSessionAccuracy,
                    lastSessionRepairs = current.lastSessionRepairs,
                    additionCorrect = current.additionCorrect,
                    additionAttempts = nextAdditionAttempts,
                    subtractionCorrect = current.subtractionCorrect,
                    subtractionAttempts = nextSubtractionAttempts
                )

                current.copy(
                    attemptsTotal = attempts,
                    streak = 0,
                    selectedWrongAnswer = answer,
                    repairRound = current.repairRound + 1,
                    repairRounds = current.repairRounds + 1,
                    consecutiveCorrect = 0,
                    consecutiveWrong = consecutiveWrong,
                    difficultyLevel = nextDifficulty,
                    speedBumpActive = false,
                    struggleSupportActive = needsSupport,
                    recoveryMissionQueued = needsSupport,
                    recoveryMissionActive = false,
                    additionAttempts = nextAdditionAttempts,
                    subtractionAttempts = nextSubtractionAttempts,
                    coachMessage = if (needsSupport) {
                        "Struggle support: facem următoarea întrebare mai ușoară și numărăm împreună."
                    } else {
                        "Aproape. Atinge fiecare comoară pe rând și vezi ce număr apare."
                    }
                )
            }
        }
    }

    fun nextQuestion() {
        _uiState.update { current ->
            val startsRecoveryMission = current.recoveryMissionQueued
            val generated = generateGame(current.difficultyLevel, current)
            generated.copy(
                streak = current.streak,
                correctTotal = current.correctTotal,
                attemptsTotal = current.attemptsTotal,
                difficultyLevel = current.difficultyLevel,
                consecutiveCorrect = if (current.consecutiveCorrect >= 3) 0 else current.consecutiveCorrect,
                consecutiveWrong = if (current.consecutiveWrong >= 2) 0 else current.consecutiveWrong,
                repairRounds = current.repairRounds,
                sessionSecondsElapsed = current.sessionSecondsElapsed,
                sessionSecondsTotal = current.sessionSecondsTotal,
                dailyTarget = current.dailyTarget,
                maxDifficulty = current.maxDifficulty,
                lifetimeCoins = current.lifetimeCoins,
                completedSessions = current.completedSessions,
                bestStreak = current.bestStreak,
                dailyStreak = current.dailyStreak,
                lastCompletionDay = current.lastCompletionDay,
                lastSessionMinutes = current.lastSessionMinutes,
                lastSessionAccuracy = current.lastSessionAccuracy,
                lastSessionRepairs = current.lastSessionRepairs,
                additionCorrect = current.additionCorrect,
                additionAttempts = current.additionAttempts,
                subtractionCorrect = current.subtractionCorrect,
                subtractionAttempts = current.subtractionAttempts,
                sessionHistory = current.sessionHistory,
                speedBumpActive = current.speedBumpActive && !startsRecoveryMission,
                struggleSupportActive = startsRecoveryMission,
                recoveryMissionQueued = false,
                recoveryMissionActive = startsRecoveryMission,
                coachMessage = if (startsRecoveryMission) {
                    recoveryMissionMessage()
                } else {
                    nextMissionMessage(current.difficultyLevel, generated.operation)
                }
            )
        }
    }

    fun playAgain() {
        _uiState.update { current ->
            generateGame(1, current).copy(
                lifetimeCoins = current.lifetimeCoins,
                completedSessions = current.completedSessions,
                bestStreak = current.bestStreak,
                dailyStreak = current.dailyStreak,
                lastCompletionDay = current.lastCompletionDay,
                lastSessionMinutes = current.lastSessionMinutes,
                lastSessionAccuracy = current.lastSessionAccuracy,
                lastSessionRepairs = current.lastSessionRepairs,
                dailyTarget = current.dailyTarget,
                sessionSecondsTotal = current.sessionSecondsTotal,
                maxDifficulty = current.maxDifficulty,
                additionCorrect = current.additionCorrect,
                additionAttempts = current.additionAttempts,
                subtractionCorrect = current.subtractionCorrect,
                subtractionAttempts = current.subtractionAttempts,
                sessionHistory = current.sessionHistory
            )
        }
    }

    fun startFreshSession() {
        _uiState.update { current ->
            generateGame(1, current).copy(
                lifetimeCoins = current.lifetimeCoins,
                completedSessions = current.completedSessions,
                bestStreak = current.bestStreak,
                dailyStreak = current.dailyStreak,
                lastCompletionDay = current.lastCompletionDay,
                lastSessionMinutes = current.lastSessionMinutes,
                lastSessionAccuracy = current.lastSessionAccuracy,
                lastSessionRepairs = current.lastSessionRepairs,
                dailyTarget = current.dailyTarget,
                sessionSecondsTotal = current.sessionSecondsTotal,
                maxDifficulty = current.maxDifficulty,
                additionCorrect = current.additionCorrect,
                additionAttempts = current.additionAttempts,
                subtractionCorrect = current.subtractionCorrect,
                subtractionAttempts = current.subtractionAttempts,
                sessionHistory = current.sessionHistory,
                coachMessage = "Pauza s-a terminat. Pornim încet, cu obiecte mici."
            )
        }
    }

    fun setDailyTarget(target: Int) {
        val normalizedTarget = target.coerceIn(6, 18)
        progressStore.edit().putInt("dailyTarget", normalizedTarget).apply()
        _uiState.update { state -> state.copy(dailyTarget = normalizedTarget) }
    }

    fun setSessionMinutes(minutes: Int) {
        val normalizedSeconds = minutes.coerceIn(10, 25) * 60
        progressStore.edit().putInt("sessionSecondsTotal", normalizedSeconds).apply()
        _uiState.update { state ->
            state.copy(
                sessionSecondsTotal = normalizedSeconds,
                sessionSecondsElapsed = state.sessionSecondsElapsed.coerceAtMost(normalizedSeconds)
            )
        }
    }

    fun setMaxDifficulty(maxDifficulty: Int) {
        val normalizedDifficulty = maxDifficulty.coerceIn(2, 5)
        progressStore.edit().putInt("maxDifficulty", normalizedDifficulty).apply()
        _uiState.update { state ->
            state.copy(
                maxDifficulty = normalizedDifficulty,
                difficultyLevel = state.difficultyLevel.coerceAtMost(normalizedDifficulty)
            )
        }
    }

    fun completeOnboarding(preset: OnboardingPreset) {
        val normalizedTarget = preset.dailyTarget.coerceIn(6, 18)
        val normalizedSeconds = preset.sessionMinutes.coerceIn(10, 25) * 60
        val normalizedDifficulty = preset.maxDifficulty.coerceIn(2, 5)
        progressStore.edit()
            .putBoolean("onboardingComplete", true)
            .putInt("dailyTarget", normalizedTarget)
            .putInt("sessionSecondsTotal", normalizedSeconds)
            .putInt("maxDifficulty", normalizedDifficulty)
            .apply()

        _uiState.update { state ->
            state.copy(
                showOnboarding = false,
                dailyTarget = normalizedTarget,
                sessionSecondsTotal = normalizedSeconds,
                sessionSecondsElapsed = state.sessionSecondsElapsed.coerceAtMost(normalizedSeconds),
                maxDifficulty = normalizedDifficulty,
                difficultyLevel = state.difficultyLevel.coerceAtMost(normalizedDifficulty),
                coachMessage = "Start ales: ${preset.title}. Pornim cu pași mici și siguri.",
                missionTitle = nextMissionMessage(state.difficultyLevel.coerceAtMost(normalizedDifficulty), state.operation)
            )
        }
    }

    private fun generateGame(difficulty: Int, sourceState: GameState? = null): GameState {
        val recoveryMission = sourceState?.recoveryMissionQueued == true
        val range = learningNumberRangeFor(difficulty = difficulty, recoveryMission = recoveryMission)
        val operation = when {
            sourceState == null -> MathOperation.Addition
            recoveryMission -> MathOperation.Addition
            else -> selectAdaptiveOperationForNextGame(
                difficultyLevel = difficulty,
                maxDifficulty = sourceState.maxDifficulty,
                correctTotal = sourceState.correctTotal,
                consecutiveWrong = sourceState.consecutiveWrong,
                additionCorrect = sourceState.additionCorrect,
                additionAttempts = sourceState.additionAttempts,
                subtractionCorrect = sourceState.subtractionCorrect,
                subtractionAttempts = sourceState.subtractionAttempts
            )
        }
        val num1: Int
        val num2: Int
        val correct: Int

        if (operation == MathOperation.Addition) {
            val sum = range.random()
            num1 = (1 until sum).random()
            num2 = sum - num1
            correct = sum
        } else {
            num1 = range.random().coerceAtLeast(3)
            num2 = (1 until num1).random()
            correct = num1 - num2
        }
        val options = mutableSetOf(correct)

        while (options.size < 4) {
            val deviation = (-4..4).random()
            val candidate = correct + deviation
            if (candidate > 0 && candidate != correct) {
                options.add(candidate)
            }
        }

        val item1 = treasureItems.random()
        var item2 = treasureItems.random()
        while (item2 == item1) {
            item2 = treasureItems.random()
        }
        val rightItem = if (operation == MathOperation.Subtraction) item1 else item2

        return GameState(
            num1 = num1,
            num2 = num2,
            operation = operation,
            item1 = item1,
            item2 = rightItem,
            options = options.toList().shuffled(),
            difficultyLevel = difficulty,
            recoveryMissionActive = recoveryMission,
            missionTitle = if (recoveryMission) recoveryMissionMessage() else nextMissionMessage(difficulty, operation)
        )
    }

    private fun recoveryMissionMessage(): String {
        return "Port sigur: revenim la comori mici până la 4."
    }

    private fun nextMissionMessage(difficulty: Int, operation: MathOperation): String {
        return if (operation == MathOperation.Subtraction) {
            "Misiune de minus: mutăm comori în cufăr."
        } else {
            nextMissionMessage(difficulty)
        }
    }

    private fun nextMissionMessage(difficulty: Int): String {
        return when (difficulty) {
            1 -> "Încălzire: numărăm încet până la 4."
            2 -> "Misiune nouă: adunări mici până la 5."
            3 -> "Nivel curajos: apar primele scăderi mici."
            4 -> "Speed bump: adunăm și scădem până la 10."
            else -> "Mastery: răspunsuri sigure până la 12."
        }
    }

    private fun saveProgress(
        lifetimeCoins: Int,
        completedSessions: Int,
        bestStreak: Int,
        dailyStreak: Int,
        lastCompletionDay: Int,
        lastSessionMinutes: Int,
        lastSessionAccuracy: Int,
        lastSessionRepairs: Int,
        additionCorrect: Int,
        additionAttempts: Int,
        subtractionCorrect: Int,
        subtractionAttempts: Int
    ) {
        progressStore.edit()
            .putInt("lifetimeCoins", lifetimeCoins)
            .putInt("completedSessions", completedSessions)
            .putInt("bestStreak", bestStreak)
            .putInt("dailyStreak", dailyStreak)
            .putInt("lastCompletionDay", lastCompletionDay)
            .putInt("lastSessionMinutes", lastSessionMinutes)
            .putInt("lastSessionAccuracy", lastSessionAccuracy)
            .putInt("lastSessionRepairs", lastSessionRepairs)
            .putInt("additionCorrect", additionCorrect)
            .putInt("additionAttempts", additionAttempts)
            .putInt("subtractionCorrect", subtractionCorrect)
            .putInt("subtractionAttempts", subtractionAttempts)
            .apply()
    }

    private fun loadSessionHistory(): List<SessionRecord> {
        val count = progressStore.getInt("sessionHistoryCount", 0).coerceIn(0, 4)
        return (0 until count).mapNotNull { index ->
            val prefix = "sessionHistory_${index}_"
            val minutes = progressStore.getInt("${prefix}minutes", -1)
            if (minutes <= 0) {
                null
            } else {
                SessionRecord(
                    dayIndex = progressStore.getInt("${prefix}dayIndex", 0),
                    minutes = minutes,
                    accuracy = progressStore.getInt("${prefix}accuracy", 100),
                    repairs = progressStore.getInt("${prefix}repairs", 0),
                    coins = progressStore.getInt("${prefix}coins", 0),
                    difficulty = progressStore.getInt("${prefix}difficulty", 1)
                )
            }
        }
    }

    private fun saveSessionHistory(history: List<SessionRecord>) {
        val boundedHistory = history.take(4)
        val editor = progressStore.edit().putInt("sessionHistoryCount", boundedHistory.size)
        repeat(4) { index ->
            val prefix = "sessionHistory_${index}_"
            val record = boundedHistory.getOrNull(index)
            if (record == null) {
                editor
                    .remove("${prefix}dayIndex")
                    .remove("${prefix}minutes")
                    .remove("${prefix}accuracy")
                    .remove("${prefix}repairs")
                    .remove("${prefix}coins")
                    .remove("${prefix}difficulty")
            } else {
                editor
                    .putInt("${prefix}dayIndex", record.dayIndex)
                    .putInt("${prefix}minutes", record.minutes)
                    .putInt("${prefix}accuracy", record.accuracy)
                    .putInt("${prefix}repairs", record.repairs)
                    .putInt("${prefix}coins", record.coins)
                    .putInt("${prefix}difficulty", record.difficulty)
            }
        }
        editor.apply()
    }
}

object OfflineAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private val executor = java.util.concurrent.Executors.newSingleThreadExecutor()

    fun play(context: android.content.Context, resId: Int) {
        val appContext = context.applicationContext
        executor.execute {
            try {
                mediaPlayer?.let { player ->
                    player.setOnCompletionListener(null)
                    if (player.isPlaying) {
                        player.stop()
                    }
                    player.release()
                }
            } catch (_: Exception) {
            }
            mediaPlayer = null

            try {
                val player = MediaPlayer.create(appContext, resId)
                if (player != null) {
                    mediaPlayer = player
                    player.setOnCompletionListener { completedPlayer ->
                        executor.execute {
                            try {
                                completedPlayer.setOnCompletionListener(null)
                                completedPlayer.release()
                            } catch (_: Exception) {
                            }
                            if (mediaPlayer == completedPlayer) {
                                mediaPlayer = null
                            }
                        }
                    }
                    player.start()
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}

object SpeechNarrator {
    private var textToSpeech: TextToSpeech? = null
    private var pendingText: String? = null

    fun speak(context: android.content.Context, text: String) {
        val appContext = context.applicationContext
        val engine = textToSpeech
        if (engine == null) {
            pendingText = text
            var createdEngine: TextToSpeech? = null
            createdEngine = TextToSpeech(appContext) { status ->
                val readyEngine = createdEngine ?: textToSpeech
                if (status == TextToSpeech.SUCCESS && readyEngine != null) {
                    readyEngine.language = Locale.forLanguageTag("ro-RO")
                    readyEngine.setSpeechRate(0.88f)
                    readyEngine.setPitch(1.05f)
                    pendingText?.let { speakNow(readyEngine, it) }
                    pendingText = null
                }
            }
            textToSpeech = createdEngine
        } else {
            speakNow(engine, text)
        }
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        pendingText = null
    }

    private fun speakNow(engine: TextToSpeech, text: String) {
        engine.stop()
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "osea_${System.currentTimeMillis()}")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MathGameScreen()
            }
        }
    }

    override fun onDestroy() {
        SpeechNarrator.shutdown()
        super.onDestroy()
    }
}

@Composable
fun MathGameScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val countedItems = remember(
        state.num1,
        state.num2,
        state.operation,
        state.item1,
        state.item2,
        state.options,
        state.repairRound,
        state.correctTotal,
        state.attemptsTotal,
        state.recoveryMissionActive
    ) {
        mutableStateMapOf<String, Int>()
    }

    LaunchedEffect(state.isCorrecting) {
        if (state.isCorrecting) {
            val audioNames = listOf("correct_1", "correct_2", "correct_3", "correct_4")
            val resId = context.resources.getIdentifier(audioNames.random(), "raw", context.packageName)
            if (resId != 0) {
                OfflineAudioPlayer.play(context, resId)
            }
            delay(1250)
            viewModel.nextQuestion()
        }
    }

    LaunchedEffect(state.repairRound) {
        if (state.selectedWrongAnswer != null) {
            val audioNames = listOf("wrong_1", "wrong_2", "wrong_3")
            val resId = context.resources.getIdentifier(audioNames.random(), "raw", context.packageName)
            if (resId != 0) {
                OfflineAudioPlayer.play(context, resId)
            }
        }
    }

    LaunchedEffect(state.showCelebration) {
        if (state.showCelebration) {
            val resId = context.resources.getIdentifier("victory_sound", "raw", context.packageName)
            if (resId != 0) {
                OfflineAudioPlayer.play(context, resId)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF062C43), OceanBg, Color(0xFF051827))
                    )
                )
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_osea_cove_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.86f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                OceanBg.copy(alpha = 0.22f),
                                OceanBg.copy(alpha = 0.08f),
                                OceanBg.copy(alpha = 0.66f)
                            )
                        )
                    )
            )
            OceanBackdrop()
            if (state.showOnboarding) {
                OnboardingScreen(onPresetSelected = viewModel::completeOnboarding)
            } else if (state.showCelebration) {
                CelebrationScreen(state = state, onPlayAgain = viewModel::playAgain)
            } else if (sessionTimeUp(state)) {
                SessionBreakScreen(state = state, onStartFreshSession = viewModel::startFreshSession)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PremiumHero(state = state)
                    Spacer(modifier = Modifier.height(10.dp))
                    MasteryFocusStrip(state = state)
                    Spacer(modifier = Modifier.height(10.dp))
                    DashboardHeader(state = state)
                    Spacer(modifier = Modifier.height(10.dp))
                    LearningJourney(correctTotal = state.correctTotal, dailyTarget = state.dailyTarget)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProblemStage(
                        state = state,
                        countedItems = countedItems,
                        guidedItemId = nextGuidedItemId(state, countedItems.keys),
                        onItemTapped = { id ->
                            val guidedItemId = nextGuidedItemId(state, countedItems.keys)
                            val nextCountedItems = nextCountedItemsAfterTap(
                                countedItems = countedItems.toMap(),
                                tappedItemId = id,
                                guidedItemId = guidedItemId
                            )
                            if (nextCountedItems != countedItems) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                countedItems.clear()
                                countedItems.putAll(nextCountedItems)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CoachPanel(
                        state = state,
                        countedCount = countedItems.size,
                        onSpeak = {
                            SpeechNarrator.speak(
                                context = context,
                                text = buildCoachNarration(state = state, countedCount = countedItems.size)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    AnswerGrid(
                        options = state.options,
                        wrongAnswer = state.selectedWrongAnswer,
                        isCorrecting = state.isCorrecting,
                        correctAnswer = correctAnswerFor(state),
                        isEnabled = answerButtonsUnlocked(state, countedItems.size) && !sessionTimeUp(state),
                        onAnswer = { answer ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onAnswer(answer)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RewardHarbor(state = state)
                    Spacer(modifier = Modifier.height(12.dp))
                    CaptainQuestBoard(state = state)
                    Spacer(modifier = Modifier.height(12.dp))
                    ParentInsightStrip(
                        state = state,
                        onDailyTargetSelected = viewModel::setDailyTarget,
                        onSessionMinutesSelected = viewModel::setSessionMinutes,
                        onMaxDifficultySelected = viewModel::setMaxDifficulty
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun OceanBackdrop() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color(0xFF00B8D4).copy(alpha = 0.14f),
            radius = size.width * 0.35f,
            center = Offset(size.width * 0.12f, size.height * 0.14f)
        )
        drawCircle(
            color = Color(0xFFFFD54F).copy(alpha = 0.12f),
            radius = size.width * 0.28f,
            center = Offset(size.width * 0.92f, size.height * 0.24f)
        )
        repeat(7) { index ->
            val y = size.height * (0.18f + index * 0.105f)
            drawLine(
                color = Color.White.copy(alpha = 0.06f),
                start = Offset(0f, y),
                end = Offset(size.width, y + 16f),
                strokeWidth = 3f
            )
        }
    }
}

@Composable
internal fun OnboardingScreen(
    onPresetSelected: (OnboardingPreset) -> Unit
) {
    var selectedPreset by remember { mutableStateOf(recommendedOnboardingPresetForAge(4)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Corabia lui Oséa",
            color = StarGold,
            fontSize = 35.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Alege ritmul de start",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Image(
            painter = painterResource(id = R.drawable.item_ship),
            contentDescription = "Corabie de start",
            modifier = Modifier.size(132.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(18.dp))
        onboardingPresetOptions().forEach { preset ->
            OnboardingPresetOption(
                preset = preset,
                selected = selectedPreset.id == preset.id,
                onClick = { selectedPreset = preset }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = selectedPreset.color.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, selectedPreset.color.copy(alpha = 0.52f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(selectedPreset.color.copy(alpha = 0.26f))
                        .border(1.dp, Color.White.copy(alpha = 0.42f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedPreset.dailyTarget.toString(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = onboardingPresetSummary(selectedPreset),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Poți schimba oricând din Parent Dash.",
                        color = TextSandy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 13.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedPreset.color,
                contentColor = OceanBg
            ),
            onClick = { onPresetSelected(selectedPreset) }
        ) {
            Text(
                text = "Pornește aventura",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun OnboardingPresetOption(
    preset: OnboardingPreset,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = if (selected) preset.color.copy(alpha = 0.18f) else Color(0xFF123343).copy(alpha = 0.76f),
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) preset.color.copy(alpha = 0.82f) else Color.White.copy(alpha = 0.14f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(preset.color.copy(alpha = if (selected) 0.32f else 0.14f))
                    .border(1.dp, Color.White.copy(alpha = if (selected) 0.52f else 0.2f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = preset.sessionMinutes.toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = preset.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (preset.recommended) {
                        Spacer(modifier = Modifier.width(7.dp))
                        StatusPill("Recomandat", EmeraldGreen)
                    }
                }
                Text(
                    text = preset.detail,
                    color = TextSandy,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 15.sp
                )
            }
            Text(
                text = if (selected) "✓" else "",
                color = preset.color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(20.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PremiumHero(state: GameState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Corabia lui Oséa",
                color = StarGold,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = state.missionTitle,
                color = Color.White.copy(alpha = 0.86f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Nivel", color = TextSandy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    state.difficultyLevel.toString(),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun MasteryFocusStrip(state: GameState) {
    val focus = roundFocusFor(state)
    val focusColor = when {
        state.selectedWrongAnswer != null -> RubyRed
        state.struggleSupportActive -> CoralBlue
        state.speedBumpActive -> StarGold
        state.operation == MathOperation.Subtraction -> StarGold
        else -> EmeraldGreen
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = focusColor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, focusColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = focusColor.copy(alpha = 0.22f),
                border = BorderStroke(1.dp, focusColor.copy(alpha = 0.58f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        if (state.operation == MathOperation.Subtraction) "-" else "+",
                        color = focusColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    focus.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 15.sp
                )
                Text(
                    focus.goal,
                    color = TextSandy,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 13.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, focusColor.copy(alpha = 0.34f))
            ) {
                Text(
                    "Focus",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    color = focusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun LearningJourney(correctTotal: Int, dailyTarget: Int) {
    val activeIslandIndex = activeLearningIslandIndexFor(correctTotal)
    val activeIsland = learningIslands[activeIslandIndex]
    val coinsToActiveIsland = coinsToActiveLearningIsland(correctTotal)
    val segmentProgress = activeLearningIslandSegmentProgress(correctTotal)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF082B38).copy(alpha = 0.82f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Harta Mastery",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "$correctTotal/$dailyTarget comori",
                    color = StarGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            VoyageMissionStrip(
                activeIsland = activeIsland,
                coinsToActiveIsland = coinsToActiveIsland,
                segmentProgress = segmentProgress,
                dailyProgress = dailyRingProgress(current = correctTotal, total = dailyTarget)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                learningIslands.forEachIndexed { index, island ->
                    val reached = correctTotal >= island.targetCoins
                    IslandNode(
                        island = island,
                        reached = reached,
                        active = index == activeIslandIndex,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun VoyageMissionStrip(
    activeIsland: LearningIsland,
    coinsToActiveIsland: Int,
    segmentProgress: Float,
    dailyProgress: Float
) {
    val missionLabel = if (coinsToActiveIsland == 0) {
        "Insulă cucerită"
    } else {
        "Mai ai $coinsToActiveIsland ${if (coinsToActiveIsland == 1) "comoară" else "comori"}"
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = activeIsland.color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, activeIsland.color.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = activeIsland.color,
                border = BorderStroke(2.dp, Color.White.copy(alpha = 0.74f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        activeIsland.icon,
                        color = OceanBg,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Misiune pe hartă",
                    color = TextSandy,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    activeIsland.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { segmentProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = activeIsland.color,
                    trackColor = Color.White.copy(alpha = 0.14f),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { dailyProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50)),
                    color = StarGold,
                    trackColor = Color.White.copy(alpha = 0.08f),
                    strokeCap = StrokeCap.Round
                )
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, activeIsland.color.copy(alpha = 0.46f))
            ) {
                Text(
                    missionLabel,
                    modifier = Modifier
                        .width(72.dp)
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    color = if (coinsToActiveIsland == 0) EmeraldGreen else StarGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
internal fun RewardHarbor(state: GameState) {
    val unlockedRewards = unlockedRewardCountFor(state.lifetimeCoins)
    val nextReward = nextRewardDefinitionFor(state.lifetimeCoins)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF2D1E16).copy(alpha = 0.72f),
        border = BorderStroke(1.dp, StarGold.copy(alpha = 0.34f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Colecția lui Oséa",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "${state.lifetimeCoins} comori strânse în total",
                        color = TextSandy.copy(alpha = 0.74f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = StarGold.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, StarGold.copy(alpha = 0.46f))
                ) {
                    Text(
                        text = "Record ${state.bestStreak}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = StarGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            RewardProgressCard(
                lifetimeCoins = state.lifetimeCoins,
                nextReward = nextReward
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rewardDefinitions.forEachIndexed { index, reward ->
                    RewardBadge(
                        reward = reward,
                        isUnlocked = index < unlockedRewards,
                        isNext = nextReward == reward,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardProgressCard(
    lifetimeCoins: Int,
    nextReward: RewardDefinition?
) {
    val progress = rewardProgressToNextFor(lifetimeCoins)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, (nextReward?.rarity?.color ?: EmeraldGreen).copy(alpha = 0.38f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(16.dp),
                color = (nextReward?.color ?: EmeraldGreen).copy(alpha = 0.18f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (nextReward != null) {
                        Image(
                            painter = painterResource(nextReward.drawableRes),
                            contentDescription = nextReward.label,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize().padding(6.dp)
                        )
                    } else {
                        Text("✓", color = EmeraldGreen, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (nextReward == null) "Colecție completă" else "Următor: ${nextReward.label}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                    Text(
                        text = if (nextReward == null) "gata" else "-${coinsToNextRewardFor(lifetimeCoins)}",
                        color = nextReward?.rarity?.color ?: EmeraldGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(50)),
                    color = nextReward?.rarity?.color ?: EmeraldGreen,
                    trackColor = Color.White.copy(alpha = 0.12f),
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = if (nextReward == null) {
                        "Toate comorile de bază sunt în port."
                    } else {
                        "${nextReward.rarity.label} • deblocat la ${nextReward.unlockCoins} comori"
                    },
                    color = TextSandy.copy(alpha = 0.72f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
private fun RewardBadge(
    reward: RewardDefinition,
    isUnlocked: Boolean,
    isNext: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isUnlocked -> 1.06f
            isNext -> 1.03f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.68f, stiffness = 300f),
        label = "rewardScale"
    )
    Column(
        modifier = modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isUnlocked -> reward.color.copy(alpha = 0.88f)
                        isNext -> reward.rarity.color.copy(alpha = 0.18f)
                        else -> Color.White.copy(alpha = 0.1f)
                    }
                )
                .border(
                    if (isNext) 2.dp else 1.dp,
                    when {
                        isUnlocked -> Color.White.copy(alpha = 0.82f)
                        isNext -> reward.rarity.color.copy(alpha = 0.78f)
                        else -> Color.White.copy(alpha = 0.14f)
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked && reward.rarity != commonReward) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.18f),
                        radius = size.minDimension * 0.42f,
                        center = Offset(size.width * 0.34f, size.height * 0.26f)
                    )
                }
            }
            Image(
                painter = painterResource(reward.drawableRes),
                contentDescription = reward.label,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isNext) 5.dp else 6.dp)
                    .alpha(if (isUnlocked || isNext) 1f else 0.34f)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Surface(
            shape = RoundedCornerShape(50),
            color = reward.rarity.color.copy(alpha = if (isUnlocked || isNext) 0.18f else 0.08f),
            border = BorderStroke(1.dp, reward.rarity.color.copy(alpha = if (isUnlocked || isNext) 0.42f else 0.16f))
        ) {
            Text(
                text = reward.rarity.label,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                color = reward.rarity.color.copy(alpha = if (isUnlocked || isNext) 1f else 0.45f),
                fontSize = 6.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = reward.label,
            color = Color.White.copy(alpha = if (isUnlocked) 0.86f else 0.42f),
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            lineHeight = 9.sp
        )
        Text(
            text = if (isUnlocked) reward.detail else "${reward.unlockCoins} comori",
            color = TextSandy.copy(alpha = if (isUnlocked || isNext) 0.62f else 0.32f),
            fontSize = 7.sp,
            textAlign = TextAlign.Center,
            lineHeight = 8.sp,
            maxLines = 2
        )
    }
}

@Composable
internal fun CaptainQuestBoard(state: GameState) {
    val quests = captainQuestsFor(state)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF123343).copy(alpha = 0.88f),
        border = BorderStroke(1.dp, CoralBlue.copy(alpha = 0.34f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Misiuni de azi",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "3 trasee scurte pentru sesiunea asta",
                        color = TextSandy.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = CoralBlue.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CoralBlue.copy(alpha = 0.42f))
                ) {
                    Text(
                        text = "${quests.count { it.progress >= 1f }}/${quests.size}",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        color = CoralBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            quests.forEachIndexed { index, quest ->
                CaptainQuestRow(quest = quest)
                if (index != quests.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CaptainQuestRow(quest: CaptainQuest) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, quest.color.copy(alpha = 0.28f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(15.dp),
                color = quest.color.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.24f))
            ) {
                Image(
                    painter = painterResource(quest.drawableRes),
                    contentDescription = quest.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(5.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = quest.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                    Text(
                        text = quest.valueText,
                        color = quest.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                LinearProgressIndicator(
                    progress = { quest.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = quest.color,
                    trackColor = Color.White.copy(alpha = 0.12f),
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = quest.detail,
                    color = TextSandy.copy(alpha = 0.76f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
private fun IslandNode(
    island: LearningIsland,
    reached: Boolean,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        val scale by animateFloatAsState(
            targetValue = when {
                active -> 1.12f
                reached -> 1.06f
                else -> 1f
            },
            animationSpec = spring(dampingRatio = 0.62f, stiffness = 320f),
            label = "islandScale"
        )
        Box(
            modifier = Modifier
                .scale(scale)
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    when {
                        active -> island.color
                        reached -> island.color.copy(alpha = 0.74f)
                        else -> Color.White.copy(alpha = 0.12f)
                    }
                )
                .border(
                    2.dp,
                    when {
                        active -> StarGold
                        reached -> Color.White.copy(alpha = 0.78f)
                        else -> Color.White.copy(alpha = 0.2f)
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = island.icon,
                color = if (active || reached) OceanBg else Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = island.title,
            color = Color.White.copy(alpha = if (active || reached) 0.96f else 0.62f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 11.sp
        )
        Text(
            text = if (active && !reached) "misiune acum" else island.focus,
            color = Color.White.copy(alpha = if (active || reached) 0.7f else 0.42f),
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            lineHeight = 9.sp
        )
    }
}

@Composable
private fun DashboardHeader(state: GameState) {
    val accuracy = if (state.attemptsTotal == 0) 100 else (state.correctTotal * 100 / state.attemptsTotal)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DailyRing(
            modifier = Modifier.weight(1f),
            title = "Timp",
            current = state.sessionSecondsElapsed,
            total = state.sessionSecondsTotal,
            color = CoralBlue,
            centerText = "${state.sessionSecondsElapsed / 60}m"
        )
        DailyRing(
            modifier = Modifier.weight(1f),
            title = "Azi",
            current = state.correctTotal,
            total = state.dailyTarget,
            color = StarGold,
            centerText = "${state.correctTotal}/${state.dailyTarget}"
        )
        DailyRing(
            modifier = Modifier.weight(1f),
            title = "Sigur",
            current = accuracy,
            total = 100,
            color = EmeraldGreen,
            centerText = "$accuracy%"
        )
    }
}

@Composable
private fun DailyRing(
    modifier: Modifier,
    title: String,
    current: Int,
    total: Int,
    color: Color,
    centerText: String
) {
    val progress = dailyRingProgress(current = current, total = total)
    Surface(
        modifier = modifier.height(78.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF082B38).copy(alpha = 0.72f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.28f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(42.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    strokeWidth = 5.dp
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(42.dp),
                    color = color,
                    strokeWidth = 5.dp,
                    strokeCap = StrokeCap.Round
                )
                Text(centerText, color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = TextSandy, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun ProblemStage(
    state: GameState,
    countedItems: Map<String, Int>,
    guidedItemId: String?,
    onItemTapped: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = Color(0xFF123343).copy(alpha = 0.94f),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (state.operation == MathOperation.Addition) {
                    "Câte comori sunt în total?"
                } else {
                    "Câte comori rămân?"
                },
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (state.operation == MathOperation.Addition) {
                    "Atinge comoara luminoasă ca să apară ordinea numărării."
                } else {
                    "Mută comorile în cufăr, apoi numără doar ce rămâne pe punte."
                },
                color = TextSandy.copy(alpha = 0.78f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            RoundStepCueStrip(cue = roundStepCueFor(state, countedItems.size))
            Spacer(modifier = Modifier.height(14.dp))
            if (state.operation == MathOperation.Subtraction) {
                SubtractionActionStage(
                    state = state,
                    countedItems = countedItems,
                    guidedItemId = guidedItemId,
                    onItemTapped = onItemTapped
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TreasureGroup(
                        count = state.num1,
                        item = state.item1,
                        groupId = "left",
                        countedItems = countedItems,
                        guidedItemId = guidedItemId,
                        onItemTapped = onItemTapped,
                        modifier = Modifier.weight(1f)
                    )
                    OperatorBadge(state.operation.symbol)
                    TreasureGroup(
                        count = state.num2,
                        item = state.item2,
                        groupId = "right",
                        countedItems = countedItems,
                        guidedItemId = guidedItemId,
                        onItemTapped = onItemTapped,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, StarGold.copy(alpha = 0.24f))
            ) {
                Text(
                    text = "${state.num1} ${state.operation.symbol} ${state.num2} = ?",
                    modifier = Modifier.padding(horizontal = 26.dp, vertical = 10.dp),
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
            AnimatedVisibility(visible = answerButtonsUnlocked(state, countedItems.size)) {
                ResultPreviewCard(state = state)
            }
        }
    }
}

@Composable
private fun SubtractionActionStage(
    state: GameState,
    countedItems: Map<String, Int>,
    guidedItemId: String?,
    onItemTapped: (String) -> Unit
) {
    val movedCount = subtractionMovedTouchesFor(state, countedItems.size)
    val remainingCounted = subtractionRemainingCountedFor(state, countedItems.size)
    val answer = correctAnswerFor(state)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TreasureGroup(
                count = state.num1,
                item = state.item1,
                groupId = "left",
                overrideTitle = "pe punte",
                overrideDescription = "${state.num1} la start",
                countedItems = countedItems,
                guidedItemId = guidedItemId,
                onItemTapped = onItemTapped,
                subtractionTakeAwayCount = state.num2,
                wideLayout = true,
                modifier = Modifier.weight(1.35f)
            )
            SubtractionChestProgress(
                state = state,
                movedCount = movedCount,
                remainingCounted = remainingCounted,
                answer = answer,
                modifier = Modifier.weight(0.75f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SubtractionStepPill(
                label = "În cufăr",
                value = "$movedCount/${state.num2}",
                color = StarGold,
                modifier = Modifier.weight(1f)
            )
            SubtractionStepPill(
                label = "Rămân",
                value = "$remainingCounted/$answer",
                color = EmeraldGreen,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SubtractionChestProgress(
    state: GameState,
    movedCount: Int,
    remainingCounted: Int,
    answer: Int,
    modifier: Modifier = Modifier
) {
    val readyToCountRemainder = movedCount >= state.num2
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = StarGold.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, StarGold.copy(alpha = 0.42f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OperatorBadge("-")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "cufăr",
                color = StarGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                "$movedCount/${state.num2} mutate",
                color = TextSandy,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            MiniResultObjects(count = movedCount, item = state.item1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (readyToCountRemainder) {
                    "$remainingCounted/$answer rămase"
                } else {
                    "mutăm întâi"
                },
                color = if (readyToCountRemainder) EmeraldGreen else StarGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun SubtractionStepPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.11f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.36f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextSandy, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun RoundStepCueStrip(cue: RoundStepCue) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = cue.color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, cue.color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(17.dp),
                color = cue.color.copy(alpha = 0.24f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.44f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = cue.badge,
                        color = Color.White,
                        fontSize = if (cue.badge.length <= 2) 24.sp else 19.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cue.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 18.sp
                )
                Text(
                    text = cue.detail,
                    color = TextSandy,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun ResultPreviewCard(state: GameState) {
    val answer = correctAnswerFor(state)
    val label = if (state.operation == MathOperation.Addition) "Total sigur" else "Rămân pe punte"
    val detail = if (state.operation == MathOperation.Addition) {
        "Ultimul număr citit devine răspunsul."
    } else {
        "${state.num1} de pe punte minus ${state.num2} în cufăr."
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = StarGold.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, StarGold.copy(alpha = 0.42f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.24f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = answer.toString(),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        color = StarGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = detail,
                        color = TextSandy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (state.operation == MathOperation.Subtraction) {
                        SubtractionResultObjects(state = state)
                    } else {
                        MiniResultObjects(count = answer, item = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtractionResultObjects(state: GameState) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ResultObjectLane(
            label = "rămân",
            count = remainingOnDeckCountFor(state),
            item = state.item1,
            color = EmeraldGreen,
            modifier = Modifier.weight(1f)
        )
        ResultObjectLane(
            label = "în cufăr",
            count = movedToChestCountFor(state),
            item = state.item1,
            color = StarGold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ResultObjectLane(
    label: String,
    count: Int,
    item: PirateItem,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.32f))
    ) {
        Column(modifier = Modifier.padding(7.dp)) {
            Text(
                text = "$count $label",
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            MiniResultObjects(count = count, item = item)
        }
    }
}

@Composable
private fun MiniResultObjects(count: Int, item: PirateItem?) {
    val displayCount = count.coerceAtMost(12)
    val rows = (displayCount + 5) / 6
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        for (row in 0 until rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                val start = row * 6
                val end = minOf(start + 6, displayCount)
                for (index in start until end) {
                    Surface(
                        modifier = Modifier.size(21.dp),
                        shape = RoundedCornerShape(7.dp),
                        color = Color.White.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
                    ) {
                        if (item?.drawableRes != null) {
                            Image(
                                painter = painterResource(id = item.drawableRes),
                                contentDescription = item.nameSingular,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.padding(2.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.item_gold_coin),
                                contentDescription = "Bănuț",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TreasureGroup(
    count: Int,
    item: PirateItem,
    groupId: String,
    overrideTitle: String? = null,
    overrideDescription: String? = null,
    countedItems: Map<String, Int>,
    guidedItemId: String?,
    onItemTapped: (String) -> Unit,
    subtractionTakeAwayCount: Int = 0,
    wideLayout: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, item.color.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = overrideTitle ?: if (count == 1) item.nameSingular else item.namePlural,
                color = item.color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = overrideDescription ?: item.description,
                color = TextSandy.copy(alpha = 0.74f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            InteractiveItemGrid(
                count = count,
                item = item,
                groupId = groupId,
                countedItems = countedItems,
                guidedItemId = guidedItemId,
                onItemTapped = onItemTapped,
                subtractionTakeAwayCount = subtractionTakeAwayCount,
                wideLayout = wideLayout
            )
        }
    }
}

@Composable
private fun OperatorBadge(label: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(StarGold)
            .border(2.dp, Color.White.copy(alpha = 0.72f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = OceanBg, fontSize = 28.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun InteractiveItemGrid(
    count: Int,
    item: PirateItem,
    groupId: String,
    countedItems: Map<String, Int>,
    guidedItemId: String?,
    onItemTapped: (String) -> Unit,
    subtractionTakeAwayCount: Int = 0,
    wideLayout: Boolean = false
) {
    val columns = when {
        wideLayout && count <= 4 -> 2
        wideLayout && count <= 9 -> 3
        wideLayout -> 4
        count <= 4 -> 2
        count <= 8 -> 3
        else -> 4
    }
    val itemSize = when {
        wideLayout && count <= 4 -> 72.dp
        wideLayout && count <= 8 -> 58.dp
        wideLayout -> 48.dp
        count <= 4 -> 62.dp
        count <= 8 -> 44.dp
        else -> 38.dp
    }
    val rows = (count + columns - 1) / columns

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        for (rowIndex in 0 until rows) {
            Row(horizontalArrangement = Arrangement.Center) {
                val start = rowIndex * columns
                val end = minOf(start + columns, count)
                for (index in start until end) {
                    val itemId = "${groupId}_$index"
                    val countedOrder = countedItems[itemId]
                    val nextOrder = if (itemId == guidedItemId) countedItems.size + 1 else null
                    val isTakenAway = subtractionTakeAwayCount > 0 &&
                        countedOrder != null &&
                        countedOrder <= subtractionTakeAwayCount
                    val isGuidedTakeAway = subtractionTakeAwayCount > 0 &&
                        nextOrder != null &&
                        nextOrder <= subtractionTakeAwayCount
                    val remainingCountedNumber = countedOrder
                        ?.takeIf { subtractionTakeAwayCount == 0 || it > subtractionTakeAwayCount }
                        ?.let { it - subtractionTakeAwayCount }
                    val remainingNextNumber = nextOrder
                        ?.takeIf { subtractionTakeAwayCount == 0 || it > subtractionTakeAwayCount }
                        ?.let { it - subtractionTakeAwayCount }
                    PirateItemView(
                        item = item,
                        accentColor = item.color,
                        countedNumber = remainingCountedNumber,
                        nextCountNumber = remainingNextNumber,
                        isTakenAway = isTakenAway,
                        isGuidedTakeAway = isGuidedTakeAway,
                        size = itemSize,
                        bubbleSize = if (itemSize > 48.dp) 22.dp else 18.dp,
                        onClick = { onItemTapped(itemId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PirateItemView(
    item: PirateItem,
    accentColor: Color,
    countedNumber: Int?,
    nextCountNumber: Int?,
    isTakenAway: Boolean,
    isGuidedTakeAway: Boolean,
    size: Dp,
    bubbleSize: Dp,
    onClick: () -> Unit
) {
    val isGuidedNext = nextCountNumber != null && countedNumber == null
    val isActiveGuide = isGuidedNext || isGuidedTakeAway
    val scale by animateFloatAsState(
        targetValue = when {
            countedNumber != null -> 1.12f
            isTakenAway -> 0.92f
            isActiveGuide -> 1.08f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 340f),
        label = "treasureScale"
    )
    val guideAlpha by animateFloatAsState(
        targetValue = if (isActiveGuide) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = 260f),
        label = "guideAlpha"
    )

    Box(
        modifier = Modifier
            .padding(3.dp)
            .scale(scale)
            .size(size)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (isActiveGuide) 0.34f else 0.24f),
                        when {
                            isGuidedTakeAway -> RubyRed.copy(alpha = 0.26f)
                            isGuidedNext -> StarGold.copy(alpha = 0.22f)
                            isTakenAway -> RubyRed.copy(alpha = 0.14f)
                            else -> accentColor.copy(alpha = 0.18f)
                        }
                    )
                )
            )
            .border(
                width = if (isActiveGuide) 3.dp else 2.dp,
                color = when {
                    countedNumber != null -> StarGold
                    isGuidedTakeAway -> RubyRed.copy(alpha = 0.92f)
                    isGuidedNext -> StarGold.copy(alpha = 0.92f)
                    isTakenAway -> RubyRed.copy(alpha = 0.58f)
                    else -> Color.White.copy(alpha = 0.14f)
                },
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isActiveGuide) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .alpha(guideAlpha)
                    .border(1.dp, Color.White.copy(alpha = 0.72f), RoundedCornerShape(15.dp))
            )
        }
        if (item.drawableRes != null) {
            Image(
                painter = painterResource(id = item.drawableRes),
                contentDescription = item.nameSingular,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isActiveGuide) 4.dp else 6.dp)
                    .alpha(if (isTakenAway) 0.42f else 1f)
            )
        } else {
            TreasureIllustration(
                shape = item.shape,
                accentColor = accentColor,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .alpha(if (isTakenAway) 0.42f else 1f)
            )
        }
        if (isGuidedNext || isGuidedTakeAway) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Box(
                    modifier = Modifier
                        .size(bubbleSize)
                        .clip(CircleShape)
                        .background(if (isGuidedTakeAway) RubyRed else CoralBlue)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isGuidedTakeAway) "-1" else nextCountNumber.toString(),
                        color = Color.White,
                        fontSize = (bubbleSize.value * 0.48f).sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
        if (isTakenAway) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(bubbleSize)
                        .clip(CircleShape)
                        .background(RubyRed)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-1",
                        color = Color.White,
                        fontSize = (bubbleSize.value * 0.42f).sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
        if (countedNumber != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(bubbleSize)
                        .clip(CircleShape)
                        .background(StarGold)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = countedNumber.toString(),
                        color = OceanBg,
                        fontSize = (bubbleSize.value * 0.52f).sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun TreasureIllustration(
    shape: TreasureShape,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val glow = Color.White.copy(alpha = 0.72f)
        val ink = OceanBg.copy(alpha = 0.86f)

        when (shape) {
            TreasureShape.Boat -> {
                drawRoundRect(
                    color = Color(0xFFB97838),
                    topLeft = Offset(w * 0.12f, h * 0.62f),
                    size = Size(w * 0.76f, h * 0.18f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                )
                val mastX = w * 0.48f
                drawLine(glow, Offset(mastX, h * 0.18f), Offset(mastX, h * 0.68f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
                drawPath(
                    path = Path().apply {
                        moveTo(mastX, h * 0.2f)
                        lineTo(w * 0.78f, h * 0.54f)
                        lineTo(mastX, h * 0.54f)
                        close()
                    },
                    color = Color(0xFFFFF3D0)
                )
                drawPath(
                    path = Path().apply {
                        moveTo(mastX, h * 0.28f)
                        lineTo(w * 0.2f, h * 0.56f)
                        lineTo(mastX, h * 0.56f)
                        close()
                    },
                    color = accentColor.copy(alpha = 0.9f)
                )
            }

            TreasureShape.Coin -> {
                drawCircle(Color(0xFFFFF176), radius = w * 0.34f, center = Offset(w * 0.5f, h * 0.5f))
                drawCircle(Color(0xFFFFB300), radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.07f))
                drawLine(ink, Offset(w * 0.37f, h * 0.5f), Offset(w * 0.63f, h * 0.5f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
            }

            TreasureShape.Gem -> {
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.5f, h * 0.12f)
                        lineTo(w * 0.84f, h * 0.38f)
                        lineTo(w * 0.68f, h * 0.84f)
                        lineTo(w * 0.32f, h * 0.84f)
                        lineTo(w * 0.16f, h * 0.38f)
                        close()
                    },
                    color = accentColor
                )
                drawLine(glow, Offset(w * 0.28f, h * 0.4f), Offset(w * 0.72f, h * 0.4f), strokeWidth = w * 0.04f, cap = StrokeCap.Round)
                drawLine(glow.copy(alpha = 0.45f), Offset(w * 0.5f, h * 0.14f), Offset(w * 0.5f, h * 0.82f), strokeWidth = w * 0.035f, cap = StrokeCap.Round)
            }

            TreasureShape.Compass -> {
                drawCircle(Color(0xFFFFE0A3), radius = w * 0.36f, center = Offset(w * 0.5f, h * 0.5f))
                drawCircle(accentColor, radius = w * 0.32f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.05f))
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.5f, h * 0.18f)
                        lineTo(w * 0.62f, h * 0.58f)
                        lineTo(w * 0.5f, h * 0.52f)
                        lineTo(w * 0.38f, h * 0.58f)
                        close()
                    },
                    color = RubyRed
                )
                drawCircle(ink, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.5f))
            }

            TreasureShape.Shell -> {
                drawOval(
                    color = Color(0xFFFFF3E0),
                    topLeft = Offset(w * 0.18f, h * 0.24f),
                    size = Size(w * 0.64f, h * 0.52f)
                )
                drawOval(
                    color = accentColor.copy(alpha = 0.38f),
                    topLeft = Offset(w * 0.22f, h * 0.3f),
                    size = Size(w * 0.56f, h * 0.42f)
                )
                repeat(5) { index ->
                    val x = w * (0.28f + index * 0.11f)
                    drawLine(accentColor, Offset(w * 0.5f, h * 0.28f), Offset(x, h * 0.74f), strokeWidth = w * 0.025f, cap = StrokeCap.Round)
                }
            }

            TreasureShape.Map -> {
                drawRoundRect(
                    color = Color(0xFFFFE6A9),
                    topLeft = Offset(w * 0.18f, h * 0.22f),
                    size = Size(w * 0.64f, h * 0.56f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                )
                drawLine(accentColor, Offset(w * 0.34f, h * 0.24f), Offset(w * 0.34f, h * 0.76f), strokeWidth = w * 0.03f)
                drawLine(accentColor, Offset(w * 0.58f, h * 0.24f), Offset(w * 0.58f, h * 0.76f), strokeWidth = w * 0.03f)
                drawCircle(RubyRed, radius = w * 0.05f, center = Offset(w * 0.63f, h * 0.58f))
            }

            TreasureShape.Anchor -> {
                drawCircle(accentColor, radius = w * 0.1f, center = Offset(w * 0.5f, h * 0.22f), style = Stroke(width = w * 0.045f))
                drawLine(accentColor, Offset(w * 0.5f, h * 0.32f), Offset(w * 0.5f, h * 0.76f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
                drawLine(accentColor, Offset(w * 0.28f, h * 0.48f), Offset(w * 0.72f, h * 0.48f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
                drawLine(accentColor, Offset(w * 0.24f, h * 0.72f), Offset(w * 0.5f, h * 0.82f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
                drawLine(accentColor, Offset(w * 0.76f, h * 0.72f), Offset(w * 0.5f, h * 0.82f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
            }

            TreasureShape.Spyglass -> {
                drawLine(Color(0xFF8D5524), Offset(w * 0.24f, h * 0.66f), Offset(w * 0.74f, h * 0.34f), strokeWidth = w * 0.18f, cap = StrokeCap.Round)
                drawLine(accentColor, Offset(w * 0.22f, h * 0.67f), Offset(w * 0.76f, h * 0.33f), strokeWidth = w * 0.1f, cap = StrokeCap.Round)
                drawCircle(glow, radius = w * 0.12f, center = Offset(w * 0.78f, h * 0.3f), style = Stroke(width = w * 0.05f))
            }

            TreasureShape.Island -> {
                drawOval(
                    color = Color(0xFFFFD180),
                    topLeft = Offset(w * 0.18f, h * 0.58f),
                    size = Size(w * 0.64f, h * 0.22f)
                )
                drawLine(Color(0xFF8D5524), Offset(w * 0.5f, h * 0.6f), Offset(w * 0.44f, h * 0.34f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
                drawCircle(Color(0xFF66BB6A), radius = w * 0.13f, center = Offset(w * 0.34f, h * 0.34f))
                drawCircle(Color(0xFF7CB342), radius = w * 0.13f, center = Offset(w * 0.54f, h * 0.3f))
                drawCircle(Color(0xFF81C784), radius = w * 0.12f, center = Offset(w * 0.62f, h * 0.42f))
            }

            TreasureShape.Key -> {
                drawCircle(accentColor, radius = w * 0.18f, center = Offset(w * 0.34f, h * 0.46f), style = Stroke(width = w * 0.07f))
                drawLine(accentColor, Offset(w * 0.5f, h * 0.46f), Offset(w * 0.82f, h * 0.46f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
                drawLine(accentColor, Offset(w * 0.68f, h * 0.46f), Offset(w * 0.68f, h * 0.62f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
                drawLine(accentColor, Offset(w * 0.78f, h * 0.46f), Offset(w * 0.78f, h * 0.58f), strokeWidth = w * 0.05f, cap = StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun CoachPanel(
    state: GameState,
    countedCount: Int,
    onSpeak: () -> Unit
) {
    val totalItems = visibleObjectCountFor(state)
    val remainingTouches = remainingTouchesFor(state, countedCount)
    val nextCountNumber = if (remainingTouches > 0) countedCount + 1 else null
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = when {
            state.isCorrecting -> EmeraldGreen.copy(alpha = 0.16f)
            state.selectedWrongAnswer != null -> RubyRed.copy(alpha = 0.16f)
            else -> Color.White.copy(alpha = 0.1f)
        },
        border = BorderStroke(
            1.dp,
            when {
                state.isCorrecting -> EmeraldGreen.copy(alpha = 0.55f)
                state.selectedWrongAnswer != null -> RubyRed.copy(alpha = 0.48f)
                else -> Color.White.copy(alpha = 0.14f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Căpitanul Coach", color = StarGold, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    AnimatedVisibility(visible = state.speedBumpActive) {
                        StatusPill("Speed bump", StarGold)
                    }
                    AnimatedVisibility(visible = state.recoveryMissionActive) {
                        StatusPill("Port sigur", EmeraldGreen)
                    }
                    AnimatedVisibility(visible = state.struggleSupportActive && !state.recoveryMissionActive) {
                        StatusPill("Suport", CoralBlue)
                    }
                }
                ListenButton(onClick = onSpeak)
            }
            Spacer(modifier = Modifier.height(8.dp))
            CountingTrail(
                countedCount = countedCount,
                totalItems = totalItems,
                subtractionTakeAwayCount = if (state.operation == MathOperation.Subtraction) state.num2 else 0
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = when {
                    state.isCorrecting -> "Bravo! Următoarea comoară vine imediat."
                    state.selectedWrongAnswer != null && countedCount < totalItems -> {
                        if (state.operation == MathOperation.Addition) {
                            "Reparăm: numără din nou, de la 1 până la $totalItems."
                        } else {
                            val movedCount = subtractionMovedTouchesFor(state, countedCount)
                            if (movedCount < state.num2) {
                                "Reparăm: mută întâi ${state.num2} comori în cufăr. Atinge comoara cu -1."
                            } else {
                                "Reparăm: acum numără doar comorile rămase pe punte."
                            }
                        }
                    }
                    state.selectedWrongAnswer != null -> {
                        "Gata, ai refăcut numărarea. Alege răspunsul corect."
                    }
                    state.recoveryMissionActive && countedCount < totalItems -> {
                        "Port sigur: doar comori mici. Atinge comoara luminoasă și ancorăm baza fără grabă."
                    }
                    countedCount < totalItems -> {
                        if (state.operation == MathOperation.Subtraction) {
                            val movedCount = subtractionMovedTouchesFor(state, countedCount)
                            val remainingCounted = subtractionRemainingCountedFor(state, countedCount)
                            val answer = correctAnswerFor(state)
                            if (movedCount < state.num2) {
                                "Scădem: $movedCount din ${state.num2} sunt în cufăr. Atinge comoara luminoasă cu -1."
                            } else {
                                "Acum rămân pe punte: $remainingCounted din $answer. Atinge comoara luminoasă ca să numeri ce a rămas."
                            }
                        } else {
                            val noun = if (remainingTouches == 1) "comoară" else "comori"
                            "Numărate: $countedCount din $totalItems. Comoara luminoasă este numărul $nextCountNumber. Atinge-o ca să continui. Mai ai $remainingTouches $noun."
                        }
                    }
                    else -> if (state.operation == MathOperation.Addition) {
                        "Toate sunt numărate. Acum răspunsul e ultimul număr pe care îl vezi."
                    } else {
                        "Toate sunt numărate. Acum scade comorile din cufăr și alege câte rămân."
                    }
                },
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 19.sp
            )
            AnimatedVisibility(visible = state.recoveryMissionActive && state.selectedWrongAnswer == null) {
                RecoveryMissionCard(state = state)
            }
            AnimatedVisibility(visible = state.isCorrecting) {
                CorrectRewardBurst(state = state)
            }
            AnimatedVisibility(visible = state.selectedWrongAnswer != null) {
                MasteryRepairCard(state = state)
            }
        }
    }
}

@Composable
private fun RecoveryMissionCard(state: GameState) {
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = EmeraldGreen.copy(alpha = 0.14f),
            border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.48f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.42f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.item_anchor),
                        contentDescription = "Ancoră de port sigur",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(6.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Misiune de recuperare",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Doar ${visibleObjectCountFor(state)} comori. Ținta este să numărăm sigur, nu repede.",
                        color = TextSandy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CorrectRewardBurst(state: GameState) {
    val scale by animateFloatAsState(
        targetValue = if (state.isCorrecting) 1.04f else 0.96f,
        animationSpec = spring(dampingRatio = 0.48f, stiffness = 260f),
        label = "correctRewardScale"
    )
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            shape = RoundedCornerShape(20.dp),
            color = StarGold.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, StarGold.copy(alpha = 0.62f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.item_gold_coin),
                        contentDescription = "Bănuț de recompensă",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(6.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Comoară +1",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Streak ${state.streak}. Colecția are ${state.lifetimeCoins} comori.",
                        color = TextSandy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 14.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.item_treasure_chest),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "Mastery",
                        color = StarGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun MasteryRepairCard(state: GameState) {
    val answer = correctAnswerFor(state)
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color.Black.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, CoralBlue.copy(alpha = 0.38f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Plan de reparare",
                    color = CoralBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RepairStepChip(
                        count = state.num1,
                        item = state.item1,
                        title = if (state.operation == MathOperation.Subtraction) "pe punte" else null,
                        modifier = Modifier.weight(1f)
                    )
                    Text(state.operation.symbol, color = StarGold, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    RepairStepChip(
                        count = state.num2,
                        item = state.item2,
                        title = if (state.operation == MathOperation.Subtraction) "în cufăr" else null,
                        modifier = Modifier.weight(1f)
                    )
                    Text("=", color = StarGold, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Surface(
                        modifier = Modifier.height(58.dp).width(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = EmeraldGreen.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.55f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = answer.toString(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (state.operation == MathOperation.Addition) {
                        "Atingem din nou pe rând. Ultimul număr citit este răspunsul sigur."
                    } else {
                        "Scădem ce intră în cufăr. Ce rămâne pe punte este răspunsul sigur."
                    },
                    color = TextSandy,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun RepairStepChip(
    count: Int,
    item: PirateItem,
    title: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, item.color.copy(alpha = 0.52f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                if (item.drawableRes != null) {
                    Image(
                        painter = painterResource(id = item.drawableRes),
                        contentDescription = item.nameSingular,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(3.dp)
                    )
                } else {
                    TreasureIllustration(
                        shape = item.shape,
                        accentColor = item.color,
                        modifier = Modifier.fillMaxSize().padding(5.dp)
                    )
                }
            }
            Column {
                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = title ?: if (count == 1) item.nameSingular else item.namePlural,
                    color = TextSandy,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CountingTrail(
    countedCount: Int,
    totalItems: Int,
    subtractionTakeAwayCount: Int = 0
) {
    val dotSize = if (totalItems <= 6) 30.dp else 24.dp
    val numberSize = if (totalItems <= 6) 13.sp else 10.sp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 1..totalItems) {
            val isCounted = index <= countedCount
            val isTakeAway = subtractionTakeAwayCount > 0 && index <= subtractionTakeAwayCount
            val fillColor = when {
                isCounted && isTakeAway -> RubyRed
                isCounted -> StarGold
                else -> Color.White.copy(alpha = 0.12f)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dotSize)
                    .clip(RoundedCornerShape(50))
                    .background(fillColor)
                    .border(
                        1.dp,
                        if (isCounted) Color.White.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.16f),
                        RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isTakeAway) "-1" else (index - subtractionTakeAwayCount).toString(),
                    color = when {
                        isCounted && isTakeAway -> Color.White
                        isCounted -> OceanBg
                        else -> Color.White.copy(alpha = 0.55f)
                    },
                    fontSize = numberSize,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun StatusPill(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.55f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun ListenButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(34.dp)
            .width(86.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = CoralBlue.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, CoralBlue.copy(alpha = 0.52f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Canvas(modifier = Modifier.size(16.dp)) {
                val body = Path().apply {
                    moveTo(size.width * 0.08f, size.height * 0.38f)
                    lineTo(size.width * 0.34f, size.height * 0.38f)
                    lineTo(size.width * 0.62f, size.height * 0.18f)
                    lineTo(size.width * 0.62f, size.height * 0.82f)
                    lineTo(size.width * 0.34f, size.height * 0.62f)
                    lineTo(size.width * 0.08f, size.height * 0.62f)
                    close()
                }
                drawPath(body, color = Color.White)
                drawArc(
                    color = Color.White.copy(alpha = 0.82f),
                    startAngle = -36f,
                    sweepAngle = 72f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.48f, size.height * 0.28f),
                    size = Size(size.width * 0.34f, size.height * 0.44f),
                    style = Stroke(width = size.width * 0.08f, cap = StrokeCap.Round)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Ascultă",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun AnswerGrid(
    options: List<Int>,
    wrongAnswer: Int?,
    isCorrecting: Boolean,
    correctAnswer: Int,
    isEnabled: Boolean,
    onAnswer: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AnswerButton(
                number = options[0],
                wrongAnswer = wrongAnswer,
                isCorrecting = isCorrecting,
                correctAnswer = correctAnswer,
                isEnabled = isEnabled,
                onAnswer = onAnswer,
                modifier = Modifier.weight(1f)
            )
            AnswerButton(
                number = options[1],
                wrongAnswer = wrongAnswer,
                isCorrecting = isCorrecting,
                correctAnswer = correctAnswer,
                isEnabled = isEnabled,
                onAnswer = onAnswer,
                modifier = Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AnswerButton(
                number = options[2],
                wrongAnswer = wrongAnswer,
                isCorrecting = isCorrecting,
                correctAnswer = correctAnswer,
                isEnabled = isEnabled,
                onAnswer = onAnswer,
                modifier = Modifier.weight(1f)
            )
            AnswerButton(
                number = options[3],
                wrongAnswer = wrongAnswer,
                isCorrecting = isCorrecting,
                correctAnswer = correctAnswer,
                isEnabled = isEnabled,
                onAnswer = onAnswer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AnswerButton(
    number: Int,
    wrongAnswer: Int?,
    isCorrecting: Boolean,
    correctAnswer: Int,
    isEnabled: Boolean,
    onAnswer: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isWrong = wrongAnswer == number
    val isCorrect = isCorrecting && number == correctAnswer
    val bgColor by animateColorAsState(
        targetValue = when {
            isCorrect -> EmeraldGreen
            isWrong -> RubyRed
            !isEnabled -> Color(0xFF253846)
            else -> CardBg
        },
        label = "answerColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (isCorrect) 1.04f else if (isWrong) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = 380f),
        label = "answerScale"
    )

    Surface(
        modifier = modifier
            .height(82.dp)
            .scale(scale)
            .clickable(enabled = isEnabled && !isCorrecting) { onAnswer(number) },
        shape = RoundedCornerShape(24.dp),
        color = bgColor,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(2.dp, if (isCorrect) Color.White else Color.White.copy(alpha = if (isEnabled) 0.1f else 0.05f))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = number.toString(),
                color = Color.White.copy(alpha = if (isEnabled) 1f else 0.42f),
                fontSize = 38.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
internal fun ParentInsightStrip(
    state: GameState,
    onDailyTargetSelected: (Int) -> Unit,
    onSessionMinutesSelected: (Int) -> Unit,
    onMaxDifficultySelected: (Int) -> Unit
) {
    val accuracy = if (state.attemptsTotal == 0) 100 else (state.correctTotal * 100 / state.attemptsTotal)
    val efficiencyScore = learningEfficiencyScore(
        correct = state.correctTotal,
        attempts = state.attemptsTotal,
        repairRounds = state.repairRounds,
        consecutiveWrong = state.consecutiveWrong
    )
    val efficiencyLabel = learningEfficiencyLabel(score = efficiencyScore, attempts = state.attemptsTotal)
    val hasEnoughSignal = state.attemptsTotal >= 3
    val fitColor = when {
        !hasEnoughSignal -> TextSandy
        accuracy >= 95 && state.difficultyLevel <= 2 -> StarGold
        accuracy < 70 -> RubyRed
        efficiencyScore < 70 -> StarGold
        else -> EmeraldGreen
    }
    val efficiencyColor = when {
        !hasEnoughSignal -> TextSandy
        efficiencyScore >= 85 -> EmeraldGreen
        efficiencyScore >= 70 -> StarGold
        else -> RubyRed
    }
    val fitLabel = when {
        !hasEnoughSignal -> "Se calibrează"
        accuracy >= 95 && state.difficultyLevel <= 2 -> "Prea ușor curând"
        accuracy < 70 -> "Prea greu"
        efficiencyScore < 70 -> "Ghicit detectat"
        else -> "Potrivit"
    }
    val parentRecommendation = when {
        !hasEnoughSignal -> "Mai strângem câteva răspunsuri înainte de concluzie."
        accuracy >= 95 && state.difficultyLevel <= 2 -> "Dacă rămâne peste 95%, aplicația ridică treptat nivelul."
        accuracy < 70 -> "Sub 70% intră suportul: întrebări mai mici și numărare ghidată."
        efficiencyScore < 70 -> "Eficiența a scăzut: păstrăm obiecte ghidate, răspunsuri blocate și reparații scurte."
        else -> "Ritmul e bun: păstrăm mastery și creștem doar după streak-uri."
    }
    val nextParentStep = parentNextStepFor(
        additionCorrect = state.additionCorrect,
        additionAttempts = state.additionAttempts,
        subtractionCorrect = state.subtractionCorrect,
        subtractionAttempts = state.subtractionAttempts,
        efficiencyScore = efficiencyScore,
        difficultyLevel = state.difficultyLevel
    )
    var showSessionSettings by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Parent Dash",
                    color = TextSandy,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                ParentSettingsToggle(
                    isOpen = showSessionSettings,
                    onClick = { showSessionSettings = !showSessionSettings }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ParentMetric("Minute", "${state.sessionSecondsElapsed / 60}/${state.sessionSecondsTotal / 60}")
                ParentMetric("Acuratețe", "$accuracy%")
                ParentMetric("Eficiență", "$efficiencyScore%")
                ParentMetric("Reparări", state.repairRounds.toString())
            }
            AnimatedVisibility(visible = state.lastSessionMinutes > 0) {
                LastSessionSummary(state = state)
            }
            AnimatedVisibility(visible = state.sessionHistory.isNotEmpty()) {
                SessionJournal(history = state.sessionHistory)
            }
            Spacer(modifier = Modifier.height(10.dp))
            ParentAuditPanel(state = state)
            AnimatedVisibility(visible = showSessionSettings) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    ParentLearningControls(
                        state = state,
                        onDailyTargetSelected = onDailyTargetSelected,
                        onSessionMinutesSelected = onSessionMinutesSelected,
                        onMaxDifficultySelected = onMaxDifficultySelected
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = fitColor.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, fitColor.copy(alpha = 0.42f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Learning Plan",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            fitLabel,
                            color = fitColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        parentRecommendation,
                        color = TextSandy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NextParentStepCard(text = nextParentStep, color = fitColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    SkillInsightRow(
                        title = "Adunare",
                        correct = state.additionCorrect,
                        attempts = state.additionAttempts,
                        active = state.operation == MathOperation.Addition
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    SkillInsightRow(
                        title = "Scădere",
                        correct = state.subtractionCorrect,
                        attempts = state.subtractionAttempts,
                        active = state.operation == MathOperation.Subtraction
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    EfficiencyInsightRow(
                        score = efficiencyScore,
                        label = efficiencyLabel,
                        color = efficiencyColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        InsightPill("Acuratețe ${accuracy}%", fitColor)
                        InsightPill("Eficiență $efficiencyScore%", efficiencyColor)
                        InsightPill("Ghicit blocat", CoralBlue)
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentAuditPanel(state: GameState) {
    val signals = parentAuditSignalsFor(state)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, CoralBlue.copy(alpha = 0.28f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Audit părinte",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "2HL",
                    color = CoralBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            signals.forEachIndexed { index, signal ->
                ParentAuditSignalRow(signal = signal)
                if (index != signals.lastIndex) {
                    Spacer(modifier = Modifier.height(7.dp))
                }
            }
        }
    }
}

@Composable
private fun ParentAuditSignalRow(signal: ParentAuditSignal) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = signal.title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = signal.valueText,
                color = signal.color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { signal.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = signal.color,
            trackColor = Color.White.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
        Text(
            text = signal.detail,
            color = TextSandy.copy(alpha = 0.76f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 12.sp
        )
    }
}

@Composable
private fun EfficiencyInsightRow(
    score: Int,
    label: String,
    color: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.11f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.34f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Guess Guard",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "$label · $score%",
                    color = color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = color,
                trackColor = Color.White.copy(alpha = 0.12f),
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "Scade când apar reparații sau răspunsuri greșite consecutive.",
                color = TextSandy.copy(alpha = 0.76f),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun SkillInsightRow(
    title: String,
    correct: Int,
    attempts: Int,
    active: Boolean
) {
    val accuracy = skillAccuracy(correct, attempts)
    val hasEnoughSignal = attempts >= 3
    val color = when {
        !hasEnoughSignal -> TextSandy
        accuracy >= 85 -> EmeraldGreen
        accuracy < 70 -> RubyRed
        else -> StarGold
    }
    val label = when {
        !hasEnoughSignal -> "calibrare"
        accuracy >= 85 -> "solid"
        accuracy < 70 -> "de lucrat"
        else -> "în progres"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(62.dp)
        )
        LinearProgressIndicator(
            progress = { if (hasEnoughSignal) accuracy / 100f else 0.18f },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = color,
            trackColor = Color.White.copy(alpha = 0.12f),
            strokeCap = StrokeCap.Round
        )
        Text(
            text = if (hasEnoughSignal) "$accuracy%" else "${attempts}/3",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.width(38.dp)
        )
        Text(
            text = if (active) "acum" else label,
            color = if (active) StarGold else TextSandy,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.width(54.dp)
        )
    }
}

@Composable
private fun NextParentStepCard(text: String, color: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.34f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                "Următorul pas",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text,
                color = TextSandy,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun LastSessionSummary(state: GameState) {
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = StarGold.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, StarGold.copy(alpha = 0.32f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Ultima sesiune",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "${state.lastSessionMinutes} min · ${state.lastSessionAccuracy}% · ${state.lastSessionRepairs} reparări",
                    color = TextSandy,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun SessionJournal(history: List<SessionRecord>) {
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CoralBlue.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, CoralBlue.copy(alpha = 0.28f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Jurnal de căpitan",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        sessionJournalTrendLabel(history),
                        color = CoralBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.End
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                history.take(3).forEachIndexed { index, record ->
                    SessionJournalRow(index = index, record = record)
                    if (index < history.take(3).lastIndex) {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionJournalRow(index: Int, record: SessionRecord) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = if (index == 0) StarGold.copy(alpha = 0.24f) else Color.White.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, if (index == 0) StarGold.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.16f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    (index + 1).toString(),
                    color = if (index == 0) StarGold else TextSandy,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Text(
            "${record.minutes}m",
            color = TextSandy,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(32.dp)
        )
        LinearProgressIndicator(
            progress = { record.accuracy / 100f },
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(RoundedCornerShape(50)),
            color = if (record.accuracy >= 85) EmeraldGreen else if (record.accuracy < 70) RubyRed else StarGold,
            trackColor = Color.White.copy(alpha = 0.12f),
            strokeCap = StrokeCap.Round
        )
        Text(
            "${record.accuracy}%",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.width(34.dp)
        )
        Text(
            "L${record.difficulty}",
            color = TextSandy,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.width(24.dp)
        )
    }
}

@Composable
private fun ParentSettingsToggle(
    isOpen: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(32.dp)
            .width(78.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = if (isOpen) StarGold.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, if (isOpen) StarGold.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.16f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = if (isOpen) "Gata" else "Setări",
                color = if (isOpen) StarGold else TextSandy,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun ParentLearningControls(
    state: GameState,
    onDailyTargetSelected: (Int) -> Unit,
    onSessionMinutesSelected: (Int) -> Unit,
    onMaxDifficultySelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.07f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                "Setări sesiune",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingSegmentRow(
                label = "Țintă",
                options = listOf(8, 12, 16),
                selected = state.dailyTarget,
                valueLabel = { "$it" },
                onSelected = onDailyTargetSelected
            )
            Spacer(modifier = Modifier.height(6.dp))
            SettingSegmentRow(
                label = "Minute",
                options = listOf(10, 15, 25),
                selected = state.sessionSecondsTotal / 60,
                valueLabel = { "$it" },
                onSelected = onSessionMinutesSelected
            )
            Spacer(modifier = Modifier.height(6.dp))
            SettingSegmentRow(
                label = "Challenge",
                options = listOf(2, 3, 5),
                selected = state.maxDifficulty,
                valueLabel = {
                    when (it) {
                        2 -> "ușor"
                        3 -> "minus"
                        else -> "full"
                    }
                },
                onSelected = onMaxDifficultySelected
            )
        }
    }
}

@Composable
private fun SettingSegmentRow(
    label: String,
    options: List<Int>,
    selected: Int,
    valueLabel: (Int) -> String,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            label,
            color = TextSandy,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(54.dp)
        )
        options.forEach { option ->
            SettingChip(
                label = valueLabel(option),
                isSelected = selected == option,
                onClick = { onSelected(option) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SettingChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(30.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = if (isSelected) StarGold.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, if (isSelected) StarGold.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.12f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                color = if (isSelected) StarGold else Color.White.copy(alpha = 0.72f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun InsightPill(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.38f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun ParentMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
        Text(label, color = Color.White.copy(alpha = 0.62f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CelebrationScreen(state: GameState, onPlayAgain: () -> Unit) {
    val accuracy = if (state.attemptsTotal == 0) 100 else (state.correctTotal * 100 / state.attemptsTotal)
    val minutes = maxOf(1, state.sessionSecondsElapsed / 60)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_celebration_1779722834900),
            contentDescription = "Comoară de celebrare",
            modifier = Modifier.size(230.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Comoara Mastery!",
            fontSize = 34.sp,
            fontWeight = FontWeight.Black,
            color = StarGold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Oséa, ai terminat ținta de azi.",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "O sesiune scurtă, clară și cu adevărat stăpânită.",
            fontSize = 16.sp,
            color = TextSandy,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF123343).copy(alpha = 0.9f),
            border = BorderStroke(1.dp, StarGold.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CelebrationMetric(
                        title = "Minute",
                        value = "$minutes",
                        detail = "din 25",
                        color = CoralBlue,
                        modifier = Modifier.weight(1f)
                    )
                    CelebrationMetric(
                        title = "Acuratețe",
                        value = "$accuracy%",
                        detail = "mastery",
                        color = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CelebrationMetric(
                        title = "Reparări",
                        value = state.repairRounds.toString(),
                        detail = "cu coach",
                        color = if (state.repairRounds == 0) EmeraldGreen else StarGold,
                        modifier = Modifier.weight(1f)
                    )
                    CelebrationMetric(
                        title = "Colecție",
                        value = state.lifetimeCoins.toString(),
                        detail = "comori",
                        color = StarGold,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Streak zilnic: ${state.dailyStreak} zile",
                            color = StarGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when {
                                accuracy >= 95 -> "Ritmul a fost foarte sigur. Următoarea sesiune poate urca ușor nivelul."
                                accuracy < 70 -> "Azi am reparat calm. Următoarea sesiune începe mai ușor, cu numărare ghidată."
                                else -> "Sesiunea a fost potrivită: suficient de grea ca să învețe, suficient de clară ca să rămână motivat."
                            },
                            color = TextSandy,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        Button(
            onClick = onPlayAgain,
            colors = ButtonDefaults.buttonColors(containerColor = StarGold),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(0.84f)
        ) {
            Text(
                text = "Joacă încă o sesiune",
                fontSize = 20.sp,
                color = OceanBg,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
internal fun SessionBreakScreen(
    state: GameState,
    onStartFreshSession: () -> Unit
) {
    val accuracy = if (state.attemptsTotal == 0) 100 else (state.correctTotal * 100 / state.attemptsTotal)
    val minutes = maxOf(1, state.sessionSecondsTotal / 60)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(136.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.16f),
            border = BorderStroke(2.dp, CoralBlue.copy(alpha = 0.62f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.item_ship),
                    contentDescription = "Corabie de pauză",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Pauză de punte",
            color = StarGold,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Timpul sesiunii s-a terminat. Creierul de căpitan are nevoie de odihnă.",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF123343).copy(alpha = 0.9f),
            border = BorderStroke(1.dp, CoralBlue.copy(alpha = 0.42f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CelebrationMetric(
                        title = "Minute",
                        value = "$minutes",
                        detail = "focus",
                        color = CoralBlue,
                        modifier = Modifier.weight(1f)
                    )
                    CelebrationMetric(
                        title = "Comori",
                        value = "${state.correctTotal}/${state.dailyTarget}",
                        detail = "azi",
                        color = StarGold,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CelebrationMetric(
                        title = "Acuratețe",
                        value = "$accuracy%",
                        detail = "sigur",
                        color = if (accuracy >= 70) EmeraldGreen else RubyRed,
                        modifier = Modifier.weight(1f)
                    )
                    CelebrationMetric(
                        title = "Reparări",
                        value = state.repairRounds.toString(),
                        detail = "calm",
                        color = if (state.repairRounds == 0) EmeraldGreen else StarGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        Button(
            onClick = onStartFreshSession,
            colors = ButtonDefaults.buttonColors(containerColor = CoralBlue),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(0.84f)
        ) {
            Text(
                text = "Începe sesiune nouă",
                fontSize = 19.sp,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun CelebrationMetric(
    title: String,
    value: String,
    detail: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(94.dp),
        shape = RoundedCornerShape(18.dp),
        color = color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = TextSandy, fontSize = 10.sp, fontWeight = FontWeight.Black)
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(detail, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}
