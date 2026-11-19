package vn.noi.english.audio

class SpeechSession {
    fun start(): Long = 0
    fun isCurrent(token: Long): Boolean = true
    fun cancel() {}
    fun finish(token: Long) {}
}
