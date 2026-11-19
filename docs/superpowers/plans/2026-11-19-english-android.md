# English Android Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Xây dựng bản Android Kotlin cài được, giữ giao diện “nói.” phương án 02, có đủ 15 bài luyện giao tiếp bằng nói/chọn/gõ và tiến độ lưu trên thiết bị.

**Architecture:** Một module Android với Compose và Material 3. Nội dung và bộ so khớp thuần Kotlin tách khỏi ViewModel, kho DataStore và dịch vụ âm thanh Android; ViewModel điều phối học tập, giao diện chỉ phát sự kiện và hiển thị trạng thái.

**Tech Stack:** Kotlin 2.1.20, Compose compiler cùng phiên bản Kotlin, AGP 8.9.2, Gradle 8.11.1, JDK 17, minSdk 26, compileSdk/targetSdk 35. Compose BOM 2025.04.01, Activity Compose 1.10.1, Lifecycle 2.9.0, DataStore Preferences 1.1.7, Coroutines 1.10.2, JUnit 4.13.2. Đây là bộ phiên bản ghim để build bản debug, không phải tuyên bố đáp ứng chính sách Play Store hiện hành; chỉ điều chỉnh khi có lỗi tương thích được xác minh.

**Spec:** `docs/superpowers/specs/2026-11-19-english-android-design.md`.

**Kết quả triển khai 2026-11-19:** Đã hoàn thành bản debug theo Task 1–5, giữ nhánh `feature/android-mvp`. 18/18 test JVM/Robolectric và 5/5 test Android API 35 đạt, lint 0 lỗi / 12 cảnh báo, APK build và cài/mở được. Các checkbox bên dưới là kế hoạch gốc; kết quả, thay đổi có chủ đích và giới hạn kiểm chứng ghi tại `docs/implementation-report.md`. Chất lượng giọng nói trên điện thoại thật chưa được kiểm chứng. Reviewer độc lập gửi ba phát hiện đã sửa, nhưng bị hạn mức ngắt trước kết luận toàn bộ; phần còn lại do tác giả tự rà soát.

## Global Constraints

- Ứng dụng Android viết bằng Kotlin, sử dụng Internet khi luyện nói.
- 5 chủ đề, mỗi chủ đề 3 bài: chào hỏi, giới thiệu bản thân, gọi món, mua sắm, hỏi đường.
- Mỗi bài gồm 3 cụm từ hữu ích và 4 lượt trả lời có hướng dẫn; thời lượng mục tiêu khoảng 5 phút.
- Lưu tiến độ trên điện thoại; không đăng nhập, không backend riêng ở bản đầu.
- Không chấm điểm phát âm, không đánh giá âm vị hay từng từ, không trò chuyện AI tự do, không bảo đảm hoạt động ngoại tuyến.
- Không dùng điểm số, bảng xếp hạng, giới hạn lượt thử hoặc khóa bài để tạo áp lực.
- Nút thao tác ít nhất 48dp, nhãn rõ cho TalkBack, hỗ trợ cỡ chữ hệ thống và cuộn trên màn hình nhỏ.
- Không tự thu âm ngay khi mở màn hình, không thu âm nền. Một phiên nhận diện tại một thời điểm.
- Giao diện chuẩn: `professional-v2-reviewed.html`; tên tạm “nói.”, package `vn.noi.english`.

## Review Focus

1. Đáp án gần mẫu nhưng thiếu phủ định/thiếu tên không được chấp nhận nhầm; Task 1 kiểm thử đối nghịch.
2. Ghi DataStore thất bại không được báo hoàn thành giả; Task 2 kiểm thử với kho giả gây IOException.
3. Kết quả nhận diện trả muộn sau khi chuyển lượt/rời màn hình không được ghi đè câu mới; Task 3 kiểm thử phiên nhận diện hết hiệu lực.
4. Thiết bị không có nhận diện/giọng đọc hoặc từ chối quyền vẫn hoàn thành được bài bằng gõ/chọn; Task 4 kiểm thử luồng thay thế.
5. Đóng/mở lại, xoay hoặc tăng cỡ chữ không làm mất bài/lượt và không tự bật micro; Task 4–5 kiểm tra khôi phục và bố cục.

## File Structure

