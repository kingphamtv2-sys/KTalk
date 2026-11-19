# Thiết kế ứng dụng Android học giao tiếp tiếng Anh — bản đầu

Trạng thái: Người dùng đã duyệt phạm vi và giao diện phương án 02, yêu cầu cập nhật thiết kế và tiếp tục xây dựng bằng Kotlin ngày 2026-11-19. Các lựa chọn triển khai bên dưới là quyết định của người xây dựng trong phạm vi đã thống nhất.

## Mục đích

Giúp người Việt mới học tiếng Anh tập nói trong tình huống hằng ngày, tăng vốn cụm từ và bớt sợ sai. Có giải thích tiếng Việt, hội thoại mẫu và gợi ý trả lời. Nói vào micro là cách tương tác chính; chọn hoặc gõ luôn có sẵn để người học tiếp tục khi chưa tự tin.

## Phạm vi đã thống nhất

- Ứng dụng Android viết bằng Kotlin, sử dụng Internet khi luyện nói.
- 5 chủ đề, mỗi chủ đề 3 bài: chào hỏi, giới thiệu bản thân, gọi món, mua sắm, hỏi đường.
- Mỗi bài gồm 3 cụm từ hữu ích và 4 lượt trả lời có hướng dẫn; thời lượng mục tiêu khoảng 5 phút.
- Kiểm tra nội dung câu trả lời, cung cấp câu mẫu để nghe và luyện lại.
- Lưu tiến độ trên điện thoại; không đăng nhập, không backend riêng ở bản đầu.
- Không chấm điểm phát âm, không đánh giá âm vị hay từng từ, không trò chuyện AI tự do, không bảo đảm hoạt động ngoại tuyến.
- Không dùng điểm số, bảng xếp hạng, giới hạn lượt thử hoặc khóa bài để tạo áp lực.

## Các hướng đã xem xét

1. Kotlin native: tích hợp trực tiếp các khả năng âm thanh Android, tập trung một nền tảng; mở rộng iOS sẽ cần ứng dụng riêng. Người dùng đã chọn hướng này.
2. Flutter: có thể chia sẻ mã nguồn khi thêm iOS nhưng vẫn cần xử lý tích hợp âm thanh từng nền tảng. Chưa chọn vì hiện chỉ tập trung Android.

Những lựa chọn kỹ thuật bên dưới là đề xuất của người xây dựng, không phải yêu cầu người dùng đã nêu.

## Thiết kế tương tác được giữ lại

Tham chiếu chuẩn: `.superpowers/brainstorm/english-v1/content/professional-v2-reviewed.html`.
Ảnh đối chiếu: `.superpowers/brainstorm/english-v1/professional-v2-preview.jpg`.
Bản `learning-flow-v1.html` chỉ còn là lịch sử phác thảo, không phải mẫu giao diện để triển khai.

### Hệ thống giao diện phương án 02 đã duyệt

