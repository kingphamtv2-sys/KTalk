# KTalk 0.6.0 — bài học và kiểm tra màn hình S9+

## Nội dung đã cải thiện

- Giữ nguyên thứ tự và ID của **45 bài / 15 chủ đề**, **180 câu mẫu**, ba cụm từ mỗi bài và **300 mục Vocabulary**. Không thay schema DataStore hay chức năng thu/nhận diện âm thanh.
- Mỗi bài có mục tiêu thực hành, vai người học, đối tác và mẹo áp dụng riêng. Trước bài thấy tên và tình huống trước khi nghe từ; sau bài có phần nhắc lại và gợi ý tự tập.
- Biên tập **180 đáp án nhiễu** thành tiếng Anh có nghĩa; sửa những lời dẫn giống câu hỏi kiểm tra thành lời thoại phù hợp hơn. Lựa chọn ở cả 45 bài có vị trí ổn định theo ID, không còn cùng một mẫu xen kẽ ở 15 bài đầu.
- Đối tác đúng vai: nhân viên quán, người chỉ đường, tài xế, lễ tân… Bài gọi điện có người nghe máy ở hai lượt đầu và Alex sau khi chuyển máy.
- [Bản nội dung đầy đủ để đọc/rà soát](content/45-lesson-quality.md) gồm45 mục tiêu, tình huống, mẹo và180 lượt với bản dịch, câu mẫu, phương án không phù hợp.

## Các lỗi được sửa

