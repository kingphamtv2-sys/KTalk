# Kiểm tra Android trên thiết bị

Chỉ đánh dấu khi đã chạy thật. Unit test và build APK không thay thế kiểm chứng micro, TTS hoặc bố cục trên điện thoại.

Thiết bị/API: AVD KTalkS9Api29 (Android 10/API29) và NoiTest35 (Android 15/API35), Google APIs. Khung1080×2220/480dpi=360×740dp theo tỷ lệ S9+, thêm540dpi≈320×658dp; font1.0/1.3/2.0. Chưa có điện thoại thật. Test tự động dùng adapter âm thanh giả cho các luồng không cần micro; không đánh dấu các mục chất lượng âm thanh từ kết quả UI test.

- [x] Cài APK debug và mở ứng dụng; bản 45 bài cài mới có tiến độ 0/45 (kiểm tra UI tự động).
- [x] Cài đè bản 15 bài lên 45 bài vẫn giữ bài cũ đã hoàn thành: 1/15 → 1/45.
- [x] Cài đè bản 0.2.0 lên 0.3.0: giữ 1/45 bài; đánh dấu thẻ rồi đóng/mở lại giữ 1 Đã nhớ / 1 Cần ôn, không đổi tiến độ hội thoại.
- [x] Xem ảnh chụp trang từ vựng, thẻ đã lật và quiz từ APK chạy trên emulator.
- [x] Cài đè 0.3.0 → 0.4.0 giữ cả 1/45 bài và 1 Đã nhớ/1 Cần ôn; tìm không dấu `ao mua`, xem ví dụ và lưu thêm từ; force-stop/mở lại giữ 1 Đã nhớ/2 Cần ôn.
- [x] Xem ảnh chụp Vocabulary300 và thẻ mới có ví dụ Anh–Việt trên APK0.4.0.
- [x] Cài đè0.4.0 →0.4.1 giữ1/45 bài và1 Đã nhớ/1 Cần ôn, đóng/mở lại vẫn giữ.
- [x] Micro adapter thật trên emulator: nhận no-match7 sau ready, thông báo đúng, gõ vẫn kiểm tra được; không thử bằng giọng thật.
- [x] Hộp thoại Android provider thật mở en-US, Back trả trạng thái hủy/Idle, không tự bật lại và vẫn thấy các nút phục hồi.
- [ ] Thử bản0.4.1 trên Galaxy S9+ người dùng, ghi mã SR/SYS nếu lỗi tiếp diễn; chưa có thiết bị kết nối để kiểm chứng.
- [x] Cài đè0.4.1 →0.5.0 giữ1/45bài và1 Đã nhớ/1 Cần ôn qua đóng/mở lại.
- [x] Xem ảnh giao diện mới trên360×740, font1.0/2.0; thẻ từ, quiz, bài học, tiến độ, hội thoại; kiểm tra nút chữ lớn và ô tìm sau rà soát.
- [x] Cài đè APK cuối0.5.0 →0.6.0 giữ1/45 bài và1 Đã nhớ/1 Cần ôn sau force-stop/mở lại; cùng chứng thư ký.
- [x] `MainActivity` thật nhận IME insets, không đẩy cả cửa sổ lên; kiểm tra bàn phím trên API29/35.
- [x] Tạm tắt Google TTS trên AVD, mở lỗi dài + bàn phím ở320dp/font2, nhập/gửi câu trả lời và đóng thông báo; bật lại engine, khôi phục cấu hình.
- [x] Xem ảnh bài học có mục tiêu/vai, lễ tân, kết thúc bài, chữ lớn và nhãn tab Học; rà soát độc lập45bài/180lượt.
- [ ] Mở thủ công đủ 15 chủ đề, 45 bài; từ vựng và hội thoại khác nhau theo từng bài (cấu trúc/đáp án của tất cả bài đã kiểm tra tự động).
- [ ] Từ chối quyền micro, hoàn thành một bài bằng chọn/gõ.
- [ ] Từ chối quyền vĩnh viễn, mở cài đặt rồi quay lại và thử nói.
- [ ] Có dịch vụ nhận diện: thu tiếng Anh, xem/sửa văn bản, thử lại và kiểm tra.
- [ ] Nghe câu hỏi/câu mẫu với giọng tiếng Anh; thử thiết bị thiếu engine hoặc thiếu giọng.
- [ ] Mất mạng/không nghe rõ: hiển thị lỗi kỹ thuật, không báo câu trả lời sai.
- [ ] Bắt đầu nghe rồi chuyển cách trả lời hoặc rời màn hình: không áp dụng callback cũ.
- [ ] Về nền/cuộc gọi, quay lại: micro không tự bật.
- [ ] Xoay màn hình: giữ bài/lượt, có thể nói lại; không hiển thị giả đang nghe.
- [ ] Hoàn thành đủ 4 lượt mới thấy kết quả; học lại không đếm trùng.
- [ ] Đóng/mở ứng dụng: giữ tiến độ, tiếp tục đúng bài/lượt chưa xong.
- [ ] Màn hình nhỏ, fontScale 1.5 và 2.0: nội dung cuộn được, nút không bị che.
- [ ] TalkBack đọc được nhãn nút nghe, micro, chế độ trả lời và tiến độ.

## Tự động

108 test JVM/Robolectric,39 test API29 và39 test API35 đạt cho0.6.0. Gồm nội dung/ID45bài/Vocabulary300, tên Việt, nhánh trả lời nhất quán, sửa văn bản rỗng, gõ/IME, thông báo lỗi cùng bàn phím, Activity thật và các hồi quy micro/ActivityResult. Lint0 lỗi/16 cảnh báo có sẵn; APK build thành công. [Báo cáo](../lesson-quality-report.md). Giả lập không kiểm chứng chất lượng nhận diện/TTS bằng giọng nói thật hay Samsung Keyboard/TalkBack trên S9+ thật.
