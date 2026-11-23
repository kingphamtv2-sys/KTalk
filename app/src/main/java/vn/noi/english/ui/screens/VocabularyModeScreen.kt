package vn.noi.english.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import vn.noi.english.content.Curriculum
import vn.noi.english.ui.components.*
import vn.noi.english.ui.theme.*
import vn.noi.english.vocabulary.*

@Composable fun VocabularyModeScreen(state: VocabularyState, model: VocabularyViewModel, onSpeak: (String) -> Unit) {
    key(state.page, state.cardIndex, state.quizIndex) {
        Page {
            if (state.page != VocabularyPage.Home) BackHeader(
                when(state.page) {
                    VocabularyPage.Cards -> "Thẻ ${state.cardIndex + 1} / ${state.sessionCards.size}"
                    VocabularyPage.Quiz -> "Câu ${state.quizIndex + 1} / ${state.questions.size}"
                    else -> "Vocabulary"
                }, onBack = { model.back() })
            else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Eyebrow("Vocabulary"); InfoPill("${VocabularyCatalog.cards.size} mục từ", Teal, Mint)
                }
                ScreenHeading("Học từ vựng")
                Muted("Nhớ từng cụm từ. Tự tin hơn khi nói.")
            }

            if (state.error != null) Panel(Warm, false) { Text(state.error) }
            when {
                state.loading -> Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                !state.ready -> {
                    Muted("Chưa tải được mức nhớ đã lưu. Bạn có thể thử lại hoặc chuyển sang học hội thoại.")
                    MainButton("Tải lại từ vựng", onClick = model::reload)
                }
                else -> when(state.page) {
                    VocabularyPage.Home -> VocabularyHome(state, model)
                    VocabularyPage.Cards -> WordFlashcard(state, model, onSpeak)
                    VocabularyPage.CardsDone -> {
                        Spacer(Modifier.height(20.dp)); ScreenHeading("Lượt học đã hoàn tất")
                        Muted("Những mức nhớ bạn đã đánh dấu được lưu trên điện thoại. Từ đã bỏ qua chưa bị đánh dấu.")
                        Panel(Mint, false) { Text("${state.knownCount} / ${state.eligible.size} đã nhớ", style = MaterialTheme.typography.titleLarge); Muted("${state.reviewCount} từ cần ôn trong chủ đề đang chọn") }
                        MainButton("Về từ vựng", onClick = model::openHome)
                        SecondaryButton("Học thêm từ", onClick = { model.startCards() })
                    }
                    VocabularyPage.Quiz -> WordQuiz(state, model, onSpeak)
                    VocabularyPage.QuizResult -> {
                        Eyebrow("Kết quả lượt ôn này"); ScreenHeading("Bạn vừa ôn thêm\n${state.questions.size} từ.")
                        Panel(Mint, false) { Text("${state.quizScore} / ${state.questions.size} câu đúng", style = MaterialTheme.typography.headlineMedium); Muted("Sai một chút cũng không sao. Hãy nghe lại và thử lần nữa.") }
                        Muted("Quiz không tự đổi mức nhớ. Bạn tự đánh dấu Đã nhớ / Cần ôn khi học thẻ từ.")
                        MainButton("Ôn bằng thẻ từ", onClick = { model.startCards() })
                        SecondaryButton("Làm lượt khác", onClick = model::startQuiz)
                        SecondaryButton("Về từ vựng", onClick = model::openHome)
                    }
                }
            }
        }
    }
}