- Tên tạm của ứng dụng: “nói.”; dùng tên này trong bản đầu, có thể đổi trước phát hành. Package đề xuất: `vn.noi.english`.
- Nền sáng trắng sương `#F7F9FB`, chữ chính xanh mực `#172D3E`, chữ phụ `#61717E`, hành động chính xanh lam `#254FDB`, viền trung tính `#E3E9EE`, phản hồi tích cực xanh dịu `#E8F3EF`.
- Phông Be Vietnam Pro nếu đóng gói được từ nguồn chính thức cùng giấy phép; dùng sans-serif hệ thống làm phương án thay thế. Không tải font từ mạng trong thời gian chạy ứng dụng.
- Bo góc thẻ 14–20dp, nút 12dp, khoảng đệm màn hình 22dp. Chữ tiêu đề 24–28sp, nội dung 14–16sp, nhãn phụ tối thiểu 12sp trong ứng dụng thật; hỗ trợ tăng cỡ chữ hệ thống. Không sao chép kích thước chữ nhỏ của bảng giới thiệu bản phác thảo vào Android.
- Trang chủ có thương hiệu, thông báo tiến độ lưu trên thiết bị, lời mời học, thẻ bài gợi ý xanh mực và danh sách 5 chủ đề với biểu tượng nét mảnh. Thẻ hiển thị “Bắt đầu học” với người mới hoặc “Tiếp tục học” khi có bài dở.
- Thanh điều hướng dưới chỉ ở khu vực trang chủ: Học mỗi ngày và Tiến độ. Tiến độ là màn hình phụ trình bày tổng bài hoàn thành, tiến độ từng chủ đề, không thêm điểm số hoặc thống kê không được lưu.
- Hội thoại dùng nhân vật Alex, thẻ câu hỏi nền trắng, bản dịch bên dưới, nút nghe có nhãn, gợi ý nền xanh nhạt có thể ẩn, nhóm Nói / Chọn câu / Gõ và nút micro tròn nổi bật. Dùng biểu tượng chuẩn Material, không dùng ký tự trang trí thay nút.
- Hoàn thành dùng dấu xác nhận xanh dịu, tiêu đề khích lệ, tổng 4 lượt / 3 cụm từ và các cụm từ vừa học; nút về danh sách bài và học lại.
- Phần giới thiệu thiết kế, menu 5 màn hình ở cạnh ngoài điện thoại, nhãn “mô phỏng”, khung điện thoại và thanh trạng thái giả chỉ thuộc bản xem thử, không xuất hiện trong ứng dụng Android. Android dùng thanh hệ thống thật.
- Bản phác thảo chỉ có nội dung đại diện theo chủ đề và rút gọn một lượt; ứng dụng thật phải có nội dung riêng cho từng bài và hoàn thành đủ 4 lượt, không có nút bỏ qua tới kết quả mẫu.

### 1. Chủ đề và tiến độ

Hiển thị 5 chủ đề, số bài hoàn thành trong tổng 15 bài, nút tiếp tục học. Lần cài đầu hiển thị 0/15, không sử dụng tiến độ mẫu 2/15 trong bản phác thảo. Tất cả bài đều mở. Có thể học lại bài đã hoàn thành.

### 2. Danh sách bài

Mỗi chủ đề có 3 bài, hiển thị trạng thái hoàn thành. Người học tự chọn bài, không bắt buộc học theo thứ tự.

| Chủ đề | Ba bài |
| --- | --- |
| Chào hỏi | Chào buổi sáng; Hỏi thăm đơn giản; Gặp người mới |
| Giới thiệu bản thân | Tên của bạn; Bạn đến từ đâu?; Công việc của bạn |
| Gọi món | Gọi đồ uống; Chọn món ăn; Xin thanh toán |
| Mua sắm | Hỏi giá; Chọn kích cỡ; Mua món đồ |
| Hỏi đường | Tìm địa điểm; Hỏi hướng đi; Xác nhận đường đi |

### 3. Từ vựng trước bài

3 cụm từ tiếng Anh, nghĩa tiếng Việt, nút nghe riêng từng cụm từ, mô tả ngắn tình huống và nút bắt đầu hội thoại. Nội dung phải tương ứng đúng bài đã chọn; bản thật không tái sử dụng bài minh họa cho các chủ đề khác.

### 4. Hội thoại

Hiển thị tiến độ 1/4 đến 4/4, câu hỏi của nhân vật, bản dịch tiếng Việt, nút nghe câu hỏi. Gợi ý câu trả lời mặc định mở và có thể ẩn. Ba chế độ: Nói, Chọn câu, Gõ; mặc định Nói.

Luồng Nói: chạm micro → xin quyền khi cần → đang nghe → dừng hoặc kết thúc nhận diện → hiển thị văn bản nhận diện có thể sửa → kiểm tra câu trả lời. Có thể nói lại trước khi kiểm tra. Dừng phát mẫu trước khi bắt đầu nghe; dừng nghe khi chuyển chế độ hoặc rời màn hình.

Luồng Chọn câu: hiển thị các phương án có một câu phù hợp; chọn rồi nhận phản hồi. Luồng Gõ: nhập và kiểm tra, không chấp nhận đầu vào rỗng. Cả ba chế độ dùng cùng một quy tắc kiểm tra nội dung.

Phản hồi phù hợp: xác nhận nhẹ nhàng, giải thích ngắn bằng tiếng Việt, nghe câu mẫu, thử lại hoặc sang lượt tiếp theo. Phản hồi chưa phù hợp: đưa cấu trúc gợi ý và cho thử lại; không tăng tiến độ lượt. Lỗi nhận diện, mất mạng, hết thời gian hoặc không nghe rõ là trạng thái kỹ thuật riêng, không đánh dấu người học trả lời sai. Luôn có đường chuyển sang gõ/chọn.

