package io.agora.chatroom.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.agora.chatroom.ui.theme.AlphabetBodyLarge
import io.agora.chatroom.ui.theme.LargeCorner
import io.agora.chatroom.ui.theme.neutralColor2
import io.agora.chatroom.ui.theme.neutralColor95
import io.agora.chatroom.ui.theme.primaryColor5
import io.agora.chatroom.ui.viewmodel.messages.MessageComposerViewModel
import io.agora.chatroom.uikit.R
import kotlinx.coroutines.delay


/**
 * Custom input field that we use for our UI. It's fairly simple - shows a basic input with clipped
 * corners and a border stroke, with some extra padding on each side.
 *
 * Within it, we allow for custom decoration, so that the user can define what the input field looks like
 * when filled with content.
 *
 * @param value The current input value.
 * @param onValueChange Handler when the value changes as the user types.
 * @param modifier Modifier for styling.
 * @param enabled If the Composable is enabled for text input or not.
 * @param maxLines The number of lines that are allowed in the input, no limit by default.
 * @param border The [BorderStroke] that will appear around the input field.
 * @param innerPadding The padding inside the input field, around the label or input.
 * @param keyboardOptions The [KeyboardOptions] to be applied to the input.
 * @param decorationBox Composable function that represents the input field decoration as it's filled with content.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WidgetInputField(
    isDarkTheme:Boolean? = false,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    viewModel: MessageComposerViewModel,
    maxLines: Int = Int.MAX_VALUE,
    border: BorderStroke = BorderStroke(1.dp, if (isDarkTheme == true) neutralColor2 else neutralColor95),
    innerPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit,
) {

    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }

    val focusManager = LocalFocusManager.current

    // 创建一个软键盘控制器
    val keyboard = LocalSoftwareKeyboardController.current

    // 创建一个焦点请求器
    val focus = remember {
        FocusRequester()
    }

    // Workaround to move cursor to the end after selecting a suggestion
    val selection = if (textFieldValueState.isCursorAtTheEnd()) {
        TextRange(value.length)
    } else {
        textFieldValueState.selection
    }

    val textFieldValue = textFieldValueState.copy(
        text = value,
        selection = selection
    )

    val description = stringResource(id = R.string.stream_compose_cd_message_input)

    BasicTextField(
        modifier = Modifier
            .focusRequester(focus)
            .border(border = border, shape = LargeCorner)
            .clip(LargeCorner)
            .background(if (isDarkTheme == true) neutralColor2 else neutralColor95)
            .padding(innerPadding)
            .semantics { contentDescription = description },
        value = textFieldValue,
        onValueChange = {
            textFieldValueState = it
            if (value != it.text) {
                onValueChange(it.text)
            }
        },
        textStyle = AlphabetBodyLarge,
        cursorBrush = SolidColor(primaryColor5),
        decorationBox = { innerTextField -> decorationBox(innerTextField) },
        maxLines = maxLines,
        singleLine = maxLines == 1,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
    )

    LaunchedEffect(viewModel.isShowKeyboard.value) {
        if (viewModel.isShowKeyboard.value){
            delay(100)
            focus.requestFocus()
            keyboard?.show()
        }else{
            delay(100)
            focusManager.clearFocus()
            keyboard?.hide()
        }
    }
}

/**
 * Check if the [TextFieldValue] state represents a UI with the cursor at the end of the input.
 *
 * @return True if the cursor is at the end of the input.
 */
private fun TextFieldValue.isCursorAtTheEnd(): Boolean {
    val textLength = text.length
    val selectionStart = selection.start
    val selectionEnd = selection.end

    return textLength == selectionStart && textLength == selectionEnd
}
