# KTalk 0.4.0 — 300 mục từ giao tiếp và cải thiện chức năng

## Nội dung mới

Thêm **165 từ và cụm từ**, mỗi chủ đề 11 mục, nâng tổng danh mục Vocabulary lên **300 mục trong 15 chủ đề**, mỗi chủ đề 20 mục. [Danh sách 165 mục mới](content/165-vocabulary-expansion.md) gồm chào hỏi, thông tin cá nhân, đồ ăn, mua sắm, hỏi đường, gia đình, sinh hoạt, thời gian, thời tiết, đi lại, khách sạn, sở thích, nhờ giúp đỡ, nhà cửa và điện thoại.

Mỗi mục mới có nghĩa Việt và một câu ví dụ Anh–Việt ngắn, có thể nghe riêng từ hoặc nghe câu ví dụ. Ví dụ chỉ hiện sau khi lật thẻ, giúp người học thử nhớ nghĩa trước. Thẻ mở rộng chỉ ghi nguồn chủ đề; 135 mục gốc vẫn giữ nguồn bài học, ID và nghĩa cũ. Không thay đổi nội dung 45 bài hội thoại.

## Cải thiện sử dụng

- Tìm kiếm tiếng Anh hoặc nghĩa Việt, không phân biệt hoa/thường, gộp khoảng trắng, hỗ trợ bỏ dấu tiếng Việt và đ/Đ. Kết hợp được bộ lọc chủ đề. Thống kê trên kết quả đang lọc; có nút xóa tìm kiếm và thông báo khi không có từ phù hợp.
- Mỗi lượt tối đa 10 thẻ để học ngắn. Giữ thứ tự ưu tiên Cần ôn, Chưa học, Đã nhớ. Bỏ qua không tự đánh dấu và lỗi ghi vẫn giữ thẻ đang mở.
- Các lượt tiếp theo ưu tiên các thẻ ít xuất hiện gần đây trong cùng mức nhớ, giúp đi tiếp được qua danh mục khi bỏ qua thẻ. Thứ tự luân phiên chỉ giữ trong phiên ứng dụng, mức nhớ vẫn lưu trên máy.
- Quiz vẫn tối đa 5 câu, 4 lựa chọn, phản hồi ngay, không tự thay đổi mức nhớ hoặc tiến độ hội thoại.
- Sửa từ chối câu trả lời dạng đầy đủ tương đương câu mẫu viết tắt trong bài thật, như `Sure, I will send you a song.` Các dạng phủ định vẫn giữ nguyên ý nghĩa khi so khớp; không dùng fuzzy matching.
- Sửa giữ vị trí cuộn cũ khi chuyển lượt hội thoại: cỡ chữ lớn phải thấy đầu trang và câu hỏi mới. Test ban đầu chỉ kiểm tra một phần câu hỏi đã bỏ sót lỗi; bổ sung kiểm tra nhãn lượt ở đầu trang đã tái hiện lỗi trước khi sửa.
- Xóa thông báo lỗi nghe trước khi thử nghe lại, để lần phát thành công không còn báo lỗi cũ.
- Khi trở về từ cài đặt, khởi tạo lại engine TTS đang được chọn, kiểm tra hỗ trợ tiếng Anh và bỏ qua callback từ engine cũ. Dùng đúng vòng đời khởi tạo và giải phóng của [Android TextToSpeech](https://developer.android.com/reference/android/speech/tts/TextToSpeech).
- Chuẩn hóa khóa tìm kiếm một lần cho danh mục bất biến, dùng lại danh sách lọc và thống kê theo mỗi trạng thái; không chuẩn hóa toàn bộ catalog nhiều lần khi gõ hoặc vẽ tiến độ.

## Kiểm chứng

Danh mục có test đủ 300 ID duy nhất, 20 mục/chủ đề, 165 ví dụ đầy đủ và giữ nguyên 135 thẻ cũ. Mọi mục được tạo quiz với nhiều seed để kiểm tra có đủ 4 lựa chọn khác nhau. Test trạng thái bao phủ tìm kiếm không dấu/đ, kết hợp chủ đề, không kết quả, khóa sửa bộ lọc khi đang học/lưu, phiên ngắn và giữ mức nhớ.

Test UI chạy trên cả Robolectric và Android API35: tìm `AO MUA`, mở đúng `a raincoat`, lật xem ví dụ, chuyển đúng câu tiếng Anh vào adapter giọng đọc, lưu/xóa tìm kiếm, font2.0, thiếu giọng đọc, lỗi đọc/ghi riêng, thao tác chuyển tab trong cùng khung hình, giữ các luồng học 45 bài và bàn phím thật. Các test âm thanh giả kiểm chứng tương tác, không đo chất lượng giọng đọc hoặc nhận diện.

## Rà soát độc lập và hồi quy

Reviewer độc lập kết luận **Ready with fixes**, không có Critical, 5 Important và 2 Minor. Các Important đã được tái hiện trước sửa và sửa trong một lượt, không thuê thêm re-review:

1. **Lặp nhóm 10 thẻ khi bỏ qua:** test chủ đề 20 mục xác nhận hai lượt đầu không trùng và lượt thứ ba quay vòng, không ghi mức nhớ ngầm.
2. **Quiz có bản dịch gần tương đương:** loại cả hai chiều các nhóm nhắc lại, giữ máy/đợi, chỗ ngồi/ghế và lời tạm biệt, ngoài hai nhóm đã có trước. Test trực tiếp danh sách distractor và 200 seed mỗi chiều; mọi thẻ vẫn có đủ 4 lựa chọn.
3. **Mở rộng viết tắt quá rộng:** từ chối các dạng khẳng định kết thúc mệnh đề như `Yes, we're.`, `Sure, I'll.`, `Here you're.`. Hỗ trợ thêm dạng hợp lệ `There's a book inside.`, `Where's the elevator?`, `She's Linh.` trên các lượt thật. Giữ dạng phủ định cuối câu `I can't.` và không chấp nhận `I can.` thay thế.
4. **Chi phí tìm kiếm lặp:** test xác nhận các getter UI dùng lại đúng đối tượng danh sách đã lọc; khóa bất biến và regex được tạo một lần, không đo thời gian bằng assertion dễ dao động.
5. **Không phục hồi sau cài giọng:** test Robolectric trực tiếp `AndroidSpeechOutput`, giả lập engine thiếu tiếng Anh, cài ngôn ngữ, refresh rồi đọc thành công; engine cũ được shutdown, callback cũ không ghi đè Ready, close rồi refresh không mở lại engine.

Hai chi tiết nhỏ cũng được xử lý trong phạm vi cải thiện người dùng yêu cầu: tách hàm ID thuần để truy cập danh mục mở rộng trước không gây vòng khởi tạo (test fresh JVM đã thất bại trước sửa), và đổi câu dịch thành “Tôi cần người giúp điền mẫu này” để rõ hướng nhờ giúp đỡ. Không còn phát hiện nào được hoãn hoặc declined-to-judge.

Micro/TTS trên điện thoại thật chưa được kiểm chứng; nội dung và mức nhớ không cần backend, còn nhận diện/giọng đọc phụ thuộc dịch vụ trên thiết bị.

## Kết quả nghiệm thu cuối

Lệnh `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedDebugAndroidTest` đạt **BUILD SUCCESSFUL** sau toàn bộ sửa lỗi của reviewer. Log: `.tools/expansion-final-verification.log`.

- **57/57 JVM/Robolectric**, không failures/errors: 2 test audio, 6 nội dung hội thoại, 7 matcher, 7 trạng thái hội thoại, 15 giao diện, 1 khởi tạo catalog, 6 catalog/quiz, 13 trạng thái từ vựng.
- **18/18 Android API35**, không failures/errors: 15 test giao diện và 3 test thiết bị về DataStore/bàn phím/tạo lại Activity. Giữ toàn bộ kiểm thử cũ.
- Lint **0 lỗi / 15 cảnh báo có sẵn**. Không nâng dependency ngoài phạm vi.
- `apksigner` xác minh chữ ký hợp lệ và cùng chứng thư với APK0.3.0. `aapt` xác nhận tên KTalk, versionCode4/versionName0.4.0.
- Thử nâng cấp thực trên emulator: cài APK0.3.0, hoàn thành `greetings-morning` và đánh dấu 1 Đã nhớ/1 Cần ôn; cài đè APK0.4.0 vẫn có **1/45 bài**, **1/300 Đã nhớ**, **1 Cần ôn**. Tìm `ao mua`, mở đúng `a raincoat`, xem ví dụ và đánh dấu Cần ôn. Force-stop/mở lại giữ **1/45**, **1 Đã nhớ/2 Cần ôn/297 Chưa học**. Log `.tools/expansion-upgrade-check.log` kết thúc bằng PASS.
- Đã xem ảnh chụp từ APK trên emulator: [trang Vocabulary](testing/screenshots/expanded-vocabulary-home.png), [thẻ từ mới và ví dụ](testing/screenshots/expanded-vocabulary-example.png).

## Bản cài

[KTalk-0.4.0-300-vocabulary.apk](../releases/KTalk-0.4.0-300-vocabulary.apk), versionName **0.4.0**, versionCode **4**, package `vn.noi.english`. Bản debug để cài thử, chưa phải bản Play Store. Cài cập nhật cùng chữ ký để giữ dữ liệu, không gỡ bản cũ trước.

Kích thước **18,261,528 byte**. SHA-256: `a7fe61dba32f6308e562d8f371088530ff3309f750a02ce71399565afded45b6`. Bản APK lưu bền trong `releases/`, không bị xóa khi Gradle build lại.

Giữ checkout và nhánh hiện có, lưu commit cục bộ; không push hoặc merge. Không thay đổi schema hai DataStore hoặc ID 135 mục cũ. Không có phát hiện reviewer nào còn hoãn.
