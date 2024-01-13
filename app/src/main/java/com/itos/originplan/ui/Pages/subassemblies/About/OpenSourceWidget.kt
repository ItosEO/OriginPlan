import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.itos.originplan.MainActivity
import com.itos.originplan.R
import com.itos.originplan.datatype.OriginCardItem

@Composable
fun OpenSourceWidget(context: Context) {
    val items = listOf(
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_outline_code),

            label = "Github",
            onClick = {
                (context as? MainActivity)?.openLink("https://github.com/ItosEO/OriginPlan")
            }
        ),
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_outline_lisence),

            label = "许可证",
            onClick = {
                (context as? MainActivity)?.showLicenses()
            }
        ),

        )
    ItemsCardWidget(
        title = {
            Text(text = "开源")
        },
        items = items,
        showItemIcon = true
    )
}
