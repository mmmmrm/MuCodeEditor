package com.mucheng.editor.position

// 二维位置而不是区间，只表示一个点
data class Position(var column: Int, var row: Int, var index: Int){

    fun toColumnRowPosition(): ColumnRowPosition {
        return ColumnRowPosition(column, row)
    }

}