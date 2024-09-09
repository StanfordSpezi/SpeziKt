package edu.stanford.bdh.engagehf.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.spezi.core.design.component.DefaultElevatedCard
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.theme.lighten
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import java.time.ZonedDateTime

private const val TEXT_WEIGHT = 0.9f

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    message: Message,
    onAction: (Action) -> Unit,
) {
    DefaultElevatedCard(
        modifier = modifier
            .padding(vertical = Spacings.small)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(Colors.surface.lighten())
                .padding(horizontal = Spacings.small)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(top = Spacings.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MessageIcon(message.icon)
                Spacer(modifier = Modifier.width(Spacings.small))
                Text(
                    modifier = Modifier.testIdentifier(MessageItemTestIdentifiers.TITLE),
                    text = message.title,
                    style = TextStyles.titleMedium,
                    color = Colors.onBackground,
                )
            }
            message.description.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (message.isExpanded) {
                        Text(
                            modifier = Modifier
                                .weight(TEXT_WEIGHT)
                                .testIdentifier(MessageItemTestIdentifiers.DESCRIPTION),
                            text = it,
                            style = TextStyles.bodySmall,
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .weight(TEXT_WEIGHT)
                                .testIdentifier(MessageItemTestIdentifiers.DESCRIPTION),
                            text = it,
                            style = TextStyles.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        modifier = Modifier.width(Sizes.Icon.small),
                        onClick = {
                            onAction(Action.ToggleExpand(message))
                        }) {
                        Icon(
                            imageVector = if (message.isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (message.isExpanded) "Show less" else "Show more",
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacings.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                message.dueDateFormattedString?.let {
                    Text(
                        modifier = Modifier.testIdentifier(MessageItemTestIdentifiers.DUE_DATE),
                        text = "Due Date: $it",
                        style = TextStyles.labelSmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.testIdentifier(MessageItemTestIdentifiers.ACTION_BUTTON),
                    colors = ButtonDefaults.buttonColors(containerColor = primary),
                    enabled = !message.isLoading,
                    onClick = {
                        onAction(Action.MessageItemClicked(message))
                    },
                ) {
                    if (message.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(Sizes.Content.small))
                    } else {
                        Text(
                            text = stringResource(R.string.message_item_button_action_text),
                            color = Colors.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageIcon(
    messageTypeIcon: Int,
    contentDescription: String? = null,
    size: Dp = Sizes.Icon.small,
) {
    Icon(
        painter = painterResource(id = messageTypeIcon),
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
    )
}

enum class MessageItemTestIdentifiers {
    TITLE,
    DESCRIPTION,
    DUE_DATE,
    ACTION_BUTTON,
}

@Composable
@ThemePreviews
fun MessageListPreview() {
    SpeziTheme(isPreview = true) {
        LazyColumn(modifier = Modifier.padding(Spacings.medium)) {
            items(sampleMessages) { message ->
                MessageItem(message = message, onAction = { })
            }
        }
    }
}

private val sampleMessages = listOf(
    Message(
        id = java.util.UUID.randomUUID().toString(),
        dueDate = ZonedDateTime.now().plusDays(1),
        completionDate = null,
        type = MessageType.WeightGain,
        title = "Weight Gained",
        description = "You gained weight. Please take action.",
        action = "New Weight Entry",
    ),
    Message(
        id = java.util.UUID.randomUUID().toString(),
        dueDate = ZonedDateTime.now().plusDays(2),
        completionDate = null,
        type = MessageType.MedicationChange,
        title = "Medication Change",
        description = "Your medication has been changed. Please take action. " +
            "Your medication has been changed. Please take action. Your medication " +
            "has been changed. " +
            "Please take action.",
        action = "Go to medication",
    ),
    Message(
        id = java.util.UUID.randomUUID().toString(),
        dueDate = ZonedDateTime.now().plusDays(2),
        completionDate = null,
        type = MessageType.Unknown,
        title = "Medication Change",
        description = "Your medication has been changed. Please take action. " +
            "Your medication has been changed. Please take action. Your medication " +
            "has been changed. " +
            "Please take action.",
        action = "Go to medication",
    ),
)
