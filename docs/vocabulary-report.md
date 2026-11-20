# KTalk 0.3.0 — Vocabulary / Học từ vựng

## Cách học

Tab **Từ vựng** cung cấp 135 thẻ từ và cụm từ từ 45 bài hội thoại hiện có. Người mới có thể nghe tiếng Anh, thử nhớ rồi bấm **Xem nghĩa** để xem giải thích tiếng Việt và bài học chứa từ đó. Sau khi lật thẻ, chọn **Đã nhớ** hoặc **Cần ôn**; ứng dụng lưu thành công rồi mới chuyển thẻ. Bỏ qua thẻ không thay đổi mức nhớ.

Có bộ lọc 15 chủ đề, học tất cả hoặc chỉ ôn các từ đã đánh dấu cần ôn. **Kiểm tra nhanh** chọn tối đa 5 từ trong chủ đề, ưu tiên từ cần ôn và chưa học; mỗi câu có 4 lựa chọn và phản hồi ngay sau khi chọn. Điểm quiz chỉ tồn tại trong lượt hiện tại, không tự đánh dấu từ là đã nhớ.

Đây là cách ôn đơn giản bằng tự đánh giá, chưa có lịch lặp lại ngắt quãng hay thông báo nhắc học. Không cần tài khoản hoặc backend. Nghe mẫu tiếp tục dùng Android TextToSpeech, có nút **Cài giọng** khi thiết bị thiếu giọng tiếng Anh.

## Lưu trữ và xử lý lỗi

- `VocabularyCatalog` lấy dữ liệu trực tiếp từ nội dung bài học. ID chuẩn hóa theo tiếng Anh, độc lập với thứ tự và bản dịch; khi bổ sung một mục tiếng Anh trùng, catalog gộp nghĩa và nguồn bài học. Hiện có đúng 135 mục tiếng Anh khác nhau.
- `VocabularyViewModel` độc lập với trạng thái hội thoại. Mức nhớ được ghi bằng giao dịch vào file DataStore `vocabulary_progress`, tách khỏi `learning_progress`.
- Lỗi đọc không hiển thị tiến độ giả bằng 0; có nút tải lại. Lỗi ghi giữ nguyên thẻ và nghĩa đang mở để thử lại; chống bấm lưu lặp hoặc đổi tab trong khi ghi.
- Từ vựng vẫn mở được nếu kho tiến độ hội thoại lỗi, và hội thoại vẫn mở được nếu kho từ vựng lỗi. Đổi tab, thẻ hoặc câu hỏi sẽ dừng âm thanh cũ.
- Các câu hỏi tránh lựa chọn trùng nghĩa và những cặp dễ gây nhập nhằng đã xác định trong nội dung, gồm giới thiệu tên/người và hóa đơn/biên nhận.

## Rà soát độc lập

Reviewer đọc mã và kiểm thử, kết luận **With fixes**, không có lỗi Critical. Ba vấn đề Important đã được xử lý bằng test hồi quy có lượt thất bại trước sửa và đạt sau sửa: đáp án quiz gây nhập nhằng, kho hội thoại lỗi chặn tab từ vựng, và callback chuyển tab trong cùng khung hình vượt qua trạng thái đang lưu. Không chạy thêm lượt re-review; người triển khai xác minh sửa lỗi bằng regression và bộ kiểm thử đầy đủ.

## Bản cài

APK: [KTalk-0.3.0-vocabulary.apk](../releases/KTalk-0.3.0-vocabulary.apk), version **0.3.0**, versionCode **3**, package `vn.noi.english`. Đây là bản debug để cài thử. Cài cập nhật lên bản cũ cùng chữ ký, **không gỡ ứng dụng cũ**, để giữ dữ liệu học.

Kích thước **18,287,509 byte**. SHA-256: `d152dd325bd96284a72b9fda7ce6047ec9fb69e21da8d7140c744b57c082ec00`.

## Kiểm chứng

- Lượt đầy đủ ngày 20/11/2026: `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedDebugAndroidTest` — **BUILD SUCCESSFUL**. Log cục bộ: `.tools/vocabulary-final.log`.
- **41/41 test JVM/Robolectric** đạt: 19 test lõi có sẵn, 11 test giao diện, 3 test catalog/quiz, 8 test trạng thái từ vựng.
- **14/14 test Android API 35** đạt: 11 test giao diện và 3 test thiết bị về DataStore/bàn phím/tạo lại Activity. File DataStore thật được đóng và mở lại để kiểm chứng hai loại tiến độ độc lập.
- Lint: **0 lỗi / 15 cảnh báo** có sẵn, không nâng dependency ngoài phạm vi tính năng.
- `aapt` xác nhận tên **KTalk**, versionCode **3**, versionName **0.3.0**. `apksigner` xác minh APK hợp lệ và cùng chứng thư với bản 0.2.0 dựng từ commit `55a5b42` trong thư mục tạm. SHA-256 chứng thư: `104cbe1d932c69d2b756aa4c2ef762451888f0ebde5177cb4ad11173765dbce2`.
- Cài bản 0.2.0, hoàn thành `greetings-morning`, rồi cài đè bản 0.3.0 bằng `adb install -r`: vẫn giữ **1/45 bài**. Đánh dấu một thẻ Đã nhớ, một thẻ Cần ôn; force-stop rồi mở lại vẫn có **1/135 đã nhớ, 1 cần ôn, 133 chưa học** và tiến độ hội thoại không đổi. Log `.tools/vocabulary-upgrade-check.log` kết thúc bằng PASS.
- Đã xem ảnh chụp từ APK chạy thật trên emulator: [trang từ vựng](testing/screenshots/vocabulary-home.png), [thẻ đã lật](testing/screenshots/vocabulary-card.png), [quiz](testing/screenshots/vocabulary-quiz.png). Test riêng có cỡ chữ 2.0 và tình huống thiếu giọng đọc.

Chất lượng nhận diện micro/TTS trên điện thoại thật vẫn cần kiểm tra trực tiếp. Các test giao diện dùng adapter âm thanh giả; không coi chúng là bằng chứng chất lượng giọng đọc hoặc nhận diện.

Giữ nhánh hiện tại và lưu commit cục bộ theo cách triển khai đã chọn; không push hoặc merge. APK được sao chép vào `releases/` để các lượt build sau không xóa bản bàn giao; file APK được bỏ qua trong Git.
