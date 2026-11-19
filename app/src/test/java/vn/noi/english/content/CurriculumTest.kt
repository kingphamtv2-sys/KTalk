package vn.noi.english.content

import org.junit.Assert.*
import org.junit.Test
import vn.noi.english.learning.AnswerMatcher

class CurriculumTest {
    @Test fun fortyFiveDistinctLessonsArePlayable() {
        assertEquals(15, Curriculum.topics.size)
        val lessons = Curriculum.topics.flatMap { it.lessons }
        assertEquals(45, lessons.size)
        assertEquals(45, lessons.map { it.id }.toSet().size)
        assertEquals(180, lessons.flatMap { it.turns }.map { it.id }.toSet().size)
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
        assertEquals(180, lessons.flatMap { it.turns }.map { it.prompt }.toSet().size)
    }
    @Test fun oldLessonIdsRemainInOrderForExistingProgress() {
        assertEquals(listOf("greetings-morning", "greetings-wellbeing", "greetings-meeting", "introductions-name", "introductions-country", "introductions-work", "food-drink", "food-meal", "food-bill", "shopping-price", "shopping-size", "shopping-buy", "directions-place", "directions-route", "directions-confirm"), Curriculum.lessons.take(15).map { it.id })
    }
    @Test fun answerChoicesRequireUnderstandingTheCurrentPrompt() {
        val distractors = Curriculum.topics.flatMap { it.lessons }.flatMap { it.turns }
            .flatMap { turn -> turn.choices.filterNot { AnswerMatcher.matches(it, turn) } }
        assertTrue("A single repeated distractor makes every question guessable", distractors.groupingBy { it }.eachCount().values.max() <= 3)
        assertTrue(Curriculum.topics[4].lessons[1].turns[2].choices.contains("Turn right."))
    }
    @Test fun alternativeAnswersDoNotContradictFollowingDialogueFacts() {
        val contradictions = listOf(
            Triple("family-siblings", 0, "I'm an only child."), Triple("family-siblings", 0, "I have one brother."),
            Triple("routine-breaks", 2, "No, thank you."),
            Triple("weather-clothes", 1, "Yes, I have one."), Triple("weather-clothes", 2, "No, thank you. I have one."),
            Triple("weather-picnic", 1, "Let's stay inside."),
            Triple("leisure-hobbies", 0, "I like cooking."), Triple("leisure-hobbies", 0, "I like listening to music."),
            Triple("leisure-weekend", 0, "I stayed at home."), Triple("leisure-weekend", 1, "I went alone."),
            Triple("help-lost-bag", 1, "It's a blue bag."), Triple("home-guest", 2, "Water, please."),
            Triple("hotel-check-out", 2, "Yes, please."),
        )
        contradictions.forEach { (id, index, answer) -> assertFalse("$id: $answer", AnswerMatcher.matches(answer, Curriculum.lesson(id)!!.turns[index])) }
        val call = Curriculum.lesson("phone-call")!!
        assertTrue(AnswerMatcher.matches("Hello, this is Nam.", call.turns[0]))
        assertTrue("Open name slot must not force Linh on the following turns", call.turns.drop(1).none { "Linh" in it.prompt || "Linh" in it.translation })
    }
    @Test fun newLessonsAcceptCommonSpokenAndExpandedForms() {
        listOf(
            Triple("hotel-check-out", 1, "Room two oh five."),
            Triple("hotel-services", 1, "What's the wifi password?"),
            Triple("routine-chores", 1, "Yes, I will take it out."),
            Triple("help-lost-bag", 0, "I have lost my bag."),
            Triple("phone-text", 1, "We are meeting at ten, right?"),
        ).forEach { (id, index, answer) -> assertTrue("$id: $answer", AnswerMatcher.matches(answer, Curriculum.lesson(id)!!.turns[index])) }
    }
    @Test fun newLessonsDoNotGiveAwayAnswersByRepeatingOnePositionPattern() {
        val orders = Curriculum.lessons.drop(15).map { lesson ->
            lesson.turns.map { turn -> turn.choices.indexOfFirst { AnswerMatcher.matches(it, turn) } }
        }
        assertTrue("Every lesson follows the same answer positions", orders.toSet().size > 2)
    }
}
