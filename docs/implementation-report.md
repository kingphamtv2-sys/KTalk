# Báo cáo triển khai bản Android đầu tiên

## Phạm vi

Kotlin, Compose, giao diện phương án 02, 5 chủ đề / 15 bài / 60 lượt hội thoại, giải thích tiếng Việt, nói/chọn/gõ, nghe mẫu và DataStore. Nội dung và font được đóng gói, không có đăng nhập, backend, API key, LLM hoặc chấm phát âm.

## Rà soát

Reviewer độc lập đã đọc mã và gửi 3 phát hiện cụ thể trước khi phiên reviewer bị dừng bởi hạn mức nhà cung cấp, chưa gửi kết luận đầy đủ. Không xem đây là phê duyệt độc lập toàn bộ mã. Người triển khai tiếp tục tự rà soát vòng đời, quyền micro, lưu dữ liệu, xử lý lỗi và thực hiện kiểm chứng bên dưới.

Ba phát hiện đã tái hiện bằng test thất bại và sửa:

1. Slot tên riêng nhận nhầm câu như “I'm a student”, “I'm from Vietnam”, “My name is is”. Phân loại slot tên người/nơi ở, chặn cụm câu ở slot tên người, giữ hỗ trợ tên tiếng Việt có dấu.
2. Toàn bộ câu hỏi dùng một lựa chọn sai giống nhau. Thay bằng 60 lựa chọn được soạn theo từng lượt, gồm đối chiếu trái/phải tại bài hỏi đường.
3. Dấu câu ở giữa câu khiến bản nhận diện giọng nói và câu nhập có dấu bị xử lý khác nhau. Chuẩn hóa dấu câu về ranh giới từ, giữ nguyên phủ định và so khớp toàn câu.

Test nhập câu trên emulator có một lần bấm trượt khi bàn phím hệ thống đang chuyển trạng thái. Lượt chẩn đoán riêng xác nhận ViewModel nhận đúng câu và phản hồi. Test chuyển chế độ đợi bàn phím đóng bằng Espresso trước thao tác bấm tiếp theo.

Kiểm tra thủ công trên MainActivity thật phát hiện khoảng đệm IME bị tính hai lần, để thừa 336dp trên bàn phím. Test thiết bị mới tái hiện lỗi này trước khi sửa. Đã đánh dấu phần inset Scaffold đã dùng bằng `consumeWindowInsets`, kiểm chứng nhập và bấm kiểm tra ngay khi bàn phím đang mở. Test mới đạt cùng toàn bộ suite.

## Các quyết định trong triển khai

- Dùng thư mục dự án mới ngay tại `G:/learnEnglish`, khởi tạo Git với nhánh `feature/android-mvp` sau khi xác định Git có sẵn. Không có checkout hoặc nhánh cũ cần tách; không merge/push. Hạn chế: không có lịch sử trước phiên triển khai.
- Dùng Robolectric cho cùng các test Compose bên cạnh emulator. Lợi ích: kiểm tra UI ngay trên JVM; hạn chế: không chứng minh phần cứng micro hoặc engine giọng đọc.
- Chạy kiểm tra lưu file thật trên Android thay cho Windows, vì FileStorage của Android phụ thuộc hành vi thay thế file không giống Windows. Hạn chế: kiểm tra này cần emulator/thiết bị; không bỏ kiểm tra lưu dữ liệu.
- Các nhãn Compose tiếng Việt được giữ trong Kotlin ở bản một ngôn ngữ; app name/theme/font dùng resources. Hạn chế: cần tách resources nếu bổ sung ngôn ngữ giao diện.
- Giữ mã nguồn và công cụ cục bộ trong dự án, không cài lại global PATH. Gradle wrapper, font và giấy phép có trong Git; JDK/SDK/cache/build không đưa vào Git.

## Giới hạn kiểm chứng

Không có điện thoại thật hoặc phiên nói thật để đánh giá chất lượng nhận diện/phát âm mẫu. UI tests dùng adapter âm thanh giả khi thử chọn/gõ và thiết bị không có dịch vụ. Dịch vụ Android thật được tích hợp nhưng chất lượng micro/TTS còn phụ thuộc thiết bị, giọng cài sẵn và mạng. Không coi kiểm thử giao diện là kiểm thử chất lượng âm thanh.

Giữ các phiên bản dependency đã ghim trong kế hoạch; 12 cảnh báo lint được để lại: 9 gợi ý phiên bản dependency mới, 2 gợi ý KTX và 1 gợi ý cấu hình backup cho Android cũ. `allowBackup=false` đã áp dụng cho Android cũ và có quy tắc loại trừ backup/transfer cho Android 12+. Đây không phải lỗi build. Không thêm migration phiên bản dữ liệu vì đây là bản cài đầu.

## Kết quả cuối

Đã chạy ngày 2026-11-19:

```powershell
./gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedDebugAndroidTest
```

- BUILD SUCCESSFUL; 18/18 test JVM/Robolectric đạt.
- 5/5 test trên emulator Google APIs Android 15 / API 35 đạt: file DataStore thật, keyboard insets trên MainActivity và tạo lại Activity giữ trạng thái, cỡ chữ 2x, gõ khi thiếu speech, hoàn thành đủ 4 lượt bằng chọn.
- Lint: 0 lỗi, 12 cảnh báo như trên.
- APK: `app/build/outputs/apk/debug/app-debug.apk`, 18,536,326 byte (khoảng 18.5 MB).
- SHA-256: `e983bb25281d77f8eef9101401a952d296e654be573f9ae263123da78a45778d`.
- APK cài/mở được trên emulator; ảnh tại `docs/testing/screenshots/`.
- Lượt kiểm chứng cuối: `.tools/handoff-verification.log`, BUILD SUCCESSFUL trong 37 giây. XML kết quả trong `app/build/test-results/` và `app/build/outputs/androidTest-results/`.
- Chữ ký APK debug đã xác minh bằng `apksigner verify`, v2 hợp lệ; đây là chữ ký debug, không phải ký release.

Đây là bản debug có thể cài thử, chưa ký release hoặc xuất bản Play Store. Nhánh mã nguồn giữ tại `feature/android-mvp`, không có remote và không thực hiện push/merge.
