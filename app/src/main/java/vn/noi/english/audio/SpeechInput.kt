package vn.noi.english.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Build
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import vn.noi.english.learning.AudioPhase

interface SpeechInput {
    val available: Boolean
    fun start(onResult: (String) -> Unit, onError: (String) -> Unit, onPhase: (AudioPhase) -> Unit)
    fun stop()
    fun cancel()
    fun close()
}

class AndroidSpeechInput(context: Context, private val onStartupFailure: (() -> Boolean)? = null) : SpeechInput {
    private val app = context.applicationContext
    private val handler = Handler(Looper.getMainLooper())
    private val session = SpeechSession()
    private var recognizer: SpeechRecognizer? = null
    private var phaseCallback: ((AudioPhase) -> Unit)? = null
    private var timeout: Runnable? = null
    private var ready = false
    private var stopRequested = false
    private var closed = false
    override val available get() = SpeechRecognizer.isRecognitionAvailable(app)

    override fun start(onResult: (String) -> Unit, onError: (String) -> Unit, onPhase: (AudioPhase) -> Unit) {
        cancel()
        if (closed) return
        if (!available) {
            onPhase(AudioPhase.Idle)
            if (!recoverWithSystem()) onError("Máy chưa có dịch vụ nhận diện tương thích. Hãy kiểm tra cài đặt nhận diện, hoặc chọn / gõ để học.")
            return
        }
        val token = session.start()
        ready = false; stopRequested = false
        phaseCallback = onPhase
        fun finish() { cancel() }
        try {
            val speech = SpeechRecognizer.createSpeechRecognizer(app)
            recognizer = speech
            speech.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { if (session.isCurrent(token) && !stopRequested) { ready = true; onPhase(AudioPhase.Listening) } }
                override fun onBeginningOfSpeech() { if (session.isCurrent(token) && !stopRequested) { ready = true; onPhase(AudioPhase.Listening) } }
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() { if (session.isCurrent(token)) { ready = true; onPhase(AudioPhase.Processing) } }
                override fun onPartialResults(partialResults: Bundle?) = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit
                override fun onError(error: Int) {
                    if (!session.isCurrent(token)) return
                    val failure = SpeechInputErrors.forCode(error)
                    val mayRecover = failure.startupFallback && !ready && !stopRequested
                    Log.w("KTalkSpeech", "Recognition error=$error sdk=${Build.VERSION.SDK_INT} ready=$ready stopped=$stopRequested")
                    finish()
                    if (!mayRecover || !recoverWithSystem()) onError(failure.message)
                }
                override fun onResults(results: Bundle?) {
                    if (!session.isCurrent(token)) return
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull { it.isNotBlank() }?.trim()
                    finish()
                    if (text.isNullOrBlank()) onError("Mình chưa nghe rõ. Bạn hãy thử lại nhé.") else onResult(text)
                }
            })
            onPhase(AudioPhase.Preparing)
            speech.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            })
            if (session.isCurrent(token)) timeout = Runnable {
                if (session.isCurrent(token)) {
                    val mayRecover = !ready && !stopRequested
                    finish()
                    if (!mayRecover || !recoverWithSystem()) onError(if (mayRecover)
                        "Micro chưa sẵn sàng sau khi chờ. Thử nhận diện bằng Android hoặc kiểm tra cài đặt nhận diện."
                        else "Phiên nghe đã kết thúc. Bạn có thể nói lại hoặc chuyển sang gõ.")
                }
            }.also { handler.postDelayed(it, 25_000) }
        } catch (_: SecurityException) {
            finish(); onError(SpeechInputErrors.forCode(SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS).message)
        } catch (_: Exception) {
            finish(); if (!recoverWithSystem()) onError("Chưa mở được dịch vụ micro. Thử nhận diện bằng Android hoặc kiểm tra cài đặt nhận diện.")
        }
    }
    private fun recoverWithSystem(): Boolean = try { !closed && onStartupFailure?.invoke() == true } catch (_: Exception) { false }
    override fun stop() {
        if (recognizer == null) return
        stopRequested = true
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
    override fun close() { closed = true; cancel() }
}
