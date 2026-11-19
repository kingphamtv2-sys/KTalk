# KTalk — Tiếng Anh giao tiếp cho người Việt

Biểu tượng KTalk: chữ **K** trong bong bóng hội thoại, nền xanh lam. [Xem biểu tượng](docs/design/ktalk-icon.png). Bản APK cập nhật tên và icon: `app/build/outputs/apk/debug/KTalk-debug.apk`.

Ứng dụng Android Kotlin / Jetpack Compose theo [thiết kế đã duyệt](docs/superpowers/specs/2026-11-19-english-android-design.md), giao diện nền sáng, xanh mực và xanh lam. Android 8.0 (API 26) trở lên.

## Có trong bản đầu

- 5 chủ đề hằng ngày, 15 bài riêng, mỗi bài 3 cụm từ và 4 lượt hội thoại.
- Giải thích tiếng Việt và gợi ý câu trả lời có thể ẩn.
- Nói qua micro, xem/sửa kết quả nhận diện trước khi kiểm tra; luôn có chế độ chọn hoặc gõ.
- Nghe câu hỏi, cụm từ và câu trả lời mẫu bằng giọng tiếng Anh trên thiết bị.
- Tiến độ lưu bằng DataStore trên điện thoại, tiếp tục bài chưa học xong, học lại không đếm trùng.
- Không cần tài khoản, API key hoặc backend; chưa có trò chuyện AI tự do hoặc chấm phát âm.

## Build

Bộ phiên bản được ghim trong Gradle: JDK 17, Gradle 8.11.1, AGP 8.9.2, Kotlin 2.1.20; SDK 35 và Build Tools 35.0.0. Google Maven và Maven Central cần truy cập được khi tải dependency lần đầu.

Mở thư mục này bằng Android Studio, cài SDK 35 và chọn Gradle JDK 17; hoặc dùng PowerShell:

```powershell
# Nếu dùng bộ công cụ đã cài riêng trong .tools của phiên triển khai:
. ./scripts/android-env.ps1

# Hoặc đặt JAVA_HOME, ANDROID_HOME và local.properties theo máy của bạn.
./gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

APK debug được tạo tại `app/build/outputs/apk/debug/app-debug.apk` khi lệnh build thành công. `.tools/` và `local.properties` là cấu hình cục bộ, không đưa vào Git. Không cần Android Studio để chạy wrapper nếu JDK/SDK đã cài.

## Cài thử trên điện thoại

Chép APK debug sang điện thoại để mở/cài hoặc bật USB debugging và dùng:

```powershell
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n vn.noi.english/.MainActivity
```

Đây là bản debug để thử nghiệm, chưa phải gói phát hành Play Store.

## Nhận diện, giọng đọc và dữ liệu

Luyện nói cần dịch vụ nhận diện tiếng Anh tương thích Android SpeechRecognizer trên điện thoại; dịch vụ này có thể dùng Internet. Cấp quyền micro khi bạn muốn nói; từ chối quyền vẫn học được bằng chọn/gõ. Khi máy chưa nghe rõ hoặc dịch vụ lỗi, ứng dụng thông báo riêng, không đánh dấu câu trả lời sai.

Nghe mẫu dùng Android TextToSpeech với giọng en-US. Nếu thiếu giọng/engine, dùng nút **Cài giọng** hoặc tìm **Chuyển văn bản thành giọng nói** trong cài đặt điện thoại. Không cam kết micro hoặc đọc mẫu hoạt động ngoại tuyến.

Ứng dụng lưu mã bài đã hoàn thành và vị trí bài/lượt gần nhất. Không lưu file thu âm hoặc lịch sử câu trả lời. Dịch vụ nhận diện của thiết bị có thể xử lý giọng nói qua mạng theo chính sách của dịch vụ đó. Tiến độ chưa đồng bộ giữa thiết bị; xóa dữ liệu hoặc gỡ app có thể làm mất tiến độ. Sao lưu tự động của ứng dụng đã tắt.

Kiểm tra câu trả lời chỉ so với biến thể được soạn sẵn và một số cấu trúc có chỗ điền. Một câu tiếng Anh đúng nhưng nằm ngoài mẫu vẫn có thể được đề nghị thử theo mẫu; kết quả không phải đánh giá toàn diện ngữ pháp/phát âm.

## Kiểm thử

Unit tests bao phủ nội dung, đáp án, khôi phục, ghi lỗi, đọc lỗi và callback phiên nghe. Test lưu file DataStore thật chạy trên Android để dùng đúng hệ thống file của thiết bị; thư viện FileStorage Android không thay thế file bằng cùng cách trên Windows. Compose flow tests trong `src/androidTest/ui` được dùng chung trên Robolectric và trên thiết bị:

```powershell
./gradlew.bat :app:testDebugUnitTest
./gradlew.bat :app:connectedDebugAndroidTest
```

Robolectric kiểm tra được tương tác Compose nhưng không thay cho thử giọng nói thực tế. Checklist thủ công: [android-manual-checklist.md](docs/testing/android-manual-checklist.md).

Kết quả đã xác minh: **18/18 test JVM/Robolectric, 5/5 test trên emulator API 35 đạt; build APK thành công; lint 0 lỗi / 12 cảnh báo**. Đã cài và mở APK trên emulator. Chưa kiểm chứng chất lượng micro/TTS trên điện thoại thật. Chi tiết rà soát, quyết định triển khai và các giới hạn: [báo cáo triển khai](docs/implementation-report.md).

## Cấu trúc

- `content/`: nội dung 15 bài, không gọi mạng.
- `learning/`: bộ so khớp, ViewModel và trạng thái học.
- `data/`: lưu tiến độ DataStore.
- `audio/`: adapter nhận diện và đọc mẫu Android.
- `ui/`: theme, thành phần và màn hình Compose.
- `docs/design/approved-prototype.html`: bản xem thử tham chiếu, không phải mã ứng dụng.

Font Be Vietnam Pro được đóng gói cùng ứng dụng; giấy phép tại [licenses/BeVietnamPro-OFL.txt](licenses/BeVietnamPro-OFL.txt).
