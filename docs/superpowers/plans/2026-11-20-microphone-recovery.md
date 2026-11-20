# Microphone Recovery Implementation Plan

> **For agentic workers:** superpowers:executing-plans, native execution/end review already chosen. Bugfix authorized directly; retain checkout and existing design.

**Goal:** Không kẹt ở lỗi chung khi micro không khởi động được, thêm đường nhận diện Android và khôi phục an toàn.
**Architecture:** SpeechInputErrors + production adapter startup recovery callback; SystemSpeechRequest manages request identity/intent/result; MainActivity launcher integration; Preparing UI and recovery actions.
**Spec:** docs/superpowers/specs/2026-11-20-microphone-recovery-design.md

## Review focus

Không auto bật lại micro sau cancel/đổi bài; không fallback vòng lặp khi không nghe rõ/quyền/mạng; result không sai lượt sau rotate; package visibility cho recognizer activity; không đánh dấu phát âm sai khi lỗi kỹ thuật.

- [x] RED production adapter tests/API28,35 for startup error recovery once, readiness, no-match/network/permission/after-ready/stop, stale result/cancel/timeout; implement error mapping/Preparing/recovery then GREEN.
- [x] RED intent/result identity tests and UI preparing/recovery actions. Implement SystemSpeechRequest + MainActivity register launcher/save pending state/settings + manifest queries + UI, GREEN.
- [x] Independent end review; fix Important by regression, full JVM/Android/lint/build and same-signature APK0.4.1. Test upgrade from0.4.0 and preserve progress, inspect recovery UI. Report exact hardware test limits and commit locally.
