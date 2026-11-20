# Kiểm tra Android trên thiết bị

Chỉ đánh dấu khi đã chạy thật. Unit test và build APK không thay thế kiểm chứng micro, TTS hoặc bố cục trên điện thoại.

Thiết bị/API: emulator NoiTest35, Google APIs Android 15 / API 35, Pixel 6. Chưa có điện thoại thật. Test tự động dùng adapter âm thanh giả cho các luồng không cần micro; không đánh dấu các mục chất lượng âm thanh từ kết quả UI test.

- [x] Cài APK debug và mở ứng dụng; bản 45 bài cài mới có tiến độ 0/45 (kiểm tra UI tự động).
- [x] Cài đè bản 15 bài lên 45 bài vẫn giữ bài cũ đã hoàn thành: 1/15 → 1/45.
- [x] Cài đè bản 0.2.0 lên 0.3.0: giữ 1/45 bài; đánh dấu thẻ rồi đóng/mở lại giữ 1 Đã nhớ / 1 Cần ôn, không đổi tiến độ hội thoại.
- [x] Xem ảnh chụp trang từ vựng, thẻ đã lật và quiz từ APK chạy trên emulator.
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

41 test JVM/Robolectric và 14 test trên emulator đều đạt cho bản 0.3.0. Bao gồm toàn bộ nội dung 45 bài, tiếp tục tiến độ cũ, hoàn thành một bài mới, cỡ chữ lớn, bàn phím trên MainActivity thật, tạo lại Activity và DataStore lưu/khôi phục bằng file thật. Bổ sung thẻ từ, quiz, chặn chuyển tab trong lúc lưu và xử lý lỗi của hai kho tiến độ độc lập. Lint 0 lỗi / 15 cảnh báo có sẵn; APK debug build thành công. Chi tiết trong README và báo cáo Vocabulary. Các mục thủ công được ghi riêng để tránh nhầm với chất lượng micro/TTS.
