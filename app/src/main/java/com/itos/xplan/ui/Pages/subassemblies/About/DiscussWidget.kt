import android.content.Intent
import android.net.Uri
import android.text.util.Linkify
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.itos.xplan.R
import com.itos.xplan.datatype.OriginCardItem
import com.itos.xplan.utils.OUI
import com.itos.xplan.XPlan.Companion.app

fun show_author() {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("coolmarket://u/3287595")
        app.startActivity(intent)
        app.finish()
    } catch (e: java.lang.Exception) {
        Toast.makeText(app, "打开酷安失败，已为您打开作者B站", Toast.LENGTH_SHORT).show()
        OUI.openLink("https://space.bilibili.com/329223542")
    }
}

fun moreapp() {
    MaterialAlertDialogBuilder(app)
        .setTitle("更多软件")
        .setMessage(app.resources.openRawResource(R.raw.moreapp).bufferedReader().readText())
        .setPositiveButton("了解", null)
        .show()
        .findViewById<MaterialTextView>(android.R.id.message)?.apply {
            setTextIsSelectable(true)
            Linkify.addLinks(this, Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS)
            // The first time the link is clicked the background does not change color and
            // the view needs to get focus once.
            requestFocus()
        }

}
@Composable
fun DiscussWidget() {
    val items = listOf(
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_bilibili),
            label = "BiliBili（开发者）",
            onClick = {
                OUI.openLink("https://space.bilibili.com/329223542")
            }),
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_bilibili),
            label = "BiliBili（合作伙伴）",
            onClick = {
                OUI.openLink("https://space.bilibili.com/1289434708")
            }
        ),
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_outline_coolapk),
            label = "酷安（开发者）",
            onClick = {
                show_author()
            }
        ),
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_outline_qq),
            label = "QQ频道",
            onClick = {
                OUI.openLink("https://pd.qq.com/s/8vadinclc")
            }
        ),
        OriginCardItem(
            icon = Icons.Default.Share,
            label = "更多软件",
            onClick = {
                moreapp()
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
