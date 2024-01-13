import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itos.xplan.datatype.OriginCardItem

@Composable
fun ItemsCardWidget(
    colors: CardColors = CardDefaults.elevatedCardColors(),
    onClick: (() -> Unit)? = null,
    showItemIcon: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    items: List<OriginCardItem>,
    buttons: (@Composable () -> Unit)? = null
) {
    CardWidget(
        colors = colors,
        onClick = onClick,
        icon = icon,
        title = title,
        content = {
            @Composable
            fun ItemWidget(item: OriginCardItem) {
                Row(
                    modifier = Modifier
                        .clickable(enabled = item.onClick != null, onClick = item.onClick ?: {})
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (showItemIcon) {
                        if (item.icon != null) {
                            Icon(imageVector = item.icon, contentDescription = item.label)
                        } else {
                            Spacer(modifier = Modifier.size(32.dp))
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(text = item.label, style = MaterialTheme.typography.bodyLarge)
                        if (item.content != null) {
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            Column {
                items.forEach {
                    ItemWidget(it)
                }
            }
        },
        buttons = buttons
    )
}

@Composable
fun CardWidget(
    colors: CardColors = CardDefaults.elevatedCardColors(),
    onClick: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    buttons: (@Composable () -> Unit)? = null
) {
    ElevatedCard(
        colors = colors,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = onClick != null, onClick = onClick ?: {})
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.secondary) {
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        icon()
                    }
                }
            }
            if (title != null) {
                ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        title()
                    }
                }
            }
            if (content != null) {
                Box {
                    content()
                }
            }
            if (buttons != null) {
                Box {
                    buttons()
                }
            }
        }
    }
}