# Kiểm tra Android trên thiết bị

Chỉ đánh dấu khi đã chạy thật. Unit test và build APK không thay thế kiểm chứng micro, TTS hoặc bố cục trên điện thoại.

Thiết bị/API: emulator NoiTest35, Google APIs Android 15 / API 35, Pixel 6. Chưa có điện thoại thật. Test tự động dùng adapter âm thanh giả cho các luồng không cần micro; không đánh dấu các mục chất lượng âm thanh từ kết quả UI test.

- [x] Cài APK debug và mở ứng dụng; cài mới có tiến độ 0/15.
- [ ] Mở đủ 5 chủ đề, 15 bài; từ vựng và hội thoại khác nhau theo từng bài.
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

18 test JVM/Robolectric và 5 test trên emulator đều đạt. Test bổ sung kiểm tra bàn phím mở trên MainActivity thật, nút kiểm tra vẫn bấm được; kiểm tra DataStore lưu/khôi phục bằng file thật. Lint 0 lỗi / 12 cảnh báo; APK debug build thành công. Chi tiết trong README và báo cáo triển khai. Không đánh dấu checklist thủ công từ kết quả tự động.
