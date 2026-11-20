# Vocabulary — Học từ vựng trong KTalk

Người dùng yêu cầu tự chọn phương pháp phù hợp người mới và triển khai ngay. Giữ native execution, không cần vòng hỏi thiết kế khác.

## Phạm vi

Thêm tab **Từ vựng**, tiêu đề **Vocabulary – Học từ vựng**, học độc lập từ nội dung 45 bài. Dùng flashcards + nghe mẫu + tự đánh dấu mức nhớ + quiz chọn nghĩa ngắn. Đây là bản đầu đơn giản; chưa có thuật toán ôn cách quãng hoặc nhắc lịch.

Gộp các mục có cùng tiếng Anh (không phân biệt hoa/thường, chuẩn hóa khoảng trắng/ellipsis/apostrophe), gom nghĩa và nguồn chủ đề. ID dựa trên tiếng Anh chuẩn hóa, không phụ thuộc thứ tự hoặc bản dịch. Không gọi 135 mục là 135 từ duy nhất; hiển thị số sau khi gộp từ dữ liệu thật.

Trang chính có thống kê Đã nhớ / Cần ôn / Chưa học, lọc chủ đề, Học thẻ từ, Ôn từ cần nhớ, Kiểm tra nhanh. Flashcard ban đầu chỉ hiện tiếng Anh; bấm Xem nghĩa để lật, xem nghĩa Việt và bài nguồn, nghe mẫu bất cứ lúc nào. Sau khi xem nghĩa mới đánh dấu Đã nhớ hoặc Cần ôn; lưu thành công rồi sang thẻ tiếp. Cho qua thẻ không đánh dấu, quay lại thẻ trước, kết thúc về trang chính. Không tự coi xem thẻ là đã nhớ.

Quiz tối đa 5 từ trong chủ đề đang chọn (ít hơn khi chủ đề ít từ), ưu tiên từ cần ôn/chưa học và xáo thứ tự trong nhóm. Mỗi câu có 4 nghĩa khác nhau, chỉ một đáp án, loại toàn bộ nghĩa đã biết của chính từ đó khỏi distractors. Cho nghe tiếng Anh, phản hồi đáp án đúng/sai và nghĩa đúng; chốt một lựa chọn mỗi câu. Kết quả chỉ thuộc lượt quiz hiện tại. Quiz không tự thay đổi trạng thái Đã nhớ/Cần ôn, được nói rõ trên màn hình.

## Dữ liệu và điều hướng

Lưu hai tập word ID đã nhớ/cần ôn bằng Preferences DataStore riêng `vocabulary_progress`, chỉnh một mục bằng transaction giữ hai tập loại trừ nhau. Không sửa `learning_progress`, không đánh dấu bài hội thoại hoàn thành khi học từ. Trạng thái đọc/ghi lỗi có Retry, không báo lưu thành công hoặc tự sang thẻ khi ghi lỗi. Không mất từ đang học khi lưu lỗi. Không ghi đè trạng thái rỗng khi đọc lỗi.

VocabularyViewModel riêng giữ màn Home/Cards/CardSummary/Quiz/QuizResult, chủ đề, vị trí, mặt thẻ và lượt quiz trong vòng đời Activity. Tiến độ nhớ từ lưu trên máy; thẻ/quiz đang làm có thể bắt đầu lại khi ứng dụng bị đóng. Khi đổi tab hoặc thẻ/câu hỏi phải dừng đọc mẫu. Không dùng micro trong chế độ này. Thiếu TTS vẫn học được bằng chữ và có đường mở cài giọng.

Nav dưới 3 mục: Học mỗi ngày / Từ vựng / Tiến độ. Có thể quay về trang chính Vocabulary bằng Back từ phiên học; Back ở trang chính về Học mỗi ngày. Chặn thao tác đổi phiên/tab trong thời gian đang ghi. Giữ nguyên phần 3 từ trước bài hội thoại.

## Kiểm chứng và bàn giao

Test catalog gộp từ/giữ các nghĩa; quiz không mơ hồ và không tăng điểm khi bấm lại; tự đánh dấu, lọc chủ đề, ôn danh sách rỗng, lỗi đọc/ghi, khôi phục tiến độ riêng, không sửa tiến độ hội thoại. Compose tests cho thẻ/quiz và chữ lớn; Android test lưu file thật. Chạy hồi quy 45 bài và build/lint, rà soát độc lập cuối. Bump version 0.3.0/code3, giữ package/chữ ký. APK KTalk-0.3.0-vocabulary.apk.
