package com.jozeftvrdy.game.guessorder.repository

import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.game.model.Result
import com.jozeftvrdy.game.guessorder.game.model.TurnResult
import com.jozeftvrdy.game.guessorder.game.model.failureResult
import com.jozeftvrdy.game.guessorder.game.model.successResult
import kotlin.math.min
import kotlin.random.Random

class PlayGameRepositoryImpl : PlayGameRepository {
    override fun validateTurnGuess(
        turnGuess: List<ItemFill>,
        solution: List<ItemFill>
    ): Result<TurnResult> {
        fun MutableMap<ItemFill, Int>.increaseCountFor(value: ItemFill) {
            val oldValue = this[value]?:0
            this[value] = oldValue + 1
        }

        if (turnGuess.size != solution.size) {
            return IllegalArgumentException("Cannot check if guess is correct, because guess and solution sizes do not match.").failureResult()
        }

        if (solution.isEmpty()) {
            return IllegalArgumentException("Cannot check if guess is correct, because solution is empty.").failureResult()
        }

        var greatSuccessCount = 0
        val solutionValuesCountOnDiffPlaces = mutableMapOf<ItemFill, Int>()
        val guessValuesCountOnDiffPlaces = mutableMapOf<ItemFill, Int>()

        for (index in turnGuess.indices) {
            val guessValue = turnGuess[index]
            val solutionValue = solution[index]
            if (guessValue == solutionValue) {
                greatSuccessCount++
            } else  {
                solutionValuesCountOnDiffPlaces.increaseCountFor(solutionValue)
                guessValuesCountOnDiffPlaces.increaseCountFor(guessValue)
            }
        }

        var mildSuccessCount = 0
        for ((guessValue, numberOfValueInGuess) in guessValuesCountOnDiffPlaces) {
            val numberOfValueInSolution = solutionValuesCountOnDiffPlaces[guessValue]?:0

            // if we guessed 3 times this value and solution has 4 times this value we just add 3 mild successes
            // if we guessed 4 times this value and solution has only 3 times this value we just add 3 mild successes
            // basically min()
            mildSuccessCount += min(numberOfValueInGuess, numberOfValueInSolution)
        }

        return TurnResult(
            greatSuccessCount = greatSuccessCount,
            mildSuccessCount = mildSuccessCount
        ).successResult()
    }

    override fun generateSolution(turnGuess: List<ItemFill>, gameData: InitialGameData): List<ItemFill> {
        val entries = ItemFill.entries
        lateinit var solution: List<ItemFill>
        do {
            solution = turnGuess.map {
                entries[Random.nextInt(gameData.colorsCount)]
            }
        } while (turnGuess == solution)
        require(solution.isNotEmpty())
        require(solution.size == turnGuess.size)
        return solution
    }
}