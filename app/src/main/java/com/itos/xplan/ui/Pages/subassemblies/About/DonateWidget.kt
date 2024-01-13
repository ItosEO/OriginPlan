import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import com.itos.xplan.R
import com.itos.xplan.datatype.OriginCardItem
import com.itos.xplan.utils.OUI

@Composable
fun DonateWidget() {
    LocalContext.current

    val items = listOf(
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_alipay),
            label = "支付宝",
            onClick = {
                OUI.showImageDialog("zfb.jpg")
            }
        ),
        OriginCardItem(
            icon = ImageVector.vectorResource(R.drawable.ic_wechatpay),
            label = "微信",
            onClick = {
                OUI.showImageDialog("wx.png")
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
