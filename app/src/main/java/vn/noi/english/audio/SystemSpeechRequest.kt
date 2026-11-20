package vn.noi.english.audio

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent

sealed interface SystemSpeechResult {
    data class Text(val value: String) : SystemSpeechResult
    data class Failure(val message: String) : SystemSpeechResult
    data object Cancelled : SystemSpeechResult
    data object Empty : SystemSpeechResult
    data object Ignored : SystemSpeechResult
}

/** One external recognition request, bound to the exact lesson turn that launched it. */
class SystemSpeechRequest {
    var pendingTurn: String? = null
        private set
    fun begin(turn: String, automatic: Boolean = false, resumed: Boolean = true, focused: Boolean = true): Intent? {
        if (automatic && (!resumed || !focused)) return null
        if (pendingTurn != null) return null
        pendingTurn = turn
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "KTalk · Hãy nói câu tiếng Anh")
        }
    }
    fun restore(turn: String?) { pendingTurn = turn }
    fun cancel() { pendingTurn = null }
    fun consume(turn: String, eligible: Boolean, resultCode: Int, data: Intent?): SystemSpeechResult {
        val matches = pendingTurn != null && pendingTurn == turn && eligible
        cancel()
        if (!matches) return SystemSpeechResult.Ignored
        if (resultCode == Activity.RESULT_CANCELED) return SystemSpeechResult.Cancelled
        if (resultCode != Activity.RESULT_OK) return SystemSpeechResult.Failure(when (resultCode) {
            RecognizerIntent.RESULT_AUDIO_ERROR -> "Hộp thoại Android không truy cập được micro (SYS-AUDIO). Kiểm tra quyền micro và đóng ứng dụng đang dùng micro."
            RecognizerIntent.RESULT_CLIENT_ERROR -> "Ứng dụng nhận diện Android chưa mở được phiên nghe (SYS-CLIENT). Kiểm tra hoặc cập nhật dịch vụ nhận diện trong Cài đặt."
            RecognizerIntent.RESULT_NETWORK_ERROR -> "Nhận diện của Android chưa kết nối được mạng (SYS-NETWORK). Kiểm tra Wi-Fi hoặc dữ liệu di động rồi thử lại."
            RecognizerIntent.RESULT_SERVER_ERROR -> "Máy chủ nhận diện của Android đang lỗi (SYS-SERVER). Đợi một chút rồi thử lại hoặc dùng chọn / gõ."
            RecognizerIntent.RESULT_NO_MATCH -> "Android chưa nghe rõ câu tiếng Anh. Bạn thử nói lại gần micro hơn, hoặc chọn / gõ để tiếp tục."
            else -> "Ứng dụng nhận diện Android báo lỗi SYS-$resultCode. Bạn có thể kiểm tra cài đặt nhận diện hoặc tiếp tục bằng chọn / gõ."
        })
        val text = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull { it.isNotBlank() }?.trim()
        return if (text.isNullOrEmpty()) SystemSpeechResult.Empty else SystemSpeechResult.Text(text)
    }
}
