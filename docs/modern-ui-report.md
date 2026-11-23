# KTalk 0.5.0 — Giao diện mới

## Kết quả thiết kế

Theo yêu cầu tự chọn và triển khai của người dùng, giữ màu xanh và nhận diện KTalk, làm mới toàn bộ giao diện theo hướng tối giản, rõ nhóm nội dung và dễ chạm. Giữ Be Vietnam Pro, Kotlin/Compose và biểu tượng launcher; không thêm thư viện hoặc tài nguyên từ mạng.

- Trang chính có header thương hiệu gọn, card gradient cho bài gợi ý/tiếp tục, danh sách chủ đề dạng card với trạng thái học thực tế.
- Cỡ chữ và line-height được đồng bộ, card24dp, nút16dp, vùng chạm tối thiểu48dp. Các phần chữ không bị ép chiều cao cố định.
- Vocabulary có ô tìm rõ ràng, bộ lọc, card mức nhớ và các action nhất quán. Ví dụ Anh–Việt nằm trong khối riêng; Đã nhớ/Cần ôn chuyển từ hàng ngang sang dọc ở cỡ chữ lớn.
- Quiz có ký hiệu A–D, phản hồi đúng/sai vẫn bằng chữ và màu, không tự thay đổi mức nhớ.
- Hội thoại gộp speaker/audio vào card câu hỏi; gợi ý ngắn ở trước phần trả lời, ví dụ đầy đủ/nghe mẫu phía sau để micro gần câu hỏi hơn. Có thể Ẩn/Hiện cả hai phần. Nói/chọn/gõ, Preparing, trạng thái lỗi và các nút Android/settings giữ nguyên.
- Tiến độ có vòng phần trăm từ số bài thực tế, card chủ đề thống nhất. Không thêm streak/XP/score giả hoặc tài khoản. Thanh điều hướng và thông báo lỗi cùng hệ thống màu.

Không thay đổi45bài/300mục, matcher, ViewModel, DataStore, adapter audio hoặc quyền Android. Giữ key reset vị trí cuộn theo lượt và xử lý IME/safeDrawing.

## Rà soát và sửa lỗi

Reviewer độc lập đọc toàn bộ diff và12 ảnh normal/font2.0, kết luận **Ready with two Home progress fixes**, không có Critical. Hai Important được sửa trong một lượt với3 regression đã RED trước sửa và GREEN sau sửa:

1. Saved position của lần học lại một bài đã hoàn thành không được đẩy bài đó lên như bài mới khi còn bài chưa xong. Hero nay ưu tiên saved unfinished, rồi bài chưa hoàn thành đầu tiên; khi mọi bài xong, CTA ghi **Học lại**. Test xác minh chọn đúng bài và không làm giảm số bài đã hoàn thành.
2. Chủ đề có một bài đang học nhưng0bài hoàn thành không ghi Chưa bắt đầu; hiển thị **Đang học ·0/3 bài hoàn thành** từ saved unfinished.

Hai chi tiết thị giác được chỉnh trong phạm vi redesign: ba segment Nói/Chọn câu/Gõ dùng chiều cao bằng nhau khi text wrap; viền ô tìm dùng outline có tương phản3.23:1 với nền trắng. Không chạy thêm re-review; người triển khai kiểm tra lại ảnh sau sửa và full regression.

Reviewer không phán đoán giọng nói/micro trên S9+ và nghiệm thu APK/cài đè; phần thứ nhất vẫn cần điện thoại thật, phần thứ hai được người triển khai kiểm tra riêng bên dưới.

## Kiểm chứng

