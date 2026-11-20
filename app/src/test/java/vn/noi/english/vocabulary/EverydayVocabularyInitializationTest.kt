package vn.noi.english.vocabulary

import org.junit.Assert.assertEquals
import org.junit.Test

class EverydayVocabularyInitializationTest {
    @Test fun standaloneExtendedCatalogCanBeInitializedBeforeCombinedCatalog() {
        assertEquals(165, EverydayVocabulary.cards.size)
        assertEquals(300, VocabularyCatalog.cards.size)
    }
}
