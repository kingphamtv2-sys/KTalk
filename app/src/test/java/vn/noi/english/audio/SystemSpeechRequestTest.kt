package vn.noi.english.audio

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28, 35])
class SystemSpeechRequestTest {
    private fun text(vararg words: String) = Intent().putStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS, arrayListOf(*words))
    @Test fun intentRequestsEnglishAndResultIsConsumedOnlyOnce() {
        val request = SystemSpeechRequest()
        val intent = request.begin("greetings-morning:0")!!
        assertEquals(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, intent.action)
        assertEquals("en-US", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE))
        assertEquals(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL))
        assertNull(request.begin("greetings-morning:0"))
        assertEquals(SystemSpeechResult.Text("Good morning"), request.consume("greetings-morning:0", true, Activity.RESULT_OK, text(" ", " Good morning ")))
        assertNull(request.pendingTurn)
        assertEquals(SystemSpeechResult.Ignored, request.consume("greetings-morning:0", true, Activity.RESULT_OK, text("late duplicate")))
    }
    @Test fun changedTurnOrModeAndCancelledOrEmptyResultsNeverSupplyAnAnswer() {
        val request = SystemSpeechRequest()
        request.begin("lesson:0")
        assertEquals(SystemSpeechResult.Ignored, request.consume("lesson:1", true, Activity.RESULT_OK, text("old")))
        request.begin("lesson:0")
        assertEquals(SystemSpeechResult.Ignored, request.consume("lesson:0", false, Activity.RESULT_OK, text("wrong mode")))
        request.begin("lesson:0")
        assertEquals(SystemSpeechResult.Cancelled, request.consume("lesson:0", true, Activity.RESULT_CANCELED, null))
        request.begin("lesson:0")
        assertEquals(SystemSpeechResult.Empty, request.consume("lesson:0", true, Activity.RESULT_OK, text(" ")))
    }
    @Test fun pendingRequestSurvivesActivityRecreationButIsClearedAfterLaunchFailure() {
        val first = SystemSpeechRequest(); first.begin("lesson:2")
        val restored = SystemSpeechRequest(); restored.restore(first.pendingTurn)
        assertEquals(SystemSpeechResult.Text("Hello"), restored.consume("lesson:2", true, Activity.RESULT_OK, text("Hello")))
        restored.begin("lesson:2"); restored.cancel()
        assertNull(restored.pendingTurn)
        assertNotNull(restored.begin("lesson:2"))
    }
    @Test fun providerFailuresAreNeverReportedAsUserCancellation() {
        listOf(RecognizerIntent.RESULT_AUDIO_ERROR, RecognizerIntent.RESULT_CLIENT_ERROR, RecognizerIntent.RESULT_NETWORK_ERROR,
            RecognizerIntent.RESULT_NO_MATCH, RecognizerIntent.RESULT_SERVER_ERROR, 999).forEach { code ->
            val request = SystemSpeechRequest(); request.begin("lesson:0")
            val result = request.consume("lesson:0", true, code, null)
            assertNotEquals("provider result$code is not user cancellation", SystemSpeechResult.Cancelled, result)
            assertNotEquals(SystemSpeechResult.Ignored, result)
            assertTrue(result is SystemSpeechResult.Failure)
            val message = (result as SystemSpeechResult.Failure).message
            if (code == RecognizerIntent.RESULT_AUDIO_ERROR) assertTrue(message.contains("micro"))
            if (code == RecognizerIntent.RESULT_NETWORK_ERROR) assertTrue(message.contains("mạng"))
            if (code == 999) assertTrue(message.contains("SYS-999"))
            assertNull(request.pendingTurn)
        }
    }
    @Test fun automaticLaunchRequiresForegroundAndDoesNotQueueARecordingForLater() {
        val request = SystemSpeechRequest()
        assertNull(request.begin("lesson:0", automatic = true, resumed = false, focused = true))
        assertNull(request.pendingTurn)
        assertNull(request.begin("lesson:0", automatic = true, resumed = true, focused = false))
        assertNull(request.pendingTurn)
        assertNotNull(request.begin("lesson:0", automatic = true, resumed = true, focused = true))
        // Delivery stays valid independently of the launch-only foreground guard.
        assertEquals(SystemSpeechResult.Text("Hello"), request.consume("lesson:0", true, Activity.RESULT_OK, text("Hello")))
    }
}
