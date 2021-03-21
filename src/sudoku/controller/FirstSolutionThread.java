package sudoku.controller;

import sudoku.model.Board;
import sudoku.model.SudokuBoardSolver;

/**
 * Thread to calculate first solution for given board.
 */
public class FirstSolutionThread extends ThreadObservable implements Runnable {
    /**
     * Solver for sudoku board.
     */
    private SudokuBoardSolver solverFirst;

    /**
     * Sudoku board to solve.
     */
    private Board sudokuBoard;

    /**
     * First solution, calculated by thread.
     */
    private Board firstSolution;

    /**
     * Initialize thread, that calculates first solution for given board.
     *
     * @param solverFirst Solver for sudoku board.
     * @param sudokuBoard Sudoku board to solve.
     */
    public FirstSolutionThread(SudokuBoardSolver solverFirst,
                               Board sudokuBoard) {
        this.solverFirst = solverFirst;
        this.sudokuBoard = sudokuBoard;
    }

    /**
     * Executes thread.
     */
    @Override
    public void run() {
        try {
            firstSolution = solverFirst.findFirstSolution(sudokuBoard);
        } finally {
            notifyThreadObservers(firstSolution, this);
        }
    }
}
