package vn.noi.english.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
            else { Eyebrow("Vocabulary"); ScreenHeading("Học từ vựng"); Muted("Nhớ từng cụm từ. Tự tin hơn khi nói.") }

            if (state.error != null) Panel(Color(0xFFF6EEDF), false) { Text(state.error) }
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
    Box {
        OutlinedButton({ menuOpen = true }, Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = RoundedCornerShape(12.dp)) { Text(topicName) }
        DropdownMenu(menuOpen, { menuOpen = false }, Modifier.heightIn(max = 320.dp)) {
            DropdownMenuItem(text = { Text("Tất cả chủ đề") }, onClick = { menuOpen = false; model.selectTopic(null) })
            Curriculum.topics.forEach { topic -> DropdownMenuItem(text = { Text(topic.title) }, onClick = { menuOpen = false; model.selectTopic(topic.id) }) }
        }
    }
    Panel {
        Text("${state.knownCount} / ${state.eligible.size} đã nhớ", Modifier.testTag("known-word-count"), style = MaterialTheme.typography.headlineMedium)
        LinearProgressIndicator(progress = { state.knownCount / state.eligible.size.coerceAtLeast(1).toFloat() }, modifier = Modifier.fillMaxWidth(), color = Blue, trackColor = Line, drawStopIndicator = {})
        Muted("Cần ôn: ${state.reviewCount} · Chưa học: ${state.unseenCount}")
        Text("Mức nhớ do bạn tự đánh giá sau khi xem thẻ.", style = MaterialTheme.typography.bodySmall, color = Subtle)
    }
    MainButton("Học thẻ từ", onClick = { model.startCards() })
    SecondaryButton("Ôn từ cần nhớ", enabled = state.reviewCount > 0, onClick = { model.startCards(reviewOnly = true) })
    Spacer(Modifier.height(8.dp))
    Panel(Color(0xFFEEF2FA), false) {
        Text("Một lượt ôn thật ngắn", style = MaterialTheme.typography.titleMedium)
        Muted("Chọn nghĩa tiếng Việt cho tối đa 5 từ. Ưu tiên từ cần ôn và từ chưa học trong chủ đề đang chọn.")
        MainButton("Kiểm tra nhanh", onClick = model::startQuiz)
    }
    Muted("${VocabularyCatalog.cards.size} mục từ và cụm từ từ ${Curriculum.lessons.size} bài hội thoại. Những mục có cùng tiếng Anh được gộp lại khi xuất hiện ở nhiều bài.")
    Muted("Tiến độ từ vựng lưu riêng trên điện thoại, không làm thay đổi số bài hội thoại đã hoàn thành.")
}

@Composable private fun WordFlashcard(state: VocabularyState, model: VocabularyViewModel, onSpeak: (String) -> Unit) {
    val card = state.currentCard ?: return
    Eyebrow("Nghe · nhớ · dùng lại")
    Panel {
        Text(card.english, style = MaterialTheme.typography.headlineLarge)
        AudioButton("Nghe từ") { onSpeak(card.english) }
        if (state.revealed) {
            HorizontalDivider(color = Line)
            card.meanings.forEach { meaning -> Text(meaning, style = MaterialTheme.typography.bodyLarge) }
            val sourceTitles = card.lessonIds.mapNotNull { Curriculum.lesson(it)?.title }.take(2).joinToString("; ")
            Muted("Gặp trong bài: $sourceTitles")
        } else {
            Muted("Thử nhớ nghĩa trước khi lật thẻ.")
            MainButton("Xem nghĩa", onClick = model::reveal)
        }
    }
    Muted("Trạng thái: " + when(card.id) { in state.progress.known -> "Đã nhớ"; in state.progress.review -> "Cần ôn"; else -> "Chưa học" })
    if (state.revealed) {
        MainButton(if (state.saving) "Đang lưu…" else "Đã nhớ", enabled = !state.saving, onClick = { model.markCurrent(true) })
        SecondaryButton("Cần ôn", enabled = !state.saving, onClick = { model.markCurrent(false) })
        Muted("Chỉ chọn Đã nhớ khi bạn tự tin. Ứng dụng lưu xong mới chuyển sang thẻ tiếp.")
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(model::previousCard, Modifier.weight(1f).heightIn(min = 48.dp), enabled = !state.saving && state.cardIndex > 0) { Text("Thẻ trước") }
        OutlinedButton(model::nextCard, Modifier.weight(1f).heightIn(min = 48.dp), enabled = !state.saving) { Text("Bỏ qua thẻ") }
    }
}

@Composable private fun WordQuiz(state: VocabularyState, model: VocabularyViewModel, onSpeak: (String) -> Unit) {
    val question = state.currentQuestion ?: return
    Eyebrow("Chọn nghĩa tiếng Việt")
    Panel {
        Text(question.card.english, style = MaterialTheme.typography.headlineLarge)
        AudioButton("Nghe từ") { onSpeak(question.card.english) }
    }
    question.options.forEach { option ->
        val color = when {
            state.quizChoice == null -> Color.White
            option == question.correctMeaning -> Mint
            option == state.quizChoice -> Color(0xFFF6EEDF)
            else -> Color.White
        }
        OutlinedButton({ model.answerQuiz(option) }, Modifier.fillMaxWidth().heightIn(min = 52.dp),
            enabled = state.quizChoice == null, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Line),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = color, contentColor = Ink, disabledContainerColor = color, disabledContentColor = Ink),
            contentPadding = PaddingValues(16.dp)) { Text(option, Modifier.fillMaxWidth()) }
    }
    if (state.quizChoice != null) {
        Panel(if (state.quizChoice == question.correctMeaning) Mint else Color(0xFFF6EEDF), false) {
            Text(if (state.quizChoice == question.correctMeaning) "✓ Đúng rồi!" else "Mình cùng nhớ lại nhé", style = MaterialTheme.typography.titleMedium)
            Text("Nghĩa đúng: ${question.correctMeaning}")
        }
        MainButton(if (state.quizIndex == state.questions.lastIndex) "Xem kết quả" else "Câu tiếp theo", onClick = model::nextQuestion)
    }
    Muted("Lượt kiểm tra này không tự đánh dấu từ là Đã nhớ.")
}