@Composable private fun VocabularyHome(state: VocabularyState, model: VocabularyViewModel) {
    var menuOpen by remember { mutableStateOf(false) }
    val topicName = Curriculum.topics.find { it.id == state.topicId }?.title ?: "Tất cả chủ đề"
    OutlinedTextField(state.query, model::setQuery, modifier = Modifier.fillMaxWidth().testTag("vocabulary-search"),
        label = { Text("Tìm từ Anh / Việt") }, placeholder = { Text("water, nước, ao mua") }, leadingIcon = { Icon(Icons.Outlined.Search, null) },
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = CardWhite, focusedContainerColor = CardWhite, unfocusedBorderColor = MaterialTheme.colorScheme.outline),
        singleLine = true, shape = RoundedCornerShape(18.dp))
    if (state.query.isNotEmpty()) TextButton({ model.setQuery("") }) { Text("Xóa tìm kiếm") }
    Box {
        OutlinedButton({ menuOpen = true }, Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Line),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = CardWhite, contentColor = Ink), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)) {
            Icon(Icons.Outlined.Tune, null, Modifier.size(20.dp), tint = Blue)
            Spacer(Modifier.width(10.dp)); Text(topicName, Modifier.weight(1f)); Icon(Icons.Outlined.ArrowDropDown, null)
        }
        DropdownMenu(menuOpen, { menuOpen = false }, Modifier.heightIn(max = 320.dp)) {
            DropdownMenuItem(text = { Text("Tất cả chủ đề") }, onClick = { menuOpen = false; model.selectTopic(null) })
            Curriculum.topics.forEach { topic -> DropdownMenuItem(text = { Text(topic.title) }, onClick = { menuOpen = false; model.selectTopic(topic.id) }) }
        }
    }
    Panel(BlueTint, false) {
        Text("${state.knownCount} / ${state.eligible.size} đã nhớ", Modifier.testTag("known-word-count"), style = MaterialTheme.typography.headlineMedium)
        LinearProgressIndicator(progress = { state.knownCount / state.eligible.size.coerceAtLeast(1).toFloat() }, modifier = Modifier.fillMaxWidth().height(6.dp), color = Blue, trackColor = Blue.copy(alpha = 0.1f), drawStopIndicator = {})
        Muted("Cần ôn: ${state.reviewCount} · Chưa học: ${state.unseenCount}")
        Text("Mức nhớ do bạn tự đánh giá sau khi xem thẻ.", style = MaterialTheme.typography.bodySmall, color = Subtle)
    }
    if (state.eligible.isEmpty()) {
        Text("Không tìm thấy từ phù hợp.", style = MaterialTheme.typography.titleMedium)
        Muted("Hãy thử từ khóa khác hoặc chọn tất cả chủ đề. Bạn có thể gõ nghĩa tiếng Việt không dấu.")
    }
    Text("Mỗi lượt tối đa 10 thẻ. Ưu tiên từ cần ôn và từ chưa học. Thống kê theo kết quả đang lọc.", style = MaterialTheme.typography.bodySmall, color = Subtle)
    MainButton("Học thẻ từ", enabled = state.eligible.isNotEmpty(), onClick = { model.startCards() })
    SecondaryButton("Ôn từ cần nhớ", enabled = state.reviewCount > 0, onClick = { model.startCards(reviewOnly = true) })
    Spacer(Modifier.height(8.dp))
    Panel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(color = Mint, shape = RoundedCornerShape(12.dp)) { Icon(Icons.Outlined.Bolt, null, Modifier.padding(10.dp).size(22.dp), tint = Teal) }
            Text("Một lượt ôn thật ngắn", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        }
        Muted("Chọn nghĩa tiếng Việt cho tối đa 5 từ. Ưu tiên từ cần ôn và từ chưa học trong chủ đề đang chọn.")
        MainButton("Kiểm tra nhanh", enabled = state.eligible.isNotEmpty(), onClick = model::startQuiz)
    }
    Text("${VocabularyCatalog.cards.size} mục từ và cụm từ trong ${Curriculum.topics.size} chủ đề, gồm từ trong ${Curriculum.lessons.size} bài hội thoại và từ mở rộng có ví dụ Anh–Việt.", style = MaterialTheme.typography.bodySmall, color = Subtle)
    Text("Tiến độ từ vựng lưu riêng trên điện thoại, không làm thay đổi số bài hội thoại đã hoàn thành.", style = MaterialTheme.typography.bodySmall, color = Subtle)
}

