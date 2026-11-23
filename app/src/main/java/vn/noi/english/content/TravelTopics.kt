package vn.noi.english.content

import vn.noi.english.content.LessonContent.lesson
import vn.noi.english.content.LessonContent.turn as t
import vn.noi.english.content.LessonContent.words

internal object TravelTopics {
    val topics = listOf(
        Topic("transport", "Đi lại", listOf(
            lesson("transport", "taxi", "Đi taxi", "Bạn lên taxi, nói điểm đến và hỏi thời gian di chuyển.",
                words("the airport" to "Sân bay", "How long…?" to "Bao lâu?", "stop here" to "Dừng ở đây"),
                t("Hello, where can I take you today?", "Xin chào, hôm nay tôi chở bạn đến đâu?", "To the airport, please.", "To + điểm đến + please.", "Dùng To… khi nói với tài xế nơi bạn muốn đến.", "At half past nine, please.", listOf("The airport, please.", "Please take me to the airport.")),
                t("The airport is quite far from here.", "Sân bay cách đây khá xa.", "How long does it take?", "Hỏi thời gian đi: How long + does it take?", "Câu này hỏi chuyến đi mất bao lâu.", "Where is the train station?", listOf("How long will it take?", "How long is the journey?")),
                t("About thirty minutes. Is that all right?", "Khoảng ba mươi phút. Như vậy có được không?", "Yes, that's fine. Thank you.", "Yes + that's fine + thank you.", "That's fine giúp xác nhận bạn đồng ý với thông tin vừa nghe.", "The airport has two terminals.", listOf("Yes, that's fine.", "Yes, thank you.")),
                t("We are at the airport entrance. Shall I stop here?", "Chúng ta ở cổng sân bay rồi. Tôi dừng ở đây nhé?", "Yes, please stop here.", "Please stop + here.", "Here là ở đây; please giúp lời yêu cầu lịch sự.", "I usually travel by train.", listOf("Yes, here is fine.", "Please stop here. Thank you."))),
            lesson("transport", "bus", "Đi xe buýt", "Bạn hỏi xe buýt đến trung tâm và cách xuống đúng bến.",
                words("bus stop" to "Điểm dừng xe buýt", "city center" to "Trung tâm thành phố", "get off" to "Xuống xe"),
                t("Hello! Are you going into town?", "Chào bạn! Bạn đang đi vào trong thành phố phải không?", "Does this bus go to the city center?", "Xác nhận tuyến xe: Does this bus go to + địa điểm?", "Dùng Does this bus go to… để hỏi tuyến xe có đến nơi bạn cần không.", "What time does the museum close?", listOf("Does this bus go downtown?")),
                t("Yes, it does. How many tickets do you need?", "Có. Bạn cần mấy vé?", "One ticket, please.", "Số lượng + ticket(s) + please.", "One ticket là một vé; two tickets thêm s.", "The bus leaves at ten.", listOf("Two tickets, please.", "Just one, please.")),
                t("You can pay here. The bus makes several stops.", "Bạn có thể trả tiền ở đây. Xe sẽ dừng ở một số bến.", "Where should I get off?", "Hỏi bến cần xuống: Where + should I + get off?", "Get off dùng khi xuống xe buýt hoặc tàu.", "What color is your bus?", listOf("Which stop should I get off at?")),
                t("Get off at Central Square. I'll tell you when we arrive.", "Xuống ở Quảng trường Trung tâm. Tôi sẽ báo khi đến nơi.", "Thank you for letting me know.", "Thank you + for letting me know.", "Đây là lời cảm ơn vì người khác giúp báo thông tin cho bạn.", "My train ticket costs ten dollars.", listOf("Thank you very much.", "Thanks for your help."))),
            lesson("transport", "train", "Mua vé tàu", "Bạn mua vé tàu đi Đà Nẵng và hỏi sân ga.",
                words("one-way ticket" to "Vé một chiều", "return ticket" to "Vé khứ hồi", "platform" to "Sân ga"),
                t("Good afternoon. Where would you like a train ticket to?", "Chào buổi chiều. Bạn muốn mua vé tàu đến đâu?", "A ticket to Da Nang, please.", "A ticket to + điểm đến + please.", "To đứng trước nơi bạn muốn đi đến.", "At nine o'clock, please.", listOf("I'd like a ticket to Da Nang.", "One ticket to Da Nang, please.")),
                t("Would you like a one-way or a return ticket?", "Bạn muốn vé một chiều hay khứ hồi?", "One-way, please.", "One-way / Return + please.", "One-way là một chiều; return gồm cả lượt đi và về.", "Window seat, please.", listOf("Return, please.", "A one-way ticket, please.")),
                t("The train leaves at nine. Here's your ticket.", "Tàu khởi hành lúc chín giờ. Đây là vé của bạn.", "Which platform is it?", "Hỏi sân ga: Which platform + is it?", "Which dùng để hỏi lựa chọn cụ thể, ở đây là sân ga nào.", "Where can I buy a bus ticket?", listOf("Which platform does it leave from?")),
                t("Platform two. Please keep your ticket with you.", "Sân ga số hai. Bạn giữ vé bên mình nhé.", "Thank you. Platform two, right?", "Nhắc lại: Platform two + right?", "Thêm right? ở cuối để xác nhận thông tin.", "Platform three, right?", listOf("Platform 2, right?", "Thank you, I'll keep my ticket.")))
        )),
        Topic("hotel", "Khách sạn", listOf(
            lesson("hotel", "check-in", "Nhận phòng", "Bạn đến lễ tân khách sạn để nhận phòng đã đặt.",
                words("a reservation" to "Đặt phòng trước", "check in" to "Nhận phòng", "room key" to "Chìa khóa phòng"),
                t("Welcome to our hotel. How can I help you this evening?", "Chào mừng đến khách sạn. Tối nay tôi giúp gì được cho bạn?", "I'd like to check in, please.", "I'd like to + check in + please.", "Check in là làm thủ tục nhận phòng.", "The restaurant opens at six.", listOf("I'd like to check in.", "Can I check in, please?")),
                t("Do you have a reservation with us?", "Bạn đã đặt phòng ở đây chưa?", "Yes, I have a reservation.", "Yes + I have a reservation.", "Reservation là việc đặt chỗ trước, ở đây là đặt phòng.", "My flight leaves at seven.", listOf("Yes, I do.", "Yes, I booked a room.")),
                t("May I see your passport for check-in?", "Tôi có thể xem hộ chiếu để làm thủ tục nhận phòng không?", "Of course. Here you are.", "Of course + Here you are.", "Here you are dùng khi đưa một vật cho người khác.", "My passport is blue.", listOf("Sure, here you are.", "Here you are.")),
                t("Here is your room key. You're in room two-oh-five.", "Đây là chìa khóa. Bạn ở phòng 205.", "Thank you. Where is the elevator?", "Where is + the elevator?", "Elevator là thang máy; bạn cũng có thể nói lift.", "The elevator is on your left.", listOf("Thank you. Where is the lift?", "Where is the elevator, please?"))),
            lesson("hotel", "services", "Hỏi dịch vụ khách sạn", "Bạn hỏi lễ tân về bữa sáng, Wi-Fi và khăn tắm.",
                words("breakfast" to "Bữa sáng", "Wi-Fi password" to "Mật khẩu Wi-Fi", "a towel" to "Khăn tắm"),
                t("Good morning. How can I help you with breakfast?", "Chào buổi sáng. Bạn cần hỏi gì về bữa sáng?", "What time is breakfast?", "What time is + breakfast?", "Câu này hỏi giờ phục vụ bữa sáng.", "Where can I buy a train ticket?", listOf("When is breakfast?", "What time do you serve breakfast?")),
                t("Breakfast is from seven to ten. Anything else?", "Bữa sáng từ bảy đến mười giờ. Bạn cần gì nữa không?", "What's the Wi-Fi password?", "What's + the Wi-Fi password?", "Password có nghĩa là mật khẩu.", "Breakfast starts at seven.", listOf("What is the Wi-Fi password?", "Can I have the Wi-Fi password, please?", "What's the wifi password?", "What is the wifi password?", "Can I have the wifi password, please?")),
                t("The password is on your key card. Do you need anything for your room?", "Mật khẩu ở trên thẻ phòng. Bạn có cần gì cho phòng không?", "Could I have another towel, please?", "Could I have + another towel + please?", "Another towel nghĩa là thêm một chiếc khăn tắm.", "My room is on the third floor.", listOf("Can I have another towel, please?", "Another towel, please.")),
                t("Certainly. We'll send a towel to your room.", "Được ạ. Chúng tôi sẽ mang khăn lên phòng.", "Thank you for your help.", "Thank you + for your help.", "Cảm ơn người hỗ trợ sau khi yêu cầu đã được tiếp nhận.", "How much is a bus ticket?", listOf("Thank you very much!", "That's great, thank you."))),
            lesson("hotel", "check-out", "Trả phòng", "Bạn vừa lưu trú ở phòng 205. Bạn trả phòng, lấy biên lai, thanh toán bằng thẻ và gửi hành lý một lát.",
                words("check out" to "Trả phòng", "luggage" to "Hành lý", "leave my bag" to "Gửi lại túi của tôi"),
                t("Good morning. Are you ready to leave the hotel?", "Chào buổi sáng. Bạn sẵn sàng rời khách sạn chưa?", "Yes, I'd like to check out.", "I'd like to + check out.", "Check out là làm thủ tục trả phòng.", "The hotel has six floors.", listOf("I'd like to check out, please.", "Yes, I'm checking out.")),
                t("What was your room number?", "Số phòng của bạn là bao nhiêu?", "Room two-oh-five.", "Bạn ở phòng 205: Room + two-oh-five.", "Số phòng 205 có thể đọc two-oh-five hoặc two hundred and five.", "I'm staying for two nights.", listOf("Room 205.", "Room two oh five.", "Room two hundred and five.", "Two hundred and five.", "Two oh five.")),
                t("Here is your final bill. Would you like a receipt for your stay?", "Đây là hóa đơn cuối. Bạn có muốn lấy biên lai lưu trú không?", "Yes, please. Can I pay by card?", "Yes, please + Can I pay by card?", "Can I pay by card? hỏi liệu có thể thanh toán bằng thẻ không.", "My room is on the second floor.", listOf("Yes, please. I'd like to pay by card.", "Yes, please. I would like to pay by card.")),
                t("Of course. Your payment is complete. Do you need help with your bags?", "Được ạ. Bạn đã thanh toán xong. Bạn có cần hỗ trợ về hành lý không?", "Can I leave my bag here for an hour?", "Can I leave my bag here + for an hour?", "Leave my bag here nghĩa là gửi túi ở đây; for an hour là trong một giờ.", "What time does breakfast start tomorrow?", listOf("Can I leave my luggage here, please?", "Can you keep my bag for an hour?")))
        ))
    )
}