```text
settings.gradle.kts, build.gradle.kts, gradle.properties, gradlew, gradlew.bat
gradle/wrapper/gradle-wrapper.{jar,properties}    # wrapper chính thức
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/java/vn/noi/english/
  MainActivity.kt                              # quyền, vòng đời, khởi tạo
  content/Lesson.kt                            # Topic, Lesson, Turn, Vocabulary
  content/Curriculum.kt                        # 5 chủ đề, 15 bài, 60 lượt
  learning/AnswerMatcher.kt                    # so khớp có chủ đích
  learning/LearningState.kt                    # trạng thái và sự kiện thuần
  learning/LearningViewModel.kt                # điều phối, lưu trước chuyển màn hình
  data/ProgressRepository.kt                   # interface và DataStore
  audio/SpeechInput.kt                         # SpeechRecognizer, phiên chống callback cũ
  audio/SpeechOutput.kt                        # TextToSpeech, trạng thái engine
  ui/NoiApp.kt                                 # điều hướng và kết nối trạng thái
  ui/theme/Theme.kt                            # màu, kiểu chữ, hình dạng
  ui/components/Components.kt                 # nút, thẻ, tiến độ, nghe mẫu
  ui/screens/HomeScreen.kt
  ui/screens/LessonsScreen.kt
  ui/screens/VocabularyScreen.kt
  ui/screens/ConversationScreen.kt
  ui/screens/CompletionScreen.kt
  ui/screens/ProgressScreen.kt
app/src/main/res/values/{strings,themes}.xml
app/src/main/res/drawable/ic_launcher.xml
app/src/test/java/vn/noi/english/{content,learning,audio}/...
app/src/androidTest/java/vn/noi/english/ui/LearningFlowTest.kt
README.md, docs/testing/android-manual-checklist.md
.gitignore                                    # .tools, SDK, builds, local.properties, .superpowers
```

## Task 1: Bộ công cụ, nội dung và kiểm tra câu trả lời

**Files:** root Gradle files, `app/build.gradle.kts`, `content/Lesson.kt`, `content/Curriculum.kt`, `learning/AnswerMatcher.kt`; tests `CurriculumTest.kt`, `AnswerMatcherTest.kt`.

**Interfaces:** `Vocabulary(english: String, vietnamese: String)`; `Turn(id, prompt, translation, hint, sample, explanation: String, choices: List<String>, accepted: List<String>, slotPatterns: List<String>)`; `Lesson(id, topicId, title, situation: String, vocabulary: List<Vocabulary>, turns: List<Turn>)`; `Topic(id, title: String, lessons: List<Lesson>)`; `Curriculum.topics: List<Topic>`; `AnswerMatcher.matches(answer: String, turn: Turn): Boolean`.

- [ ] Chuẩn bị JDK 17, Gradle và SDK 35 từ nguồn chính thức trong `.tools/`, đặt biến môi trường theo phiên làm việc; không ghi global PATH. Nếu công cụ cần chấp nhận giấy phép thì xử lý theo quyền được cấp, báo cụ thể nếu bị chặn. Dùng wrapper chính thức và checksum tải về; không tự viết wrapper jar.
- [ ] Tạo cấu hình build tối thiểu theo Tech Stack. Chạy `.\gradlew.bat --version`; kỳ vọng Gradle 8.11.1 / JVM 17. Cấu hình `local.properties` trỏ SDK, bỏ qua Git.
- [ ] Viết `CurriculumTest`: 5 chủ đề, tổng 15 bài, ID duy nhất, mỗi bài 3 từ và 4 lượt, mọi sample và accepted đều matches; choices đúng một đáp án hợp lệ.
- [ ] Viết `AnswerMatcherTest`: chấp nhận chữ hoa/dấu câu và dạng I'm/I am; tên “Ngọc Anh” hợp lệ trong mẫu tên; bác bỏ rỗng, chỉ dấu câu, “My name is”, sai ngữ cảnh và biến thể thêm/bỏ “not” làm đổi nghĩa. Chạy test, xác nhận thất bại vì thiếu model/logic.
- [ ] Tạo model và bộ 15 bài riêng theo bảng spec, hội thoại mạch lạc mỗi bài 4 lượt. Dùng danh sách biến thể cụ thể; regex slot có neo toàn câu, giới hạn từ và ký tự; không dùng fuzzy matching hoặc substring.
- [ ] Chạy `.\gradlew.bat :app:testDebugUnitTest`; kỳ vọng hai nhóm test PASS. Khi có Git, commit `feat: add curriculum and answer checking`; hiện không có Git thì ghi nhận file, không dừng triển khai vì thiếu commit.

## Task 2: Tiến độ và trạng thái học tập

**Files:** `data/ProgressRepository.kt`, `learning/LearningState.kt`, `learning/LearningViewModel.kt`; tests `LearningViewModelTest.kt`, `ProgressTest.kt`.

