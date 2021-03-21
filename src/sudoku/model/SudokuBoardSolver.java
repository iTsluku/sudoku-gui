package sudoku.model;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Collections;

/**
 * Sudoku board solver, to solve a sudoku-board with strategies and
 * guess-algorithms and find all possible solutions to it.
 */
public class SudokuBoardSolver implements SudokuSolver {

    /**
     * List of saturators.
     */
    private List<Saturator> saturators;

    /**
     * Construct sudoku-board-solver.
     */
    public SudokuBoardSolver() {
        saturators = new ArrayList<>();
    }

    /**
     * Solves board if possible.
     *
     * @param board     Board to solve.
     * @param onlyFirst Sets if first or all solutions will be returned.
     * @return List of boards, that represent a solution for given board,
     * return null iff no solution was found.
     */
    private List<Board> solve(Board board, boolean onlyFirst) {
        List<Board> solutions = new ArrayList<>();
        Deque<Board> stack = new ArrayDeque<>();
        stack.push(board);

        while (!stack.isEmpty()) {
            Board boardTop = stack.pop();

            try {
                saturateDirect(boardTop);
            } catch (UnsolvableSudokuException e) {
                continue;
            }

            if (boardTop.isSolution()) {
                solutions.add(boardTop);

                if (onlyFirst) {
                    return solutions;
                }
            } else {
                List<Board> candidates = getCandidates(boardTop);

                for (int i = candidates.size() - 1; i >= 0; i--) {
                    stack.push(candidates.get(i));
                }
            }
        }

        if (solutions.isEmpty()) {
            return null;
        } else {
            Collections.sort(solutions);
            return solutions;
        }
    }

    /**
     * Apply set solving strategies on board.
     *
     * @param board Board to apply changes.
     * @return True, if and only if board was changed.
     * @throws UnsolvableSudokuException Sudoku board is not solvable.
     */
    private boolean saturateDirect(Board board)
            throws UnsolvableSudokuException {
        if (!saturators.isEmpty()) {
            try {
                boolean change = false;

                for (Saturator saturator : saturators) {
                    boolean changeBoard = saturator.saturate(board);

                    if (changeBoard) {
                        change = true;
                    }
                }
                return change;
            } catch (UnsolvableSudokuException e) {
                throw new UnsolvableSudokuException(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Get boards where guesses result in solvable boards.
     *
     * @param board Board to get candidates of.
     * @return List of corresponding candidates.
     */
    private List<Board> getCandidates(Board board) {
        int[] coordinates = findBracktrackingField(board);
        int row = coordinates[0];
        int col = coordinates[1];
        int[] pos = board.getPossibilities(Structure.ROW, row, col);
        List<Board> candidates = new ArrayList<>();

        for (int po : pos) {
            Board boardClone = board.clone();

            try {
                boardClone.setCell(Structure.ROW, row, col, po);
            } catch (InvalidSudokuException e) {
                continue;
            }
            candidates.add(boardClone);
        }
        return candidates;
    }

    /**
     * Find coordinates of cell to guess on/ apply backtracking.
     *
     * @param board Board to find backtracking cell.
     * @return Coordinates (row,cal) of cell.
     */
    private int[] findBracktrackingField(Board board) {
        final int tupelSize = 2;
        int[] coordinates = new int[tupelSize];
        int minCardinality = board.getNumbers() + 1;

        for (int row = 0; row < board.getNumbers(); row++) {
            for (int col = 0; col < board.getNumbers(); col++) {
                if (board.getCell(Structure.ROW, row, col)
                        == Board.UNSET_CELL) {
                    int[] pos = board.getPossibilities(Structure.ROW, row, col);

                    if (pos.length < minCardinality) {
                        minCardinality = pos.length;
                        coordinates[0] = row;
                        coordinates[1] = col;
                    }
                }
            }
        }
        return coordinates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSaturator(Saturator saturator) {
        saturators.add(saturator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board saturate(Board board) {
        Board boardClone = board.clone();
        boolean done = false;

        while (!done) {
            try {
                boolean change = saturateDirect(boardClone);

                if (boardClone.isSolution() || !change) {
                    done = true;
                }
            } catch (UnsolvableSudokuException e) {
                done = true;
            }
        }
        return boardClone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board findFirstSolution(Board board) {
        Board boardClone = board.clone();
        List<Board> firstSolution = solve(boardClone, true);

        if (firstSolution == null) {
            return null;
        } else {
            return firstSolution.get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Board> findAllSolutions(Board board) {
        Board boardClone = board.clone();
        return solve(boardClone, false);
    }
}
