# KTalk 0.4.1 — Phục hồi micro và nhận diện giọng nói

## Vấn đề được báo và giới hạn chẩn đoán

Người dùng dùng **Samsung Galaxy S9+**, thông báo chung xuất hiện ngay khi bấm micro. Không có điện thoại này kết nối ADB và chưa có mã lỗi thực tế, nên chưa xác định được engine hoặc nguyên nhân riêng trên máy. Không coi thông báo này là bằng chứng người học phát âm sai.

Điều tra mã 0.4.0 phát hiện lỗi audio, client, server và ngôn ngữ bị gộp vào cùng một câu “Dịch vụ chưa nhận diện được lời nói”. UI cũng báo Đang nghe trước callback sẵn sàng. Bản sửa xử lý các điểm có bằng chứng đó và bổ sung đường tương thích qua hộp thoại Android.

## Thay đổi

- **Preparing → Listening:** hiển thị “Đang mở micro…” trong lúc khởi tạo, chỉ báo “Đang nghe” sau callback ready/beginning. Không cho bấm micro lặp trong lúc khởi tạo/xử lý; vẫn đổi sang chọn/gõ hoặc quay lại được.
- **Fallback một lần:** nếu không có RecognitionService hoặc lỗi kỹ thuật xuất hiện trước ready, thử `ACTION_RECOGNIZE_SPEECH` của Android một lần. Không hardcode Google/Samsung. Có nút **Nhận diện bằng Android** để chủ động thử trực tiếp.
- Không tự mở thêm phiên khi không nghe rõ, hết thời gian nói, lỗi mạng, thiếu quyền, dịch vụ bận/rate limit, sau khi đã bắt đầu nghe hoặc đã chủ động dừng. Chờ khởi tạo quá lâu có thể thử fallback nếu app còn đang được sử dụng.
- **Thông báo theo nguyên nhân:** lỗi truy cập micro, client, server, mạng, quyền, ngôn ngữ và bận có hướng xử lý riêng. Mã `SR-*` hoặc `SYS-*` giúp gửi lại thông tin khi lỗi tiếp diễn. Log chỉ mã/trạng thái/SDK, không ghi lời nói.
- **Cài đặt nhận diện:** mở cài đặt quyền của KTalk khi chưa có quyền micro, hoặc cài đặt dịch vụ nhận diện khi đã có quyền. Nếu máy không hỗ trợ màn hình đó, mở cài đặt chung. Thiếu provider vẫn chọn/gõ để học.
- **Kết quả an toàn:** hộp thoại hệ thống chỉ điền văn bản để sửa/kiểm tra, không tự chấm hay sang lượt. Chỉ nhận kết quả của đúng bài/lượt/chế độ đang chờ, bỏ callback trùng/cũ; giữ pending key qua tạo lại Activity.
- Khi mất focus hoặc pause, dừng phiên nhúng. Tự động mở hộp thoại chỉ khi Activity resumed và có focus; quay lại không tự bật micro. Kết quả hệ thống vẫn được nhận ở giai đoạn STARTED phù hợp vòng đời.
- Giữ 45 bài, 300 mục từ và cả hai DataStore/ID cũ. Không backend, tài khoản hoặc quyền mới.

