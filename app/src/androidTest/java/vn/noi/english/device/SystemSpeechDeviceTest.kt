package vn.noi.english.device

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.IntentFilter
import android.speech.RecognizerIntent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import vn.noi.english.MainActivity

@RunWith(AndroidJUnit4::class)
class SystemSpeechDeviceTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun openConversation() {
        compose.waitUntil(5_000) { compose.onAllNodesWithText("Chào hỏi").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Chào hỏi").performScrollTo().performClick()
        compose.onNodeWithText("Chào buổi sáng").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
    }
    @Test fun androidRecognitionResultFillsEditableTextWithoutAutoSubmitting() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val result = Intent().putStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS, arrayListOf("Good morning"))
        val monitor = instrumentation.addMonitor(IntentFilter(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),
            Instrumentation.ActivityResult(Activity.RESULT_OK, result), true)
        try {
            openConversation()
            compose.onNodeWithText("Nhận diện bằng Android").performScrollTo().performClick()
            compose.waitUntil(5_000) { compose.onAllNodesWithTag("answer-input").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithTag("answer-input").assertTextContains("Good morning")
            compose.onNodeWithText("✓ Câu trả lời phù hợp!").assertDoesNotExist()
            compose.onNodeWithText("Lượt 1 / 4").assertExists()
            compose.onNodeWithText("Kiểm tra câu trả lời").performScrollTo().performClick()
            compose.onNodeWithText("✓ Câu trả lời phù hợp!").assertExists()
        } finally { instrumentation.removeMonitor(monitor) }
    }
    @Test fun cancellingAndroidRecognitionLeavesTheTurnUnchangedAndRetryAvailable() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val monitor = instrumentation.addMonitor(IntentFilter(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),
            Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null), true)
        try {
            openConversation()
            compose.onNodeWithText("Nhận diện bằng Android").performScrollTo().performClick()
            compose.waitUntil(5_000) { compose.onAllNodesWithText("Bạn đã dừng nhận diện. Chạm micro để thử lại hoặc tiếp tục bằng chọn / gõ.").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Lượt 1 / 4").assertExists()
            compose.onNodeWithText("✓ Câu trả lời phù hợp!").assertDoesNotExist()
            compose.onNodeWithText("Nhận diện bằng Android").performScrollTo().assertIsEnabled()
            compose.onNodeWithText("Gõ").performScrollTo().performClick()
            compose.onNodeWithTag("answer-input").assertExists()
        } finally { instrumentation.removeMonitor(monitor) }
    }
    @Test fun androidAudioFailureIsDistinctFromUserCancellation() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val monitor = instrumentation.addMonitor(IntentFilter(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),
            Instrumentation.ActivityResult(RecognizerIntent.RESULT_AUDIO_ERROR, null), true)
        try {
            openConversation()
            compose.onNodeWithText("Nhận diện bằng Android").performScrollTo().performClick()
            compose.waitUntil(5_000) { compose.onAllNodesWithText("Hộp thoại Android không truy cập được micro", substring = true).fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Bạn đã dừng nhận diện.", substring = true).assertDoesNotExist()
            compose.onNodeWithText("Lượt 1 / 4").assertExists()
            compose.onNodeWithText("Cài đặt nhận diện").performScrollTo().assertIsEnabled()
        } finally { instrumentation.removeMonitor(monitor) }
    }
}
