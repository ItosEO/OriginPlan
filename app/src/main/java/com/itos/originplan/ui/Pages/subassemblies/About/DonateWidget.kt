import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import com.itos.originplan.R
import com.itos.originplan.datatype.OriginCardItem
import com.itos.originplan.utils.showImageDialog

@Composable
fun DonateWidget(context: Context) {
    LocalContext.current

    val items = listOf(
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_alipay),
            label = "支付宝",
            onClick = {
                showImageDialog("zfb.jpg",context)
            }
        ),
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_wechatpay),
            label = "微信",
            onClick = {
                showImageDialog("wx.png",context)
            }
        ),

        )
    ItemsCardWidget(
        title = {
            Text(text = "捐赠")
        },
        items = items,
        showItemIcon = true
    )
}
