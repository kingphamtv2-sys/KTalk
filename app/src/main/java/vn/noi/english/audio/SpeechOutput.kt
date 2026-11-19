package vn.noi.english.audio

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class VoiceState { Initializing, Ready, Unavailable }
interface SpeechOutput {
    val status: StateFlow<VoiceState>
    fun speak(text: String, onError: (String) -> Unit)
    fun stop()
    fun close()
}
class AndroidSpeechOutput(context: Context) : SpeechOutput {
    private val mutable = MutableStateFlow(VoiceState.Initializing)
    override val status: StateFlow<VoiceState> = mutable
    private var engine: TextToSpeech? = null
    private var closed = false
    private val handler = Handler(Looper.getMainLooper())
    private var utteranceId: String? = null
    private var errorCallback: ((String) -> Unit)? = null
    private var sequence = 0L
    init {
        try {
            engine = TextToSpeech(context.applicationContext) { result -> handler.post {
                if (!closed) {
                    val language = if (result == TextToSpeech.SUCCESS) engine?.setLanguage(Locale.US) else null
                    mutable.value = if (language != null && language >= TextToSpeech.LANG_AVAILABLE) VoiceState.Ready else VoiceState.Unavailable
                    engine?.setSpeechRate(0.85f)
                }
            } }
            engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(id: String?) = Unit
                override fun onDone(id: String?) = Unit
                @Deprecated("Android callback")
                override fun onError(id: String?) { handler.post {
                    if (!closed && utteranceId == id) errorCallback?.invoke("Chưa phát được câu mẫu. Hãy kiểm tra giọng tiếng Anh trong cài đặt giọng đọc.")
                } }
            })
        } catch (_: Exception) { mutable.value = VoiceState.Unavailable }
    }
    override fun speak(text: String, onError: (String) -> Unit) {
        if (closed || status.value != VoiceState.Ready) {
            onError(if (status.value == VoiceState.Initializing) "Giọng đọc đang khởi động. Bạn thử lại sau một chút nhé." else "Thiết bị chưa có giọng đọc tiếng Anh. Bạn có thể cài giọng đọc trong phần Cài đặt.")
            return
        }
        stop(); errorCallback = onError
        val id = "noi-${++sequence}"; utteranceId = id
        if (engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, id) == TextToSpeech.ERROR) onError("Chưa phát được câu mẫu. Bạn hãy thử lại hoặc kiểm tra cài đặt giọng đọc.")
    }
    override fun stop() { utteranceId = null; errorCallback = null; engine?.stop() }
    override fun close() { closed = true; stop(); engine?.shutdown(); engine = null }
}
