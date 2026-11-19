package vn.noi.english.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import vn.noi.english.audio.*
import vn.noi.english.data.Progress
import vn.noi.english.data.ProgressRepository
import vn.noi.english.learning.*
import vn.noi.english.ui.theme.NoiTheme

@RunWith(AndroidJUnit4::class)
class LearningFlowTest {
    @get:Rule val compose = createComposeRule()
    private fun launch(fontScale: Float = 1f): LearningViewModel {
        val repo = object : ProgressRepository {
            override val progress = MutableStateFlow(Progress())
            override suspend fun savePosition(lessonId: String, turnIndex: Int) { progress.value = progress.value.copy(lessonId = lessonId, turnIndex = turnIndex) }
            override suspend fun complete(lessonId: String) { progress.value = progress.value.copy(completedLessonIds = progress.value.completedLessonIds + lessonId, lessonId = null, turnIndex = 0) }
        }
        val vm = LearningViewModel(repo)
        val input = object : SpeechInput {
            override val available = false
            override fun start(onResult: (String) -> Unit, onError: (String) -> Unit, onPhase: (AudioPhase) -> Unit) { onError("Không có dịch vụ nhận diện") }
            override fun stop() {}
            override fun cancel() {}
            override fun close() {}
        }
        val output = object : SpeechOutput {
            override val status = MutableStateFlow(VoiceState.Unavailable)
            override fun speak(text: String, onError: (String) -> Unit) { onError("Chưa có giọng đọc") }
            override fun stop() {}
            override fun close() {}
        }
        compose.setContent {
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(currentDensity.density, fontScale)) {
                NoiTheme { NoiApp(vm, input, output, { vm.message("Bạn chưa cấp quyền micro. Hãy chọn hoặc gõ để tiếp tục.") }, {}) }
            }
        }
        compose.waitUntil(5_000) { vm.state.value.ready }
        return vm
    }
    @Test fun beginnerCanCompleteAllFourTurnsWithoutMicrophone() {
        val vm = launch()
        compose.onNodeWithText("Chào hỏi").performScrollTo().performClick()
        compose.onNodeWithText("Chào buổi sáng").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        val lesson = vn.noi.english.content.Curriculum.topics[0].lessons[0]
        lesson.turns.forEachIndexed { i, turn ->
            compose.onNodeWithText("Chọn câu").performScrollTo().performClick()
            compose.onNode(hasText(turn.sample) and hasClickAction()).performScrollTo().performClick()
            compose.onNodeWithText(if (i == 3) "Hoàn thành bài" else "Lượt tiếp theo").performScrollTo().performClick()
        }
        compose.waitUntil(5_000) { vm.state.value.screen == Screen.Completion }
        compose.onNodeWithText("Thêm một chút\ntự tin hôm nay.").assertExists()
        compose.runOnIdle { org.junit.Assert.assertEquals(setOf("greetings-morning"), vm.state.value.progress.completedLessonIds) }
    }
    @Test fun missingSpeechServiceStillAllowsTypingAndDoesNotAdvanceEmptyAnswer() {
        val vm = launch()
        compose.onNodeWithText("Bắt đầu học").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Bắt đầu nói").performScrollTo().performClick()
        compose.onNodeWithText("Gõ", substring = false).performScrollTo().performClick()
        compose.onNodeWithText("Kiểm tra câu trả lời").performScrollTo().performClick()
        compose.runOnIdle { org.junit.Assert.assertEquals(0, vm.state.value.turnIndex) }
        compose.onNodeWithTag("answer-input").performTextInput("A coffee please")
        // Let the system IME finish its transition before tapping the button below the field.
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Kiểm tra câu trả lời").performScrollTo().performClick()
        compose.onNodeWithText("Thử theo gợi ý nhé").assertExists()
    }
    @Test fun largeTextStillAllowsStartingLessonAndChoosingAnswer() {
        launch(fontScale = 2f)
        compose.onNodeWithText("Bắt đầu học").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText("Chọn câu").performScrollTo().performClick()
        compose.onNode(hasText("Good morning!") and hasClickAction()).performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText("Lượt tiếp theo").performScrollTo().assertIsDisplayed()
    }
}
