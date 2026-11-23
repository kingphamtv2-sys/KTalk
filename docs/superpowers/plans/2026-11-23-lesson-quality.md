# KTalk Lesson Quality Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Triển khai trực tiếp theo lựa chọn hiện có, một rà soát độc lập cuối.

**Goal:** 45 bài có mục tiêu/vai rõ, lựa chọn có nghĩa, hội thoại tự nhiên và học được thuận tiện ở kích thước S9+.
**Architecture:** Thêm catalog LessonGuide tra theo ID; biên tập dữ liệu tại nguồn. Compose sử dụng guide ở trước bài/hội thoại/hoàn thành. Không đổi lưu trữ hoặc audio.
**Tech Stack:** Kotlin, Compose, JUnit/Robolectric, AVD29/35 hiện có.
**Spec:** docs/superpowers/specs/2026-11-23-lesson-quality-design.md

## Global Constraints

45 bài/15 chủ đề, 4 lượt/bài, 300 mục từ; giữ toàn bộ ID/thứ tự, schema, chữ ký. Không dependency ứng dụng mới.

## Review Focus

Đáp án nhiễu vô tình hợp lệ; nhánh trả lời cá nhân mâu thuẫn lượt sau; vai sai trong gọi điện; mục tiêu không được thực hành; font2.0/bàn phím/thông báo chiếm vùng học. Kiểm tra mẫu và toàn bộ nguồn, không dùng test đối chiếu lại chính builder.

### Task 1: Nội dung và hướng dẫn

Files: content/LessonGuide.kt mới; Curriculum.kt, Distractors.kt, EverydayTopics.kt, TravelTopics.kt, SocialTopics.kt; ui/screens/VocabularyScreen.kt, ConversationScreen.kt, CompletionScreen.kt; LearningFlowTest.kt; CurriculumTest.kt.
- [x] RED UI: trước bài thấy tên và mục tiêu; khách sạn hiển thị lễ tân; hoàn thành thấy nội dung đã luyện. Run testDebugUnitTest --tests '*LearningFlowTest*', Expected: các test mới FAIL do thiếu hướng dẫn/vai.
- [x] Thêm45 guide (learnerRole/partnerRole/objective/tip), hiển thị theo ID; biên tập180 lựa chọn và các lời dẫn gượng. Giữ4lượt và từ vựng, bỏ giả định cố định tên Linh khi slot cho phép tên khác. Phân phối vị trí bằng shuffle có seed turnID cho cả45bài.
- [x] Test đầy đủ45guide, ID cũ, mọi sample/accepted/choice, nhánh cá nhân. Chạy toàn bộ testDebugUnitTest, Expected:0failure.

### Task 2: Thuận tiện sử dụng

Files: ConversationScreen.kt, VocabularyModeScreen.kt, NoiApp.kt, AndroidManifest.xml nếu thực sự cần resize; LearningFlowTest.kt.
- [x] Tái hiện và chụp bất tiện trên AVD ở360/320dp; RED hành vi trước sửa. Không thay đổi chỉ vì sở thích.
- [x] Sửa nhỏ nhất; kiểm tra keyboard action giữ đáp án, phản hồi và nút đi tiếp cuộn tới được; font2.0 mọi nút48dp+, filter/quiz/micro fallback dùng được.
- [x] Chạy toàn bộ JVM và Android29/35; lint, assemble; Expected0failure,0lint errors.

### Task 3: Review và bàn giao

- [x] Review độc lập toàn bộ diff và biên tập45bài/180lượt, ảnh S9+ thường/phóng lớn/chữ lớn. Phân loại findings, một lượt sửa với regression nếu cần.
- [x] Version0.6.0/code7; verify cùng chữ ký; cài đè từ0.5.0 giữ bài1/45 và từknown/review. Ảnh thực, báo cáo, APK trong releases; source commit/sync vào cùng origin theo quyền đã có.
