package vn.noi.english.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import vn.noi.english.data.LocalProgressRepository
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressDeviceTest {
    @get:Rule val folder = TemporaryFolder()
    @Test fun diskStoreSurvivesRepositoryRecreationAndDeduplicatesCompletedLessons() = runTest {
        val file = folder.newFile("learning.preferences_pb")
        var job = SupervisorJob()
        var repo = LocalProgressRepository(PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + job)) { file })
        repo.savePosition("greetings-morning", 2)
        assertEquals(2, repo.progress.first().turnIndex)
        job.cancelAndJoin()
        job = SupervisorJob()
        repo = LocalProgressRepository(PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + job)) { file })
        assertEquals("greetings-morning", repo.progress.first().lessonId)
        assertEquals(2, repo.progress.first().turnIndex)
        repo.complete("greetings-morning"); repo.complete("greetings-morning")
        assertEquals(setOf("greetings-morning"), repo.progress.first().completedLessonIds)
        assertNull(repo.progress.first().lessonId)
        job.cancelAndJoin()
    }
}
