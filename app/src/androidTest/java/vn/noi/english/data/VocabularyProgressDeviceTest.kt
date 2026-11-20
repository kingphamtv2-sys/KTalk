package vn.noi.english.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VocabularyProgressDeviceTest {
    @get:Rule val folder = TemporaryFolder()
    @Test fun wordMarksPersistSeparatelyAndNeverOverwriteConversationProgress() = runTest {
        val conversationFile = folder.newFile("conversation.preferences_pb")
        val vocabularyFile = folder.newFile("words.preferences_pb")
        var job = SupervisorJob()
        var scope = CoroutineScope(Dispatchers.IO + job)
        var conversation = LocalProgressRepository(PreferenceDataStoreFactory.create(scope = scope) { conversationFile })
        var words = LocalVocabularyRepository(PreferenceDataStoreFactory.create(scope = scope) { vocabularyFile })
        conversation.complete("greetings-morning")
        conversation.savePosition("directions-route", 2)
        words.mark("word:good morning", true)
        words.mark("word:good morning", false)
        assertEquals(VocabularyProgress(review = setOf("word:good morning")), words.progress.first())
        assertEquals(Progress(setOf("greetings-morning"), "directions-route", 2), conversation.progress.first())
        job.cancelAndJoin()
        job = SupervisorJob(); scope = CoroutineScope(Dispatchers.IO + job)
        conversation = LocalProgressRepository(PreferenceDataStoreFactory.create(scope = scope) { conversationFile })
        words = LocalVocabularyRepository(PreferenceDataStoreFactory.create(scope = scope) { vocabularyFile })
        assertEquals(VocabularyProgress(review = setOf("word:good morning")), words.progress.first())
        words.mark("word:good morning", true)
        assertEquals(VocabularyProgress(known = setOf("word:good morning")), words.progress.first())
        assertEquals(Progress(setOf("greetings-morning"), "directions-route", 2), conversation.progress.first())
        job.cancelAndJoin()
    }
}
