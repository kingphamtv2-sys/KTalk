package vn.noi.english.audio

import android.content.Context
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowTextToSpeech
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SpeechOutputRecoveryTest {
    @Test fun refreshAfterVoiceInstallationReinitializesEngineAndIgnoresOldCallbacks() {
        val output = AndroidSpeechOutput(ApplicationProvider.getApplicationContext<Context>())
        val oldEngine = ShadowTextToSpeech.getLastTextToSpeechInstance()
        shadowOf(oldEngine).onInitListener.onInit(TextToSpeech.SUCCESS)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(VoiceState.Unavailable, output.status.value)
        ShadowTextToSpeech.addLanguageAvailability(Locale.US)
        output.refresh()
        val newEngine = ShadowTextToSpeech.getLastTextToSpeechInstance()
        assertNotSame(oldEngine, newEngine)
        assertTrue(shadowOf(oldEngine).isShutdown)
        shadowOf(newEngine).onInitListener.onInit(TextToSpeech.SUCCESS)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(VoiceState.Ready, output.status.value)
        output.speak("I need a raincoat.") { fail(it) }
        assertEquals("I need a raincoat.", shadowOf(newEngine).lastSpokenText)
        shadowOf(oldEngine).onInitListener.onInit(TextToSpeech.ERROR)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(VoiceState.Ready, output.status.value)
        output.close(); output.refresh()
        assertSame(newEngine, ShadowTextToSpeech.getLastTextToSpeechInstance())
    }
}
