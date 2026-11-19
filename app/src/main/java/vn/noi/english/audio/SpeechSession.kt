package vn.noi.english.audio

class SpeechSession {
    private var sequence = 0L
    private var active: Long? = null
    fun start(): Long = (++sequence).also { active = it }
    fun isCurrent(token: Long): Boolean = active == token
    fun cancel() { active = null }
    fun finish(token: Long) { if (isCurrent(token)) active = null }
}