1. Chấp nhận gọi trà/chọn cỡ áo khác nhưng lượt sau mặc định cà phê/cỡ vừa: chuyển lời dẫn sang đồ uống/áo vừa chọn. Mô tả túi tìm thấy không ép số sách khi lượt trước cho phép một hoặc hai cuốn.
2. Một số câu hỏi về vé hoặc hỏi lại số tầng là phản hồi hợp lý nhưng bị đưa vào lựa chọn sai: thay các lựa chọn đó. Gợi ý thời tiết không còn đề xuất câu mưa trong tình huống đang nắng.
3. Tên Việt **An** và tên ghép có An bị coi là mạo từ: nhận đúng trong các cấu trúc giới thiệu tên, vẫn loại số, câu thiếu tên và phủ định. Người nói họ tên đầy đủ được giải thích và có thể đáp **No, that's my full name.**
4. Bài trả phòng và hẹn gặp nêu rõ **phòng 205 / ba giờ chiều** ngay trong tình huống và gợi ý; không bắt người học đoán dữ kiện chưa được cung cấp.
5. Xóa hết kết quả nhận diện từng làm ô sửa biến mất: giữ ô nhập và focus khi đang chỉnh, chỉ trở về micro khi chọn nói lại/đổi cách trả lời/sang lượt khác.
6. Nút hoàn tất trên bàn phím kiểm tra câu trả lời và đóng bàn phím, áp dụng cả chế độ Gõ và sửa lời nói. Không tự chuyển lượt; nhập dài vẫn cuộn trong ô tối đa 4 dòng hiển thị.
7. Khi đồng thời phóng lớn màn hình/cỡ chữ, nhãn tab chính rút gọn thành **Học** để dễ đọc.
8. API35 phát hiện thông báo giọng đọc dài cộng bàn phím có thể làm vùng học cao0: giới hạn chiều cao thông báo theo vùng khả dụng, cuộn đọc nội dung, giữ nút **Đã hiểu**; tạm ẩn dòng nhắc cài giọng khi bàn phím đang mở. Không thay nội dung lỗi hay yêu cầu cài giọng.
9. Kiểm tra `MainActivity` thật trên Android 10 phát hiện cửa sổ không nhận sự kiện vùng bàn phím, dẫn tới đẩy cả trang lên trên. Thêm `adjustResize` theo [hướng dẫn Android chính thức](https://developer.android.com/develop/ui/compose/system/setup-e2e), cùng kiểm thử tích hợp xác nhận sự kiện IME và cửa sổ không bị đẩy khỏi màn hình. Host kiểm thử Compose có cấu hình cửa sổ riêng, nên kiểm thử thành phần không thay được ca này.

## Kiểm chứng và giới hạn

Đã có một lượt rà soát độc lập đủ 180 lượt và chín ảnh từ APK. Các phát hiện có tác động đến việc học được tái hiện rồi sửa; không dùng lượt rà soát thứ hai thay cho kiểm thử. Lần chạy sandbox bị chặn khóa `.robolectric-download-lock` được chạy lại bằng tiến trình Gradle mới; đây là lỗi môi trường, không phải kết quả kiểm thử ứng dụng.

| Kiểm tra | Kết quả |
|---|---|
| JVM / Robolectric | 108/108 đạt, không lỗi hoặc bỏ qua |
| Android 10 / API 29 | 39/39 đạt, gồm Activity thật |
| Android 15 / API 35 | 39/39 đạt, gồm Activity thật |
| Lint / assembleDebug | Build thành công; 0 lỗi lint, 16 cảnh báo có sẵn |

Log gốc: `.tools/lesson-s9-api29-final-release.log`, `.tools/lesson-s9-api35-final-release.log`. Ca lỗi giọng đọc và bàn phím có chu trình RED trên API35 trước sửa rồi GREEN; ca `MainActivity` có RED do không nhận IME insets trên API29 rồi GREEN sau `adjustResize`. Test Activity thật chờ DataStore lưu xong trước khi tìm chế độ Gõ; không dùng timing của repository giả để suy cho IO thật.

Cấu hình kiểm tra dùng AVD Google APIs **Android10/API29** và **Android15/API35**, khung1080×2220 ở480dpi (**360×740dp**), thêm540dpi (**320×658dp**, làm tròn). Kiểm tra font1.0/1.3/2.0 trong các luồng. Đây là cấu hình theo tỷ lệ18.5:9 của S9+, suy ra từ [thông số màn hình Samsung](https://www.samsung.com/ph/support/mobile-devices/what-are-the-new-samsung-galaxy-s9-and-s9-specs/), không phải máy/ROM Samsung thật.

Các phần reviewer để ngỏ đã được xử lý như sau:

- **Thông báo dài + bàn phím ở 320dp/font2:** bổ sung ca persistentVoiceErrorAndKeyboardLeaveRoomToEditOnAZoomedDisplay, xác nhận IME thực sự mở trên Android. API29 đạt trước sửa; API35 tái hiện vùng học0 rồi được sửa. Không suy từ ca chuyển Nói→Gõ vì chuyển chế độ xóa lỗi nhận diện.
- **Samsung Keyboard, TalkBack, micro/TTS trên S9+ thật:** chưa có máy kết nối; không suy diễn chất lượng phần cứng hoặc nhận diện từ AVD. Chi phí nếu khác biệt trên máy thật là cần kiểm tra lại với bản APK được bàn giao; các lối chọn/gõ vẫn có sẵn.
- **Hiệu quả tiếp thu/trình độ:** mục tiêu là điều được thực hành, không phải chứng chỉ hay khẳng định đã thành thạo sau bốn lượt.
- **Kết quả native/cài đè/ảnh sau sửa:** được kiểm chứng ở bước nghiệm thu của người triển khai, không coi lời xác nhận trước khi chạy là bằng chứng.

## Ảnh từ APK

[Trước bài](testing/screenshots/s9-lesson-brief.png) · [Hội thoại](testing/screenshots/s9-conversation.png) · [Gõ với bàn phím](testing/screenshots/s9-typed-keyboard.png) · [Phản hồi](testing/screenshots/s9-typed-feedback.png) · [Hoàn thành](testing/screenshots/s9-completion.png)

[Tình huống khách sạn](testing/screenshots/s9-hotel-brief.png) · [Đối tác lễ tân](testing/screenshots/s9-hotel-conversation.png) · [Chữ lớn](testing/screenshots/s9-hotel-large.png) · [Flashcard chữ lớn](testing/screenshots/s9-flashcard-large.png) · [Phóng lớn màn hình](testing/screenshots/s9-zoom-large-input.png)

[Lỗi giọng đọc khi bàn phím đang mở](testing/screenshots/s9-error-keyboard-large.png) · [Tiếp tục bằng gõ sau lỗi](testing/screenshots/s9-error-recovered.png). Hai ảnh này và ảnh trước bài được chụp lại từ APK cuối. Đã xem ảnh sau sửa: ô nhập đủ chỗ, thông báo chừa nút Đã hiểu, bàn phím gửi được câu trả lời; nội dung thông báo cuộn để đọc hết. Kiểm tra thực bằng cách tạm tắt Google TTS trên AVD riêng, rồi bật lại và khôi phục font/density; không thay cài đặt điện thoại người dùng.

## APK bàn giao

File `releases/KTalk-0.6.0-lesson-quality.apk`, **0.6.0**, versionCode **7**, package `vn.noi.english`, kích thước **18,423,763 byte**. Đây là APK debug để cài thử, được bàn giao riêng và không lưu trong Git.

SHA256: `a8880c174559b789d29a1da5935dc8c6e335e3d3085639fb917077d01e229811`.

Chứng thư ký SHA256: `104cbe1d932c69d2b756aa4c2ef762451888f0ebde5177cb4ad11173765dbce2`, cùng 0.5.0. Không gỡ bản cũ nếu muốn giữ tiến độ trên điện thoại.

Cài 0.5.0 trên AVD sạch, hoàn thành một bài và đánh dấu một Đã nhớ/một Cần ôn; cài đè APK cuối 0.6.0, force-stop/mở lại vẫn **1/45 bài, 1/300 Đã nhớ, 1 Cần ôn**. `.tools/lesson-quality-release-manual.log` PASS cho cả cài đè và lỗi TTS thực với bàn phím. Các lần chạy lại dùng fixture sạch trên đúng AVD đã xác minh danh tính, để không nhầm số từ được đánh dấu thêm với lỗi mất tiến độ.