Chỉ đánh giá mức phù hợp với câu trả lời mẫu trong bài. Không trình bày kết quả này như đánh giá ngữ pháp toàn diện hay chất lượng phát âm.

### 5. Hoàn thành

Sau khi hoàn thành đủ 4 lượt, lưu bài đã hoàn thành và hiển thị số lượt đã luyện, các cụm từ đã gặp, nút về danh sách bài và học lại. Hoàn thành bằng chọn/gõ cũng được tính. Không hiển thị điểm phát âm. Học lại không tăng trùng số bài hoàn thành.

## Kiến trúc đề xuất

- Một ứng dụng Kotlin với Jetpack Compose, Material 3, ViewModel và luồng trạng thái một chiều. Giữ hệ thống giao diện phương án 02 đã duyệt, tiếng Việt là ngôn ngữ giao diện.
- Đề xuất minSdk 26; chọn compileSdk, targetSdk và phiên bản thư viện ổn định tương thích với bộ công cụ có thể cài khi lập kế hoạch. Không tự tuyên bố khả năng xuất bản Play Store.
- Nội dung 15 bài đóng gói trong ứng dụng, có mã chủ đề/bài/lượt ổn định. Mỗi lượt chứa câu hỏi, bản dịch, gợi ý, câu mẫu, phương án chọn, tập đáp án chấp nhận và lời giải thích.
- Tách giao diện với dịch vụ SpeechRecognizer, TextToSpeech, kho tiến độ và bộ kiểm tra nội dung để kiểm thử logic độc lập.
- Dùng Android SpeechRecognizer với locale en-US; dùng dịch vụ nhận diện sẵn có trên thiết bị và cho phép hoạt động qua mạng. Kiểm tra khả năng cung cấp dịch vụ trước khi bật micro. Không giả định mọi thiết bị có Google Speech Services.
- Dùng Android TextToSpeech với giọng tiếng Anh en-US. Kiểm tra engine/giọng, thông báo nếu chưa có và cho phép mở cài đặt giọng; thiếu âm thanh không chặn chế độ gõ/chọn. Chưa dùng nhà cung cấp âm thanh tính phí, không có API key trong ứng dụng.
- SpeechRecognizer và TextToSpeech phải giải phóng tài nguyên đúng vòng đời. Nội dung trả về nhận diện chỉ dùng trong lượt học, không lưu file thu âm.
- Khi xin quyền micro, giải thích bằng tiếng Việt mục đích luyện nói và việc dịch vụ trên thiết bị có thể xử lý giọng nói qua mạng. Từ chối quyền không chặn ứng dụng; chỉ dẫn mở cài đặt khi quyền bị từ chối vĩnh viễn và vẫn cung cấp chọn/gõ.

## Quy tắc kiểm tra câu trả lời

Không dùng LLM hoặc chỉ kiểm tra từ khóa rời rạc. Chuẩn hóa chữ hoa/thường, khoảng trắng, dấu câu thông thường và các dạng viết tắt được hỗ trợ; so khớp với biến thể được soạn sẵn của từng lượt.

Với câu cá nhân hóa (tên, nơi ở, công việc), dùng mẫu có chỗ điền giới hạn và kiểm tra có nội dung trong chỗ điền; hướng dẫn rõ cấu trúc đang luyện. Ví dụ chấp nhận “My name is Linh” và “I'm Linh” ở lượt giới thiệu tên; không chấp nhận câu chỉ có “My name is”. Soạn các cách trả lời thay thế phổ biến để giảm từ chối câu đúng. Khi không khớp, phản hồi “Hãy thử theo mẫu…” thay vì khẳng định câu tiếng Anh sai. Không dùng so khớp gần đúng rộng có thể chấp nhận sai ý nghĩa như bỏ mất phủ định.

## Tiến độ và khôi phục

