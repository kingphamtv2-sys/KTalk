package vn.noi.english.content

import org.junit.Assert.*
import org.junit.Test
import vn.noi.english.learning.AnswerMatcher

class CurriculumTest {
    @Test fun fifteenDistinctLessonsArePlayable() {
        assertEquals(5, Curriculum.topics.size)
        val lessons = Curriculum.topics.flatMap { it.lessons }
        assertEquals(15, lessons.size)
        assertEquals(15, lessons.map { it.id }.toSet().size)
        assertEquals(60, lessons.flatMap { it.turns }.map { it.id }.toSet().size)
        lessons.forEach { lesson ->
            assertEquals(3, lesson.vocabulary.size)
            assertEquals(4, lesson.turns.size)
            lesson.turns.forEach { turn ->
                assertTrue(turn.prompt.isNotBlank() && turn.translation.isNotBlank())
                assertTrue("Sample rejected: ${turn.id}", AnswerMatcher.matches(turn.sample, turn))
                turn.accepted.forEach { assertTrue(AnswerMatcher.matches(it, turn)) }
                assertEquals("Choices: ${turn.id}", 1, turn.choices.count { AnswerMatcher.matches(it, turn) })
            }
        }
        assertEquals(60, lessons.flatMap { it.turns }.map { it.prompt }.toSet().size)
    }
    @Test fun answerChoicesRequireUnderstandingTheCurrentPrompt() {
        val distractors = Curriculum.topics.flatMap { it.lessons }.flatMap { it.turns }
            .flatMap { turn -> turn.choices.filterNot { AnswerMatcher.matches(it, turn) } }
        assertTrue("A single repeated distractor makes every question guessable", distractors.groupingBy { it }.eachCount().values.max() <= 3)
        assertTrue(Curriculum.topics[4].lessons[1].turns[2].choices.contains("Turn right."))
    }
}
