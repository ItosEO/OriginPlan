import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.itos.originplan.MainActivity
import com.itos.originplan.R
import com.itos.originplan.datatype.OriginCardItem
fun show_author(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("coolmarket://u/3287595")
        (context as? MainActivity)?.startActivity(intent)
        (context as? MainActivity)?.finish()
    } catch (e: java.lang.Exception) {
        Toast.makeText(context, "打开酷安失败，已为您打开作者B站", Toast.LENGTH_SHORT).show()
        (context as? MainActivity)?.openLink("https://space.bilibili.com/329223542")
    }
}

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
                show_author(context)
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