Dùng Preferences DataStore lưu tập mã bài đã hoàn thành và vị trí học gần nhất (bài, lượt); không lưu bản ghi âm hay văn bản câu trả lời. Ghi tiến độ khi qua lượt, ghi hoàn thành bền vững trước khi hiển thị thành công. Khi quay lại bài chưa xong, tiếp tục ở lượt đã lưu; vào học lại bài đã xong bắt đầu từ lượt đầu. Sau khi ứng dụng bị đóng/tiến trình bị hủy, không tự bật micro; câu đang soạn hoặc đang nhận diện có thể phải nhập/nói lại.

Tiến độ thuộc cài đặt ứng dụng trên thiết bị, không đồng bộ tài khoản; gỡ ứng dụng hoặc xóa dữ liệu có thể làm mất tiến độ. Đề xuất tắt sao lưu đám mây tự động cho dữ liệu ứng dụng trong bản đầu để giữ đúng phạm vi lưu trên thiết bị.

## Tính ổn định và khả năng tiếp cận

- Nút thao tác ít nhất 48dp, nhãn rõ cho TalkBack, hỗ trợ cỡ chữ hệ thống và cuộn trên màn hình nhỏ.
- Phân biệt trạng thái bằng nhãn/chữ ngoài màu sắc; không dùng đồng hồ đếm ngược tạo áp lực.
- Không tự thu âm ngay khi mở màn hình, không thu âm nền. Một phiên nhận diện tại một thời điểm.
- Nếu bị ngắt bởi cuộc gọi, về nền hoặc xoay màn hình, dừng tác vụ âm thanh an toàn, giữ bài/lượt và cho chủ động thử lại. UI không giả vờ vẫn đang nghe.
- Nội dung đóng gói có thể đọc khi không có mạng, nhưng không cam kết nhận diện hoặc âm thanh ngoại tuyến; khi dịch vụ lỗi vẫn cho học bằng chọn/gõ.

## Tiêu chí nghiệm thu

1. Cài và mở ứng dụng Android; truy cập đủ 5 chủ đề, 15 bài với nội dung riêng.
2. Nghe mẫu tiếng Anh trên thiết bị có engine/giọng phù hợp; xử lý rõ khi không có dịch vụ.
3. Dùng micro trên thiết bị có dịch vụ nhận diện và cấp quyền; nhận văn bản, sửa hoặc nói lại, kiểm tra, tiến đủ 4 lượt.
4. Hoàn thành toàn bộ luồng tương đương bằng chọn hoặc gõ khi từ chối quyền micro.
5. Phân biệt không nghe rõ/lỗi dịch vụ với câu chưa phù hợp; không làm mất vị trí bài khi thử lại.
6. Hoàn thành bài, đóng và mở lại ứng dụng vẫn thấy tiến độ đúng; học lại không đếm trùng.
7. Kiểm thử bộ so khớp với biến thể hợp lệ, câu rỗng, thiếu chỗ điền và câu sai ngữ cảnh; kiểm tra cấu trúc 15 bài và trạng thái học.
8. Build debug APK; chạy kiểm tra tự động khả thi. Chỉ xác nhận micro/âm thanh thực tế khi đã thử trên emulator/thiết bị có dịch vụ; báo rõ phần chưa kiểm chứng nếu thiếu môi trường.

## Những việc để người triển khai quyết định

Tên ứng dụng tạm, package name, cấu trúc module/package, phiên bản công cụ tương thích, câu hội thoại cụ thể và biến thể đáp án, các chi tiết khoảng cách/biểu tượng, chiến lược kiểm thử UI phù hợp với môi trường. Giữ phạm vi và luồng đã duyệt; không bổ sung đăng nhập, cloud hay dịch vụ tính phí nếu chưa thống nhất.

## Điều kiện môi trường hiện tại

Thư mục `G:/learnEnglish` chưa là Git repository. Kiểm tra chưa thấy java, adb, gradle hoặc git trong PATH và chưa thấy Android Studio/JDK/SDK ở các vị trí thông thường trên C:, F:, G: hoặc LocalAppData. Kế hoạch phải có bước thiết lập bộ công cụ trong thư mục dự án hoặc dùng môi trường được cấp quyền, không coi việc thiếu SDK là lý do bỏ phần mã có thể triển khai. Chưa build hoặc chạy APK; phải báo riêng kết quả build, kiểm thử logic và kiểm thử thiết bị.
