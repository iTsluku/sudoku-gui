package sudoku.model;

/**
 * Solution strategy, that checks for all non-fix cells, if there is only one
 * possibility left.
 */
public class EnforceCell implements Saturator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saturate(Board board) throws UnsolvableSudokuException {
        boolean done = false;
        boolean change = false;

        while (!done) {
            done = !findEnforcedCell(board);
            if (!done) {
                change = true;
            }
        }
        return change;
    }

    /**
     * Find enforced cell.
     *
     * @param board Board to find enforced cell on.
     * @return True if board was changed.
     * @throws UnsolvableSudokuException Exception if board is unsolvable.
     */
    private boolean findEnforcedCell(Board board)
            throws UnsolvableSudokuException {
        for (int row = 0; row < board.getNumbers(); row++) {
            for (int col = 0; col < board.getNumbers(); col++) {
                if (board.getCell(Structure.ROW, row, col)
                        == Board.UNSET_CELL) {
                    int[] pos = board.getPossibilities(Structure.ROW, row,
                            col);

                    if (pos.length == 1) {
                        try {
                            board.setCell(Structure.ROW, row, col, pos[0]);
                            return true;
                        } catch (InvalidSudokuException e) {
                            throw new UnsolvableSudokuException("Error! "
                                    + "Passed sudoku was not solvable.");
                        }
                    }
                }
            }
        }
        return false;
    }
}
