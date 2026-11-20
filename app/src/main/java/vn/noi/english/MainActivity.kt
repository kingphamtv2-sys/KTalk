package vn.noi.english

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vn.noi.english.audio.*
import vn.noi.english.data.LocalProgressRepository
import vn.noi.english.data.LocalVocabularyRepository
import vn.noi.english.vocabulary.VocabularyViewModel
import vn.noi.english.learning.*
import vn.noi.english.ui.NoiApp
import vn.noi.english.ui.theme.NoiTheme

class MainActivity : ComponentActivity() {
    private val vocabularyVm: VocabularyViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = VocabularyViewModel(LocalVocabularyRepository(applicationContext)) as T
        }
    }
    private val vm: LearningViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = LearningViewModel(LocalProgressRepository(applicationContext)) as T
        }
    }
    private lateinit var input: AndroidSpeechInput
    private lateinit var output: AndroidSpeechOutput
    private var permissionDialog by mutableStateOf(false)
    private var pendingTurn: String? = null
    private val systemSpeech = SystemSpeechRequest()
    private var resumed = false
    private fun turnKey() = "${vm.state.value.lessonId}:${vm.state.value.turnIndex}"
    private fun canAcceptSpeech(): Boolean = vm.state.value.let {
        it.screen == Screen.Conversation && it.mode == AnswerMode.Speak && !it.saving && it.feedback != Feedback.Accepted
    }
    private val systemSpeechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val recognized = systemSpeech.consume(turnKey(), canAcceptSpeech(), result.resultCode, result.data)
        if (recognized != SystemSpeechResult.Ignored) vm.setAudio(AudioPhase.Idle)
        when (recognized) {
            is SystemSpeechResult.Text -> { vm.message(null); vm.setAnswer(recognized.value) }
            is SystemSpeechResult.Failure -> vm.message(recognized.message)
            SystemSpeechResult.Cancelled -> vm.message("Bạn đã dừng nhận diện. Chạm micro để thử lại hoặc tiếp tục bằng chọn / gõ.")
            SystemSpeechResult.Empty -> vm.message("Chưa nhận được câu tiếng Anh. Bạn có thể thử lại hoặc chọn / gõ để tiếp tục.")
            SystemSpeechResult.Ignored -> Unit
        }
    }
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && pendingTurn == turnKey() && canAcceptSpeech()) {
            if (resumed && hasWindowFocus()) beginListening()
            else vm.message("Đã cấp quyền micro. Chạm micro để bắt đầu nói.")
        }
        else if (!granted) vm.message("Micro chưa được cấp quyền. Bạn vẫn có thể chọn hoặc gõ câu trả lời.")
        pendingTurn = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        systemSpeech.restore(savedInstanceState?.getString("system_speech_turn"))
        input = AndroidSpeechInput(this) { launchSystemSpeech(showUnavailable = false, automatic = true) }; output = AndroidSpeechOutput(this)
        setContent {
            NoiTheme {
                NoiApp(vm, input, output, ::requestMicrophone, ::openVoiceSettings, vocabularyVm,
                    onSystemSpeech = { launchSystemSpeech() }, openSpeechSettings = ::openRecognitionSettings)
                if (permissionDialog) {
                    val askedBefore = getPreferences(MODE_PRIVATE).getBoolean("asked_microphone", false)
                    val permanentlyDenied = askedBefore && !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                    AlertDialog(
                        onDismissRequest = { permissionDialog = false },
                        title = { Text(if (permanentlyDenied) "Cho phép micro trong cài đặt" else "Luyện nói bằng micro") },
                        text = { Text("Micro chỉ được bật khi bạn chạm để luyện nói. Dịch vụ nhận diện trên thiết bị có thể gửi giọng nói qua mạng để xử lý. Ứng dụng không lưu bản ghi âm. Bạn vẫn có thể chọn hoặc gõ khi chưa muốn dùng micro.") },
                        confirmButton = { TextButton(onClick = {
                            permissionDialog = false
                            if (permanentlyDenied) {
                                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
                            } else {
                                getPreferences(MODE_PRIVATE).edit().putBoolean("asked_microphone", true).apply()
                                pendingTurn = turnKey()
                                permissionRequest.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }) { Text(if (permanentlyDenied) "Mở cài đặt" else "Tiếp tục") } },
                        dismissButton = { TextButton(onClick = { permissionDialog = false }) { Text("Để sau") } },
                    )
                }
            }
        }
    }
    private fun requestMicrophone() {
        if (!canAcceptSpeech() || systemSpeech.pendingTurn != null || vm.state.value.audioPhase != AudioPhase.Idle) return
        vm.message(null)
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) beginListening()
        else permissionDialog = true
    }
    private fun beginListening() {
        if (!canAcceptSpeech() || systemSpeech.pendingTurn != null) return
        output.stop()
        input.start({ vm.setAnswer(it) }, { vm.message(it) }, { vm.setAudio(it) })
    }
    private fun launchSystemSpeech(showUnavailable: Boolean = true, automatic: Boolean = false): Boolean {
        if (!canAcceptSpeech()) return false
        if (systemSpeech.pendingTurn != null) return true
        input.cancel(); output.stop(); vm.message(null)
        val intent = systemSpeech.begin(turnKey(), automatic, resumed, hasWindowFocus()) ?: return false
        return try {
            vm.setAudio(AudioPhase.Preparing)
            systemSpeechLauncher.launch(intent)
            true
        } catch (_: Exception) {
            systemSpeech.cancel(); vm.setAudio(AudioPhase.Idle)
            if (showUnavailable) vm.message("Máy chưa mở được ứng dụng nhận diện giọng nói. Hãy bật hoặc cập nhật dịch vụ nhận diện có tiếng Anh trong Cài đặt; bạn vẫn có thể chọn / gõ.")
            false
        }
    }
    private fun openRecognitionSettings() {
        input.cancel(); output.stop(); vm.setAudio(AudioPhase.Idle)
        val intent = if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        else Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
        try { startActivity(intent) }
        catch (_: Exception) {
            try { startActivity(Intent(Settings.ACTION_SETTINGS)) }
            catch (_: Exception) { vm.message("Bạn hãy mở Cài đặt điện thoại để kiểm tra quyền micro và dịch vụ nhận diện tiếng Anh.") }
        }
    }
    private fun openVoiceSettings() {
        try { startActivity(Intent("com.android.settings.TTS_SETTINGS")) }
        catch (_: Exception) { vm.message("Bạn có thể tìm mục Chuyển văn bản thành giọng nói trong Cài đặt của điện thoại.") }
    }
    override fun onStop() {
        input.cancel(); output.stop(); vm.setAudio(AudioPhase.Idle)
        super.onStop()
    }
    override fun onResume() {
        super.onResume()
        resumed = true
        output.refresh()
    }
    override fun onPause() {
        resumed = false
        input.cancel(); output.stop(); vm.setAudio(AudioPhase.Idle)
        super.onPause()
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus && ::input.isInitialized && ::output.isInitialized) {
            input.cancel(); output.stop(); vm.setAudio(AudioPhase.Idle)
        }
    }
    override fun onDestroy() { input.close(); output.close(); super.onDestroy() }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("system_speech_turn", systemSpeech.pendingTurn)
        super.onSaveInstanceState(outState)
    }
}
