package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.util.AppStrings

@Composable
fun DisplayScreen(
    expression: String,
    previewResult: String,
    finalResult: String,
    errorMessageKey: String?,
    language: Language,
    onCopy: (String) -> Unit,
    onShare: (String, String) -> Unit,
    onSaveFavorite: (title: String, expr: String, result: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFavoriteDialog by remember { mutableStateOf(false) }
    var favoriteTitle by remember { mutableStateOf("") }
    var favoriteNote by remember { mutableStateOf("") }

    val exprScrollState = rememberScrollState()
    val resScrollState = rememberScrollState()

    LaunchedEffect(expression) {
        exprScrollState.scrollTo(exprScrollState.maxValue)
    }

    LaunchedEffect(finalResult) {
        resScrollState.scrollTo(resScrollState.maxValue)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("display_screen_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Expression Line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScrollState),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    color = if (expression.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = if (expression.length > 15) 24.sp else 30.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("text_expression")
                )
            }

            // Error or Result Display
            if (errorMessageKey != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = AppStrings.get(errorMessageKey, language),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        textAlign = TextAlign.End
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(resScrollState),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (finalResult.isNotEmpty()) {
                        Text(
                            text = "= $finalResult",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = if (finalResult.length > 12) 36.sp else 44.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("text_final_result")
                        )
                    } else if (previewResult.isNotEmpty()) {
                        Text(
                            text = "= $previewResult",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("text_preview_result")
                        )
                    } else {
                        Spacer(modifier = Modifier.height(44.dp))
                    }
                }
            }

            // Action Toolbar (Copy, Share, Favorite)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pro Calculator",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val activeValue = if (finalResult.isNotEmpty()) finalResult else previewResult

                    // Copy Button
                    IconButton(
                        onClick = {
                            if (activeValue.isNotEmpty()) {
                                onCopy(activeValue)
                                Toast.makeText(context, AppStrings.get("copied", language), Toast.LENGTH_SHORT).show()
                            } else if (expression.isNotEmpty()) {
                                onCopy(expression)
                                Toast.makeText(context, AppStrings.get("copied", language), Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_copy_display")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = AppStrings.get("copy", language),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = {
                            if (finalResult.isNotEmpty()) {
                                onShare(expression, finalResult)
                            } else if (expression.isNotEmpty()) {
                                onShare(expression, previewResult.ifEmpty { expression })
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_share_display")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = AppStrings.get("share", language),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Favorite Button
                    IconButton(
                        onClick = {
                            if (finalResult.isNotEmpty() || expression.isNotEmpty()) {
                                favoriteTitle = expression
                                showFavoriteDialog = true
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_favorite_display")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.StarBorder,
                            contentDescription = AppStrings.get("add_favorite", language),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Add Favorite Dialog
    if (showFavoriteDialog) {
        AlertDialog(
            onDismissRequest = { showFavoriteDialog = false },
            title = { Text(AppStrings.get("add_favorite", language), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = favoriteTitle,
                        onValueChange = { favoriteTitle = it },
                        label = { Text(AppStrings.get("title", language)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = favoriteNote,
                        onValueChange = { favoriteNote = it },
                        label = { Text(AppStrings.get("note", language)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val expr = expression
                        val res = if (finalResult.isNotEmpty()) finalResult else previewResult
                        onSaveFavorite(favoriteTitle, expr, res)
                        showFavoriteDialog = false
                        Toast.makeText(context, AppStrings.get("saved", language) ?: AppStrings.get("copied", language), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(AppStrings.get("save", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showFavoriteDialog = false }) {
                    Text(AppStrings.get("cancel", language))
                }
            }
        )
    }
}