**Interfaces:** `Progress(completedLessonIds: Set<String>, lessonId: String?, turnIndex: Int)`; `ProgressRepository.progress: Flow<Progress>`; `suspend savePosition(lessonId: String, turnIndex: Int)`; `suspend complete(lessonId: String)`. `LearningViewModel.state: StateFlow<LearningState>`; `openLesson(id)`, `startConversation()`, `setMode(mode)`, `setAnswer(text)`, `submit()`, `nextTurn()`, `retry()`, `home()`.

- [ ] Viết test bằng kho giả và dispatcher test: sai đáp án không qua lượt; đúng không tự qua lượt trước khi nhấn tiếp; đủ bốn lượt mới hoàn thành; học lại không đếm trùng; thiếu vị trí khởi đầu ở lượt 0; bài đã hoàn thành học lại từ 0.
- [ ] Thêm test ghi lỗi IOException không sang màn hoàn thành, có thông báo và thử lại được. Vị trí bài/lượt sai hoặc ID không còn trong nội dung khôi phục an toàn về đầu, không crash.
- [ ] Chạy các test mới và xác nhận đỏ trước triển khai. Tạo DataStore repository và ViewModel; hoàn thành bằng transaction cập nhật tập ID, không lưu câu trả lời/âm thanh.
- [ ] Mọi thao tác chuyển lượt chờ ghi thành công, ngăn nhấn lặp khi đang ghi, hiển thị lỗi tiếng Việt và cho retry. Quan sát DataStore đến khi load xong rồi mới mở học, tránh ghi đè tiến độ khởi tạo.
- [ ] Chạy unit tests, kỳ vọng toàn bộ PASS; commit khi có Git.

## Task 3: Nhận diện và đọc mẫu thật

**Files:** `audio/SpeechInput.kt`, `audio/SpeechOutput.kt`, `MainActivity.kt`, manifest; test `SpeechSessionTest.kt`.

**Interfaces:** `SpeechInput.available: Boolean`; `start(onResult: (String) -> Unit, onError: (String) -> Unit)`, `stop()`, `cancel()`, `close()`. `SpeechOutput.speak(text: String, onError: (String) -> Unit)`, `stop()`, `close()`. Trạng thái đầu vào `Idle/Listening/Processing/Error`, đầu ra `Initializing/Ready/Unavailable`.

- [ ] Viết test bộ token phiên thuần Kotlin: cancel/đổi chế độ/đổi lượt vô hiệu callback cũ; chỉ callback thuộc phiên hiện tại cập nhật trạng thái. Chạy xác nhận đỏ.
- [ ] Implement SpeechRecognizer en-US, một phiên tại một thời điểm trên main thread. Stop cho nhận kết quả, cancel bỏ kết quả và hủy token. Tách ERROR_NO_MATCH/SPEECH_TIMEOUT/NETWORK/RECOGNIZER_BUSY thành thông báo kỹ thuật, không gọi submit.
- [ ] Implement TextToSpeech en-US, kiểm tra init/language/voice, không đọc trước ready. Nói thì dừng TTS, nghe mẫu thì hủy recognizer. Cung cấp đường mở cài giọng Android khi thiếu engine/voice.
- [ ] Manifest có RECORD_AUDIO, INTERNET, queries nhận diện và TTS; allowBackup false. Giải thích micro trước khi xin quyền bằng Activity Result API, xử lý từ chối thường/vĩnh viễn và quay lại cài đặt.
- [ ] ON_STOP và khi chuyển màn hình phải cancel nghe và stop đọc, xoay màn hình không để state giả đang nghe; onDispose/destroy đóng tài nguyên. Không thu âm nền hoặc lưu file.
- [ ] Chạy unit tests. Đánh dấu kiểm tra micro/TTS thực tế cần thiết bị, không coi unit test là kiểm chứng phần cứng; commit khi có Git.

## Task 4: Giao diện Compose theo phương án 02

**Files:** `ui/**`, resources, `MainActivity.kt`; test `LearningFlowTest.kt`.

**Interfaces:** `NoiApp(viewModel: LearningViewModel, speechInput: SpeechInput, speechOutput: SpeechOutput)`; từng screen nhận state và callback, không tự truy cập DataStore. UI state gồm `Home/Progress/Lessons/Vocabulary/Conversation/Completion`, mode `Speak/Choose/Type`, feedback `None/Accepted/TryTemplate` và trạng thái lưu/âm thanh tách riêng.

