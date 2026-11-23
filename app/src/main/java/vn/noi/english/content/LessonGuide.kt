package vn.noi.english.content

/** Practical context, independent of saved progress and answer matching. */
data class LessonGuide(
    val learnerRole: String,
    val partnerRole: String,
    val objective: String,
    val tip: String,
    val turnSpeakers: List<String> = emptyList(),
) {
    fun speakerAt(turnIndex: Int): String = turnSpeakers.getOrElse(turnIndex) { partnerRole }
}

object LessonGuides {
    private fun guide(you: String, partner: String, goal: String, tip: String) = LessonGuide(you, partner, goal, tip)
    private val entries = mapOf(
        "greetings-morning" to guide("Người bạn", "Alex", "Chào buổi sáng, trả lời câu hỏi thăm và đáp lại lời chúc.", "Đáp lại lời chúc bằng You too!; thêm Thank you để thể hiện sự lịch sự."),
        "greetings-wellbeing" to guide("Người bạn", "Alex", "Trả lời câu hỏi thăm sau giờ học và nhận lời mời nghỉ một chút.", "I'm a little tired giúp nói bạn hơi mệt; không cần lúc nào cũng trả lời I'm fine."),
        "greetings-meeting" to guide("Học viên mới", "Alex", "Giới thiệu tên và làm quen với một người bạn trong lớp.", "Thay Linh bằng tên của bạn khi nói Hello! I'm…; thêm too khi đáp Nice to meet you."),
        "introductions-name" to guide("Bạn cùng lớp", "Alex", "Giới thiệu tên, hỏi tên người đối diện và xác nhận tên gọi.", "My name is… và I'm… đều dùng để giới thiệu tên; chọn cách ngắn mà bạn thấy dễ nói."),
        "introductions-country" to guide("Người bạn mới", "Alex", "Nói bạn đến từ đâu và đang sống ở thành phố nào.", "I'm from nói về quê hương; I live in nói về nơi đang sống. Hai nơi có thể khác nhau."),
        "introductions-work" to guide("Người bạn mới", "Alex", "Nói về việc học, công việc và hỏi lại người đối diện.", "Dùng I'm a student cho nghề/vai trò; dùng I study… khi nói mình học gì hoặc lúc nào."),
        "food-drink" to guide("Khách gọi đồ uống", "Nhân viên quán", "Gọi đồ uống, chọn nóng hoặc đá và nói yêu cầu về đường.", "Nói tên đồ uống rồi thêm please. Khi gọi xong, dùng That's all, thank you."),
        "food-meal" to guide("Khách dùng bữa", "Nhân viên phục vụ", "Xin thực đơn, gọi món và yêu cầu món không cay.", "I'd like… lịch sự hơn khi gọi món; Not spicy, please giúp nói rõ bạn không muốn ăn cay."),
        "food-bill" to guide("Khách dùng bữa", "Nhân viên phục vụ", "Xin hóa đơn, chọn cách thanh toán và trả lời về biên lai.", "The bill là hóa đơn cần trả; a receipt là biên lai xác nhận đã thanh toán."),
        "shopping-price" to guide("Người mua hàng", "Nhân viên cửa hàng", "Hỏi giá một chiếc áo và nói bạn muốn xem thêm trước khi mua.", "How much is it? hỏi giá một món; a little expensive là cách nhẹ nhàng nói giá hơi cao."),
        "shopping-size" to guide("Người mua hàng", "Nhân viên cửa hàng", "Chọn cỡ áo, nhận lời thử áo và nhận xét áo có vừa không.", "Try it on là mặc thử; It fits well nói áo vừa, It's too small nói áo quá nhỏ."),
        "shopping-buy" to guide("Người mua hàng", "Nhân viên cửa hàng", "Chốt mua một chiếc túi, chọn màu và thanh toán.", "I'll take it thông báo quyết định mua. Khi trả tiền, nói by card hoặc in cash."),
        "directions-place" to guide("Người hỏi đường", "Người đi đường", "Hỏi vị trí nhà ga và kiểm tra có thể đi bộ đến đó không.", "Bắt đầu bằng Excuse me khi hỏi người lạ; near here nghĩa là gần chỗ bạn đang đứng."),
        "directions-route" to guide("Người hỏi đường", "Người chỉ đường", "Hỏi cách tới ngân hàng và nhắc lại các hướng đi quan trọng.", "Nhắc lại go straight, turn left và on the right để kiểm tra mình nghe đúng trước khi đi."),
        "directions-confirm" to guide("Người hỏi đường", "Người chỉ đường", "Nhờ nhắc lại, xác nhận vị trí bảo tàng và thời gian đi bộ.", "Could you repeat that, please? dùng được ở nhiều tình huống khi bạn chưa nghe rõ."),
        "family-photo" to guide("Người giới thiệu gia đình", "Alex", "Giới thiệu mẹ qua ảnh, nói tên và kể mẹ có sống cùng bạn không.", "This is… giới thiệu một người; her name là tên của cô ấy, his name là tên của anh ấy."),
        "family-siblings" to guide("Người có chị/em gái", "Alex", "Kể về một chị hoặc em gái, sở thích và hoạt động cùng nhau.", "Đây là bài nhập vai có một chị/em gái. Dùng older hoặc younger để nói lớn hay nhỏ tuổi hơn."),
        "family-weekend" to guide("Người con về thăm nhà", "Alex", "Kể khi nào bạn thăm bố mẹ và gia đình làm gì cùng nhau.", "Dùng on Sunday cho một ngày cụ thể; together giúp diễn tả hoạt động cả nhà cùng làm."),
        "routine-morning" to guide("Người đi làm", "Alex", "Kể giờ thức dậy, bữa sáng và cách đi làm hằng ngày.", "At đi với giờ; by bus hoặc by motorbike nói phương tiện, không thêm a sau by."),
        "routine-breaks" to guide("Đồng nghiệp", "Đồng nghiệp", "Nói về giờ nghỉ trưa và nhận lời nghỉ, gọi đồ uống.", "Let's take a break là lời rủ cùng nghỉ. Some water, please là yêu cầu ngắn, lịch sự."),
        "routine-chores" to guide("Người cùng nhà", "Người cùng nhà", "Nhận lời làm việc nhà và thống nhất khi nào sẽ dọn dẹp.", "Sure, I can do that nhận lời giúp; I'll… nói việc bạn sẽ làm tiếp theo."),
        "time-clock" to guide("Học viên chờ vào lớp", "Alex", "Hỏi giờ, hiểu giờ rưỡi và kiểm tra có đến sớm không.", "Half past two và two thirty cùng là 2:30; chú ý phân biệt thirty với thirteen."),
        "time-meeting" to guide("Người hẹn gặp bạn", "Alex", "Nhận lời gặp bạn và thống nhất giờ, địa điểm cho ngày mai.", "Dùng at trước giờ gặp và on trước ngày; See you there nhắc lại nơi hai người vừa thống nhất."),
        "time-reschedule" to guide("Người cần đổi lịch", "Alex", "Xin đổi cuộc hẹn từ ba giờ sang bốn giờ một cách lịch sự.", "I'm sorry, I can't make it… giải thích bạn không tới được; đề xuất giờ mới với Can we meet…?"),
        "weather-today" to guide("Người bạn", "Alex", "Nói về thời tiết, sở thích và nhận lời ngồi ở nơi mát hơn.", "It's sunny/hot mô tả thời tiết; I prefer… nói lựa chọn bạn thích hơn."),
        "weather-clothes" to guide("Người mượn ô", "Alex", "Chọn áo khoác, mượn ô và hứa trả lại vào ngày mai.", "Borrow là mượn của ai; bring it back là mang trả. Nói rõ thời gian trả khi mượn đồ."),
        "weather-picnic" to guide("Người cùng đi dã ngoại", "Alex", "Đổi ngày dã ngoại vì mưa và chia nhau chuẩn bị đồ ăn, nước uống.", "Let's go on Sunday instead đề xuất đổi sang Chủ nhật; I'll bring… nhận phần chuẩn bị của bạn."),
        "transport-taxi" to guide("Hành khách taxi", "Tài xế", "Nói điểm đến sân bay, hỏi thời gian và yêu cầu dừng xe.", "To the airport, please là cách nói gọn khi lên taxi; How long does it take? hỏi thời gian đi."),
        "transport-bus" to guide("Hành khách xe buýt", "Tài xế xe buýt", "Hỏi tuyến tới trung tâm, mua vé và xác nhận bến xuống.", "Get on là lên xe; get off là xuống xe. Hỏi bến xuống trước để đỡ lo bỏ lỡ."),
        "transport-train" to guide("Người mua vé tàu", "Nhân viên bán vé", "Mua vé tới Đà Nẵng, chọn loại vé và hỏi sân ga.", "One-way là một chiều; return là khứ hồi. Nhắc lại số sân ga với right? để xác nhận."),
        "hotel-check-in" to guide("Khách lưu trú", "Lễ tân", "Nhận phòng đã đặt, đưa hộ chiếu và hỏi vị trí thang máy.", "I'd like to check in, please mở đầu thủ tục nhận phòng; Here you are dùng khi đưa hộ chiếu."),
        "hotel-services" to guide("Khách lưu trú", "Lễ tân", "Hỏi giờ ăn sáng, mật khẩu Wi-Fi và xin thêm khăn tắm.", "Could I have…? hoặc Can I have…? giúp xin một vật lịch sự; nhớ thêm please."),
        "hotel-check-out" to guide("Khách trả phòng", "Lễ tân", "Trả phòng, xác nhận số phòng, thanh toán và xin gửi hành lý.", "Check out là trả phòng; leave my bag here là gửi túi lại, khác với làm mất túi."),
        "leisure-hobbies" to guide("Người thích đọc sách", "Alex", "Kể về loại sách yêu thích, thói quen nghe nhạc và chia sẻ bài hát.", "Trong bài này bạn nhập vai người thích đọc sách; sau I like thường dùng động từ thêm -ing."),
        "leisure-invitation" to guide("Người rủ bạn đi chơi", "Alex", "Rủ bạn đi xem phim và thống nhất giờ, chỗ gặp.", "Would you like to…? dùng để mời ai làm gì; Let's watch… đề xuất loại phim sẽ xem."),
        "leisure-weekend" to guide("Người vừa đi công viên", "Alex", "Kể chuyến đi công viên cuối tuần trước, người đi cùng và hoạt động đã làm.", "Chuyện đã qua dùng went, walked, took. Trong vai này bạn đã đi công viên, không ở nhà."),
        "help-repeat" to guide("Người cần nghe lại", "Alex", "Nhờ nói chậm, nhắc lại và xác nhận mình đã hiểu.", "Không cần đoán khi nghe chưa rõ: Could you say that again, please? là câu rất hữu ích."),
        "help-small-favor" to guide("Người đang mang đồ", "Alex", "Nhờ mở cửa, nhận giúp mang hộp và nói chỗ đặt hộp.", "Could you…? nhờ ai làm giúp; Would you like a hand? là lời đề nghị giúp, không hỏi về bàn tay."),
        "help-lost-bag" to guide("Người mất túi màu đen", "Nhân viên đồ thất lạc", "Báo mất túi, mô tả màu và đồ bên trong để nhận lại túi.", "Trong bài túi màu đen có sách. Mine nghĩa là của tôi, không nói my mine bag."),
        "home-place" to guide("Người giới thiệu nơi ở", "Alex", "Nói loại nhà, khoảng cách tới chỗ làm và điều bạn thích ở khu phố.", "I live in an apartment: dùng an trước âm nguyên âm; close ở đây nghĩa là gần."),
        "home-room" to guide("Khách xem phòng", "Alex", "Hỏi về căn phòng, mô tả đồ đạc và trả lời lời mời mở cửa sổ.", "There is a desk nói có một chiếc bàn; big enough nghĩa là đủ rộng, enough đứng sau big."),
        "home-guest" to guide("Khách đến chơi nhà", "Alex", "Chào chủ nhà, hỏi về giày và nhận một tách trà.", "Make yourself at home nghĩa là cứ tự nhiên. Bài này bạn chọn trà để khớp lượt mời tiếp theo."),
        "phone-call" to guide("Người gọi điện", "Người nghe máy", "Giới thiệu qua điện thoại, xin gặp Alex và hỏi có tiện nói chuyện không.", "This is… thường dùng khi xưng tên qua điện thoại; Is this a good time? hỏi người kia có tiện không.").copy(turnSpeakers = listOf("Người nghe máy", "Người nghe máy", "Alex", "Alex")),
        "phone-message" to guide("Người để lại lời nhắn", "Người nhận lời nhắn", "Nhờ chuyển lời để Alex gọi lại và nói thời gian phù hợp.", "Call me back là gọi lại cho tôi; if possible làm lời đề nghị nhẹ nhàng hơn."),
        "phone-text" to guide("Người nhận tin nhắn", "Alex", "Xác nhận tin nhắn, giờ và địa điểm, hứa báo nếu đến muộn.", "Right? kiểm tra lại thông tin; I'll let you know hứa sẽ báo cho người kia biết.")
    )

    operator fun get(lessonId: String): LessonGuide = entries.getValue(lessonId)
}
