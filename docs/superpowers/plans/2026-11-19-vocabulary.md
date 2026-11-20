# Vocabulary Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:executing-plans. Native execution and independent end review already chosen by user.

**Goal:** Tab học từ vựng độc lập, flashcards/nghe mẫu/tự đánh dấu/quiz ngắn, lưu riêng trên thiết bị.
**Architecture:** Catalog + quiz generator thuần Kotlin; VocabularyRepository DataStore riêng; VocabularyViewModel độc lập; VocabularyModeScreen kết nối với nav hiện tại.
**Spec:** docs/superpowers/specs/2026-11-19-vocabulary-design.md.

## Tasks

- [x] Catalog/quiz: test đỏ gộp từ và nghĩa, ID ổn định, nguồn chủ đề; tạo question có đúng một nghĩa đúng, distractor không lấy nghĩa khác của cùng từ. Implement vocabulary/VocabularyCatalog.kt.
- [x] State/persistence: test đỏ flashcard lật, mark transaction rồi mới advance, lỗi đọc/ghi, ôn rỗng, quiz chỉ chấm một lần, lọc topic và Back; implement VocabularyState.kt/VocabularyViewModel.kt/data/VocabularyRepository.kt. Test file thật trên Android.
- [x] UI/nav/audio: test đỏ tab, lật/nghe/đánh dấu, quay lại còn mark, quiz và kết quả, chữ lớn. Implement ui/screens/VocabularyModeScreen.kt, thêm Screen.VocabularyMode, MainActivity ViewModel và NoiApp nav3 + audio lifecycle; giữ màn Vocabulary trước hội thoại.
- [x] Rà soát độc lập, sửa bằng regression test, full JVM/Android/lint/build; bump 0.3.0/code3; xác minh ký, cập nhật tài liệu, bàn giao APK và commit cục bộ.

## Review focus

Quiz không có hai nghĩa đúng; tiến độ hội thoại không bị ghi bởi từ vựng; ghi lỗi không sang thẻ; nhấn lặp không đếm điểm/lưu hai lần; Back/tab/đổi thẻ không phát âm cũ; lọc và ôn rỗng không crash; đọc lỗi không giả số liệu đã nhớ bằng0.
