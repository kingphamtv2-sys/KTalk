package vn.noi.english

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Exercises the actual activity/window; the Compose host has its own IME configuration. */
@RunWith(AndroidJUnit4::class)
class MainActivityInsetsTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun actualActivityReceivesKeyboardInsetsWithoutPanningItsWholeWindow() {
        compose.waitUntil(5_000) { compose.onAllNodesWithText("Chào hỏi").fetchSemanticsNodes().isNotEmpty() }
        compose.onNode(hasText("Chào hỏi") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("Chào buổi sáng").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        // The real activity persists the position through DataStore before navigation.
        compose.waitUntil(5_000) { compose.onAllNodesWithText("Gõ", substring = false).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Gõ", substring = false).performScrollTo().performClick()
        val deliveredIme = AtomicInteger()
        compose.runOnIdle {
            val decor = compose.activity.window.decorView
            ViewCompat.setOnApplyWindowInsetsListener(decor) { _, insets ->
                deliveredIme.set(insets.getInsets(WindowInsetsCompat.Type.ime()).bottom)
                insets
            }
            ViewCompat.requestApplyInsets(decor)
        }
        compose.onNodeWithTag("answer-input").performScrollTo().performClick().performTextInput("Good morning!")
        compose.waitUntil(5_000) { deliveredIme.get() > 0 }
        compose.runOnIdle {
            val position = IntArray(2)
            compose.activity.window.decorView.getLocationOnScreen(position)
            assertTrue("Whole window was panned above screen: y=${position[1]}", position[1] >= 0)
        }
        compose.onNodeWithTag("answer-input").assertIsDisplayed().performImeAction()
        compose.onNodeWithText("✓ Câu trả lời phù hợp!").performScrollTo().assertIsDisplayed()
    }
}
