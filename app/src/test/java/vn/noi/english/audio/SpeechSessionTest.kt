package vn.noi.english.audio

import org.junit.Assert.*
import org.junit.Test

class SpeechSessionTest {
    @Test fun cancelledAndReplacedCallbacksNeverReachNewTurn() {
        val session = SpeechSession()
        val first = session.start()
        assertTrue(session.isCurrent(first))
        session.cancel()
        assertFalse(session.isCurrent(first))
        val second = session.start()
        assertTrue(session.isCurrent(second))
        assertFalse(session.isCurrent(first))
        session.finish(second)
        assertFalse(session.isCurrent(second))
    }
}
