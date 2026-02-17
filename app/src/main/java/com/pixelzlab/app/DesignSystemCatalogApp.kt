package com.pixelzlab.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelzlab.app.designsystem.component.AppButton
import com.pixelzlab.app.designsystem.component.AppIconButton
import com.pixelzlab.app.designsystem.component.AppIconToggleButton
import com.pixelzlab.app.designsystem.component.AppLoadingWheel
import com.pixelzlab.app.designsystem.component.ButtonType
import com.pixelzlab.app.designsystem.theme.AppTheme

/**
 * Created by pixelzlab on 08/07/2023.
 */

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesignSystemCatalogApp() {
    AppTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .safeContentPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "Design System Catalog",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            item { Text("Enabled Buttons", Modifier.padding(top = 16.dp)) }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppButton(
                        onClick = {},
                        buttonType = ButtonType.Filled
                    ) {
                        Text(text = "Enabled")
                    }
                    AppButton(
                        onClick = {},
                        buttonType = ButtonType.Outlined
                    ) {
                        Text(text = "Enabled")
                    }
                    AppButton(
                        onClick = {},
                        buttonType = ButtonType.Text
                    ) {
                        Text(text = "Enabled")
                    }
                }
            }
            item { Text("Disabled Buttons", Modifier.padding(top = 16.dp)) }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppButton(
                        onClick = {},
                        buttonType = ButtonType.Filled,
                        enabled = false
                    ) {
                        Text(text = "Disabled")
                    }
                    AppButton(
                        onClick = {},
                        buttonType = ButtonType.Outlined,
                        enabled = false
                    ) {
                        Text(text = "Disabled")
                    }
                    AppButton(
                        onClick = {},
                        buttonType = ButtonType.Text,
                        enabled = false
                    ) {
                        Text(text = "Disabled")
                    }
                }
            }
            item { Text("Icon Toggle buttons", Modifier.padding(top = 16.dp)) }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    var firstChecked by remember { mutableStateOf(false) }
                    AppIconToggleButton(
                        checked = firstChecked,
                        onCheckedChange = { checked -> firstChecked = checked },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                            )
                        },
                        checkedIcon = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                            )
                        },
                    )
                    var secondChecked by remember { mutableStateOf(true) }
                    AppIconToggleButton(
                        checked = secondChecked,
                        onCheckedChange = { checked -> secondChecked = checked },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                            )
                        },
                        checkedIcon = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                            )
                        },
                    )
                    AppIconToggleButton(
                        checked = false,
                        onCheckedChange = {},
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                            )
                        },
                        checkedIcon = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                            )
                        },
                        enabled = false,
                    )
                    AppIconToggleButton(
                        checked = true,
                        onCheckedChange = {},
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                            )
                        },
                        checkedIcon = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                            )
                        },
                        enabled = false,
                    )
                }
            }
            item { Text("Icon buttons", Modifier.padding(top = 16.dp)) }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppIconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null
                        )
                    }
                    AppIconButton(
                        onClick = { },
                        enabled = false
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null
                        )
                    }
                }
            }
            item { Text("Loading", Modifier.padding(top = 16.dp)) }
            item {
                AppLoadingWheel(contentDesc = "")
            }
        }
    }
}