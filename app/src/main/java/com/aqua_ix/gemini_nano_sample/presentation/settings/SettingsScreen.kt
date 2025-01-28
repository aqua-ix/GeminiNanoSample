package com.aqua_ix.gemini_nano_sample.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aqua_ix.gemini_nano_sample.R
import com.aqua_ix.gemini_nano_sample.data.AiModelSettings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    var tempSettings by remember { mutableStateOf(settings) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var temperatureText by remember { mutableStateOf(settings.temperature.toString()) }
    var topKText by remember { mutableStateOf(settings.topK.toString()) }
    var maxOutputTokensText by remember { mutableStateOf(settings.maxOutputTokens.toString()) }

    var temperatureError by remember { mutableStateOf(false) }
    var topKError by remember { mutableStateOf(false) }
    var maxOutputTokensError by remember { mutableStateOf(false) }

    val saveMessage = stringResource(R.string.settings_save_message)
    val resetMessage = stringResource(R.string.settings_reset_message)

    LaunchedEffect(settings) {
        temperatureText = settings.temperature.toString()
        topKText = settings.topK.toString()
        maxOutputTokensText = settings.maxOutputTokens.toString()

        temperatureError = false
        topKError = false
        maxOutputTokensError = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = paddingValues.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TemperatureSection(
                text = temperatureText,
                isError = temperatureError,
                onValueChange = { value ->
                    temperatureText = value
                    value.toFloatOrNull()?.let { floatValue ->
                        if (floatValue in 0f..1f) {
                            temperatureError = false
                            tempSettings = tempSettings.copy(temperature = floatValue)
                        } else {
                            temperatureError = true
                        }
                    } ?: run {
                        temperatureError = true
                    }
                }
            )

            TopKSection(
                text = topKText,
                isError = topKError,
                onValueChange = { value ->
                    topKText = value
                    value.toIntOrNull()?.let { intValue ->
                        if (intValue in 1..40) {
                            topKError = false
                            tempSettings = tempSettings.copy(topK = intValue)
                        } else {
                            topKError = true
                        }
                    } ?: run {
                        topKError = true
                    }
                }
            )

            MaxOutputTokensSection(
                text = maxOutputTokensText,
                isError = maxOutputTokensError,
                onValueChange = { value ->
                    maxOutputTokensText = value
                    value.toIntOrNull()?.let { intValue ->
                        if (intValue in 64..1024) {
                            maxOutputTokensError = false
                            tempSettings = tempSettings.copy(maxOutputTokens = intValue)
                        } else {
                            maxOutputTokensError = true
                        }
                    } ?: run {
                        maxOutputTokensError = true
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (!temperatureError && !topKError && !maxOutputTokensError) {
                            viewModel.updateSettings(tempSettings)
                            focusManager.clearFocus()
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = saveMessage,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !temperatureError && !topKError && !maxOutputTokensError
                ) {
                    Text(text = stringResource(R.string.settings_save))
                }

                OutlinedButton(
                    onClick = {
                        val defaultSettings = AiModelSettings()
                        tempSettings = defaultSettings
                        viewModel.resetToDefault()
                        temperatureText = defaultSettings.temperature.toString()
                        topKText = defaultSettings.topK.toString()
                        maxOutputTokensText = defaultSettings.maxOutputTokens.toString()
                        temperatureError = false
                        topKError = false
                        maxOutputTokensError = false
                        focusManager.clearFocus()
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = resetMessage,
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.settings_reset))
                }
            }
        }
    }
}

@Composable
private fun TemperatureSection(
    text: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.settings_temperature_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.settings_temperature_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = {
                    defaultKeyboardAction(ImeAction.Done)
                }
            ),
            isError = isError,
            supportingText = {
                if (isError) {
                    Text(
                        text = stringResource(R.string.settings_temperature_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}

@Composable
private fun TopKSection(
    text: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.settings_top_k_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.settings_top_k_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = {
                    defaultKeyboardAction(ImeAction.Done)
                }
            ),
            isError = isError,
            supportingText = {
                if (isError) {
                    Text(
                        text = stringResource(R.string.settings_top_k_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}

@Composable
private fun MaxOutputTokensSection(
    text: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.settings_max_tokens_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.settings_max_tokens_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = isError,
            keyboardActions = KeyboardActions(
                onDone = {
                    defaultKeyboardAction(ImeAction.Done)
                }
            ),
            supportingText = {
                if (isError) {
                    Text(
                        text = stringResource(R.string.settings_max_tokens_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}