- [ ] Viết Compose test với kho/dịch vụ giả: từ chủ đề tới bài và từ vựng; hoàn thành 4 lượt bằng chọn; gõ rỗng có nhắc nhập; gõ sai vẫn ở lượt; từ chối quyền/không có speech chuyển sang chọn/gõ và hoàn thành được. Chạy để thấy thiếu UI trước khi tạo screens.
- [ ] Tạo theme màu đúng spec, hình dạng, typography và icon Material; giao diện tiếng Việt trong strings resources. Dùng font đóng gói nếu tải được từ nguồn chính thức, giữ giấy phép; nếu không dùng sans-serif hệ thống.
- [ ] Tạo Home/Lessons/Vocabulary/Progress: thẻ gợi ý theo bài gần nhất, 0/15 ở cài mới, mọi bài mở, progress tính từ tập ID. Progress không thêm streak/thời gian học không được lưu.
- [ ] Tạo Conversation: câu hỏi và dịch, nút nghe, gợi ý mặc định mở, ba chế độ, nút micro 72dp, preview nhận diện sửa được, phản hồi và nút tiếp. Không render nút kết quả mẫu hoặc nhãn mô phỏng trong ứng dụng.
- [ ] Tạo Completion chỉ sau repository xác nhận lưu bài thành công; hiển thị 4 lượt/3 cụm từ của đúng bài, học lại và về danh sách. Back xử lý theo luồng và dừng âm thanh; không mắc kẹt ở kết quả khi bấm Back.
- [ ] Test xoay/Activity recreation giữ bài/lượt, input có thể reset và micro Idle; UI cuộn ở viewport nhỏ và fontScale 1.5/2.0, mọi nút quan trọng đọc bằng TalkBack. Chạy connected tests nếu có thiết bị; ghi rõ nếu chưa chạy được.
- [ ] Đối chiếu từng màn với `professional-v2-reviewed.html`; bỏ khung điện thoại, bảng giới thiệu và status bar giả. Commit khi có Git.

## Task 5: Kiểm chứng và bàn giao APK

**Files:** `README.md`, `docs/testing/android-manual-checklist.md`, cập nhật checkboxes kế hoạch.

- [ ] Chạy `.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`. Kỳ vọng test PASS, không có lint error, APK tại `app/build/outputs/apk/debug/app-debug.apk`. Sửa lỗi trước khi tuyên bố build thành công.
- [ ] Nếu adb/emulator khả dụng: `adb devices`, cài bằng `adb install -r app/build/outputs/apk/debug/app-debug.apk`, mở app và kiểm tra UI. Nếu không có dịch vụ speech trên emulator, không dùng nó làm bằng chứng micro/TTS thực tế.
- [ ] Kiểm tra thủ công ít nhất một bài bằng micro, một bài chọn, một bài gõ; quyền từ chối/vĩnh viễn, mất mạng, không nghe rõ, đổi chế độ khi nghe, về nền, xoay, mở lại sau đóng app; tiến độ không đếm trùng. Ghi thiết bị/API và kết quả thực tế vào checklist.
- [ ] Một reviewer độc lập rà soát toàn bộ mã so với spec và năm Review Focus; sửa lỗi thực chất, chạy lại các kiểm tra chịu ảnh hưởng. Không tự coi phần chưa test là PASS.
- [ ] README hướng dẫn cài debug APK/build, vị trí SDK/JDK, dịch vụ speech cần có, các giới hạn, dữ liệu lưu trên máy và cách xóa dữ liệu. Không đưa mật khẩu, khóa ký riêng hoặc đường dẫn máy cá nhân vào file build dùng chung.
- [ ] Bàn giao đường dẫn APK đã build, nguồn Kotlin, kết quả kiểm tra và phần chưa thể xác minh. Không ký release/publish Play Store trong phạm vi bản đầu.

## Self-review

Đã đối chiếu đủ phạm vi, 5 màn chính + màn tiến độ, nội dung 15 bài, âm thanh và quyền, lưu tiến độ và lỗi ghi, khôi phục/vòng đời, khả năng tiếp cận và tiêu chí nghiệm thu. Các interface repository được dùng thống nhất; speech callbacks tách khỏi kết quả kiểm tra nội dung. Bộ công cụ hiện thiếu là việc cần xử lý trong Task 1, không phải kết quả đã hoàn tất.

## Tài liệu kỹ thuật đã kiểm tra

- https://developer.android.com/build/releases/agp-8-9-0-release-notes — AGP 8.9 dùng Gradle 8.11.1 và JDK 17.
- https://developer.android.com/develop/ui/compose/compiler — cấu hình plugin Compose compiler cho Kotlin 2.x.

## Execution handoff

Đề xuất thực hiện trực tiếp trong chat hiện tại bằng `superpowers:executing-plans`: các task phụ thuộc nối tiếp model/trạng thái/âm thanh/giao diện; một người triển khai giữ nhất quán, một reviewer độc lập kiểm tra cuối. Theo writing-plans, gửi kế hoạch cho người dùng xem và chọn cách thực hiện trước khi tạo mã ứng dụng.