- Full `testDebugUnitTest lintDebug assembleDebug connectedDebugAndroidTest` đạt **BUILD SUCCESSFUL**, log `.tools/modern-ui-final-verification.log`.
- **89/89 JVM/Robolectric**, không failures/errors. Giữ84 test cũ; thêm2 tình huống viewport nhỏ và3 regression trạng thái Home. Test compact được siết để kiểm tra đồng thời câu hỏi và toàn bộ vùng chạm micro trong viewport360×680, thay vì chỉ kiểm tra một phần node được coi là displayed.
- **27/27 Android API35** trên máy giả lập đặt **360×740**, font mặc định1.0; các luồng font2.0 dùng LocalDensity trong test. Gồm21test UI và6test thiết bị/DataStore/IME/ActivityResult; giữ test phục hồi micro API28/35 trên Robolectric.
- Lint **0 lỗi/16 cảnh báo** có từ bản0.4.1; không nâng dependency ngoài phạm vi. Lệnh đầu truyền project property thiếu quote bị PowerShell tách sai; sửa quote và chạy lại toàn bộ, không thay mã ứng dụng để ép lệnh đạt.
- Đã kiểm tra tương phản sRGB: Ink/Paper13.32:1, Subtle/Paper5.06:1, trắng/Blue5.89:1, Blue/BlueTint5.17:1, Teal/Mint4.98:1.

## Ảnh từ APK thực

Ảnh chụp emulator API35 ở360×740, không phải mockup. Đã xem từng màn normal và các màn font2.0; những phần dài vẫn cuộn được, không cắt bớt nội dung để vừa màn.

- [Trang chính](testing/screenshots/modern-home.png), [chủ đề/bài học](testing/screenshots/modern-lessons.png), [trước hội thoại](testing/screenshots/modern-before-conversation.png).
- [Hội thoại](testing/screenshots/modern-conversation.png), [hội thoại chữ lớn](testing/screenshots/modern-conversation-large.png), [điều khiển chữ lớn](testing/screenshots/modern-conversation-large-controls.png).
- [Vocabulary](testing/screenshots/modern-vocabulary.png), [Vocabulary chữ lớn](testing/screenshots/modern-vocabulary-large.png), [flashcard](testing/screenshots/modern-flashcard.png), [action flashcard chữ lớn](testing/screenshots/modern-flashcard-large.png).
- [Quiz](testing/screenshots/modern-quiz.png), [tiến độ](testing/screenshots/modern-progress.png), [hoàn thành](testing/screenshots/modern-completion.png).

## Bản cài

[KTalk-0.5.0-modern-ui.apk](../releases/KTalk-0.5.0-modern-ui.apk), **0.5.0**, versionCode **6**, package `vn.noi.english`. Bản debug để cài thử. `aapt` xác nhận tên KTalk; `apksigner` xác nhận hợp lệ và cùng chứng thư `104cbe1d932c69d2b756aa4c2ef762451888f0ebde5177cb4ad11173765dbce2` với0.4.1.

Kích thước **18,401,005 byte**, SHA256 `6fe145370f65ef1505cf65ebc615534e164b44b40ebbbde095667312513c9703`. Cài đè và không gỡ bản cũ để giữ tiến độ. Không coi kiểm thử giao diện là kiểm chứng chất lượng micro/TTS trên S9+ thật.

Thử nâng cấp thật trên emulator: cài0.4.1, hoàn thành1bài và đánh dấu1 Đã nhớ/1 Cần ôn, cài đè0.5.0 rồi force-stop/mở lại: vẫn **1/45 bài,1/300 Đã nhớ,1 Cần ôn**. Log `.tools/modern-ui-upgrade-check.log` PASS. Lượt xem màn hình cũng thử tìm/lật/đánh dấu mục mở rộng, quiz, danh sách bài và hội thoại trên360×740.

Ảnh Home/ô tìm/hội thoại chữ lớn được chụp lại từ APK cuối sau sửa. Thử hoàn thành lại bài đầu vẫn giữ1/45, rồi bắt đầu bài chưa hoàn thành: hero ghi Tiếp tục học và topic ghi Đang học1/3. `.tools/modern-ui-final-screens.log` PASS; đã xem cả màn hoàn thành và ảnh segment chữ lớn sau sửa. File APK nằm ngoài Git, source/tài liệu/ảnh được đồng bộ vào repo KTalk đã được người dùng chỉ định.
