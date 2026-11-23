package vn.noi.english.content

import vn.noi.english.content.LessonContent.lesson
import vn.noi.english.content.LessonContent.turn as t
import vn.noi.english.content.LessonContent.words

internal object EverydayTopics {
    val topics = listOf(
        Topic("family", "Gia đình", listOf(
            lesson("family", "photo", "Giới thiệu người thân", "Bạn cho Alex xem ảnh gia đình và giới thiệu mẹ của mình.",
                words("my mother" to "Mẹ của tôi", "my father" to "Bố của tôi", "family photo" to "Ảnh gia đình"),
                t("Who is this woman in your family photo?", "Người phụ nữ này trong ảnh gia đình bạn là ai?", "This is my mother.", "This is + my mother.", "Dùng This is… để giới thiệu một người trong ảnh.", "This is my father.", listOf("She's my mother.", "This is my mom.")),
                t("What's your mother's name?", "Mẹ bạn tên gì?", "Her name is Mai.", "Her name is + tên.", "Her dùng khi nói về tên của một người phụ nữ.", "She lives with me.", listOf("Her name is Lan.", "She's Mai.")).copy(slotPatterns = listOf("(?:her name is|she is) ([\\p{L}]+(?: [\\p{L}]+){0,3})"), slotKind = SlotKind.PersonName),
                t("Does your mother live with you?", "Mẹ bạn có sống cùng bạn không?", "Yes, she does.", "Yes, she does / No, she doesn't.", "Với câu hỏi Does she…, dùng does hoặc doesn't trong câu trả lời ngắn.", "Her name is Mai.", listOf("No, she doesn't.", "Yes, she lives with me.")),
                t("You have a lovely family photo!", "Bạn có một bức ảnh gia đình thật đẹp!", "Thank you. I love my family.", "Thank you + I love my family.", "Bạn có thể cảm ơn và chia sẻ tình cảm với gia đình.", "What time is lunch?", listOf("Thank you!", "Thanks!"))),
            lesson("family", "siblings", "Hỏi về anh chị em", "Trong bài này, bạn kể với Alex về một người chị hoặc em gái của mình.",
                words("brother" to "Anh / em trai", "sister" to "Chị / em gái", "only child" to "Con một"),
                t("Do you have any brothers or sisters?", "Bạn có anh chị em không?", "I have one sister.", "Trong bài này: I have one sister.", "One sister là một chị hoặc em gái; không thêm s sau sister. Bài này luyện tình huống có một chị hoặc em gái.", "My father is a teacher.", listOf("I have a sister.", "I have one younger sister.")),
                t("Is your sister older or younger than you?", "Chị/em gái của bạn lớn hay nhỏ tuổi hơn bạn?", "She's younger than me.", "She's older/younger than me.", "Older là lớn tuổi hơn, younger là nhỏ tuổi hơn.", "She's a student.", listOf("She's older than me.", "She is younger than me.")),
                t("What does your sister like doing?", "Chị/em gái của bạn thích làm gì?", "She likes reading.", "She likes + hoạt động dạng -ing.", "Dùng likes với she; reading có nghĩa là đọc sách.", "I visited my sister last week.", listOf("She likes cooking.", "She likes swimming.")),
                t("Do you spend time with your sister on weekends?", "Bạn có dành thời gian cho chị/em gái vào cuối tuần không?", "Yes, we watch movies together.", "Yes, we + hoạt động + together.", "Together nghĩa là cùng nhau.", "My sister is twenty years old.", listOf("Yes, we cook together.", "Yes, we do."))),
            lesson("family", "weekend", "Kể về gia đình", "Bạn kể với Alex về một buổi sum họp gia đình vào Chủ nhật.",
                words("visit my parents" to "Thăm bố mẹ", "have lunch together" to "Ăn trưa cùng nhau", "on Sunday" to "Vào Chủ nhật"),
                t("When do you usually visit your parents?", "Bạn thường thăm bố mẹ khi nào?", "I visit them on Sunday.", "I visit them + on Sunday.", "Dùng on trước tên một ngày trong tuần.", "My parents live near a park.", listOf("I visit them on weekends.", "On Sunday.")),
                t("What do you do at your parents' home?", "Bạn làm gì ở nhà bố mẹ?", "We have lunch together.", "We + have lunch + together.", "Have lunch nghĩa là ăn trưa.", "My parents are teachers.", listOf("We cook together.", "We talk and have lunch.")),
                t("Who cooks lunch for your family?", "Ai nấu bữa trưa cho gia đình bạn?", "My father cooks lunch.", "My father/mother + cooks lunch.", "Thêm s vào cook với chủ ngữ ngôi thứ ba số ít như he, she hoặc my father.", "We have lunch at noon.", listOf("My mother cooks lunch.", "We cook together.")),
                t("That sounds nice. Do you enjoy those visits?", "Nghe thật vui. Bạn có thích những lần thăm nhà đó không?", "Yes, I enjoy them a lot.", "Yes, I enjoy them + a lot.", "A lot giúp nhấn mạnh rằng bạn rất thích những lần gặp gỡ ấy.", "My father works in Hanoi.", listOf("Yes, I do.", "Yes, I love visiting my parents.")))
        )),
        Topic("routine", "Sinh hoạt hằng ngày", listOf(
            lesson("routine", "morning", "Buổi sáng của bạn", "Alex hỏi bạn về thói quen vào mỗi buổi sáng.",
                words("wake up" to "Thức dậy", "have breakfast" to "Ăn sáng", "go to work" to "Đi làm"),
                t("What time do you usually wake up?", "Bạn thường thức dậy lúc mấy giờ?", "I wake up at six.", "I wake up + at + giờ.", "Dùng at trước giờ cụ thể.", "I wake up in my bedroom.", listOf("I wake up at 6.", "I wake up at seven.", "I wake up at 7.")),
                t("What do you usually have for breakfast?", "Bạn thường ăn gì vào bữa sáng?", "I have bread and eggs.", "I have + đồ ăn.", "Have có thể dùng với nghĩa ăn trong ngữ cảnh bữa ăn.", "I eat at seven o'clock.", listOf("I have noodles.", "I have rice and eggs.")),
                t("How do you get to work in the morning?", "Buổi sáng bạn đi làm bằng cách nào?", "I go by bus.", "I go + by bus/motorbike.", "By bus nghĩa là bằng xe buýt; không thêm a sau by.", "I work at the airport.", listOf("I go by motorbike.", "I walk to work.")),
                t("What time does your workday start?", "Ngày làm việc của bạn bắt đầu lúc mấy giờ?", "It starts at eight.", "It starts + at + giờ.", "Starts có s vì chủ ngữ là it.", "I work at a small office.", listOf("It starts at 8.", "It starts at nine.", "It starts at 9."))),
            lesson("routine", "breaks", "Giờ ăn và nghỉ", "Bạn nói chuyện với một đồng nghiệp về bữa trưa và giờ nghỉ.",
                words("lunch break" to "Giờ nghỉ trưa", "take a break" to "Nghỉ một chút", "drink water" to "Uống nước"),
                t("When is your lunch break?", "Giờ nghỉ trưa của bạn là khi nào?", "It's at twelve.", "It's + at + giờ.", "At twelve nghĩa là lúc mười hai giờ.", "I eat in the office.", listOf("It's at 12.", "At noon.")),
                t("Where do you usually eat lunch?", "Bạn thường ăn trưa ở đâu?", "I eat at the office.", "I eat + at + địa điểm.", "At the office nghĩa là ở văn phòng.", "I eat at twelve.", listOf("I eat at home.", "I eat at a cafe.")),
                t("Would you like a short break now?", "Bây giờ bạn có muốn nghỉ một chút không?", "Yes, let's take a break.", "Yes + let's take a break.", "Let's dùng để rủ cùng làm một việc.", "The office is on the second floor.", listOf("Yes, please.", "Yes, that sounds good.")),
                t("What would you like during the break?", "Bạn muốn dùng gì trong giờ nghỉ?", "Some water, please.", "Some water + please.", "Some water là một ít nước, không nói a water khi chỉ nước nói chung.", "My break is at noon.", listOf("A cup of tea, please.", "I'd like some water."))),
            lesson("routine", "chores", "Việc nhà đơn giản", "Bạn và người cùng nhà chia sẻ việc dọn dẹp cuối ngày.",
                words("wash the dishes" to "Rửa bát", "clean the room" to "Dọn phòng", "take out the trash" to "Đổ rác"),
                t("Could you wash the dishes after dinner?", "Bạn có thể rửa bát sau bữa tối không?", "Sure, I can do that.", "Sure + I can do that.", "Sure là cách đồng ý thân thiện khi được nhờ giúp.", "The plates are blue.", listOf("Yes, of course.", "Sure.")),
                t("I'll clean the room. Can you take out the trash too?", "Tôi sẽ dọn phòng. Bạn có thể đổ rác luôn không?", "Yes, I'll take it out.", "Yes + I'll take it out.", "I'll là dạng viết tắt của I will, nói việc bạn sẽ làm.", "The bin is green.", listOf("Yes, I can.", "Sure, I'll do it.")),
                t("Where should I put these clean plates?", "Tôi nên để những chiếc đĩa sạch này ở đâu?", "Put them in the cupboard.", "Put them + in the cupboard.", "Them dùng để nhắc lại nhiều chiếc đĩa.", "We bought these plates yesterday.", listOf("In the cupboard, please.", "Put them on the shelf.")),
                t("All done! Thanks for helping with the chores.", "Xong rồi! Cảm ơn bạn đã giúp làm việc nhà.", "You're welcome. The room looks nice.", "You're welcome + một nhận xét ngắn.", "Looks nice nghĩa là trông đẹp hoặc gọn gàng.", "Dinner is at seven.", listOf("You're welcome!", "No problem!")))
        )),
        Topic("time", "Thời gian và lịch hẹn", listOf(
            lesson("time", "clock", "Hỏi giờ", "Bạn hỏi giờ và giờ bắt đầu lớp học với Alex.",
                words("What time is it?" to "Mấy giờ rồi?", "half past" to "Rưỡi / hơn ba mươi phút", "on time" to "Đúng giờ"),
                t("Hi! You're early for class.", "Chào bạn! Bạn đến lớp sớm đấy.", "What time is it, please?", "Bạn chưa biết giờ. Hỏi What time is it + please?", "Thêm please giúp lời hỏi lịch sự hơn.", "What day is it today?", listOf("What time is it?", "Do you have the time?")),
                t("It's half past two.", "Bây giờ là hai giờ rưỡi.", "It's two thirty.", "Nhắc lại giờ: It's + two thirty.", "Half past two và two thirty đều là hai giờ ba mươi.", "It's two thirteen.", listOf("It's 2:30.", "It's half past two.")),
                t("Our class starts at three. Are we early?", "Lớp học bắt đầu lúc ba giờ. Chúng ta đến sớm phải không?", "Yes, we're early.", "Yes + we're early.", "Early là sớm; late là muộn.", "No, we're thirty minutes late.", listOf("Yes, we are.", "Yes, we are early.")),
                t("Shall we go to the classroom now?", "Bây giờ chúng ta đến phòng học nhé?", "Yes, let's go.", "Yes + let's go.", "Let's go là lời rủ cùng đi.", "Our teacher is from Canada.", listOf("Sure, let's go.", "Yes, good idea."))),
            lesson("time", "meeting", "Hẹn gặp bạn", "Bạn muốn hẹn Alex ở quán cà phê lúc ba giờ chiều ngày mai. Hãy nhận lời và đề xuất giờ gặp đó.",
                words("tomorrow" to "Ngày mai", "meet" to "Gặp", "at the cafe" to "Tại quán cà phê"),
                t("Are you free tomorrow afternoon?", "Chiều mai bạn có rảnh không?", "Yes, I'm free.", "Yes + I'm free.", "Free ở đây nghĩa là rảnh, không phải miễn phí.", "I live near the cafe.", listOf("Yes, I am.", "Yes, I'm free tomorrow.")),
                t("Would you like to meet at the cafe tomorrow?", "Ngày mai bạn có muốn gặp ở quán cà phê không?", "Yes, that sounds good.", "Yes + that sounds good.", "That sounds good là cách đồng ý với một đề xuất.", "The coffee costs three dollars.", listOf("Sure, I'd love to.", "Yes, let's meet there.")),
                t("What time shall we meet at the cafe?", "Chúng ta sẽ gặp ở quán lúc mấy giờ?", "Let's meet at three.", "Trong vai này, hẹn lúc ba giờ: Let's meet + at three.", "Dùng Let's meet… để đề xuất giờ gặp.", "Let's meet at the library.", listOf("Let's meet at 3.", "At three o'clock.", "At 3 p.m.")),
                t("Great, three o'clock tomorrow at the cafe!", "Tuyệt, ba giờ ngày mai tại quán cà phê nhé!", "See you there!", "See you + there.", "There nhắc lại địa điểm vừa thống nhất.", "It was nice to meet you yesterday.", listOf("See you tomorrow!", "Great, see you there."))),
            lesson("time", "reschedule", "Đổi lịch hẹn", "Bạn không thể gặp Alex lúc ba giờ và muốn hẹn muộn hơn.",
                words("I'm sorry" to "Tôi xin lỗi", "change the time" to "Đổi giờ", "later" to "Muộn hơn / sau đó"),
                t("Hi! Are we still meeting at three today?", "Chào! Hôm nay mình vẫn gặp lúc ba giờ chứ?", "I'm sorry, I can't make it at three.", "I'm sorry + I can't make it at three.", "Can't make it ở đây nghĩa là không thể có mặt theo giờ hẹn.", "The cafe is on King Street.", listOf("Sorry, I'm busy at three.", "I'm sorry, I can't come at three.")),
                t("No problem. Would a later time work for you?", "Không sao. Giờ muộn hơn có tiện cho bạn không?", "Yes, can we meet at four?", "Can we meet + at + giờ mới?", "Can we…? giúp đề nghị đổi lịch một cách lịch sự.", "The cafe is next to the bank.", listOf("Can we meet at 4?", "Yes, four o'clock would be good.")),
                t("Four is fine. Shall we keep the same place?", "Bốn giờ được đấy. Mình vẫn gặp ở chỗ cũ nhé?", "Yes, the same cafe.", "Yes + the same cafe.", "The same nghĩa là cùng một chỗ, không thay đổi.", "I usually drink tea there.", listOf("Yes, let's meet at the same cafe.", "Yes, please.")),
                t("All right, see you at four instead!", "Được rồi, vậy gặp bạn lúc bốn giờ nhé!", "Thanks for understanding. See you!", "Thanks for understanding + See you.", "Cảm ơn người bạn vì đã thông cảm khi đổi lịch.", "I bought a clock yesterday.", listOf("Thank you. See you at four!", "Thanks, see you!")))
        )),
        Topic("weather", "Thời tiết", listOf(
            lesson("weather", "today", "Trời hôm nay", "Bạn trò chuyện với Alex trước khi ra ngoài vào một ngày nắng nóng.",
                words("sunny" to "Có nắng", "rainy" to "Có mưa", "hot" to "Nóng"),
                t("What's the weather like today?", "Hôm nay thời tiết thế nào?", "It's sunny today.", "Trong vai này trời nắng: It's + sunny + today.", "Dùng It's để mô tả thời tiết.", "It's Monday today.", listOf("It's sunny.", "It's a sunny day.")),
                t("Is it hot outside this afternoon?", "Chiều nay bên ngoài có nóng không?", "Yes, it's quite hot.", "Yes + it's quite hot.", "Quite hot nghĩa là khá nóng.", "It was sunny last week.", listOf("Yes, it is.", "Yes, it's very hot.")),
                t("Do you prefer hot weather or cool weather?", "Bạn thích trời nóng hay trời mát hơn?", "I prefer cool weather.", "I prefer + cool/hot weather.", "Prefer diễn tả bạn thích một lựa chọn hơn lựa chọn kia.", "I usually go outside at noon.", listOf("I prefer hot weather.", "I like cool weather.")),
                t("Shall we sit in the shade for a while?", "Mình ngồi trong bóng râm một lát nhé?", "Good idea. It's cooler there.", "Good idea + It's cooler there.", "Cooler nghĩa là mát hơn, phù hợp khi so sánh hai nơi.", "The park closes at six.", listOf("Yes, good idea.", "Sure, let's sit there."))),
            lesson("weather", "clothes", "Chọn trang phục", "Bạn chuẩn bị ra ngoài khi trời lạnh và có thể mưa.",
                words("a jacket" to "Áo khoác", "an umbrella" to "Ô / dù", "cold" to "Lạnh"),
                t("It's cold this morning. What will you wear?", "Sáng nay trời lạnh. Bạn sẽ mặc gì?", "I'll wear a jacket.", "I'll wear + a jacket.", "Wear nói về việc mặc quần áo trên người.", "I'll go out at eight.", listOf("I will wear a jacket.", "I'll wear a coat.")),
                t("It might rain. Do you have an umbrella?", "Trời có thể mưa. Bạn có ô không?", "No, I don't have one.", "No + I don't have one.", "One dùng thay cho an umbrella; câu này cho biết bạn chưa có ô bên mình.", "The weather is cold today.", listOf("No, I don't.", "No, I forgot mine.")),
                t("Would you like to borrow my umbrella?", "Bạn có muốn mượn ô của tôi không?", "Yes, thank you. That's kind of you.", "Yes, thank you + That's kind of you.", "That's kind of you là lời cảm ơn vì sự tốt bụng.", "My jacket is blue.", listOf("Yes, please. Thank you.", "Thank you, I'd like to borrow it.")),
                t("Please bring the umbrella back tomorrow.", "Bạn mang trả ô vào ngày mai nhé.", "Of course. I'll bring it back tomorrow.", "I'll bring it back + tomorrow.", "Bring it back nghĩa là mang trả lại.", "I borrowed it last week.", listOf("Of course, thank you.", "Sure, I will."))),
            lesson("weather", "picnic", "Kế hoạch ngoài trời", "Bạn và Alex dời buổi dã ngoại sang Chủ nhật vì thứ Bảy có mưa.",
                words("a picnic" to "Buổi dã ngoại", "rain" to "Mưa", "stay inside" to "Ở trong nhà"),
                t("Would you like to have a picnic this Saturday?", "Bạn có muốn đi dã ngoại thứ Bảy này không?", "Yes, I'd love to.", "Yes + I'd love to.", "I'd love to thể hiện bạn vui vẻ nhận lời mời.", "The park is next to the station.", listOf("Yes, that sounds fun.", "Sure, let's have a picnic.")),
                t("The forecast says rain on Saturday. What should we do?", "Dự báo nói thứ Bảy có mưa. Chúng ta nên làm gì?", "Let's go on Sunday instead.", "Let's go + on Sunday + instead.", "Instead có nghĩa là thay vào đó.", "We had a picnic last month.", listOf("Let's go on Sunday.", "We can go on Sunday.")),
                t("Sunday looks sunny. What should we bring?", "Chủ nhật có vẻ nắng. Chúng ta nên mang gì?", "Let's bring some water and food.", "Let's bring + đồ cần mang.", "Bring là mang theo thứ gì đến nơi mình sẽ đến.", "We should leave at nine.", listOf("We should bring some water.", "Let's bring some sandwiches.")),
                t("I'll bring sandwiches. Can you bring the drinks?", "Tôi sẽ mang bánh mì kẹp. Bạn mang đồ uống nhé?", "Sure, I'll bring some water.", "Sure + I'll bring + đồ uống.", "Đồng ý và nói rõ việc bạn sẽ làm giúp kế hoạch dễ thống nhất.", "The drinks cost five dollars.", listOf("Yes, I'll bring the drinks.", "Sure, I can do that.")))
        ))
    )
}
