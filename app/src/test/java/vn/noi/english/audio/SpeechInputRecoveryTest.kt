package vn.noi.english.audio

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.os.Looper
import android.speech.RecognitionService
import android.speech.SpeechRecognizer
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSpeechRecognizer
import java.time.Duration
import vn.noi.english.learning.AudioPhase

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28, 35])
class SpeechInputRecoveryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    @Before fun provideRecognitionService() {
        val info = ResolveInfo().apply { serviceInfo = ServiceInfo().apply {
            packageName = "test.speech"; name = "test.speech.Service"; exported = true
        } }
        shadowOf(context.packageManager).addResolveInfoForIntent(Intent(RecognitionService.SERVICE_INTERFACE), info)
    }
    private fun current() = shadowOf(ShadowSpeechRecognizer.getLatestSpeechRecognizer())
    private fun idle() = shadowOf(Looper.getMainLooper()).idle()

    @Test fun immediateClientFailureRecoversOnceWithoutBlamingTheLearner() {
        var fallback = 0; val errors = mutableListOf<String>(); val phases = mutableListOf<AudioPhase>()
        val input = AndroidSpeechInput(context) { fallback++; true }
        assertTrue(input.available)
        input.start({}, errors::add, phases::add); idle()
        assertEquals(AudioPhase.Preparing, phases.last())
        assertFalse(phases.contains(AudioPhase.Listening))
        val old = current(); old.triggerOnError(SpeechRecognizer.ERROR_CLIENT)
        old.triggerOnError(SpeechRecognizer.ERROR_CLIENT)
        assertEquals(1, fallback); assertTrue(errors.isEmpty())
        assertEquals(AudioPhase.Idle, phases.last()); assertTrue(old.isDestroyed)
        input.close()
    }
    @Test fun unsupportedLanguageOrServerStartupCanUseSystemDialog() {
        listOf(3, 4, 11, 12, 13).forEach { code ->
            var fallback = 0
            val input = AndroidSpeechInput(context) { fallback++; true }
            input.start({}, { fail(it) }, {}); idle(); current().triggerOnError(code)
            assertEquals("code$code", 1, fallback); input.close()
        }
    }
    @Test fun noMatchNetworkPermissionAndBusyDoNotAutomaticallyRestartMicrophone() {
        listOf(1, 2, 6, 7, 8, 9, 10).forEach { code ->
            var fallback = 0; val errors = mutableListOf<String>()
            val input = AndroidSpeechInput(context) { fallback++; true }
            input.start({}, errors::add, {}); idle(); current().triggerOnError(code)
            assertEquals("code$code", 0, fallback); assertEquals(1, errors.size)
            assertFalse(errors.single().contains("Dịch vụ chưa nhận diện được lời nói"))
            input.close()
        }
    }
    @Test fun afterReadyOrManualStopNeverLaunchesAnotherRecording() {
        var fallback = 0; val phases = mutableListOf<AudioPhase>(); val errors = mutableListOf<String>()
        val input = AndroidSpeechInput(context) { fallback++; true }
        input.start({}, errors::add, phases::add); idle()
        current().triggerOnReadyForSpeech(Bundle())
        assertEquals(AudioPhase.Listening, phases.last())
        input.stop(); current().triggerOnError(SpeechRecognizer.ERROR_CLIENT)
        assertEquals(0, fallback); assertEquals(1, errors.size)
        input.start({}, errors::add, phases::add); idle()
        current().triggerOnReadyForSpeech(Bundle()); current().triggerOnError(SpeechRecognizer.ERROR_AUDIO)
        assertEquals(0, fallback)
        assertTrue(errors.last().contains("micro", ignoreCase = true))
        input.close()
    }
    @Test fun unavailableFallbackShowsSpecificErrorAndAllowsARealRetry() {
        val errors = mutableListOf<String>(); val input = AndroidSpeechInput(context) { false }
        input.start({}, errors::add, {}); idle(); current().triggerOnError(SpeechRecognizer.ERROR_CLIENT)
        assertTrue(errors.single().contains("SR-5"))
        val results = mutableListOf<String>()
        input.start(results::add, errors::add, {}); idle()
        current().triggerOnReadyForSpeech(Bundle())
        current().triggerOnResults(Bundle().apply { putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf("Good morning")) })
        assertEquals(listOf("Good morning"), results); input.close()
    }
    @Test fun cancelledAndReplacedCallbacksCannotChangeTheCurrentAnswer() {
        var fallback = 0; val answers = mutableListOf<String>()
        val input = AndroidSpeechInput(context) { fallback++; true }
        input.start(answers::add, { fail(it) }, {}); idle(); val old = current()
        input.cancel(); input.start(answers::add, { fail(it) }, {}); idle()
        old.triggerOnError(SpeechRecognizer.ERROR_CLIENT)
        old.triggerOnResults(Bundle().apply { putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf("stale")) })
        assertEquals(0, fallback); assertTrue(answers.isEmpty())
        input.close()
    }
    @Test fun timeoutCanRecoverOnlyBeforeReadyAndCancelRemovesTimeout() {
        var fallback = 0; val errors = mutableListOf<String>()
        val input = AndroidSpeechInput(context) { fallback++; true }
        input.start({}, errors::add, {}); idle()
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(26))
        assertEquals(1, fallback); assertTrue(errors.isEmpty())
        input.start({}, errors::add, {}); idle(); current().triggerOnReadyForSpeech(Bundle())
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(26))
        assertEquals(1, fallback); assertEquals(1, errors.size)
        input.start({}, errors::add, {}); idle(); input.cancel()
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(26))
        assertEquals(1, fallback); assertEquals(1, errors.size); input.close()
    }
    @Test fun delayedReadyCallbacksAfterStopKeepProcessingUntilResult() {
        val phases = mutableListOf<AudioPhase>(); val answers = mutableListOf<String>()
        val input = AndroidSpeechInput(context)
        input.start(answers::add, { fail(it) }, phases::add); idle()
        current().triggerOnReadyForSpeech(Bundle()); input.stop()
        assertEquals(AudioPhase.Processing, phases.last())
        current().triggerOnReadyForSpeech(Bundle())
        assertEquals(AudioPhase.Processing, phases.last())
        current().triggerOnResults(Bundle().apply { putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf("Good morning")) })
        assertEquals(listOf("Good morning"), answers); input.close()
    }
}
