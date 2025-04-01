package edu.stanford.bdh.engagehf.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import edu.stanford.bdh.engagehf.bluetooth.data.models.MessageUiModel
import edu.stanford.spezi.modules.design.component.AsyncButton
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Colors.primary
import edu.stanford.spezi.ui.DefaultElevatedCard
import edu.stanford.spezi.ui.Sizes
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.lighten
import edu.stanford.spezi.ui.testing.testIdentifier

private const val TEXT_WEIGHT = 0.9f

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    model: MessageUiModel,
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
                MessageIcon(model.icon)
                Spacer(modifier = Modifier.width(Spacings.small))
                Text(
                    modifier = Modifier.testIdentifier(MessageItemTestIdentifiers.TITLE),
                    text = model.title,
                    style = TextStyles.titleMedium,
                    color = Colors.onBackground,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (model.isDismissible) {
                    val iconButtonColors = IconButtonDefaults.iconButtonColors()
                    AsyncButton(
                        shape = IconButtonDefaults.filledShape,
                        containerColor = iconButtonColors.containerColor,
                        contentColor = iconButtonColors.contentColor,
                        contentPadding = PaddingValues(Spacings.extraSmall),
                        onClick = {
                            onAction(Action.MessageItemDismissed(model.id))
                        },
                        isLoading = model.isDismissing,
                        modifier = Modifier.size(Sizes.Icon.small),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.dismiss_message),
                            tint = Colors.onBackground,
                        )
                    }
                }
            }
            model.description?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (model.isExpanded) {
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
                            onAction(Action.ToggleExpand(model.id))
                        }) {
                        Icon(
                            imageVector = if (model.isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (model.isExpanded) "Show less" else "Show more",
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacings.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                model.action?.let { action ->
                    Button(
                        modifier = Modifier.testIdentifier(MessageItemTestIdentifiers.ACTION_BUTTON),
                        colors = ButtonDefaults.buttonColors(containerColor = primary),
                        onClick = {
                            onAction(Action.MessageItemClicked(model.id))
                        },
                    ) {
                        Text(
                            text = action.description.text(),
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
    ACTION_BUTTON,
}

@Composable
@ThemePreviews
fun MessageListPreview() {
    SpeziTheme {
        LazyColumn(modifier = Modifier.padding(Spacings.medium)) {
            items(sampleMessageModels) { model ->
                MessageItem(model = model, onAction = { })
            }
        }
    }
}

private val sampleMessageModels = listOf(
    MessageUiModel(
        id = java.util.UUID.randomUUID().toString(),
        title = "Weight Gained",
        description = "You gained weight. Please take action.",
        action = MessageAction.MeasurementsAction,
        isDismissible = true,
        isDismissing = false,
        isLoading = false,
        isExpanded = true,
    ),
    MessageUiModel(
        id = java.util.UUID.randomUUID().toString(),
        title = "Medication Change",
        description = "Your medication has been changed. Please take action. " +
            "Your medication has been changed. Please take action. Your medication " +
            "has been changed. " +
            "Please take action.",
        action = MessageAction.MedicationsAction,
        isDismissible = true,
        isDismissing = false,
        isLoading = false,
        isExpanded = true,
    ),
    MessageUiModel(
        id = java.util.UUID.randomUUID().toString(),
        title = "Medication Change",
        description = "Your medication has been changed. Please take action. " +
            "Your medication has been changed. Please take action. Your medication " +
            "has been changed. " +
            "Please take action.",
        action = MessageAction.MedicationsAction,
        isDismissible = true,
        isDismissing = false,
        isLoading = false,
        isExpanded = true,
    ),
)
