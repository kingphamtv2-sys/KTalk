package vn.noi.english.content

data class Vocabulary(val english: String, val vietnamese: String)
enum class SlotKind { None, PersonName, PlaceName }
data class Turn(
    val id: String, val prompt: String, val translation: String, val hint: String,
    val sample: String, val explanation: String, val choices: List<String>,
    val accepted: List<String>, val slotPatterns: List<String> = emptyList(),
    val slotKind: SlotKind = SlotKind.None,
)
data class Lesson(
    val id: String, val topicId: String, val title: String, val situation: String,
    val vocabulary: List<Vocabulary>, val turns: List<Turn>,
)
data class Topic(val id: String, val title: String, val lessons: List<Lesson>)
