package com.mucheng.editor.indexer

import com.mucheng.editor.position.Position
import com.mucheng.editor.base.ColumnRowIndexer
import com.mucheng.editor.exceptions.CharPositionNotFoundException
import com.mucheng.editor.text.ContentProvider

// 实现了能够缓存的 -> 行列索引器
class CacheColumnRowIndexer(private val contentProvider: ContentProvider) : ColumnRowIndexer {

    // 缓存 Position
    private val cache: MutableList<Position> = ArrayList()

    // 实现 (Line & Row) -> Index 变换
    override fun columnRow2Index(column: Int, row: Int): Position {
        val result: Position
        // 通过距离最近的 CharPosition 推出 index
        for (alreadyCharPosition in cache) {
            if (alreadyCharPosition.column == column) {
                val thisRow = alreadyCharPosition.row
                val thisIndex = alreadyCharPosition.index
                if (thisRow == row) {
                    return alreadyCharPosition
                }

                result = Position(column, row, thisIndex + row - thisRow)
                cache.add(result)
                return result
            }
        }

        if (column == 1) {
            result = Position(column, row, row)
            cache.add(result)
            return result
        }

        val lastColumn = column - 1
        var offsetIndex = 0
        for (workColumn in 1..lastColumn) {
            val workRowSize =
                contentProvider.getColumnRowCount(workColumn) + 1
            offsetIndex += workRowSize
        }

        offsetIndex += row
        result = Position(column, row, offsetIndex)
        cache.add(result)
        return result
    }

    // FIXME 待修复 index -> (column, row)
    override fun index2columnRow(index: Int): Position {
        val result: Position
        // 通过距离最近的 CharPosition 推出 (column, row)
        for (alreadyCharPosition in cache) {
            if (alreadyCharPosition.index == index) {
                return alreadyCharPosition
            }
        }

        if (index == 0) {
            result = Position(1, 0, 0)
            cache.add(result)
            return result
        }

        if (index <= contentProvider.getColumnRowCount(1)) {
            result = Position(1, index, index)
            cache.add(result)
            return result
        }

        var workColumn = 1
        var offsetIndex = 0
        while (workColumn <= contentProvider.columnCount) {
            var rowCount = contentProvider.getColumnRowCount(workColumn) + 1
            offsetIndex += rowCount

            if (offsetIndex == index) {
                result = Position(workColumn, rowCount, offsetIndex)
                cache.add(result)
                return result
            }

            if (offsetIndex > index) {
                while (offsetIndex > index) {
                    if (rowCount == 0) {
                        --workColumn
                        rowCount = contentProvider.getColumnRowCount(workColumn) + 1
                    }
                    --rowCount
                    --offsetIndex
                }
                result = Position(workColumn, rowCount, offsetIndex)
                cache.add(result)
                return result
            }

            ++workColumn
        }

        throw CharPositionNotFoundException()
    }

}