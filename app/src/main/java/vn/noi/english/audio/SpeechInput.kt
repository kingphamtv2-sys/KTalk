package vn.noi.english.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import vn.noi.english.learning.AudioPhase

interface SpeechInput {
    val available: Boolean
    fun start(onResult: (String) -> Unit, onError: (String) -> Unit, onPhase: (AudioPhase) -> Unit)
    fun stop()
    fun cancel()
    fun close()
}

class AndroidSpeechInput(context: Context) : SpeechInput {
    private val app = context.applicationContext
    private val handler = Handler(Looper.getMainLooper())
    private val session = SpeechSession()
    private var recognizer: SpeechRecognizer? = null
    private var phaseCallback: ((AudioPhase) -> Unit)? = null
    private var timeout: Runnable? = null
    override val available get() = SpeechRecognizer.isRecognitionAvailable(app)

    override fun start(onResult: (String) -> Unit, onError: (String) -> Unit, onPhase: (AudioPhase) -> Unit) {
        cancel()
        if (!available) { onError("Thiết bị chưa có dịch vụ nhận diện giọng nói. Bạn vẫn có thể chọn hoặc gõ câu trả lời."); return }
        val token = session.start()
        phaseCallback = onPhase
        fun finish() { cancel() }
        try {
            val speech = SpeechRecognizer.createSpeechRecognizer(app)
            recognizer = speech
            speech.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { if (session.isCurrent(token)) onPhase(AudioPhase.Listening) }
                override fun onBeginningOfSpeech() = Unit
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() { if (session.isCurrent(token)) onPhase(AudioPhase.Processing) }
                override fun onPartialResults(partialResults: Bundle?) = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit
                override fun onError(error: Int) {
                    if (!session.isCurrent(token)) return
                    finish()
                    onError(when(error) {
                        SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Mình chưa nghe rõ. Bạn hãy nói lại hoặc chuyển sang gõ nhé."
                        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Chưa kết nối được dịch vụ nhận diện. Kiểm tra mạng hoặc chọn / gõ để tiếp tục."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Micro chưa được cấp quyền. Bạn có thể chọn hoặc gõ câu trả lời."
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Dịch vụ giọng nói đang bận. Bạn hãy thử lại sau một chút."
                        else -> "Dịch vụ chưa nhận diện được lời nói. Hãy thử lại hoặc chọn / gõ."
                    })
                }
                override fun onResults(results: Bundle?) {
                    if (!session.isCurrent(token)) return
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    finish()
                    if (text.isNullOrBlank()) onError("Mình chưa nghe rõ. Bạn hãy thử lại nhé.") else onResult(text)
                }
            })
            onPhase(AudioPhase.Listening)
            speech.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            })
            timeout = Runnable {
                if (session.isCurrent(token)) { finish(); onError("Phiên nghe đã kết thúc. Bạn có thể nói lại hoặc chuyển sang gõ.") }
            }.also { handler.postDelayed(it, 25_000) }
        } catch (_: Exception) { finish(); onError("Chưa mở được micro. Bạn hãy thử lại hoặc chuyển sang gõ.") }
    }
    override fun stop() {
        try { recognizer?.stopListening(); phaseCallback?.invoke(AudioPhase.Processing) }
        catch (_: Exception) { cancel() }
    }
    override fun cancel() {
        session.cancel()
        timeout?.let { handler.removeCallbacks(it) }; timeout = null
        val previous = recognizer; recognizer = null
        try { previous?.cancel(); previous?.destroy() } catch (_: Exception) { /* Already released by service. */ }
        phaseCallback?.invoke(AudioPhase.Idle); phaseCallback = null
    }
    override fun close() = cancel()
}
