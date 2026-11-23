package vn.noi.english.learning

import org.junit.Assert.*
import org.junit.Test
import vn.noi.english.content.Curriculum
import vn.noi.english.content.Turn

class AnswerMatcherTest {
    @Test fun commonVietnameseNameAnWorksInEveryPersonalNameSlot() {
        listOf(
            Triple("introductions-name", 0, "My name is An."),
            Triple("greetings-meeting", 0, "Hello! I'm Minh An."),
            Triple("family-photo", 1, "Her name is An."),
            Triple("phone-call", 0, "Hello, this is An."),
        ).forEach { (id, index, answer) ->
            assertTrue("$id: $answer", AnswerMatcher.matches(answer, Curriculum.lesson(id)!!.turns[index]))
        }
        val turn = Curriculum.lesson("introductions-name")!!.turns[0]
        assertFalse(AnswerMatcher.matches("My name is a student.", turn))
        assertFalse(AnswerMatcher.matches("My name is not An.", turn))
    }
    @Test fun fullFormsMatchRealLessonContractionsWithoutDroppingNegation() {
        val song = Curriculum.lessons.flatMap { it.turns }.single { it.sample == "Sure, I'll send you a song." }
        assertTrue(AnswerMatcher.matches("Sure, I will send you a song.", song))
        assertFalse(AnswerMatcher.matches("Sure, I will not send you a song.", song))
        val negative = song.copy(accepted = listOf("I can't come today."), slotPatterns = emptyList())
        assertTrue(AnswerMatcher.matches("I cannot come today.", negative))
        assertTrue(AnswerMatcher.matches("I can not come today.", negative))
        assertFalse(AnswerMatcher.matches("I can come today.", negative))
        val plural = song.copy(accepted = listOf("We're ready. You're welcome."), slotPatterns = emptyList())
        assertTrue(AnswerMatcher.matches("We are ready. You are welcome.", plural))
    }
    @Test fun contractionsRequireAFollowingPredicateButTerminalNegativesAreValid() {
        fun turn(id: String) = Curriculum.lessons.flatMap { it.turns }.single { it.id == id }
        assertFalse(AnswerMatcher.matches("Yes, we're.", turn("time-clock-3")))
        assertFalse(AnswerMatcher.matches("Sure, I'll.", turn("weather-clothes-4")))
        assertFalse(AnswerMatcher.matches("Here you're.", turn("hotel-check-in-3")))
        assertTrue(AnswerMatcher.matches("There's a book inside.", turn("help-lost-bag-3")))
        assertTrue(AnswerMatcher.matches("Thank you. Where's the elevator?", turn("hotel-check-in-4")))
        assertTrue(AnswerMatcher.matches("She's Linh.", turn("family-photo-2")))
        assertFalse(AnswerMatcher.matches("She is not Linh.", turn("family-photo-2")))
        val negative = nameTurn.copy(accepted = listOf("I cannot."), slotPatterns = emptyList())
        assertTrue(AnswerMatcher.matches("I can't.", negative))
        assertFalse(AnswerMatcher.matches("I can.", negative))
    }
    private val nameTurn = Turn("name", "What's your name?", "Bạn tên gì?", "My name is…", "My name is Linh.", "Giới thiệu tên", listOf("My name is Linh."), listOf("My name is Linh."), listOf("(?:my name is|i am) [\\p{L}]+(?: [\\p{L}]+){0,3}"))
    @Test fun acceptsNamesAndContractions() {
        listOf("My name is Ngọc Anh.", "I’m Linh!", "I am Linh", "  MY  NAME IS  LINH. ").forEach {
            assertTrue(it, AnswerMatcher.matches(it, nameTurn))
        }
    }
    @Test fun rejectsMissingNameAndWrongContext() {
        listOf("", "...", "My name is", "I'm", "A coffee, please", "My name is not Linh", "I'm not Linh", "My name is 123", "My name is Linh! A coffee please").forEach {
            assertFalse(it, AnswerMatcher.matches(it, nameTurn))
        }
    }
    @Test fun neverDiscardsNegation() {
        val turn = nameTurn.copy(accepted = listOf("I'm fine, thank you."), slotPatterns = emptyList(), sample = "I'm fine, thank you.")
        assertTrue(AnswerMatcher.matches(turn.sample, turn))
        assertFalse(AnswerMatcher.matches("I'm not fine, thank you.", turn))
    }
    @Test fun curriculumNameSlotsRejectSentenceFragments() {
        val turn = Curriculum.topics[1].lessons[0].turns[0]
        listOf("I'm a student", "I'm from Vietnam", "My name is is", "My name is a", "My name is i am").forEach {
            assertFalse(it, AnswerMatcher.matches(it, turn))
        }
        assertTrue(AnswerMatcher.matches("My name is Ngọc Anh", turn))
    }
    @Test fun internalPunctuationAndSpeechTranscriptsAreEquivalent() {
        val greeting = Curriculum.topics[0].lessons[2].turns[0]
        listOf("Hello! I'm Lan.", "hello i'm lan", "Hi, I am Ngọc Anh.").forEach {
            assertTrue(it, AnswerMatcher.matches(it, greeting))
        }
        val goodbye = Curriculum.topics[3].lessons[2].turns[3]
        assertTrue(AnswerMatcher.matches("thank you goodbye", goodbye))
        assertFalse(AnswerMatcher.matches("thank you not goodbye", goodbye))
    }
}
