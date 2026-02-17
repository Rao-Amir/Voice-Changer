package com.pixelzlab.app.feature.audio_import.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.pixelzlab.app.feature.audio_effect.navigation.navigateToApplyEffect
import com.pixelzlab.app.feature.audio_import.AudioImportScreen
import java.io.File
import java.io.FileNotFoundException

const val audioImportRoute = "audio_import"

fun NavController.navigateToAudioImport(navOptions: NavOptions?=null){
    this.navigate(audioImportRoute, navOptions)
}

fun NavGraphBuilder.audioImportScreen(navController: NavHostController){
    composable(audioImportRoute) {
        val context = LocalContext.current

        AudioImportScreen(navController) { param ->
            val uri = param as? Uri ?: return@AudioImportScreen

            // Kiểm tra loại MIME
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType?.startsWith("audio/") == true) {
                try {
                    val input = context.contentResolver.openInputStream(uri)
                    val tmpFile = File(context.cacheDir, "picked_audio.wav")

                    input?.use { inp ->
                        tmpFile.outputStream().use { out ->
                            inp.copyTo(out)
                        }
                    }

                    val path = tmpFile.absolutePath
                    navController.navigateToApplyEffect(path)

                } catch (e: FileNotFoundException) {
                    Toast.makeText(context, "Không thể mở tệp âm thanh", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Lỗi khi xử lý tệp", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Tệp được chọn không phải âm thanh", Toast.LENGTH_SHORT).show()
            }
        }
    }
}