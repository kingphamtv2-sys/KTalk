package vn.noi.english.vocabulary

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random
import vn.noi.english.content.Curriculum
import vn.noi.english.content.Vocabulary

class VocabularyCatalogTest {
    @Test fun repeatedWordsAreMergedWithoutLosingMeaningsOrSources() {
        val cards = VocabularyCatalog.cards
        assertTrue(cards.isNotEmpty())
        assertEquals(300, cards.size)
        assertEquals(cards.size, cards.map { it.id }.toSet().size)
        val morning = cards.single { it.english == "Good morning" }
        assertTrue(morning.meanings.contains("Chào buổi sáng"))
        assertEquals("word:good morning", morning.id)
        assertEquals(morning.id, VocabularyCatalog.wordId("  GOOD   MORNING  "))
        Curriculum.topics.forEach { topic -> topic.lessons.forEach { lesson -> lesson.vocabulary.forEach { word ->
            val card = cards.single { it.id == VocabularyCatalog.wordId(word.english) }
            assertTrue(card.meanings.contains(word.vietnamese))
            assertTrue(card.topicIds.contains(topic.id))
            assertTrue(card.lessonIds.contains(lesson.id))
        } } }
        val source = Curriculum.topics.first()
        val duplicate = source.copy(id = "another-topic", lessons = listOf(source.lessons.first().copy(
            id = "another-lesson", vocabulary = listOf(Vocabulary("  GOOD   MORNING ", "Chào bạn vào buổi sáng")))))
        val merged = VocabularyCatalog.fromTopics(listOf(source, duplicate)).single { it.id == "word:good morning" }
        assertEquals(listOf("Chào buổi sáng", "Chào bạn vào buổi sáng"), merged.meanings)
        assertEquals(setOf("greetings", "another-topic"), merged.topicIds)
        assertEquals(setOf("greetings-morning", "another-lesson"), merged.lessonIds)
    }
    @Test fun quizUsesUniqueQuestionsAndNoOtherMeaningOfTheSameWordAsDistractor() {
        val questions = VocabularyCatalog.quiz(VocabularyCatalog.cards, Random(42))
        assertEquals(5, questions.size)
        assertEquals(5, questions.map { it.card.id }.toSet().size)
        questions.forEach { q ->
            assertEquals(4, q.options.size)
            assertEquals(4, q.options.toSet().size)
            assertEquals(1, q.options.count { it == q.correctMeaning })
            assertTrue(q.options.filter { it != q.correctMeaning }.none { it in q.card.meanings })
        }
        val family = VocabularyCatalog.cards.filter { "family" in it.topicIds }
        assertTrue(VocabularyCatalog.quiz(family, Random(3)).all { "family" in it.card.topicIds })
        assertEquals(1, VocabularyCatalog.quiz(listOf(family.first()), Random(0)).size)
        assertTrue(VocabularyCatalog.quiz(emptyList(), Random(0)).isEmpty())
        VocabularyCatalog.cards.forEach { card ->
            val q = VocabularyCatalog.quiz(listOf(card), Random(0)).single()
            assertEquals("Not enough safe choices for ${card.english}", 4, q.options.size)
        }
    }
    @Test fun quizDoesNotOfferConfusableIdentityOrPaymentTranslations() {
        val cases = listOf(Triple("This is…", "My name is…", 3), Triple("receipt", "the bill", 36))
        cases.forEach { (english, confusable, seed) ->
            val card = VocabularyCatalog.cards.single { it.id == VocabularyCatalog.wordId(english) }
            val other = VocabularyCatalog.cards.single { it.id == VocabularyCatalog.wordId(confusable) }
            val question = VocabularyCatalog.quiz(listOf(card), Random(seed)).single()
            assertTrue("Ambiguous meanings for $english: ${question.options}", question.options.none { it in other.meanings })
        }
    }
    @Test fun expandedCatalogKeepsEveryOldWordAndAddsElevenExamplesPerTopic() {
        val original = VocabularyCatalog.fromTopics(Curriculum.topics)
        assertEquals(135, original.size)
        val originals = original.map { it.id }.toSet()
        val added = VocabularyCatalog.cards.filter { it.id !in originals }
        assertEquals(165, added.size)
        Curriculum.topics.forEach { topic ->
            assertEquals(topic.id, 20, VocabularyCatalog.cards.count { topic.id in it.topicIds })
        }
        added.forEach { card ->
            assertTrue(card.english, card.meanings.all { it.isNotBlank() })
            assertTrue(card.english, card.lessonIds.isEmpty())
            assertEquals(1, card.topicIds.size)
            assertNotNull(card.english, card.example)
            assertTrue(card.example!!.english.isNotBlank())
            assertTrue(card.example.vietnamese.isNotBlank())
        }
        original.forEach { old ->
            val current = VocabularyCatalog.cards.single { it.id == old.id }
            assertEquals(old, current)
        }
    }
    @Test fun everyExpandedCardHasFourDistinctSafeQuizOptionsAcrossSeeds() {
        VocabularyCatalog.cards.forEach { card -> repeat(5) { seed ->
            val q = VocabularyCatalog.quiz(listOf(card), Random(seed)).single()
            assertEquals(card.id, q.card.id)
            assertEquals(4, q.options.toSet().size)
            assertEquals(1, q.options.count { it in card.meanings })
        } }
    }
    @Test fun quizExcludesEquivalentConversationalTranslationsInBothDirections() {
        val pairs = listOf("Could you repeat that?" to "One more time, please", "hold on" to "Wait a moment, please",
            "a seat" to "a chair", "Goodbye" to "See you later")
        pairs.forEach { (first, second) -> listOf(first to second, second to first).forEach { (word, equivalent) ->
            val card = VocabularyCatalog.cards.single { it.english == word }
            val other = VocabularyCatalog.cards.single { it.english == equivalent }
            assertFalse(VocabularyCatalog.distractorCards(card).contains(other))
            repeat(200) { seed ->
                val q = VocabularyCatalog.quiz(listOf(card), Random(seed)).single()
                assertTrue("$word / $equivalent, seed$seed: ${q.options}", q.options.none { it in other.meanings })
            }
        } }
    }
}
