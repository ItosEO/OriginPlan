import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage() {
    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(TopAppBarDefaults.windowInsets)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "关于")
                },
            )
        },
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                StatusWidget()
            }
            item {
                DonateWidget()
            }
            item {
                DiscussWidget()
            }
            item {
                OpenSourceWidget()
            }
        }
    }
}