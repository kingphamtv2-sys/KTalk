package vn.noi.english.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import vn.noi.english.audio.*
import vn.noi.english.data.Progress
import vn.noi.english.data.ProgressRepository
import vn.noi.english.learning.*
import vn.noi.english.ui.theme.NoiTheme
import vn.noi.english.vocabulary.*
import vn.noi.english.data.VocabularyProgress
import vn.noi.english.data.VocabularyRepository

@RunWith(AndroidJUnit4::class)
class LearningFlowTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var vocabulary: VocabularyViewModel
    private val voiceStatus = MutableStateFlow(VoiceState.Unavailable)
    private var spokenText: String? = null
    private var audioStops = 0
    private lateinit var rootView: android.view.View
    private fun launch(fontScale: Float = 1f, initial: Progress = Progress(), conversationReadFails: Boolean = false, vocabularyReadFails: Boolean = false,
                       wordWriteGate: kotlinx.coroutines.CompletableDeferred<Unit>? = null, wordWriteFails: Boolean = false,
                       systemSpeech: (() -> Unit)? = null, speechSettings: (() -> Unit)? = null, compact: Boolean = false,
                       viewport: DpSize = DpSize(360.dp, 680.dp), voiceError: String = "Chưa có giọng đọc"): LearningViewModel {
        val repo = object : ProgressRepository {
            val saved = MutableStateFlow(initial)
            override val progress = kotlinx.coroutines.flow.flow {
                if (conversationReadFails) throw java.io.IOException("Conversation storage unavailable")
                emitAll(saved)
            }
            override suspend fun savePosition(lessonId: String, turnIndex: Int) { saved.value = saved.value.copy(lessonId = lessonId, turnIndex = turnIndex) }
            override suspend fun complete(lessonId: String) { saved.value = saved.value.copy(completedLessonIds = saved.value.completedLessonIds + lessonId, lessonId = null, turnIndex = 0) }
        }
        val vm = LearningViewModel(repo)
        val words = object : VocabularyRepository {
            val saved = MutableStateFlow(VocabularyProgress())
            override val progress = kotlinx.coroutines.flow.flow {
                if (vocabularyReadFails) throw java.io.IOException("Vocabulary storage unavailable")
                emitAll(saved)
            }
            override suspend fun mark(wordId: String, known: Boolean) {
                wordWriteGate?.await()
                if (wordWriteFails) throw java.io.IOException("Write failed")
                val old = saved.value
                saved.value = if (known) VocabularyProgress(old.known + wordId, old.review - wordId) else VocabularyProgress(old.known - wordId, old.review + wordId)
            }
        }
        vocabulary = VocabularyViewModel(words, kotlin.random.Random(42))
        val input = object : SpeechInput {
            override val available = false
            override fun start(onResult: (String) -> Unit, onError: (String) -> Unit, onPhase: (AudioPhase) -> Unit) { onError("Không có dịch vụ nhận diện") }
            override fun stop() {}
            override fun cancel() {}
            override fun close() {}
        }
        val output = object : SpeechOutput {
            override val status = voiceStatus
            override fun speak(text: String, onError: (String) -> Unit) {
                if (status.value == VoiceState.Ready) spokenText = text else onError(voiceError)
            }
            override fun stop() { audioStops++ }
            override fun close() {}
        }
        compose.setContent {
            rootView = androidx.compose.ui.platform.LocalView.current
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(currentDensity.density, fontScale)) {
                NoiTheme {
                    Box(if (compact) Modifier.requiredSize(viewport.width, viewport.height) else Modifier) {
                        NoiApp(vm, input, output, { vm.message("Bạn chưa cấp quyền micro. Hãy chọn hoặc gõ để tiếp tục.") }, {}, vocabulary,
                            onSystemSpeech = systemSpeech, openSpeechSettings = speechSettings)
                    }
                }
            }
        }
        compose.waitUntil(5_000) { !vm.state.value.loading }
        compose.waitUntil(5_000) { !vocabulary.state.value.loading }
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
    @Test fun expandedCatalogShowsNewTotalAndResumesOldProgress() {
        launch(initial = Progress(setOf("greetings-morning"), "directions-route", 2))
        compose.onNodeWithText("1 / 45 bài").assertExists()
        compose.onNodeWithText("Tiến độ").performClick()
        compose.onNodeWithText("1 / 45 bài").assertExists()
        compose.onNodeWithText("Học mỗi ngày").performClick()
        compose.onNodeWithText("Tiếp tục học").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.onNodeWithText("Lượt 3 / 4").assertExists()
    }
    @Test fun newFamilyLessonCanBeCompletedWithoutAudio() {
        val vm = launch()
        compose.onNodeWithText("0 / 45 bài").assertExists()
        compose.onNodeWithText("Gia đình").performScrollTo().performClick()
        compose.onNodeWithText("Giới thiệu người thân").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        val lesson = vn.noi.english.content.Curriculum.lesson("family-photo")!!
        lesson.turns.forEachIndexed { index, turn ->
            compose.onNodeWithText("Chọn câu").performScrollTo().performClick()
            compose.onNode(hasText(turn.sample) and hasClickAction()).performScrollTo().performClick()
            compose.onNodeWithText(if (index == 3) "Hoàn thành bài" else "Lượt tiếp theo").performScrollTo().performClick()
        }
        compose.waitUntil(5_000) { vm.state.value.screen == Screen.Completion }
        compose.runOnIdle { org.junit.Assert.assertEquals(setOf("family-photo"), vm.state.value.progress.completedLessonIds) }
    }
    @Test fun vocabularyCardsReviewIndependentlyOfConversationProgress() {
        launch(initial = Progress(setOf("greetings-morning")))
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithText("Học từ vựng").assertExists()
        compose.onNodeWithText("Học thẻ từ").performScrollTo().performClick()
        compose.onNodeWithText("Xem nghĩa").performScrollTo().performClick()
        compose.onNodeWithText("Chào buổi sáng").assertExists()
        compose.onNodeWithText("Đã nhớ").performScrollTo().performClick()
        compose.waitUntil(5_000) { vocabulary.state.value.progress.known.size == 1 }
        compose.onNodeWithText("Xem nghĩa").performScrollTo().performClick()
        compose.onNodeWithText("Cần ôn").performScrollTo().performClick()
        compose.waitUntil(5_000) { vocabulary.state.value.progress.review.size == 1 }
        compose.onNodeWithContentDescription("Quay lại").performScrollTo().performClick()
        compose.onNodeWithTag("known-word-count").assertTextEquals("1 / 300 đã nhớ")
        compose.onNodeWithText("Ôn từ cần nhớ").performScrollTo().performClick()
        compose.onNodeWithText("Xem nghĩa").performScrollTo().performClick()
        compose.onNodeWithText("Đã nhớ").performScrollTo().performClick()
        compose.waitUntil(5_000) { vocabulary.state.value.page == VocabularyPage.CardsDone }
        compose.onNodeWithText("Về từ vựng").performScrollTo().performClick()
        compose.onNodeWithTag("known-word-count").assertTextEquals("2 / 300 đã nhớ")
        compose.onNodeWithText("Học mỗi ngày").performClick()
        compose.onNodeWithText("Tiến độ").performClick()
        compose.onNodeWithText("1 / 45 bài").assertExists()
    }
    @Test fun vocabularyQuizShowsFeedbackAndScoresOnlyThisSession() {
        launch()
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithText("Kiểm tra nhanh").performScrollTo().performClick()
        repeat(5) { index ->
            val question = vocabulary.state.value.currentQuestion!!
            val answer = if (index == 0) question.options.first { it != question.correctMeaning } else question.correctMeaning
            compose.onNode(hasText(answer) and hasClickAction()).performScrollTo().performClick()
            compose.onNodeWithText(if (index == 4) "Xem kết quả" else "Câu tiếp theo").performScrollTo().performClick()
        }
        compose.onNodeWithText("4 / 5 câu đúng").assertExists()
        compose.runOnIdle { org.junit.Assert.assertEquals(VocabularyProgress(), vocabulary.state.value.progress) }
    }
    @Test fun vocabularyCardsSupportLargeTextAndMissingVoice() {
        launch(fontScale = 2f)
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithText("Học thẻ từ").performScrollTo().performClick()
        compose.onNodeWithText("Nghe từ").performScrollTo().performClick()
        compose.onNodeWithText("Đã hiểu").performClick()
        compose.onNodeWithText("Xem nghĩa").performScrollTo().performClick()
        compose.onNodeWithText("Đã nhớ").performScrollTo().assertIsDisplayed().performClick()
    }
    @Test fun vocabularyRemainsAvailableWhenOnlyConversationStorageFails() {
        val vm = launch(conversationReadFails = true)
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithText("Học thẻ từ").performScrollTo().performClick()
        compose.onNodeWithText("Nghe từ").performScrollTo().performClick()
        compose.onNodeWithText("Đã hiểu").performClick()
        compose.onNodeWithText("Xem nghĩa").performScrollTo().performClick()
        compose.onNodeWithText("Đã nhớ").performScrollTo().performClick()
        compose.waitUntil(5_000) { vocabulary.state.value.progress.known.size == 1 }
        compose.runOnIdle { org.junit.Assert.assertFalse(vm.state.value.ready) }
    }
    @Test fun conversationRemainsAvailableWhenOnlyVocabularyStorageFails() {
        val vm = launch(vocabularyReadFails = true)
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithText("Tải lại từ vựng").performScrollTo().assertExists()
        compose.onNodeWithText("Học mỗi ngày").performClick()
        compose.onNodeWithText("Bắt đầu học").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.runOnIdle { org.junit.Assert.assertEquals(Screen.Conversation, vm.state.value.screen) }
    }
    @Test fun sameFrameTabActionsCannotLeaveAPendingWordSave() {
        val gate = kotlinx.coroutines.CompletableDeferred<Unit>()
        val vm = launch(wordWriteGate = gate, wordWriteFails = true)
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithText("Học thẻ từ").performScrollTo().performClick()
        compose.onNodeWithText("Xem nghĩa").performScrollTo().performClick()
        val tabActions = listOf("Học mỗi ngày", "Tiến độ", "Từ vựng").map { label ->
            compose.onNodeWithText(label).fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsActions.OnClick].action!!
        }
        compose.runOnIdle {
            vocabulary.markCurrent(true)
            org.junit.Assert.assertTrue(vocabulary.state.value.saving)
            try {
                tabActions.forEach { action ->
                    action.invoke()
                    org.junit.Assert.assertEquals(Screen.VocabularyMode, vm.state.value.screen)
                    org.junit.Assert.assertEquals(VocabularyPage.Cards, vocabulary.state.value.page)
                }
            } finally { gate.complete(Unit) }
        }
        compose.waitUntil(5_000) { !vocabulary.state.value.saving && vocabulary.state.value.error != null }
        compose.onNodeWithText("Chưa lưu được mức nhớ.", substring = true).assertExists()
        compose.runOnIdle { org.junit.Assert.assertEquals(0, vocabulary.state.value.cardIndex) }
    }
    @Test fun searchNewVocabularyRevealHearExampleAndRestoreMark() {
        launch()
        compose.runOnIdle { voiceStatus.value = VoiceState.Ready }
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithTag("vocabulary-search").performTextInput("AO MUA")
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithTag("known-word-count").assertTextEquals("0 / 1 đã nhớ")
        compose.onNodeWithText("Học thẻ từ").performScrollTo().performClick()
        compose.onNodeWithText("a raincoat").assertExists()
        compose.onNodeWithText("I need a raincoat.").assertDoesNotExist()
        compose.onNodeWithText("Xem nghĩa").performScrollTo().performClick()
        compose.onNodeWithText("I need a raincoat.").assertExists()
        compose.onNodeWithText("Tôi cần một chiếc áo mưa.").assertExists()
        compose.onNodeWithText("Nghe ví dụ").performScrollTo().performClick()
        compose.runOnIdle { org.junit.Assert.assertEquals("I need a raincoat.", spokenText) }
        val stopsBefore = audioStops
        compose.onNodeWithText("Đã nhớ").performScrollTo().performClick()
        compose.waitUntil(5_000) { vocabulary.state.value.page == VocabularyPage.CardsDone }
        compose.runOnIdle { org.junit.Assert.assertTrue(audioStops > stopsBefore) }
        compose.onNodeWithText("Về từ vựng").performScrollTo().performClick()
        compose.onNodeWithTag("known-word-count").assertTextEquals("1 / 1 đã nhớ")
        compose.onNodeWithText("Xóa tìm kiếm").performScrollTo().performClick()
        compose.onNodeWithTag("known-word-count").assertTextEquals("1 / 300 đã nhớ")
    }
    @Test fun largeTextSearchNoResultsCanBeClearedAndSessionIsShort() {
        launch(fontScale = 2f)
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithTag("vocabulary-search").performTextInput("zzznothing")
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Không tìm thấy từ phù hợp.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Học thẻ từ").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Xóa tìm kiếm").performScrollTo().performClick()
        compose.onNodeWithText("Học thẻ từ").performScrollTo().performClick()
        compose.onNodeWithText("Thẻ 1 / 10").assertExists()
        compose.onNodeWithText("Xem nghĩa").performScrollTo().performClick()
        compose.onNodeWithText("Đã nhớ").performScrollTo().assertIsDisplayed()
    }
    @Test fun nextConversationTurnStartsAtQuestionAfterScrollingWithLargeText() {
        launch(fontScale = 2f)
        compose.onNodeWithText("Bắt đầu học").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.onNodeWithText("Chọn câu").performScrollTo().performClick()
        compose.onNode(hasText("Good morning!") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("Lượt tiếp theo").performScrollTo().performClick()
        compose.onNodeWithText("Lượt 2 / 4").assertIsDisplayed()
        compose.onNodeWithText("How are you this morning?").assertIsDisplayed()
    }
    @Test fun successfulAudioRetryClearsPreviousError() {
        launch()
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithText("Học thẻ từ").performScrollTo().performClick()
        compose.onNodeWithText("Nghe từ").performScrollTo().performClick()
        compose.onNodeWithText("Chưa có giọng đọc").assertExists()
        compose.runOnIdle { voiceStatus.value = VoiceState.Ready }
        compose.onNodeWithText("Nghe từ").performScrollTo().performClick()
        compose.onNodeWithText("Chưa có giọng đọc").assertDoesNotExist()
        compose.runOnIdle { org.junit.Assert.assertEquals("Good morning", spokenText) }
    }
    @Test fun microphonePreparingIsNotListeningAndRecoveryControlsRemainUsable() {
        var systemCalls = 0; var settingsCalls = 0
        val vm = launch(fontScale = 2f, systemSpeech = { systemCalls++ }, speechSettings = { settingsCalls++ })
        compose.onNodeWithText("Bắt đầu học").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.runOnIdle { vm.setAudio(AudioPhase.Preparing) }
        compose.onNodeWithText("Đang mở micro…").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Bắt đầu nói").assertIsNotEnabled()
        compose.onNodeWithText("Đang nghe… Chạm để dừng").assertDoesNotExist()
        compose.runOnIdle { vm.message("Bộ nhận diện chưa mở được phiên nghe (SR-5).") }
        compose.onNodeWithText("Cài đặt nhận diện").performScrollTo().performClick()
        compose.onNodeWithText("Nhận diện bằng Android").performScrollTo().performClick()
        compose.runOnIdle {
            org.junit.Assert.assertEquals(1, settingsCalls); org.junit.Assert.assertEquals(1, systemCalls)
            org.junit.Assert.assertEquals(0, vm.state.value.turnIndex)
            org.junit.Assert.assertEquals(Feedback.None, vm.state.value.feedback)
        }
        compose.onNodeWithText("Gõ").performScrollTo().performClick()
        compose.onNodeWithTag("answer-input").performTextInput("Good morning")
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Kiểm tra câu trả lời").performScrollTo().performClick()
        compose.onNodeWithText("✓ Câu trả lời phù hợp!").assertExists()
    }
    @Test fun compactConversationKeepsMicrophoneVisibleWithGuidanceEnabled() {
        launch(compact = true)
        compose.runOnIdle { voiceStatus.value = VoiceState.Ready }
        compose.onNodeWithText("Bắt đầu học").performScrollTo().performClick()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.onNodeWithText("Good morning!").assertIsDisplayed()
        compose.onNodeWithContentDescription("Bắt đầu nói").assertIsDisplayed()
        val micBounds = compose.onNodeWithContentDescription("Bắt đầu nói").getUnclippedBoundsInRoot()
        val viewport = compose.onNodeWithTag("screen-content").getBoundsInRoot()
        org.junit.Assert.assertTrue("Microphone $micBounds must fit entirely in $viewport", micBounds.bottom <= viewport.bottom && micBounds.top >= viewport.top)
        compose.onNodeWithText("Chào lại bằng Good morning.").assertExists()
        compose.onNodeWithText("Ẩn").performScrollTo().performClick()
        compose.onNodeWithText("Ví dụ: Good morning!").assertDoesNotExist()
        compose.onNodeWithText("Hiện").performClick()
        compose.onNodeWithText("Nghe câu mẫu").performScrollTo().assertIsDisplayed()
    }
    @Test fun compactLargeTextVocabularyCanRevealHearAndSaveExpandedWord() {
        launch(fontScale = 2f, compact = true)
        compose.onNodeWithText("Từ vựng").performClick()
        compose.onNodeWithTag("vocabulary-search").performTextInput("raincoat")
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Học thẻ từ").performScrollTo().performClick()
        compose.onNodeWithText("Xem nghĩa").performScrollTo().performClick()
        compose.onNodeWithText("I need a raincoat.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Nghe ví dụ").performScrollTo().performClick()
        compose.onNodeWithText("Đã hiểu").performClick()
        compose.onNodeWithText("Cần ôn").performScrollTo().assertIsDisplayed().performClick()
        compose.waitUntil(5_000) { vocabulary.state.value.page == VocabularyPage.CardsDone }
        compose.onNodeWithText("Về từ vựng").performScrollTo().performClick()
        compose.onNodeWithTag("known-word-count").performScrollTo().assertTextEquals("0 / 1 đã nhớ")
        compose.runOnIdle { org.junit.Assert.assertEquals(1, vocabulary.state.value.progress.review.size) }
    }
    @Test fun lessonBriefShowsItsPurposeBeforeStartingPractice() {
        launch(compact = true)
        compose.onNodeWithText("Bắt đầu học").performScrollTo().performClick()
        compose.onAllNodesWithText("Chào buổi sáng").onFirst().assertIsDisplayed()
        compose.onNodeWithText("Chào buổi sáng, trả lời câu hỏi thăm và đáp lại lời chúc.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Bạn là: Người bạn").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithContentDescription("Bắt đầu nói").performScrollTo().assertIsDisplayed()
    }
    @Test fun hotelPracticeIdentifiesTheReceptionistInsteadOfAFriend() {
        val vm = launch(compact = true, fontScale = 2f)
        compose.runOnIdle { vm.openLesson("hotel-check-in") }
        compose.onNodeWithText("Bạn là: Khách lưu trú").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.onNodeWithText("Lễ tân").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Alex", substring = false).assertDoesNotExist()
        compose.onNodeWithText("Chọn câu").performScrollTo().performClick()
        compose.onNode(hasText("I'd like to check in, please.") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("Lượt tiếp theo").performScrollTo().assertIsDisplayed()
    }
    @Test fun completionConnectsTheDialogueToAPracticalTakeaway() {
        val vm = launch(compact = true)
        compose.runOnIdle { vm.openLesson("greetings-morning") }
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        listOf("Good morning!", "I'm fine, thank you.", "Yes, I am.", "You too!").forEachIndexed { index, answer ->
            compose.onNodeWithText("Chọn câu").performScrollTo().performClick()
            compose.onNode(hasText(answer) and hasClickAction()).performScrollTo().performClick()
            compose.onNodeWithText(if (index == 3) "Hoàn thành bài" else "Lượt tiếp theo").performScrollTo().performClick()
        }
        compose.onNodeWithText("Chào buổi sáng, trả lời câu hỏi thăm và đáp lại lời chúc.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Đáp lại lời chúc bằng You too!; thêm Thank you để thể hiện sự lịch sự.").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { org.junit.Assert.assertEquals(setOf("greetings-morning"), vm.state.value.progress.completedLessonIds) }
    }
    @Test fun typedAnswerCanBeCheckedFromTheKeyboardWithoutAddingANewline() {
        val vm = launch(compact = true, fontScale = 1.3f)
        compose.runOnIdle { voiceStatus.value = VoiceState.Ready; vm.openLesson("greetings-morning") }
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.onNodeWithText("Gõ", substring = false).performScrollTo().performClick()
        compose.onNodeWithTag("answer-input").performTextInput("Good morning!")
        compose.onNodeWithTag("answer-input").performImeAction()
        compose.runOnIdle {
            org.junit.Assert.assertEquals(Feedback.Accepted, vm.state.value.feedback)
            org.junit.Assert.assertEquals("Good morning!", vm.state.value.answer)
            org.junit.Assert.assertEquals(0, vm.state.value.turnIndex)
        }
        compose.onNodeWithText("✓ Câu trả lời phù hợp!").performScrollTo().assertIsDisplayed()
    }
    @Test fun correctedSpeechCanBeCheckedFromTheKeyboard() {
        val vm = launch(compact = true)
        compose.runOnIdle { voiceStatus.value = VoiceState.Ready; vm.openLesson("greetings-morning") }
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.runOnIdle { vm.setAnswer("Good mourning") }
        compose.onNodeWithTag("answer-input").performScrollTo().performTextReplacement("Good morning!")
        compose.onNodeWithTag("answer-input").performImeAction()
        compose.runOnIdle {
            org.junit.Assert.assertEquals(Feedback.Accepted, vm.state.value.feedback)
            org.junit.Assert.assertEquals(AnswerMode.Speak, vm.state.value.mode)
        }
    }
    @Test fun zoomedDisplayWithLargeTextStillAllowsFallbackWhileAnAudioErrorIsShown() {
        val vm = launch(compact = true, viewport = DpSize(320.dp, 658.dp), fontScale = 2f)
        compose.runOnIdle { vm.openLesson("greetings-morning") }
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.runOnIdle { vm.message("Ứng dụng nhận diện Android chưa mở được phiên nghe (SYS-CLIENT). Kiểm tra hoặc cập nhật dịch vụ nhận diện trong Cài đặt.") }
        compose.onNodeWithText("Gõ", substring = false).performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithTag("answer-input").performScrollTo().performTextInput("Good morning!")
        compose.onNodeWithTag("answer-input").performImeAction()
        compose.runOnIdle { org.junit.Assert.assertEquals(Feedback.Accepted, vm.state.value.feedback) }
    }
    @Test fun speechTranscriptCanBeErasedCompletelyAndRetypedBeforeChecking() {
        val vm = launch(compact = true)
        compose.runOnIdle { voiceStatus.value = VoiceState.Ready; vm.openLesson("greetings-morning") }
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.runOnIdle { vm.setAnswer("Good mourning") }
        compose.onNodeWithTag("answer-input").performScrollTo().performClick().performTextClearance()
        compose.onNodeWithTag("answer-input").assertExists().assertIsFocused().performTextInput("Good morning!")
        compose.onNodeWithTag("answer-input").performImeAction()
        compose.runOnIdle { org.junit.Assert.assertEquals(Feedback.Accepted, vm.state.value.feedback) }
        compose.onNodeWithText("Thử nói lại").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Bắt đầu nói").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("answer-input").assertDoesNotExist()
    }
    @Test fun directCheckoutLessonExplainsTheRoomNumberBeforeExpectingIt() {
        val vm = launch(compact = true)
        compose.runOnIdle { vm.openLesson("hotel-check-out") }
        compose.onNodeWithText("Bạn vừa lưu trú ở phòng 205. Bạn trả phòng, lấy biên lai, thanh toán bằng thẻ và gửi hành lý một lát.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.onNodeWithText("Chọn câu").performScrollTo().performClick()
        compose.onNode(hasText("Yes, I'd like to check out.") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("Lượt tiếp theo").performScrollTo().performClick()
        compose.onNodeWithText("Ví dụ: Room two-oh-five.").performScrollTo().assertIsDisplayed()
    }
    @Test fun meetingLessonStatesTheFixedTimeBeforeAskingTheLearnerToProposeIt() {
        val vm = launch(compact = true)
        compose.runOnIdle { vm.openLesson("time-meeting") }
        compose.onNodeWithText("Bạn muốn hẹn Alex ở quán cà phê lúc ba giờ chiều ngày mai. Hãy nhận lời và đề xuất giờ gặp đó.").performScrollTo().assertIsDisplayed()
    }
    @Test fun zoomedLargeTextNavigationKeepsItsLabelsInsideTheScreen() {
        launch(compact = true, viewport = DpSize(320.dp, 658.dp), fontScale = 2f)
        val homeLabel = hasText("Học mỗi ngày", substring = false) or hasText("Học", substring = false)
        val screen = compose.onNodeWithTag("screen-content").getBoundsInRoot()
        listOf(homeLabel, hasText("Từ vựng", substring = false), hasText("Tiến độ", substring = false)).forEach { matcher ->
            val bounds = compose.onNode(matcher, useUnmergedTree = true).getUnclippedBoundsInRoot()
            org.junit.Assert.assertTrue("Navigation label $bounds must fit width $screen", bounds.left >= screen.left && bounds.right <= screen.right)
        }
        compose.onNodeWithText("Tiến độ").performClick()
        compose.onNode(homeLabel).performClick()
        compose.onNodeWithText("Bắt đầu học").assertExists()
    }
    @Test fun persistentVoiceErrorAndKeyboardLeaveRoomToEditOnAZoomedDisplay() {
        val error = "Thiết bị chưa có giọng đọc tiếng Anh. Bạn có thể cài giọng đọc trong phần Cài đặt."
        val vm = launch(compact = true, viewport = DpSize(320.dp, 658.dp), fontScale = 2f, voiceError = error)
        compose.runOnIdle { vm.openLesson("greetings-morning") }
        compose.onNodeWithText("Bắt đầu hội thoại").performScrollTo().performClick()
        compose.onNodeWithText("Gõ", substring = false).performScrollTo().performClick()
        compose.onNodeWithText("Nghe câu mẫu").performScrollTo().performClick()
        compose.onNodeWithText(error).assertExists()
        compose.onNodeWithTag("answer-input").performScrollTo().performClick().performTextInput("Good morning!")
        if (!android.os.Build.FINGERPRINT.contains("robolectric")) {
            compose.waitUntil(5_000) {
                androidx.core.view.ViewCompat.getRootWindowInsets(rootView)?.isVisible(androidx.core.view.WindowInsetsCompat.Type.ime()) == true
            }
        }
        val input = compose.onNodeWithTag("answer-input").getUnclippedBoundsInRoot()
        val viewport = compose.onNodeWithTag("screen-content").getBoundsInRoot()
        org.junit.Assert.assertTrue("Editable answer $input must fit in $viewport with voice error and IME", input.top >= viewport.top && input.bottom <= viewport.bottom)
        compose.onNodeWithText(error).assertExists()
        compose.onNodeWithTag("answer-input").performImeAction()
        compose.runOnIdle { org.junit.Assert.assertEquals(Feedback.Accepted, vm.state.value.feedback) }
    }
    @Test fun homeSuggestionDoesNotPromoteACompletedReplayAsANewLesson() {
        val vm = launch(initial = Progress(setOf("greetings-morning"), "greetings-morning", 2))
        compose.onNodeWithText("Hỏi thăm đơn giản").assertExists()
        compose.onNodeWithText("Bắt đầu học").performScrollTo().performClick()
        compose.runOnIdle { org.junit.Assert.assertEquals("greetings-wellbeing", vm.state.value.lessonId) }
    }
    @Test fun allCompletedLessonsHaveAnExplicitReplayActionAndKeepCompletionCount() {
        val completed = vn.noi.english.content.Curriculum.lessons.map { it.id }.toSet()
        val vm = launch(initial = Progress(completed, completed.last(), 2))
        compose.onNodeWithText("45 / 45 bài").assertExists()
        compose.onNodeWithText("Học lại").performScrollTo().performClick()
        compose.runOnIdle {
            org.junit.Assert.assertEquals(Screen.Vocabulary, vm.state.value.screen)
            org.junit.Assert.assertEquals(45, vm.state.value.progress.completedLessonIds.size)
        }
    }
    @Test fun activeTopicWithZeroCompletionsShowsLearningInsteadOfUnstarted() {
        launch(initial = Progress(lessonId = "greetings-morning", turnIndex = 1))
        compose.onNodeWithText("Tiếp tục học").assertExists()
        compose.onNode(hasText("Chào hỏi") and hasClickAction()).assert(hasText("Đang học · 0 / 3 bài hoàn thành"))
    }
}
