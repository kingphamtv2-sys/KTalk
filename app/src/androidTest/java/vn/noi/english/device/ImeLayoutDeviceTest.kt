package vn.noi.english.device

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import vn.noi.english.MainActivity

@RunWith(AndroidJUnit4::class)
class ImeLayoutDeviceTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun keyboardInsetsAreAppliedOnceAndSubmitStaysReachable() {
        compose.waitUntil(5_000) { compose.onAllNodes(hasText("Bắt đầu học") or hasText("Tiếp tục học")).fetchSemanticsNodes().isNotEmpty() }
        compose.onNode(hasText("Bắt đầu học") or hasText("Tiếp tục học")).performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.onNodeWithText("Gõ").performScrollTo().performClick()
        compose.onNodeWithTag("answer-input").performScrollTo().performClick().performTextInput("Good morning")
        val decor = compose.activity.window.decorView
        compose.waitUntil(5_000) { ViewCompat.getRootWindowInsets(decor)?.isVisible(WindowInsetsCompat.Type.ime()) == true }
        compose.waitForIdle()
        val insets = ViewCompat.getRootWindowInsets(decor)!!
        val keyboardTop = (decor.height - insets.getInsets(WindowInsetsCompat.Type.ime()).bottom) / compose.activity.resources.displayMetrics.density
        val contentBottom = compose.onNodeWithTag("screen-content").getBoundsInRoot().bottom.value
        assertTrue("Unused space above IME: ${keyboardTop - contentBottom}dp", keyboardTop - contentBottom < 32f)
        compose.onNodeWithText("Kiểm tra câu trả lời").performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText("✓ Câu trả lời phù hợp!").assertExists()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(5_000) { compose.onAllNodesWithText("✓ Câu trả lời phù hợp!").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Lượt tiếp theo").performScrollTo().assertIsDisplayed()
    }
}
