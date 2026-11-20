package vn.noi.english.audio

import android.speech.SpeechRecognizer

data class SpeechInputFailure(val message: String, val startupFallback: Boolean = false)

object SpeechInputErrors {
    fun forCode(code: Int): SpeechInputFailure = when (code) {
        SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
            SpeechInputFailure("Chưa nghe rõ câu tiếng Anh. Bạn thử nói lại gần micro hơn, hoặc chọn / gõ để tiếp tục.")
        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
            SpeechInputFailure("Dịch vụ nhận diện chưa kết nối được mạng (SR-$code). Kiểm tra Wi-Fi hoặc dữ liệu di động rồi thử lại.")
        SpeechRecognizer.ERROR_AUDIO ->
            SpeechInputFailure("Không truy cập được micro (SR-$code). Đóng ứng dụng đang dùng micro và kiểm tra quyền micro trong Cài đặt.", true)
        SpeechRecognizer.ERROR_CLIENT ->
            SpeechInputFailure("Bộ nhận diện của máy chưa mở được phiên nghe (SR-$code). Bạn có thể thử nhận diện bằng Android hoặc kiểm tra cài đặt nhận diện.", true)
        SpeechRecognizer.ERROR_SERVER, SpeechRecognizer.ERROR_SERVER_DISCONNECTED ->
            SpeechInputFailure("Dịch vụ nhận diện đang lỗi hoặc bị ngắt kết nối (SR-$code). Thử nhận diện bằng Android hoặc kiểm tra dịch vụ trong Cài đặt.", true)
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
            SpeechInputFailure("Micro chưa được cấp quyền (SR-$code). Cho phép KTalk dùng micro trong Cài đặt, hoặc tiếp tục bằng chọn / gõ.")
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
            SpeechInputFailure("Dịch vụ nhận diện đang bận (SR-$code). Đợi một chút, đóng ứng dụng đang dùng micro rồi thử lại.")
        SpeechRecognizer.ERROR_TOO_MANY_REQUESTS ->
            SpeechInputFailure("Dịch vụ đang nhận quá nhiều yêu cầu (SR-$code). Bạn đợi một chút trước khi thử lại nhé.")
        SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED ->
            SpeechInputFailure("Dịch vụ hiện tại không hỗ trợ tiếng Anh (SR-$code). Thử nhận diện bằng Android hoặc chọn dịch vụ hỗ trợ English trong Cài đặt.", true)
        SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE ->
            SpeechInputFailure("Tiếng Anh chưa sẵn sàng trong dịch vụ nhận diện (SR-$code). Kết nối mạng, cài gói English hoặc thử nhận diện bằng Android.", true)
        else -> SpeechInputFailure("Bộ nhận diện báo lỗi SR-$code. Bạn thử nhận diện bằng Android hoặc kiểm tra cài đặt nhận diện; vẫn có thể chọn / gõ để học.")
    }
}
