package com.jozeftvrdy.game.guessorder.game.model

enum class ItemFill {
    FillA,
    FillB,
    FillC,
    FillD,
    FillE,
    FillF,
    FillG,
    FillH,
    ;

    companion object {
        fun getAll(): List<ItemFill> = ItemFill.entries
        fun getFirstN(n: Int): List<ItemFill> {
            val allItems = getAll()
            require(n <= allItems.size)
            return if (n == allItems.size) {
                allItems
            } else {
                allItems.subList(0, n)
            }
        }
    }
}