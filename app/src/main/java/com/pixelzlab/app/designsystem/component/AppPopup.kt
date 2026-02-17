package com.pixelzlab.app.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.pixelzlab.app.designsystem.theme.AppThemePreview
import com.pixelzlab.app.designsystem.theme.Colors

/**
 * Created by pixelzlab on 14/10/2023.
 */
@Composable
fun AppAlertDialog(
    title: String,
    content: String,
    titleButtonPrimary: String = "OK",
    titleButtonSecondary: String = "",
    onDismissRequest: () -> Unit = {},
    onClickButtonPrimary: () -> Unit = {},
    onClickButtonSecondary: () -> Unit = {},
) {
    var openDialog by remember {
        mutableStateOf(true)
    }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
                openDialog = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(
                            top = 32.dp,
                            bottom = 24.dp
                        )
                ) {
                    Text(
                        text = title,
                        color = Colors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = content,
                        color = Colors.TextPrimary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    AppButton(
                        onClick = {
                            openDialog = false
                            onClickButtonPrimary()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = titleButtonPrimary,
                            color = Color.White,
                            fontSize = 16.sp,
                        )
                    }
                    if (titleButtonSecondary.isNotEmpty()) {
                        AppButton(
                            buttonType = ButtonType.Outlined,
                            onClick = {
                                openDialog = false
                                onClickButtonSecondary()
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            Text(
                                text = titleButtonSecondary,
                                color = Colors.TextPrimary,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppPopupPreview() {
    AppThemePreview {
        AppAlertDialog(
            title = "Thông báo",
            content = "Bạn có chắc chắn muốn xóa báo giá này không?",
            titleButtonPrimary = "Xóa",
            titleButtonSecondary = "Cancel",
            onClickButtonPrimary = {},
        )
    }
}
