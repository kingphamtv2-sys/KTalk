package vn.noi.english.content

/** Intentionally unsuitable answers, authored for each prompt rather than one giveaway option. */
internal object Distractors {
    private val byLesson = mapOf(
        "greetings-morning" to listOf("Good night!", "It is twenty dollars.", "Yes, it does.", "I am twenty years old."),
        "greetings-wellbeing" to listOf("My name is Monday.", "Yes, it is tomorrow.", "I am a break.", "See you yesterday!"),
        "greetings-meeting" to listOf("Hello! My is name Linh.", "Nice to price you!", "Yes, I do new.", "How much is this seat?"),
        "introductions-name" to listOf("My name are Linh.", "How much is your name?", "Yes, they are.", "You're welcome me."),
        "introductions-country" to listOf("I am from a student.", "I live on Vietnamese.", "No, Vietnam is in Europe.", "What do you from?"),
        "introductions-work" to listOf("I am twenty dollars.", "Yes, I am English.", "I study on the evening.", "Where do you job?"),
        "food-drink" to listOf("A menu to wear, please.", "Medium size, please.", "Two left turns, please.", "I live near this cafe."),
        "food-meal" to listOf("Can I wear the menu?", "I would chicken like rice.", "Turn right, please.", "A different shirt, please."),
        "food-bill" to listOf("My meal is from Hanoi.", "Can I have the size, please?", "By foot, please.", "Yes, I am a receipt."),
        "shopping-price" to listOf("I like this yesterday.", "How many is this T-shirt?", "It is on the right.", "Yes, I am looking yesterday."),
        "shopping-size" to listOf("I need twenty dollars size.", "Yes, turn it left.", "It fits yesterday.", "The station, thank you."),
        "shopping-buy" to listOf("I'll takes it.", "Tomorrow, please.", "By walk, please.", "Thank your goodbye."),
        "directions-place" to listOf("Yes, I am a place.", "I'd like the bill, please.", "How much is near here?", "Yes, I walk can."),
        "directions-route" to listOf("Yes, how many is the bank?", "Go sugar.", "Turn right.", "On the expensive."),
        "directions-confirm" to listOf("Could you receipt that, please?", "It is inside the coffee.", "Is it a five-dollar walk?", "Thank you for your price.")
    )
    fun forTurn(lessonId: String, turnIndex: Int): String = byLesson.getValue(lessonId)[turnIndex]
}
