# Mở rộng KTalk lên 45 bài

Người dùng cho phép tự chọn thêm 30 bài giao tiếp hằng ngày cho người mới, cập nhật trực tiếp ứng dụng. Giữ cách triển khai native trong chat đã thống nhất.

Hướng chọn: thêm 10 chủ đề, mỗi chủ đề 3 bài, thay vì kéo dài 5 chủ đề cũ. Lý do: tăng độ bao phủ tình huống hằng ngày, giữ các nhóm bài ngắn dễ chọn. Không thay luồng học hoặc chức năng âm thanh.

Chủ đề mới: Gia đình; Sinh hoạt hằng ngày; Thời gian và lịch hẹn; Thời tiết; Đi lại; Khách sạn; Sở thích và thời gian rảnh; Nhờ giúp đỡ; Nhà cửa và nơi ở; Gọi điện và nhắn tin.

Mỗi bài có tình huống riêng, 3 cụm từ kèm nghĩa Việt, 4 lượt có câu hỏi/bản dịch/gợi ý/câu mẫu/lời giải thích tiếng Việt, biến thể được chấp nhận và lựa chọn sai theo ngữ cảnh. Mục tiêu A1, câu ngắn, dùng được ngay. Không dùng nội dung y tế/pháp lý, không thêm AI hoặc tài khoản.

Giữ nguyên ID và thứ tự 15 bài cũ, giữ package/signature/DataStore để cài cập nhật không mất tiến độ. Tổng mới: 15 chủ đề, 45 bài, 180 lượt. UI lấy tổng bài và mẫu số chủ đề từ dữ liệu, không ghi cố định 15 hoặc 3. Số từ và lượt trong mỗi bài vẫn cố định 3/4 như hiện tại.

Kiểm chứng: mọi bài chơi được, ID và câu hỏi không trùng; sample/accepted đều hợp lệ, đúng một lựa chọn khớp; bảo toàn ID bài cũ; tiến độ cũ giữ nguyên và mẫu số mới là 45; test hoàn thành một bài mới qua UI; build/lint và các test hồi quy. Tăng versionCode lên 2, versionName 0.2.0, build lại KTalk-debug.apk.
