package com.jozeftvrdy.game.guessorder.repository

import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.game.model.Result
import com.jozeftvrdy.game.guessorder.game.model.TurnResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PlayGameRepositoryImplTest {
    val repo = PlayGameRepositoryImpl()
    
    @Test
    fun `When none of guessed values are in solution, then return no great nor mild success`() = runTest {
        val guess: List<ItemFill> = listOf(ItemFill.FillA,ItemFill.FillC,ItemFill.FillG,ItemFill.FillC)
        val solution: List<ItemFill> = listOf(ItemFill.FillB,ItemFill.FillH,ItemFill.FillD, ItemFill.FillF)
        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
        
        result.shouldBeInstanceOf<Result.Success<TurnResult>>()
        result.value.run {
            greatSuccessCount shouldBe 0
            mildSuccessCount shouldBe 0
        }
    }

    @Test
    fun `When exactly one of guessed values are in solution on same place, then return exactly one great success`() = runTest {
        val guess: List<ItemFill> = listOf(ItemFill.FillA,ItemFill.FillH,ItemFill.FillG,ItemFill.FillC)
        val solution: List<ItemFill> = listOf(ItemFill.FillB,ItemFill.FillH,ItemFill.FillD, ItemFill.FillF)
        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
                result.shouldBeInstanceOf<Result.Success<TurnResult>>()
        result.value.run {
            greatSuccessCount shouldBe 1
            mildSuccessCount shouldBe 0
        }
    }

    @Test
    fun `When exactly one of guessed values are in solution on different place, then return exactly one mild success`() = runTest {
        val guess: List<ItemFill> = listOf(ItemFill.FillA,ItemFill.FillH,ItemFill.FillG,ItemFill.FillC)
        val solution: List<ItemFill> = listOf(ItemFill.FillB,ItemFill.FillD,ItemFill.FillH, ItemFill.FillF)

        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
                result.shouldBeInstanceOf<Result.Success<TurnResult>>()
        result.value.run {
            greatSuccessCount shouldBe 0
            mildSuccessCount shouldBe 1
        }
    }

    @Test
    fun `When exactly two of guessed values are in solution but one on different place, then return one great success and one mild success`() = runTest {
        val guess: List<ItemFill> = listOf(ItemFill.FillA,ItemFill.FillH,ItemFill.FillG,ItemFill.FillC)
        val solution: List<ItemFill> = listOf(ItemFill.FillB,ItemFill.FillD,ItemFill.FillH,ItemFill.FillC)
        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
                result.shouldBeInstanceOf<Result.Success<TurnResult>>()
        result.value.run {
            greatSuccessCount shouldBe 1
            mildSuccessCount shouldBe 1
        }
    }

    @Test
    fun `When exactly four guessed values are just once in solution, then return just one great success and no mild success`() = runTest {
        val guess: List<ItemFill> = listOf(ItemFill.FillH,ItemFill.FillH,ItemFill.FillH, ItemFill.FillH)
        val solution: List<ItemFill> = listOf(ItemFill.FillB,ItemFill.FillD,ItemFill.FillH,ItemFill.FillC)
        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
                result.shouldBeInstanceOf<Result.Success<TurnResult>>()
        result.value.run {
            greatSuccessCount shouldBe 1
            mildSuccessCount shouldBe 0
        }
    }

    @Test
    fun `When exactly one guessed values are four times in solution, then return just one great success and no mild success`() = runTest {
        val guess: List<ItemFill> = listOf(ItemFill.FillB,ItemFill.FillD,ItemFill.FillH,ItemFill.FillC)
        val solution: List<ItemFill> = listOf(ItemFill.FillH,ItemFill.FillH,ItemFill.FillH, ItemFill.FillH)
        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
                result.shouldBeInstanceOf<Result.Success<TurnResult>>()
        result.value.run {
            greatSuccessCount shouldBe 1
            mildSuccessCount shouldBe 0
        }
    }

    @Test
    fun `When 4 guesses are correct, but two are on bad places, Then return 2 great success and 2 mild success`() = runTest {
        val guess: List<ItemFill> = listOf(ItemFill.FillB,ItemFill.FillD,ItemFill.FillH,ItemFill.FillC)
        val solution: List<ItemFill> = listOf(ItemFill.FillB,ItemFill.FillC,ItemFill.FillH, ItemFill.FillD)
        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
                result.shouldBeInstanceOf<Result.Success<TurnResult>>()
        result.value.run {
            greatSuccessCount shouldBe 2
            mildSuccessCount shouldBe 2
        }
    }

    @Test
    fun `When all guesses are correct, return all guesses as great success`() = runTest {
        val guess: List<ItemFill> = listOf(ItemFill.FillF, ItemFill.FillA, ItemFill.FillC, ItemFill.FillC)
        val solution: List<ItemFill> = listOf(ItemFill.FillF, ItemFill.FillA, ItemFill.FillC, ItemFill.FillC)
        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
        result.shouldBeInstanceOf<Result.Success<TurnResult>>()
        result.value.run {
            greatSuccessCount shouldBe 4
            mildSuccessCount shouldBe 0
        }
    }

    @Test
    fun `When guess list and result list are not same size, failure result is returned`() = runTest {
        val guess: List<ItemFill> = listOf(ItemFill.FillF, ItemFill.FillA, ItemFill.FillC, ItemFill.FillC)
        val solution: List<ItemFill> = listOf(ItemFill.FillF, ItemFill.FillC)
        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
        result.shouldBeInstanceOf<Result.Failure>()
    }

    @Test
    fun `When guess list and result list are empty, failure result is returned`() = runTest {
        val guess: List<ItemFill> = emptyList()
        val solution: List<ItemFill> = emptyList()
        val result = repo.validateTurnGuess(
            turnGuess = guess,
            solution = solution
        )
        result.shouldBeInstanceOf<Result.Failure>()
    }

}