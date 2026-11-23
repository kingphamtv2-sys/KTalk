# KTalk 0.5.0 — Giao diện hiện đại, gọn và rõ

Người dùng yêu cầu hiện đại hóa giao diện và chọn **tự chọn phong cách phù hợp, triển khai luôn**. Giữ native Kotlin/Compose, cách triển khai trực tiếp và một lượt rà soát cuối. Tái sử dụng checkout hiện có.

## Hướng thiết kế của người triển khai

Chọn tối giản, chuyên nghiệp, xanh KTalk làm màu chính, nền sáng hơi xanh, chữ xanh mực, nhấn teal/tím nhẹ cho nhóm chủ đề. Giữ Be Vietnam Pro và biểu tượng launcher. Chỉ thay màu sẽ ít cải thiện tính sử dụng; đổi sang trò chơi nhiều màu hoặc dark-first làm xa phong cách đã chọn. Vì vậy đổi đồng bộ bố cục và thành phần, giữ nhận diện quen thuộc.

## Thiết kế

- Theme có token màu rõ ràng, tiêu đề line-height gọn hơn, body dễ đọc và tương phản đủ. Card bo24, nút16, khoảng cách16, vùng chạm tối thiểu48dp; bố cục co giãn/font2.0, không ấn định chiều cao nội dung chữ.
- Trang Học mỗi ngày: thương hiệu gọn, thông điệp ngắn, hero gradient xanh cho bài gợi ý/tiếp tục, chủ đề thành card có icon và trạng thái thật. Không chế tác streak/XP/tài khoản/mục tiêu vì không có dữ liệu đó.
- Vocabulary: tìm kiếm dễ nhận biết, bộ lọc gọn, card tiến độ rõ, lượt10thẻ và quiz5câu nổi bật. Flashcard lật giữ nguyên điều kiện đánh dấu; nghĩa/ví dụ rõ nhóm. Hai nút Đã nhớ/Cần ôn đặt ngang khi đủ rộng và xếp dọc ở chữ lớn.
- Hội thoại: gộp người nói vào card câu hỏi, thông tin phụ gọn. Gợi ý ngắn vẫn hiện trước chỗ trả lời; ví dụ đầy đủ/nghe mẫu ở sau vùng trả lời để micro không bị đẩy quá sâu. Micro thành hàng có nút rõ và trạng thái bên cạnh. Toggle Ẩn/Hiện vẫn ẩn/hiện cả gợi ý và ví dụ. Không đổi logic nói/chọn/gõ, Preparing, fallback Android hoặc quyền micro.
- Tiến độ: summary có vòng tiến độ và danh sách chủ đề đồng nhất. Danh sách bài, phần từ trước hội thoại, hoàn thành, thanh điều hướng, thông báo lỗi và thiếu giọng đọc theo cùng hệ thống màu/bo góc.

## Ràng buộc và nghiệm thu

Không sửa nội dung45bài/300mục, matcher, ViewModel, DataStore hoặc adapter audio. Không dependency/permission mới. Giữ nhãn hành động/tag được test, chỉ chỉnh vị trí/trình bày; giữ windowInsets/IME và key cuộn theo lượt. Không che action khi chữ lớn hay lỗi dài. Package/khóa ký cũ, version0.5.0/code6 để cài đè giữ tiến độ.

Test hành vi có giá trị: micro thấy được ngay sau mở hội thoại trên viewport360×680 với gợi ý bật; Vocabulary tìm/lật/nghe/lưu trên viewport nhỏ font2.0. Giữ toàn bộ regression, test thiết bị/IME/system speech, full JVM/lint/Android/build, chụp và xem giao diện thực từ APK. Rà soát độc lập cuối và test nâng cấp0.4.1→0.5.0. Không coi kiểm thử UI là kiểm chứng giọng thật S9+.
