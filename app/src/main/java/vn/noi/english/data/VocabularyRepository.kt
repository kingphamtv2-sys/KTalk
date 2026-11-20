package vn.noi.english.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private val Context.vocabularyStore by preferencesDataStore(name = "vocabulary_progress")

data class VocabularyProgress(val known: Set<String> = emptySet(), val review: Set<String> = emptySet())
interface VocabularyRepository {
    val progress: Flow<VocabularyProgress>
    suspend fun mark(wordId: String, known: Boolean)
}

class LocalVocabularyRepository internal constructor(private val store: DataStore<Preferences>) : VocabularyRepository {
    constructor(context: Context) : this(context.applicationContext.vocabularyStore)
    private val knownKey = stringSetPreferencesKey("known_word_ids")
    private val reviewKey = stringSetPreferencesKey("review_word_ids")
    override val progress = store.data.map { VocabularyProgress(it[knownKey] ?: emptySet(), it[reviewKey] ?: emptySet()) }
    override suspend fun mark(wordId: String, known: Boolean) {
        store.edit {
            val knownIds = it[knownKey] ?: emptySet()
            val reviewIds = it[reviewKey] ?: emptySet()
            it[knownKey] = if (known) knownIds + wordId else knownIds - wordId
            it[reviewKey] = if (known) reviewIds - wordId else reviewIds + wordId
        }
    }
}
