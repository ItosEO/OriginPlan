package com.itos.xplan.datatype

import androidx.compose.ui.graphics.vector.ImageVector

data class OriginCardItem(
    val icon: ImageVector? = null,
    val label: String,
    val content: String? = null,
    val onClick: (() -> Unit)? = null
)
