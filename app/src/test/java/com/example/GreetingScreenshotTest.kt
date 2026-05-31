package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent { MyApplicationTheme { MathGameScreen() } }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun session_break_screenshot() {
    val state = GameState(
      correctTotal = 5,
      attemptsTotal = 6,
      repairRounds = 1,
      sessionSecondsElapsed = 10 * 60,
      sessionSecondsTotal = 10 * 60,
      dailyTarget = 12
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        SessionBreakScreen(state = state, onStartFreshSession = {})
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/session_break.png")
  }

  @Test
  fun subtraction_stage_screenshot() {
    val state = GameState(
      num1 = 5,
      num2 = 2,
      operation = MathOperation.Subtraction,
      difficultyLevel = 3
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        val countedItems = remember {
          mutableStateMapOf(
            "left_0" to 1,
            "left_1" to 2,
            "left_2" to 3
          )
        }
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF062C43))
            .padding(16.dp)
        ) {
          ProblemStage(
            state = state,
            countedItems = countedItems,
            guidedItemId = nextGuidedItemId(state, countedItems.keys),
            onItemTapped = {}
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/subtraction_stage.png")
  }

  @Test
  fun recovery_mission_stage_screenshot() {
    val state = GameState(
      num1 = 2,
      num2 = 1,
      operation = MathOperation.Addition,
      difficultyLevel = 2,
      recoveryMissionActive = true,
      struggleSupportActive = true,
      missionTitle = "Port sigur: revenim la comori mici până la 4."
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        val countedItems = remember { mutableStateMapOf<String, Int>() }
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF062C43))
            .padding(16.dp)
        ) {
          ProblemStage(
            state = state,
            countedItems = countedItems,
            guidedItemId = nextGuidedItemId(state, countedItems.keys),
            onItemTapped = {}
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/recovery_mission_stage.png")
  }

  @Test
  fun parent_dash_journal_screenshot() {
    val state = GameState(
      correctTotal = 9,
      attemptsTotal = 11,
      repairRounds = 1,
      difficultyLevel = 3,
      sessionSecondsElapsed = 11 * 60,
      sessionSecondsTotal = 15 * 60,
      lastSessionMinutes = 12,
      lastSessionAccuracy = 86,
      lastSessionRepairs = 1,
      additionCorrect = 7,
      additionAttempts = 8,
      subtractionCorrect = 2,
      subtractionAttempts = 3,
      sessionHistory = listOf(
        SessionRecord(dayIndex = 102, minutes = 12, accuracy = 86, repairs = 1, coins = 12, difficulty = 3),
        SessionRecord(dayIndex = 101, minutes = 10, accuracy = 76, repairs = 2, coins = 8, difficulty = 2),
        SessionRecord(dayIndex = 100, minutes = 9, accuracy = 72, repairs = 2, coins = 8, difficulty = 2)
      )
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF062C43))
            .padding(16.dp)
        ) {
          ParentInsightStrip(
            state = state,
            onDailyTargetSelected = {},
            onSessionMinutesSelected = {},
            onMaxDifficultySelected = {}
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/parent_dash_journal.png")
  }
}
