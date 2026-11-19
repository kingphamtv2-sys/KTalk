package vn.noi.english.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

private val Context.progressStore by preferencesDataStore(name = "learning_progress")

data class Progress(val completedLessonIds: Set<String> = emptySet(), val lessonId: String? = null, val turnIndex: Int = 0)
interface ProgressRepository {
    val progress: Flow<Progress>
    suspend fun savePosition(lessonId: String, turnIndex: Int)
    suspend fun complete(lessonId: String)
}

class LocalProgressRepository internal constructor(private val store: DataStore<Preferences>) : ProgressRepository {
    constructor(context: Context) : this(context.applicationContext.progressStore)
    private val completed = stringSetPreferencesKey("completed_lesson_ids")
    private val current = stringPreferencesKey("current_lesson_id")
    private val index = intPreferencesKey("current_turn_index")
    override val progress = store.data.map { Progress(it[completed] ?: emptySet(), it[current], it[index] ?: 0) }
    override suspend fun savePosition(lessonId: String, turnIndex: Int) {
        store.edit { it[current] = lessonId; it[index] = turnIndex }
    }
    override suspend fun complete(lessonId: String) {
        store.edit {
            it[completed] = (it[completed] ?: emptySet()) + lessonId
            it.remove(current)
            it.remove(index)
        }
    }
}
