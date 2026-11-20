# KTalk 0.4.0 — Mở rộng từ vựng và cải thiện sử dụng

Người dùng giao tự chọn nội dung cho người Việt mới bắt đầu, tiếp tục triển khai trực tiếp và yêu cầu kiểm thử kỹ/cải thiện chức năng hiện có. Giữ Kotlin/Compose, thiết kế đã chọn, lưu trên máy và không tài khoản/backend. Tái sử dụng checkout hiện có theo cách triển khai các lượt trước.

## Phương án và lựa chọn triển khai

Thêm bài hội thoại mới sẽ thay đổi phạm vi 45 bài; nhập từ qua mạng làm phức tạp nội dung và lưu trữ. Chọn bổ sung một danh mục từ vựng mở rộng đóng gói cùng app, gắn vào 15 chủ đề hiện có, giữ nguyên 135 mục gốc và ID tiến độ. Đây là lựa chọn của người triển khai trong phạm vi tự chọn đã được giao.

## Nội dung và trải nghiệm

- Thêm 165 từ/cụm từ (11 mỗi chủ đề), tổng 300 mục tiếng Anh sau chuẩn hóa. Mỗi mục mới có nghĩa Việt và câu ví dụ Anh–Việt ngắn, có nút nghe ví dụ. Không gán sai các từ mở rộng vào bài hội thoại chưa chứa chúng.
- Tìm kiếm theo tiếng Anh hoặc nghĩa Việt, hỗ trợ tiếng Việt không dấu (gồm đ/Đ), kết hợp bộ lọc chủ đề. Khi không có kết quả, thông báo rõ và không cho bắt đầu phiên rỗng. Thống kê áp dụng trên kết quả đang lọc.
- Mỗi lượt flashcard tối đa 10 mục, ưu tiên Cần ôn, Chưa học rồi Đã nhớ như cũ. Không đánh dấu bỏ qua; đánh dấu chỉ sau khi lật và chỉ chuyển khi ghi thành công. Quiz tối đa 5 câu và không thay đổi mức nhớ.
- Kiểm tra và sửa các lỗi có bằng chứng, tập trung vào vị trí cuộn khi sang lượt hội thoại và biến thể dạng đầy đủ/viết tắt của câu trả lời. Không nới kiểm tra để bỏ qua phủ định hoặc chấp nhận câu sai.
- Giữ khả năng học khi thiếu dịch vụ giọng nói, chữ lớn, hai kho tiến độ độc lập và điều hướng an toàn khi đang ghi.

## Kiểm chứng và bàn giao

Test nội dung đủ 300, ID cũ không đổi, 20 mục/chủ đề, ví dụ mới đầy đủ và mọi thẻ tạo đủ lựa chọn quiz. Test tìm không dấu/tổ hợp bộ lọc/kết quả rỗng, phiên ngắn/ưu tiên/khôi phục, regression hội thoại. Kiểm tra UI font2.0, nghe ví dụ và vòng đời âm thanh; chạy JVM, Android API35, lint, build. Một lượt rà soát độc lập cuối, sửa Important bằng regression. Thử nâng cấp thực từ APK0.3.0 để giữ cả tiến độ hội thoại/từ vựng. Bàn giao APK0.4.0/code4, cùng package/chữ ký, lưu commit cục bộ.
