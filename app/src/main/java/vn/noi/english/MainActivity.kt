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
    private fun turnKey() = "${vm.state.value.lessonId}:${vm.state.value.turnIndex}"
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && pendingTurn == turnKey() && vm.state.value.screen == Screen.Conversation && vm.state.value.mode == AnswerMode.Speak) beginListening()
        else if (!granted) vm.message("Micro chưa được cấp quyền. Bạn vẫn có thể chọn hoặc gõ câu trả lời.")
        pendingTurn = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        input = AndroidSpeechInput(this); output = AndroidSpeechOutput(this)
        setContent {
            NoiTheme {
                NoiApp(vm, input, output, ::requestMicrophone, ::openVoiceSettings, vocabularyVm)
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
        if (!input.available) { vm.message("Thiết bị chưa có dịch vụ nhận diện giọng nói. Bạn vẫn có thể chọn hoặc gõ để học."); return }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) beginListening()
        else permissionDialog = true
    }
    private fun beginListening() {
        output.stop()
        input.start({ vm.setAnswer(it) }, { vm.message(it) }, { vm.setAudio(it) })
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
        output.refresh()
    }
    override fun onDestroy() { input.close(); output.close(); super.onDestroy() }
}