Đối chiếu với tài liệu chính thức [SpeechRecognizer](https://developer.android.com/reference/android/speech/SpeechRecognizer), [RecognizerIntent](https://developer.android.com/reference/android/speech/RecognizerIntent), [Android multi-window](https://developer.android.com/develop/ui/compose/layouts/adaptive/support-multi-window-mode#android_9). Hai API native callback và kết quả activity có hệ mã lỗi khác nhau, được xử lý riêng.

## Kiểm thử và rà soát

Test adapter production `AndroidSpeechInput` bằng Robolectric **API28 (Android9)** và **API35 (Android15)**: lỗi khởi động, fallback tối đa một lần, ready, lỗi mạng/quyền/no-match/busy, dừng rồi callback muộn, callback cũ sau cancel/replacement, timeout trước/sau ready. Test `SystemSpeechRequest` trên cùng hai API: intent en-US, first nonblank result, pending/recreation, khác bài/lượt/chế độ, hủy, lỗi provider và launch guard.

UI test ở font2.0: Preparing không giả Listening, nút micro bị khóa khi chưa sẵn sàng, các nút Android/settings dùng được sau lỗi, vẫn trả lời bằng gõ. Test trên MainActivity thật dùng `Instrumentation.ActivityMonitor` thay provider bên ngoài để kiểm chứng contract trả kết quả, hủy và lỗi micro. Các test này không đo chất lượng micro/nhận diện thật.

Reviewer độc lập kết luận **Merge with fixes**, 2 Important, 1 Minor, không Critical. Đã sửa trong một lượt với regression RED→GREEN:

1. Mọi result khác OK bị coi là người dùng hủy: nay chỉ `RESULT_CANCELED` là hủy; audio/client/network/no-match/server/unknown có thông báo đúng, gồm test mã khác nhau giữa hai API và test activity lỗi audio.
2. Timer startup có thể mở hộp thoại khi app pause trong split-screen Android9: chặn launch tự động bằng resumed/focus, hủy phiên nhúng tại pause/mất focus, không xếp hàng bật mic lúc quay lại. Kết quả đang chờ được nhận độc lập với launch guard.
3. Callback ready muộn sau Stop làm UI trở lại Listening: guard `stopRequested`, giữ Processing và vẫn nhận terminal result.

Hai mục reviewer không phán đoán: nguyên nhân/giọng thật trên S9+ cần thiết bị hoặc diagnostic; build/cài nâng cấp thuộc bước nghiệm thu của người triển khai. Nguyên nhân S9+ được giữ là chưa xác minh; kết quả nghiệm thu được ghi bên dưới, không suy diễn từ test giả lập.

## Bản cài

[KTalk-0.4.1-microphone-fix.apk](../releases/KTalk-0.4.1-microphone-fix.apk), version **0.4.1**, versionCode **5**, package `vn.noi.english`. Bản debug để cài thử. Cài đè, không gỡ bản cũ để giữ tiến độ. Điện thoại vẫn cần có dịch vụ nhận diện hoạt động, hỗ trợ tiếng Anh và quyền micro phù hợp; chưa có kiểm chứng trực tiếp trên S9+.

## Kết quả nghiệm thu

- Full `testDebugUnitTest lintDebug assembleDebug connectedDebugAndroidTest`: **BUILD SUCCESSFUL**, log `.tools/microphone-final-verification.log`.
- **84/84 JVM/Robolectric**, không lỗi: giữ57 test cũ; thêm16 lượt test adapter trên API28/35, 10 lượt intent/result/lifecycle gate trên API28/35, 1 test UI phục hồi.
- **22/22 Android API35**, không lỗi: 16 test UI, 3 test thiết bị cũ, 3 test MainActivity với provider được ActivityMonitor thay thế (text/cancel/audio-error).
- Lint **0 lỗi / 16 cảnh báo**: 15 cảnh báo cũ và 1 gợi ý dùng String.toUri trong đường mở cài đặt mới; không ảnh hưởng kết quả build, không nâng dependency.
- `aapt`: tên KTalk, versionCode5/0.4.1. `apksigner`: hợp lệ, cùng chứng thư SHA256 `104cbe1d932c69d2b756aa4c2ef762451888f0ebde5177cb4ad11173765dbce2` với APK0.4.0.
- Cài thật APK0.4.0, hoàn thành một bài và đánh dấu1 Đã nhớ/1 Cần ôn, rồi `adb install -r` APK0.4.1: giữ **1/45 bài**, **1/300 đã nhớ**, **1 cần ôn**; force-stop/mở lại vẫn đúng. `.tools/microphone-upgrade-check.log` kết thúc PASS.
- Thử adapter thật trên emulator Google APIs: provider mặc định `com.google.android.tts/...GoogleTTSRecognitionService` trả **ERROR_NO_MATCH=7, ready=true**, UI hiện đúng chưa nghe rõ và không tự mở thêm phiên. Sau đó gõ `Good morning` và kiểm tra vẫn được chấp nhận, lượt không tự đổi. Log `.tools/microphone-device-diagnostic.log` chỉ ghi mã/trạng thái. Driver ban đầu tìm placeholder không được expose trong accessibility; đổi sang nhãn ô nhập ổn định rồi hoàn tất, không sửa mã app để ép test đạt.
- Bấm nút Android với provider thật mở được UI “KTalk · Hãy nói câu tiếng Anh / English (United States)”; Back trả đúng thông báo đã dừng, micro Idle và cả hai nút thử lại/cài đặt còn dùng được. `.tools/microphone-ui-check.log` có hai dòng PASS. Đã xem [ảnh thông báo no-match](testing/screenshots/microphone-recovery.png) và [ảnh các nút phục hồi](testing/screenshots/microphone-android-retry.png).

Các lượt thủ công trên emulator không có giọng nói thật và không thay thế việc thử trên Galaxy S9+ của người dùng. Không suy ra lỗi7 trên emulator là lỗi người dùng đang gặp.

APK **18,250,946 byte**, SHA256 `681baff52ad45300c9ef1f35271627d7c76b8d7faae7b5aa126e8f7235c8e24c`. Giữ nhánh/check-out hiện có và commit cục bộ, không push hoặc merge.
