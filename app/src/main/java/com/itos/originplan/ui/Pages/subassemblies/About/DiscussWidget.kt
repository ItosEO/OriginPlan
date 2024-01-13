import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.itos.originplan.MainActivity
import com.itos.originplan.R
import com.itos.originplan.datatype.OriginCardItem

@Composable
fun DiscussWidget(context: Context) {
    val items = listOf(
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_bilibili),
            label = "BiliBili（开发者）",
            onClick = {
                (context as? MainActivity)?.openLink("https://space.bilibili.com/329223542")
            }),
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_bilibili),
            label = "BiliBili（合作伙伴）",
            onClick = {
                (context as? MainActivity)?.openLink("https://space.bilibili.com/1289434708")
            }
        ),
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_outline_coolapk),
            label = "酷安（开发者）",
            onClick = {
                (context as? MainActivity)?.show_author()
            }
        ),
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_outline_qq),
            label = "QQ频道",
            onClick = {
                (context as? MainActivity)?.openLink("https://pd.qq.com/s/8vadinclc")
            }
        ),
        OriginCardItem(
            icon = Icons.Default.Share,
            label = "更多软件",
            onClick = {
                (context as? MainActivity)?.moreapp()
            }
        ),
    )
    ItemsCardWidget(
        title = {
            Text(text = "讨论&反馈&联系我们")
        },
        items = items,
        showItemIcon = true
    )
}
