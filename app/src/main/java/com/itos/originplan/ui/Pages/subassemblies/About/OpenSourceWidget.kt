import android.content.Context
import android.text.util.Linkify
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.itos.originplan.MainActivity
import com.itos.originplan.R
import com.itos.originplan.datatype.OriginCardItem

fun showLicenses(context: Context) {
    MaterialAlertDialogBuilder(context)
        .setTitle(R.string.action_licenses)
        .setMessage(
            (context as? MainActivity)?.resources?.openRawResource(R.raw.licenses)?.bufferedReader()
                ?.readText()
        )
        .setPositiveButton(android.R.string.ok, null)
        .show()
        .findViewById<MaterialTextView>(android.R.id.message)?.apply {
            setTextIsSelectable(true)
            Linkify.addLinks(this, Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS)
            requestFocus()
        }
}

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
                showLicenses(context)
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
