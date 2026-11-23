# KTalk: chất lượng bài học và sử dụng trên S9+

Người dùng giao quyền cải thiện bài học hiện có và kiểm tra giao diện S9+. Giữ người Việt mới bắt đầu làm đối tượng, 45 bài/15 chủ đề, 4 lượt/bài, 300 mục từ, Kotlin/Compose và tiến độ cục bộ. Tiếp tục triển khai trực tiếp với một rà soát độc lập cuối như đã chọn trước đó.

## Cách làm

Chỉ sửa câu chữ sẽ không giải quyết việc thiếu mục tiêu và vai học; thêm cơ chế chấm điểm/AI hoặc tăng số lượt lại vượt phạm vi. Chọn biên tập nội dung kết hợp hướng dẫn thực hành ngắn ngay trong luồng hiện tại. Đây là lựa chọn của người triển khai trong quyền tự quyết người dùng đã giao.

- Mỗi bài có mục tiêu giao tiếp, vai người học, vai đối tác và mẹo áp dụng riêng. Trước bài hiển thị tên/tình huống/mục tiêu trước ba cụm từ. Kết thúc nhắc lại điều vừa luyện và cách tập lại bằng lời của mình.
- Biên tập 180 đáp án nhiễu thành tiếng Anh có nghĩa: sai do không trả lời đúng câu hỏi hoặc sai dữ kiện đã nêu. Không đưa một câu trả lời hợp lệ nhưng chưa được matcher hỗ trợ làm đáp án sai. Vị trí đáp án đúng phân bố theo lượt ổn định, không cố định xen kẽ cho 15 bài đầu.
- Chỉnh lời thoại giống bài kiểm tra/ép tên cá nhân thành hội thoại tự nhiên hơn; giữ ID bài/lượt, từ vựng và các câu mẫu đang tương thích. Câu dẫn mới phải khớp bản dịch và gợi ý.
- Đối tác hiển thị đúng vai (lễ tân, tài xế, nhân viên…), không phải Alex cho mọi tình huống. Giữ lời giải thích tiếng Việt và nghe mẫu.
- Kiểm tra cả bố cục 360×740dp và hiển thị phóng lớn 320×658dp theo tỷ lệ18.5:9, font1.0/1.3/2.0, bàn phím, cuộn/đi tiếp, thông báo, chọn/gõ, từ vựng và quiz. Không tuyên bố đã thử máy Samsung thật khi chỉ có AVD.
- Chỉ sửa bất tiện có bằng chứng; tối thiểu kiểm tra việc đọc phản hồi/đi tiếp sau khi gõ, nút không bị bàn phím che và mẫu sửa không bắt người học cuộn ngược tìm câu hỏi.

## Tương thích và nghiệm thu

Không đổi schema DataStore, ID hay thứ tự45 bài/300 mục từ. Nâng APK cùng chữ ký, kiểm tra cài đè. Chạy toàn bộ JVM, Android10/API29 và API35, lint/build; kiểm tra ảnh thực từ APK. Lưu biên bản nội dung và kiểm thử. Không quảng cáo đạt CEFR/chấm phát âm chi tiết. Không đổi hệ thống nhận diện giọng nói.
