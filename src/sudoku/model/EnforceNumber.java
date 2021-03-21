package sudoku.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Solution strategy, that checks for all non-fix cells, if it's possible, based
 * on the possibilities for that cell, to find an enforced number. An
 * enforced number is found if on of the possibilities is not found in its
 * related row, col and box and it being the only potential enforced number,
 * for all possibilities of that cell.
 */
public class EnforceNumber implements Saturator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saturate(Board board) throws UnsolvableSudokuException {
        boolean done = false;
        boolean change = false;

        while (!done) {
            done = !findEnforcedNumber(board);
            if (!done) {
                change = true;
            }
        }
        return change;
    }

    /**
     * Find enforced number.
     *
     * @param board Board to find enforced number.
     * @return True if board was changed.
     * @throws UnsolvableSudokuException Exception if board is unsolvable.
     */
    private boolean findEnforcedNumber(Board board)
            throws UnsolvableSudokuException {
        for (int row = 0; row < board.getNumbers(); row++) {
            for (int col = 0; col < board.getNumbers(); col++) {
                if (board.getCell(Structure.ROW, row, col)
                        == Board.UNSET_CELL) {
                    int[] pos = board.getPossibilities(Structure.ROW, row, col);
                    List<Integer> enforcedNumbers = new ArrayList<>();

                    for (int i = 0; i < pos.length; i++) {
                        int boxMajor =
                                (row / board.getBoxRows()) * board.getBoxRows()
                                        + (col / board.getBoxColumns());
                        int boxMinor =
                                (row % board.getBoxRows())
                                        * board.getBoxColumns()
                                        + (col % board.getBoxColumns());
                        boolean inRow = searchNrInStructure(board,
                                Structure.ROW, row, col, pos[i]);
                        boolean inCol = searchNrInStructure(board,
                                Structure.COL, col, row, pos[i]);
                        boolean inBox = searchNrInStructure(board,
                                Structure.BOX, boxMajor, boxMinor, pos[i]);

                        if (!(inRow || inCol || inBox)) {
                            enforcedNumbers.add(pos[i]);
                        }
                    }

                    if (enforcedNumbers.isEmpty()) {
                        return false;
                    } else if (enforcedNumbers.size() > 1) {
                        throw new UnsolvableSudokuException("Error! "
                                + "Passed sudoku was not solvable.");
                    } else {
                        try {
                            board.setCell(Structure.ROW, row, col,
                                    enforcedNumbers.get(0));
                        } catch (InvalidSudokuException e) {
                            throw new UnsolvableSudokuException("Error! "
                                    + "Passed sudoku was not solvable.");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Search for potential enforced number in given structure.
     *
     * @param board  Board to search on
     * @param struct Structure to search on.
     * @param major  Major for structure, structure number of board..
     * @param minor  Minor for structure, structure element of structure number.
     * @param number Number to search.
     * @return True if number was found.
     */
    private boolean searchNrInStructure(Board board, Structure struct,
                                        int major, int minor, int number) {
        switch (struct) {

            case ROW:
                for (int columnEle = 0; columnEle < board.getNumbers();
                     columnEle++) {
                    if (columnEle != minor) {
                        if (board.getCell(Structure.ROW, major, columnEle)
                                == Board.UNSET_CELL) {
                            int[] pos = board.getPossibilities(Structure.ROW,
                                    major, columnEle);
                            for (int po : pos) {
                                if (po == number) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            case COL:
                for (int rowEle = 0; rowEle < board.getNumbers(); rowEle++) {
                    if (rowEle != minor) {
                        if (board.getCell(Structure.ROW, rowEle, major)
                                == Board.UNSET_CELL) {
                            int[] pos = board.getPossibilities(Structure.ROW,
                                    rowEle, major);
                            for (int po : pos) {
                                if (po == number) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            case BOX:
                for (int element = 0; element < board.getNumbers(); element++) {
                    if (element != minor) {
                        int elementY =
                                (major / board.getBoxRows())
                                        * board.getBoxRows()
                                        + (element / board.getBoxColumns());
                        int elementX =
                                (major % board.getBoxRows())
                                        * board.getBoxColumns()
                                        + (element % board.getBoxColumns());

                        if (board.getCell(Structure.ROW, elementY, elementX)
                                == Board.UNSET_CELL) {
                            int[] pos = board.getPossibilities(Structure.ROW,
                                    elementY, elementX);
                            for (int po : pos) {
                                if (po == number) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            default:
                return false;
        }
    }
}
