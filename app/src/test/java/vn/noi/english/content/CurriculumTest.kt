package vn.noi.english.content

import org.junit.Assert.*
import org.junit.Test
import vn.noi.english.learning.AnswerMatcher

class CurriculumTest {
    @Test fun learnerMayClarifyThatThePersonalNameTheyGaveWasTheirFullName() {
        val lesson = Curriculum.lesson("introductions-name")!!
        assertTrue(AnswerMatcher.matches("My name is Nguyễn Minh Anh.", lesson.turns[0]))
        assertTrue(AnswerMatcher.matches("No, that's my full name.", lesson.turns[2]))
        assertTrue(AnswerMatcher.matches("Yes, it is.", lesson.turns[2]))
    }
    @Test fun aValidAlternateOrderIsNotReplacedByTheSampleOnTheNextTurn() {
        val drinks = Curriculum.lesson("food-drink")!!
        assertTrue(AnswerMatcher.matches("A tea, please.", drinks.turns[0]))
        assertFalse("Tea order is called coffee", "coffee" in drinks.turns[2].prompt.lowercase())
        assertFalse("Tea order mistranslated", "cà phê" in drinks.turns[2].translation.lowercase())
        val sizes = Curriculum.lesson("shopping-size")!!
        assertTrue(AnswerMatcher.matches("Small, please.", sizes.turns[0]))
        assertTrue(AnswerMatcher.matches("Large, please.", sizes.turns[0]))
        assertFalse("Accepted sizes are replaced by medium", "medium" in sizes.turns[1].prompt.lowercase())
    }
    @Test fun plausibleRepliesAreNotOfferedAsIncorrectChoices() {
        listOf(
            Triple("directions-place", 2, "How much is a train ticket?"),
            Triple("directions-confirm", 2, "How much is a museum ticket?"),
            Triple("family-siblings", 2, "She reads at the library."),
            Triple("help-repeat", 0, "It's on the second floor, right?"),
            Triple("help-repeat", 1, "The first floor, right?"),
            Triple("help-repeat", 2, "The fourth floor, right?"),
        ).forEach { (id, index, answer) ->
            val turn = Curriculum.lesson(id)!!.turns[index]
            assertFalse("Reasonable reply offered as wrong: $id / $answer", answer in turn.choices && !AnswerMatcher.matches(answer, turn))
        }
    }
    @Test fun findingTheBagDoesNotReplaceTheAcceptedDescriptionWithOneBook() {
        val lesson = Curriculum.lesson("help-lost-bag")!!
        assertTrue(AnswerMatcher.matches("There are two books inside.", lesson.turns[2]))
        assertFalse("Two books were changed to a book", "with a book" in lesson.turns[3].prompt.lowercase())
    }
    @Test fun everyLessonHasAUsefulBriefAndAnAccuratePartner() {
        Curriculum.lessons.forEach { lesson ->
            val guide = LessonGuides[lesson.id]
            assertTrue(lesson.id, listOf(guide.objective, guide.tip, guide.learnerRole, guide.partnerRole).all { it.isNotBlank() })
            assertTrue("Speaker count: ${lesson.id}", guide.turnSpeakers.isEmpty() || guide.turnSpeakers.size == 4)
        }
        assertEquals("Lễ tân", LessonGuides["hotel-check-in"].speakerAt(0))
        assertEquals("Tài xế", LessonGuides["transport-taxi"].speakerAt(2))
        assertEquals(listOf("Người nghe máy", "Người nghe máy", "Alex", "Alex"), (0..3).map { LessonGuides["phone-call"].speakerAt(it) })
    }
    @Test fun personalNameIntroductionDoesNotForceTheSampleNameOnTheNextTurn() {
        val lesson = Curriculum.lesson("introductions-name")!!
        assertTrue(AnswerMatcher.matches("My name is Nam.", lesson.turns.first()))
        assertTrue(lesson.turns.drop(1).none { "Linh" in it.prompt || "Linh" in it.translation })
    }
    @Test fun originalLessonsDoNotRevealAnswersThroughAFixedAlternatingPosition() {
        val orders = Curriculum.lessons.take(15).map { lesson ->
            lesson.turns.map { turn -> turn.choices.indexOfFirst { AnswerMatcher.matches(it, turn) } }
        }
        assertTrue("All original lessons use the same answer positions", orders.toSet().size > 2)
    }
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
