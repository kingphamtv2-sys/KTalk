# KTalk 0.4.1 — Phục hồi nhận diện giọng nói

Người dùng báo Galaxy S9+ lỗi ngay khi chạm micro. Không có điện thoại kết nối ADB nên chưa biết mã lỗi/engine thực tế; không kết luận là lỗi phát âm, mạng hay Samsung. Điều tra mã thấy lỗi audio/client/server/ngôn ngữ đều bị gộp thành thông báo chung; trạng thái Đang nghe cũng được đặt trước callback sẵn sàng.

Giữ native SpeechRecognizer trong app. Khi dịch vụ không có hoặc lỗi kỹ thuật ngay trước ready, thử hộp thoại ACTION_RECOGNIZE_SPEECH của Android một lần trong cùng thao tác. Không tự chuyển khi không nghe rõ, mất mạng, quyền bị từ chối, đang xử lý sau khi đã ghi âm, hoặc người dùng đã bấm dừng. Kết quả hộp thoại chỉ điền văn bản để sửa/kiểm tra, không tự chấm hay sang lượt. Kết quả cũ phải bị bỏ nếu khác bài/lượt/chế độ; giữ pending request qua tạo lại Activity, không giữ âm thanh.

Thêm Preparing, chỉ Listening sau onReadyForSpeech. Thông báo theo mã lỗi có hướng xử lý, mã SR để hỗ trợ nếu lỗi kỹ thuật; log chỉ mã/trạng thái, không log nội dung nói. Cung cấp nút thử hộp thoại Android và cài đặt nhận diện khi lỗi; thiếu provider vẫn dùng chọn/gõ, không crash. Không hardcode package Google/Samsung, không đổi backend hoặc lưu tiến độ.

Test production adapter bằng Robolectric API28 và35: startup lỗi5/3/4/11/ngôn ngữ, không nghe rõ/network/permission, ready/stop, timeout/cancel/stalecallbacks, fallback một lần. Test intent/result lifecycle và Compose trạng thái mới/actions. Full JVM/Android/lint/build, review độc lập cuối, APK0.4.1/code5 cùng chữ ký; chưa tuyên bố đã kiểm chứng bằng giọng thật trên S9+.
