package vn.noi.english.content

/** Grammatical replies which miss the question or contradict its stated context. */
internal object Distractors {
    private val byLesson = mapOf(
        "greetings-morning" to listOf("Good night!", "My name is Linh.", "I'm from Vietnam.", "How old are you?"),
        "greetings-wellbeing" to listOf("I'm a student.", "My class starts tomorrow.", "My break starts at twelve.", "I arrived at nine."),
        "greetings-meeting" to listOf("How much is this shirt?", "The class starts at eight.", "I study in the evening.", "I'm from Hanoi."),
        "introductions-name" to listOf("I'm from Vietnam.", "What's your phone number?", "I'm twenty years old.", "My name is Mai."),
        "introductions-country" to listOf("I'm twenty years old.", "I work in the evening.", "No, Vietnam is in Europe.", "What do you do?"),
        "introductions-work" to listOf("I start work at nine.", "My classroom is upstairs.", "I study at the library.", "Where do you live?"),
        "food-drink" to listOf("The bill, please.", "A large cup, please.", "At nine o'clock, please.", "The cafe opens at eight."),
        "food-meal" to listOf("A table for tomorrow, please.", "At twelve o'clock, please.", "A table by the window, please.", "My shirt is too small."),
        "food-bill" to listOf("I arrived at noon.", "What time do you open tomorrow?", "On the table, please.", "I paid by card."),
        "shopping-price" to listOf("I came here by bus.", "What time is it?", "The shop opens at ten.", "I wear a medium."),
        "shopping-size" to listOf("Blue, please.", "I came here yesterday.", "It costs twenty dollars.", "By card, please."),
        "shopping-buy" to listOf("I live near this shop.", "Medium, please.", "In the shopping bag, please.", "What time does class start?"),
        "directions-place" to listOf("The station opens at six.", "At ten o'clock, please.", "My class starts at eight.", "I have two tickets."),
        "directions-route" to listOf("The bank closes at five.", "Turn left first.", "Turn right.", "On the left."),
        "directions-confirm" to listOf("What time does the park close?", "It's next to the station.", "The museum closes at six.", "How much is this shirt?")
    )
    fun forTurn(lessonId: String, turnIndex: Int): String = byLesson.getValue(lessonId)[turnIndex]
}
