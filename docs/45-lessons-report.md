# KTalk 0.2.0 — Mở rộng lên 45 bài

## Kết quả

Thêm **30 bài mới** thuộc 10 chủ đề, giữ nguyên 15 bài gốc. Tổng cộng **15 chủ đề, 45 bài, 135 mục cụm từ và 180 lượt hội thoại**. [Danh sách bài](content/45-lessons.md).

Giữ giao diện KTalk, nghe mẫu, giải thích tiếng Việt và trả lời qua nói/chọn/gõ. Không thêm tài khoản, backend, AI tự do hoặc chấm phát âm. Những giới hạn micro/TTS phụ thuộc thiết bị của bản đầu tiếp tục áp dụng.

## Nội dung và kiến trúc

- Các bài mới được tách thành EverydayTopics, TravelTopics, SocialTopics và nối sau 5 chủ đề gốc. ID và thứ tự 15 bài cũ không đổi.
- Home, Lessons và Progress lấy số lượng bài từ dữ liệu. Không còn ghi cố định mẫu số 15 trong mã UI.
- Vị trí lựa chọn của bài mới được trộn với seed ổn định theo ID lượt, tránh lặp cùng một quy luật đúng/sai cho mọi bài; giữ ổn định khi mở lại.
- Biến thể dạng viết đầy đủ được thêm ở dữ liệu mới, chẳng hạn “I'll” / “I will”. Không thay chính sách so khớp toàn cục hoặc bỏ phủ định.
- Các hội thoại có bối cảnh cụ thể; biến thể trả lời giữ cùng các dữ kiện mà câu tiếp theo sử dụng. Bài có tên người tự điền không gọi lại người học bằng một tên cố định.

## Kiểm chứng

- 24 test JVM/Robolectric: cấu trúc 45 bài/180 lượt, ID cũ, sample/accepted/choices, phiên nghe, trạng thái học, khôi phục, lỗi ghi/đọc, UI chữ lớn, tổng 45, tiếp tục bài cũ và hoàn thành bài gia đình mới.
- 7 test emulator Android 15/API 35: các luồng UI nói trên, DataStore bằng file thật, bàn phím trên MainActivity và tạo lại Activity giữ trạng thái.
- Lint: 0 lỗi, 15 cảnh báo có sẵn về dependency, KTX, cấu hình backup cũ và qualifier/resource icon.
- Kiểm tra riêng bằng `apksigner`: APK cũ và mới có cùng chứng thư ký.
- Kiểm tra cập nhật thật trên emulator: cài APK 15 bài, hoàn thành `greetings-morning`, thấy 1/15; cài đè APK 0.2.0 bằng `adb install -r`, thấy 1/45 và bài cũ vẫn đánh dấu hoàn thành. Log `.tools/45-upgrade-check.log` có PASS; [ảnh sau cập nhật](testing/screenshots/45-lessons-home.png).

Lượt đầy đủ đã đạt tại `.tools/45-final-verification.log`. Kiểm tra cài đè giữ tiến độ còn phát hiện một giả định sai trong test bàn phím: test chọn bài gợi ý, nhưng bài gợi ý thay đổi khi bài đầu đã hoàn thành. Test được sửa để chọn rõ “Chào hỏi / Chào buổi sáng”, không xóa tiến độ để ép test đạt. Suite đầy đủ đạt tại `.tools/45-handoff-final.log`; sau chỉnh một câu mô tả tiếng Việt cho cả chị/em gái, unit/lint/build được chạy lại thành công tại `.tools/45-package-final.log`. Chưa thử micro/TTS bằng giọng nói thật trên điện thoại vật lý; không coi test giao diện bằng adapter giả là kiểm chứng chất lượng nhận diện.

## Rà soát độc lập và sửa lỗi

Reviewer đọc toàn bộ 30 bài và mã tích hợp, kết luận **With fixes**, không có lỗi Critical. Đã xử lý trong một lượt sửa:

1. Đáp án thay thế không được làm câu tiếp theo mâu thuẫn (em gái/con một, sách/sở thích khác, màu túi, loại đồ uống, mượn ô, lịch dã ngoại, tên người). Test hồi quy tái hiện trước khi sửa và đã đạt sau sửa.
2. Thêm biến thể nhận diện thông thường cho `wifi`, `Room two oh five` và dạng đầy đủ của contractions. Có test literal cho từng trường hợp.
3. Nâng vấn đề vị trí đáp án đoán được thành vấn đề chất lượng học và sửa bằng shuffle có seed; test đỏ trước sửa, xanh sau sửa. Căn chỉnh tiêu đề bài vai khách và giải thích ngôi thứ ba số ít để bảo đảm nội dung phù hợp người mới.
4. Cập nhật README và checklist theo đúng 45 bài.

Reviewer không kiểm tra chữ ký APK hay cài đè; hai phần này đã được người triển khai kiểm chứng riêng như trên. Không thuê lượt re-review; các sửa lỗi được xác minh bằng test hồi quy và suite đầy đủ.

## Quyết định và giới hạn

- Triển khai trực tiếp trên nhánh hiện có theo phương án người dùng đã chọn; không tạo checkout mới, không push hoặc merge. Chi phí nếu đổi cách tích hợp sau này: cần hành động Git riêng.
- Tách builder cho nội dung mới và giữ nguyên DSL riêng của bài cũ để giảm rủi ro tiến độ; đổi lại có hai cách khai báo dữ liệu trong mã.
- Những câu trả lời ngoài tập mẫu/biến thể vẫn có thể nhận lời nhắc thử theo mẫu; không coi đây là đánh giá ngữ pháp tổng quát.
- Giữ 15 cảnh báo lint ở trên; không nâng dependency ngoài phạm vi bổ sung nội dung.

## Bản cài

APK bàn giao: `app/build/outputs/apk/debug/KTalk-0.2.0-45-lessons.apk` (có bản sao `KTalk-debug.apk`), version **0.2.0**, versionCode **2**, package `vn.noi.english`. Có thể cài cập nhật trên bản debug cũ cùng chữ ký để giữ tiến độ; không gỡ bản cũ trước khi cập nhật.

Kích thước: **18,185,321 byte** (khoảng 18.2 MB). SHA-256: `08a2d34dfc82187d4183199adbc403202737678871af6e0a7877619ed2b1c34e`.
