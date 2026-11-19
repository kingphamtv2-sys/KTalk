package vn.noi.english.content

object Curriculum {
    private data class Draft(
        val prompt: String, val translation: String, val sample: String, val hint: String,
        val explanation: String, val alternatives: List<String>, val slots: List<String>,
    )
    private fun t(q: String, vi: String, a: String, h: String, e: String,
                  alt: List<String> = emptyList(), slots: List<String> = emptyList()) = Draft(q, vi, a, h, e, alt, slots)
    private fun words(vararg pairs: Pair<String, String>) = pairs.map { Vocabulary(it.first, it.second) }
    private val name = "[\\p{L}]+(?: [\\p{L}]+){0,3}"
    private fun lesson(topic: String, slug: String, title: String, situation: String, vocabulary: List<Vocabulary>, vararg turns: Draft): Lesson {
        val id = "$topic-$slug"
        return Lesson(id, topic, title, situation, vocabulary, turns.mapIndexed { i, d ->
            Turn("$id-${i + 1}", d.prompt, d.translation, d.hint, d.sample, d.explanation,
                if (i % 2 == 0) listOf(d.sample, "I want to buy a spaceship.") else listOf("I want to buy a spaceship.", d.sample),
                listOf(d.sample) + d.alternatives, d.slots)
        })
    }
    val topics = listOf(
        Topic("greetings", "Chào hỏi", listOf(
            lesson("greetings", "morning", "Chào buổi sáng", "Bạn gặp Alex ở quán cà phê vào buổi sáng.",
                words("Good morning" to "Chào buổi sáng", "How are you?" to "Bạn khỏe không?", "Have a nice day" to "Chúc một ngày tốt lành"),
                t("Good morning!", "Chào buổi sáng!", "Good morning!", "Chào lại bằng Good morning.", "Dùng Good morning để chào vào buổi sáng.", listOf("Morning!")),
                t("How are you this morning?", "Sáng nay bạn khỏe không?", "I'm fine, thank you.", "I'm fine + thank you.", "Bạn có thể thêm lời cảm ơn sau khi trả lời.", listOf("I'm good, thanks.", "I'm well, thank you.")),
                t("I'm good too. Ready for a new day?", "Tôi cũng khỏe. Bạn sẵn sàng cho ngày mới chưa?", "Yes, I am.", "Trả lời ngắn: Yes, I am.", "Yes, I am là câu trả lời ngắn cho câu hỏi dùng are you.", listOf("Yes, I'm ready.")),
                t("Have a nice day!", "Chúc bạn một ngày tốt lành!", "You too!", "Đáp lại lời chúc: You too.", "You too có nghĩa là bạn cũng vậy.", listOf("Thank you. You too!", "Thanks, you too!"))),
            lesson("greetings", "wellbeing", "Hỏi thăm đơn giản", "Bạn gặp lại Alex sau giờ học.",
                words("I'm fine" to "Tôi khỏe", "a little tired" to "Hơi mệt", "See you later" to "Hẹn gặp lại"),
                t("Hi! How are you doing?", "Chào! Dạo này bạn thế nào?", "I'm fine, thank you.", "I'm fine + thank you.", "I'm fine là cách trả lời quen thuộc khi được hỏi thăm.", listOf("I'm good, thanks.", "I'm a little tired.")),
                t("Was your day good?", "Ngày hôm nay của bạn tốt chứ?", "Yes, it was.", "Yes + it was.", "Dùng was khi hỏi và trả lời về thời gian đã qua.", listOf("Yes, it was good.")),
                t("Do you want to take a break?", "Bạn có muốn nghỉ một chút không?", "Yes, please.", "Đồng ý lịch sự: Yes, please.", "Yes, please dùng để nhận một lời mời.", listOf("Yes, thank you.")),
                t("Let's talk later. See you!", "Mình nói chuyện sau nhé. Hẹn gặp lại!", "See you later!", "Đáp lại: See you later.", "See you later là cách chào tạm biệt thân mật.", listOf("See you!", "Bye!"))),
            lesson("greetings", "meeting", "Gặp người mới", "Bạn gặp Alex lần đầu tại lớp học.",
                words("Hello" to "Xin chào", "Nice to meet you" to "Rất vui được gặp bạn", "classmate" to "Bạn cùng lớp"),
                t("Hello! I'm Alex.", "Xin chào! Tôi là Alex.", "Hello! I'm Linh.", "Hello + I'm + tên.", "Dùng I'm để giới thiệu tên một cách tự nhiên.", slots=listOf("hello i am $name", "hi i am $name")),
                t("Nice to meet you!", "Rất vui được gặp bạn!", "Nice to meet you too!", "Nice to meet you + too.", "Thêm too để đáp lại lời chào làm quen.", listOf("Nice to meet you, too.", "Nice to meet you.")),
                t("Are you new to this class?", "Bạn mới đến lớp này phải không?", "Yes, I am.", "Yes + I am.", "Câu trả lời ngắn giúp bạn tự tin bắt đầu trò chuyện.", listOf("Yes, I'm new here.")),
                t("Welcome! Shall we sit together?", "Chào mừng bạn! Chúng ta ngồi cùng nhé?", "Yes, thank you.", "Đồng ý và cảm ơn.", "Thank you thể hiện sự lịch sự khi nhận giúp đỡ.", listOf("Yes, please.", "Sure, thank you.")))
        )),
        Topic("introductions", "Giới thiệu bản thân", listOf(
            lesson("introductions", "name", "Tên của bạn", "Bạn giới thiệu tên với Alex và hỏi lại tên bạn ấy.",
                words("My name is…" to "Tên tôi là…", "What's your name?" to "Bạn tên gì?", "spell" to "Đánh vần"),
                t("What's your name?", "Bạn tên gì?", "My name is Linh.", "My name is + tên của bạn.", "Bạn cũng có thể giới thiệu bằng I'm + tên.", slots=listOf("(?:my name is|i am) $name")),
                t("Could you ask me my name too?", "Bạn có thể hỏi lại tên tôi không?", "What's your name?", "Hỏi: What's your name?", "What's là dạng viết tắt của what is.", listOf("What is your name?")),
                t("I'm Alex. Is Linh your first name?", "Tôi là Alex. Linh là tên của bạn đúng không?", "Yes, it is.", "Yes + it is.", "Dùng it để nhắc lại tên đang nói đến.", listOf("Yes, Linh is my first name.")),
                t("Thanks for introducing yourself!", "Cảm ơn bạn đã giới thiệu bản thân!", "You're welcome.", "Đáp lại lời cảm ơn: You're welcome.", "You're welcome là cách đáp lịch sự sau lời cảm ơn.", listOf("You are welcome.", "No problem."))),
            lesson("introductions", "country", "Bạn đến từ đâu?", "Bạn nói về quê hương Việt Nam với Alex.",
                words("I'm from…" to "Tôi đến từ…", "Vietnam" to "Việt Nam", "live in" to "Sống ở"),
                t("Which country are you from?", "Bạn đến từ quốc gia nào?", "I'm from Vietnam.", "I'm from + tên quốc gia.", "From cho biết bạn đến từ đâu.", listOf("I am from Viet Nam.")),
                t("Which city do you live in?", "Bạn đang sống ở thành phố nào?", "I live in Hanoi.", "I live in + tên thành phố.", "Live in nói về nơi bạn đang sống.", slots=listOf("i live in $name")),
                t("Is Vietnam in Asia?", "Việt Nam thuộc châu Á đúng không?", "Yes, it is.", "Yes + it is.", "Dùng câu trả lời ngắn để xác nhận thông tin."),
                t("Would you like to ask where I'm from?", "Bạn có muốn hỏi tôi đến từ đâu không?", "Where are you from?", "Where + are you + from?", "Đây là câu hỏi tự nhiên khi làm quen.")),
            lesson("introductions", "work", "Công việc của bạn", "Bạn nói về việc học và công việc với Alex.",
                words("student" to "Học sinh / sinh viên", "work" to "Làm việc", "study English" to "Học tiếng Anh"),
                t("What do you do?", "Bạn làm nghề gì?", "I'm a student.", "I'm a/an + nghề nghiệp.", "Dùng a hoặc an trước nghề nghiệp số ít.", listOf("I'm a teacher.", "I'm an engineer.", "I'm an office worker.")),
                t("Do you study English?", "Bạn có học tiếng Anh không?", "Yes, I do.", "Yes + I do.", "Dùng do để trả lời ngắn cho câu hỏi Do you.", listOf("Yes, I study English.")),
                t("When do you study?", "Bạn học khi nào?", "I study in the evening.", "I study + in the evening.", "In the evening nghĩa là vào buổi tối.", listOf("I study in the morning.", "I study every day.")),
                t("How can you ask about my job?", "Bạn có thể hỏi công việc của tôi thế nào?", "What do you do?", "What + do you + do?", "Đây là câu hỏi về nghề nghiệp, không phải việc đang làm lúc này."))
        )),
        Topic("food", "Gọi món", listOf(
            lesson("food", "drink", "Gọi đồ uống", "Bạn gọi đồ uống tại một quán cà phê.",
                words("a coffee" to "Một cốc cà phê", "please" to "Làm ơn / vui lòng", "no sugar" to "Không đường"),
                t("What would you like to drink?", "Bạn muốn uống gì?", "A coffee, please.", "A + đồ uống + please.", "Thêm please để gọi đồ một cách lịch sự.", listOf("I'd like a coffee, please.", "A tea, please.")),
                t("Would you like it hot or iced?", "Bạn muốn đồ nóng hay có đá?", "Iced, please.", "Chọn: Iced, please / Hot, please.", "Iced là có đá; hot là nóng.", listOf("Hot, please.")),
                t("Would you like sugar in your coffee?", "Bạn có muốn thêm đường vào cà phê không?", "No sugar, please.", "No sugar + please.", "No sugar có nghĩa là không đường.", listOf("No, thank you.", "Yes, please.")),
                t("Is that all for your drinks order?", "Đồ uống của bạn chỉ có vậy thôi phải không?", "That's all, thank you.", "That's all + thank you.", "That's all báo rằng bạn đã gọi xong.", listOf("Yes, that's all.", "That is all, thank you."))),
            lesson("food", "meal", "Chọn món ăn", "Bạn chọn bữa trưa tại một nhà hàng nhỏ.",
                words("the menu" to "Thực đơn", "chicken rice" to "Cơm gà", "not spicy" to "Không cay"),
                t("Hello! Are you ready to order lunch?", "Xin chào! Bạn sẵn sàng gọi bữa trưa chưa?", "Can I see the menu, please?", "Can I see + the menu + please?", "Dùng Can I… để yêu cầu lịch sự.", listOf("The menu, please.")),
                t("Here is the menu. What would you like to eat?", "Đây là thực đơn. Bạn muốn ăn gì?", "I'd like chicken rice, please.", "I'd like + món ăn + please.", "I'd like là cách lịch sự để gọi món.", listOf("Chicken rice, please.")),
                t("Would you like your food spicy?", "Bạn có muốn món ăn cay không?", "Not spicy, please.", "Not spicy + please.", "Not spicy giúp nói rõ rằng bạn không ăn cay.", listOf("No, not spicy, please.", "No, thank you.")),
                t("Would you like anything else with your meal?", "Bạn có muốn thêm gì với bữa ăn không?", "No, thank you.", "Từ chối lịch sự: No, thank you.", "Cảm ơn ngay cả khi bạn từ chối lời mời.", listOf("That's all, thank you."))),
            lesson("food", "bill", "Xin thanh toán", "Bạn muốn thanh toán sau khi ăn.",
                words("the bill" to "Hóa đơn", "by card" to "Bằng thẻ", "receipt" to "Biên lai"),
                t("Did you enjoy your meal?", "Bạn có thích bữa ăn không?", "Yes, thank you.", "Yes + thank you.", "Đây là cách đáp ngắn gọn và lịch sự.", listOf("Yes, it was delicious.")),
                t("Is there anything I can bring you now?", "Bây giờ tôi có thể mang gì cho bạn?", "The bill, please.", "The bill + please.", "Bill là hóa đơn cần thanh toán.", listOf("Can I have the bill, please?")),
                t("Would you like to pay by cash or card?", "Bạn muốn trả bằng tiền mặt hay thẻ?", "By card, please.", "By card / In cash + please.", "By card là trả thẻ, in cash là trả tiền mặt.", listOf("In cash, please.")),
                t("Would you like a receipt?", "Bạn có muốn lấy biên lai không?", "Yes, please.", "Yes, please.", "Receipt là giấy xác nhận đã thanh toán.", listOf("No, thank you.")))
        )),
        Topic("shopping", "Mua sắm", listOf(
            lesson("shopping", "price", "Hỏi giá", "Bạn hỏi giá một chiếc áo trong cửa hàng.",
                words("How much…?" to "Bao nhiêu tiền?", "this T-shirt" to "Chiếc áo phông này", "expensive" to "Đắt"),
                t("Welcome! Which item do you like?", "Xin chào! Bạn thích món nào?", "This T-shirt, please.", "This + món đồ + please.", "This chỉ một món đồ ở gần bạn.", listOf("I like this T-shirt.")),
                t("What would you like to know about the T-shirt?", "Bạn muốn biết gì về chiếc áo phông?", "How much is it?", "How much + is it?", "Dùng How much is it để hỏi giá một món đồ.", listOf("How much is this T-shirt?")),
                t("It's twenty dollars. Is that OK?", "Giá là hai mươi đô. Có được không?", "That's a little expensive.", "That's + a little expensive.", "A little expensive có nghĩa là hơi đắt.", listOf("That is a little expensive.", "Yes, that's OK.")),
                t("Would you like to look around first?", "Bạn có muốn xem thêm trước không?", "Yes, thank you.", "Yes + thank you.", "Bạn có thể xem thêm trước khi quyết định mua.", listOf("Yes, please."))),
            lesson("shopping", "size", "Chọn kích cỡ", "Bạn tìm một chiếc áo vừa cỡ và thử áo.",
                words("medium" to "Cỡ vừa", "try on" to "Thử đồ", "fits well" to "Vừa vặn"),
                t("What size do you need?", "Bạn cần cỡ nào?", "Medium, please.", "Cỡ áo + please.", "Small, medium, large lần lượt là nhỏ, vừa, lớn.", listOf("Small, please.", "Large, please.")),
                t("Here is a medium. Would you like to try it on?", "Đây là cỡ vừa. Bạn có muốn thử không?", "Yes, please.", "Yes + please.", "Try it on nghĩa là mặc thử món đồ."),
                t("How does the T-shirt fit?", "Chiếc áo phông có vừa không?", "It fits well.", "It + fits well.", "It fits well có nghĩa là chiếc áo vừa vặn.", listOf("It's too small.", "It's too big.")),
                t("Do you want a different size?", "Bạn có muốn đổi cỡ khác không?", "No, this is fine.", "No + this is fine.", "This is fine giúp xác nhận món hiện tại đã phù hợp.", listOf("Yes, a large, please.", "No, thank you."))),
            lesson("shopping", "buy", "Mua món đồ", "Bạn chọn một chiếc túi và thanh toán.",
                words("I'll take it" to "Tôi sẽ lấy món này", "a bag" to "Một chiếc túi", "pay" to "Thanh toán"),
                t("Have you decided on this bag?", "Bạn đã quyết định chọn chiếc túi này chưa?", "I'll take it.", "I'll take it.", "Dùng I'll take it khi quyết định mua món đang xem.", listOf("I will take it.", "Yes, I'll take it.")),
                t("Which color would you like?", "Bạn muốn màu nào?", "Blue, please.", "Màu sắc + please.", "Blue là xanh lam; black là đen.", listOf("Black, please.", "Red, please.")),
                t("How would you like to pay for the bag?", "Bạn muốn thanh toán chiếc túi thế nào?", "By card, please.", "By card + please.", "Nói by card nếu bạn muốn dùng thẻ.", listOf("In cash, please.")),
                t("Here is your bag. Thank you for shopping!", "Đây là túi của bạn. Cảm ơn đã mua hàng!", "Thank you. Goodbye!", "Cảm ơn và chào tạm biệt.", "Kết thúc giao dịch bằng một lời cảm ơn.", listOf("Thanks, goodbye!", "Thank you!")))
        )),
        Topic("directions", "Hỏi đường", listOf(
            lesson("directions", "place", "Tìm địa điểm", "Bạn tìm nhà ga và hỏi Alex trên đường.",
                words("Excuse me" to "Xin lỗi / làm phiền", "the station" to "Nhà ga", "near here" to "Gần đây"),
                t("Hello! Do you need help finding a place?", "Xin chào! Bạn cần giúp tìm một nơi phải không?", "Yes, please.", "Yes + please.", "Nhận lời giúp đỡ bằng câu trả lời lịch sự."),
                t("Where would you like to go?", "Bạn muốn đi đâu?", "The station, please.", "The + địa điểm + please.", "The station là nhà ga.", listOf("I'd like to go to the station.", "Where is the station?")),
                t("The station is on King Street. What else would you like to ask?", "Nhà ga ở phố King. Bạn muốn hỏi gì thêm?", "Is it near here?", "Is it + near here?", "Near here có nghĩa là gần chỗ này.", listOf("Is the station near here?")),
                t("Yes, it's nearby. Can you walk there?", "Có, nó ở gần đây. Bạn có thể đi bộ đến đó không?", "Yes, I can.", "Yes + I can.", "Can được dùng để nói về khả năng.", listOf("Yes, thank you."))),
            lesson("directions", "route", "Hỏi hướng đi", "Bạn hỏi cách đi bộ từ đây tới ngân hàng.",
                words("go straight" to "Đi thẳng", "turn left" to "Rẽ trái", "on the right" to "Ở bên phải"),
                t("Excuse me, are you looking for the bank?", "Xin lỗi, bạn đang tìm ngân hàng phải không?", "Yes, how do I get there?", "Yes + how do I get there?", "How do I get there hỏi cách đến một nơi.", listOf("Yes, where is the bank?")),
                t("Go straight. Could you repeat the first step?", "Đi thẳng. Bạn có thể nhắc lại bước đầu không?", "Go straight.", "Nhắc lại: Go straight.", "Nhắc lại chỉ dẫn giúp bạn xác nhận đã nghe đúng."),
                t("Then turn left. Which way should you turn?", "Sau đó rẽ trái. Bạn nên rẽ hướng nào?", "Turn left.", "Turn + left.", "Left là trái; right là phải.", listOf("I should turn left.")),
                t("The bank is on the right. Where is it?", "Ngân hàng ở bên phải. Nó nằm ở đâu?", "On the right.", "On the + right.", "On the right nói vị trí ở bên phải.", listOf("It's on the right.", "The bank is on the right."))),
            lesson("directions", "confirm", "Xác nhận đường đi", "Bạn xác nhận lại đường tới bảo tàng trước khi đi.",
                words("Could you repeat that?" to "Bạn nhắc lại được không?", "next to" to "Bên cạnh", "Thank you for your help" to "Cảm ơn bạn đã giúp đỡ"),
                t("The museum is next to the park. Did you hear me?", "Bảo tàng ở cạnh công viên. Bạn nghe rõ chứ?", "Could you repeat that, please?", "Could you repeat that + please?", "Dùng câu này khi bạn chưa nghe rõ lời chỉ dẫn.", listOf("Can you repeat that, please?")),
                t("It's next to the park. Can you confirm the location?", "Nó ở bên cạnh công viên. Bạn xác nhận lại vị trí nhé?", "It's next to the park.", "It's + next to + the park.", "Next to có nghĩa là ở bên cạnh.", listOf("The museum is next to the park.")),
                t("It takes five minutes. Would you like to check the time?", "Mất năm phút. Bạn có muốn hỏi lại thời gian không?", "Is it a five-minute walk?", "Is it + a five-minute walk?", "A five-minute walk nghĩa là đi bộ mất năm phút.", listOf("Does it take five minutes?")),
                t("Yes, five minutes on foot. You're all set!", "Đúng, đi bộ năm phút. Bạn biết đường rồi nhé!", "Thank you for your help!", "Thank you + for your help.", "Cảm ơn cụ thể sự giúp đỡ của người chỉ đường.", listOf("Thanks for your help.", "Thank you!")))
        ))
    )
    val lessons: List<Lesson> get() = topics.flatMap { it.lessons }
    fun lesson(id: String?): Lesson? = lessons.find { it.id == id }
}
