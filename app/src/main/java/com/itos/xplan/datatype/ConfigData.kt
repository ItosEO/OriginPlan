package com.itos.xplan.datatype

private val dataList: List<List<String>> = emptyList()

data class ConfigData(
    var data: List<List<String>> = dataList,
    var shell: String = "",
    var restore :String="",
    var debug: String = ""
)
