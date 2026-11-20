# Vocabulary Expansion Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:executing-plans. Native execution and one independent end review already selected; no further design/permission gate requested.

**Goal:** 300 mục từ phù hợp người mới, tìm kiếm và ví dụ, lượt học ngắn, sửa lỗi hiện có có bằng chứng.
**Architecture:** EverydayVocabulary bổ sung WordCard vào VocabularyCatalog bằng ID chuẩn hóa; VocabularyState/VM chứa query và giới hạn phiên; Compose hiển thị tìm kiếm/ví dụ. DataStore schema và ID cũ không đổi.
**Tech Stack:** Kotlin/Compose, DataStore, SDK35/JDK17 hiện có; không dependency mới.
**Spec:** docs/superpowers/specs/2026-11-20-vocabulary-expansion-design.md

## Global constraints

Giữ 45 bài hội thoại, 15 chủ đề, package vn.noi.english, thiết kế và dữ liệu cũ. Cài cập nhật bằng chữ ký debug cũ. Nội dung mới là từ vựng mở rộng, không giả nguồn bài.

## Review focus

- Các nghĩa gần nhau trong 300 mục không xuất hiện như hai đáp án đúng trong quiz; kiểm tra toàn bộ catalog và đọc nội dung.
- ID cũ và các mức nhớ cũ không bị mất khi thêm danh mục; kiểm tra nâng cấp0.3→0.4.
- Tìm kiếm không dấu/đ, khoảng trắng, kết hợp chủ đề hoặc không kết quả; test state +UI.
- Phiên10 thẻ và quiz5 không ghi ngầm mức nhớ, lỗi ghi vẫn giữ thẻ; test VM và hồi quy cũ.
- Sang lượt hội thoại ở font lớn phải thấy câu hỏi mới; dạng đầy đủ vẫn giữ phủ định; regression UI/matcher.

### Task 1: Nội dung và catalog

Files: vocabulary/EverydayVocabulary.kt (new); VocabularyCatalog.kt; VocabularyCatalogTest.kt.
Interfaces: WordExample(english,vietnamese), WordCard.example nullable mặc định; EverydayVocabulary.cards List<WordCard>; catalog giữ fromTopics và wordId.
- [x] RED: yêu cầu300 mục duy nhất, mỗi chủ đề20, 165 ví dụ, ID/nguồn của135 gốc không đổi, quiz đủ4 lựa chọn cho mọi mục với nhiều seed.
- [x] Implement dữ liệu165 mục và merge giữ nghĩa/topic/lesson/example; chạy catalog test GREEN.

### Task 2: Tìm kiếm, phiên ngắn và UI

Files: VocabularyState.kt, VocabularyViewModel.kt, VocabularyModeScreen.kt, VocabularyViewModelTest.kt, LearningFlowTest.kt.
Interfaces: state.query:String, state.eligible lọc topic+query; model.setQuery(String); session giới hạn10; example chỉ hiện sau lật.
- [x] RED: tìm cả Anh/Việt/không dấu/đ; lọc kết hợp, không kết quả, phiên10 ưu tiên, mark cũ khôi phục. UI tìm một từ mới, nghe/lật/ví dụ/mark, không kết quả/chữ lớn.
- [x] Implement query chuẩn hóa; UI label/nút xóa, trạng thái rỗng, max10 và ví dụ; update test tổng300; GREEN.

### Task 3: Hồi quy chức năng cũ

Files: AnswerMatcher.kt/Test, NoiApp.kt, LearningFlowTest.kt.
- [x] Reproduce câu trả lời dạng đầy đủ bị từ chối trong bài thật; RED cho tương đương contraction và không bỏ phủ định; sửa tối thiểu rồi GREEN.
- [x] RED: cuộn xuống trả lời ở font2, sang lượt tiếp theo phải thấy câu hỏi mới mà không cuộn tay; reset Page theo lesson/turn; GREEN.

### Task 4: Nghiệm thu và bàn giao

- [x] Rà soát độc lập mã/nội dung, sửa Important/Critical bằng regression.
- [x] Bump0.4.0/code4; full JVM/Android/lint/build; kiểm tra chữ ký và nâng cấp từ APK0.3.0 đã seed hai tiến độ, mở lại giữ đúng dữ liệu.
- [x] Xem ảnh màn hình mới, cập nhật README/báo cáo nội dung/checklist, APK durable releases, commit cục bộ.
