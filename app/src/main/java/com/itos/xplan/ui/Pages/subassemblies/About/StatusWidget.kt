import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.itos.xplan.BuildConfig
import com.itos.xplan.R
import com.itos.xplan.XPlan.Companion.app

@Composable
fun StatusWidget() {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer

    val level = app.getString(R.string.build_type)

    CardWidget(
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = onContainerColor
        ),
        icon = {
            Image(
                modifier = Modifier
                    .size(56.dp),
                painter = rememberDrawablePainter(
                    drawable = ContextCompat.getDrawable(
                        LocalContext.current,
                        R.mipmap.ic_launcher_xplan
                    )
                ),
                contentDescription = stringResource(id = R.string.app_name)
            )
        },
        title = {
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        content = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "$level [${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})]",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    )
}