@Composable private fun WordFlashcard(state: VocabularyState, model: VocabularyViewModel, onSpeak: (String) -> Unit) {
    val card = state.currentCard ?: return
    Eyebrow("Nghe · nhớ · dùng lại")
    Panel {
        Text(card.english, style = MaterialTheme.typography.displaySmall)
        AudioButton("Nghe từ") { onSpeak(card.english) }
        if (state.revealed) {
            HorizontalDivider(color = Line)
            card.meanings.forEach { meaning -> Text(meaning, style = MaterialTheme.typography.bodyLarge) }
            if (card.lessonIds.isNotEmpty()) {
                val sourceTitles = card.lessonIds.mapNotNull { Curriculum.lesson(it)?.title }.take(2).joinToString("; ")
                Text("Gặp trong bài: $sourceTitles", style = MaterialTheme.typography.bodySmall, color = Subtle)
            } else {
                Text("Chủ đề: " + Curriculum.topics.filter { it.id in card.topicIds }.joinToString { it.title }, style = MaterialTheme.typography.bodySmall, color = Subtle)
            }
            card.example?.let { example ->
                Surface(color = Paper, shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Eyebrow("Dùng trong giao tiếp")
                        Text(example.english, style = MaterialTheme.typography.titleMedium)
                        Muted(example.vietnamese)
                        AudioButton("Nghe ví dụ") { onSpeak(example.english) }
                    }
                }
            }
        } else {
            Muted("Thử nhớ nghĩa trước khi lật thẻ.")
            MainButton("Xem nghĩa", onClick = model::reveal)
        }
    }
    InfoPill("Trạng thái: " + when(card.id) { in state.progress.known -> "Đã nhớ"; in state.progress.review -> "Cần ôn"; else -> "Chưa học" },
        color = if (card.id in state.progress.known) Teal else Blue, background = if (card.id in state.progress.known) Mint else BlueTint)
    if (state.revealed) {
        AdaptiveActions(
            primary = { MainButton(if (state.saving) "Đang lưu…" else "Đã nhớ", enabled = !state.saving, onClick = { model.markCurrent(true) }) },
            secondary = { SecondaryButton("Cần ôn", enabled = !state.saving, onClick = { model.markCurrent(false) }) })
        Muted("Chỉ chọn Đã nhớ khi bạn tự tin. Ứng dụng lưu xong mới chuyển sang thẻ tiếp.")
    }
    AdaptiveActions(
        primary = { OutlinedButton(model::previousCard, Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = RoundedCornerShape(16.dp), enabled = !state.saving && state.cardIndex > 0) { Text("Thẻ trước") } },
        secondary = { OutlinedButton(model::nextCard, Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = RoundedCornerShape(16.dp), enabled = !state.saving) { Text("Bỏ qua thẻ") } })
}

@Composable private fun WordQuiz(state: VocabularyState, model: VocabularyViewModel, onSpeak: (String) -> Unit) {
    val question = state.currentQuestion ?: return
    Eyebrow("Chọn nghĩa tiếng Việt")
    Panel {
        Text(question.card.english, style = MaterialTheme.typography.headlineLarge)
        AudioButton("Nghe từ") { onSpeak(question.card.english) }
    }
    question.options.forEachIndexed { index, option ->
        val color = when {
            state.quizChoice == null -> CardWhite
            option == question.correctMeaning -> Mint
            option == state.quizChoice -> Warm
            else -> CardWhite
        }
        OutlinedButton({ model.answerQuiz(option) }, Modifier.fillMaxWidth().heightIn(min = 52.dp),
            enabled = state.quizChoice == null, shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Line),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = color, contentColor = Ink, disabledContainerColor = color, disabledContentColor = Ink),
            contentPadding = PaddingValues(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = BlueTint, shape = CircleShape) { Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) { Text(('A' + index).toString(), color = Blue, style = MaterialTheme.typography.labelMedium) } }
                Text(option, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
    if (state.quizChoice != null) {
        Panel(if (state.quizChoice == question.correctMeaning) Mint else Warm, false) {
            Text(if (state.quizChoice == question.correctMeaning) "✓ Đúng rồi!" else "Mình cùng nhớ lại nhé", style = MaterialTheme.typography.titleMedium)
            Text("Nghĩa đúng: ${question.correctMeaning}")
        }
        MainButton(if (state.quizIndex == state.questions.lastIndex) "Xem kết quả" else "Câu tiếp theo", onClick = model::nextQuestion)
    }
    Muted("Lượt kiểm tra này không tự đánh dấu từ là Đã nhớ.")
}
