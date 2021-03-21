package sudoku.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Board model for sudoku game with nxm inner boxes, n=length of inner
 * box-row and m=length of inner box-col.
 */
public class SudokuBoard implements Board {

    /**
     * Board with Bitsets holding possibilities for cells.
     */
    private BitSet[][] board;

    /**
     * Indicates if cell is fix/ set to a number.
     */
    private boolean[][] isFixed;

    /**
     * Inner box-row length.
     */
    private int boxRows;

    /**
     * Inner box-col length.
     */
    private int boxCols;

    /**
     * Board length for row-/col-/box elements.
     */
    private int numbers;

    /**
     * Holds row,col of last set cell.
     */
    private int[] lastCelLSet;

    /**
     * Construct a sudoku board.
     *
     * @param boxRows Inner box-row length.
     * @param boxCols Inner box-col length.
     */
    public SudokuBoard(int boxRows, int boxCols) {
        assert (boxRows >= 0 && boxCols >= 0);

        this.boxRows = boxRows;
        this.boxCols = boxCols;
        numbers = boxRows * boxCols;
        board = new BitSet[numbers][numbers];
        isFixed = new boolean[numbers][numbers];
        final int tupelSize = 2;
        lastCelLSet = new int[tupelSize];
        initializeEmptyBoard();

    }

    /**
     * Initialize board, every cell holds all possibilities.
     */
    private void initializeEmptyBoard() {

        for (int row = 0; row < numbers; row++) {
            for (int column = 0; column < numbers; column++) {
                board[row][column] = new BitSet(numbers);

                for (int i = 0; i < numbers; i++) {
                    board[row][column].set(i, true);
                }
            }
        }
    }

    /**
     * Get row for a given structure,major,minor.
     *
     * @param struct Structure that is represented.
     * @param major  Structure number of board.
     * @param minor  Structure element of structure number.
     * @return Row of given 3-tupel.
     */
    private int getRow(Structure struct, int major, int minor) {
        switch (struct) {
            case ROW:
                return major;
            case COL:
                return minor;
            case BOX:
                return (major / boxRows) * boxRows + (minor / boxCols);
            default:
                assert (false);
                return Board.UNSET_CELL;
        }
    }

    /**
     * Get col for a given structure,major,minor.
     *
     * @param struct Structure that is represented.
     * @param major  Structure number of board.
     * @param minor  Structure element of structure number.
     * @return Col of given 3-tupel.
     */
    private int getCol(Structure struct, int major, int minor) {
        switch (struct) {
            case ROW:
                return minor;
            case COL:
                return major;
            case BOX:
                return (major % boxRows) * boxCols + (minor % boxCols);
            default:
                assert (false);
                return Board.UNSET_CELL;
        }
    }

    /**
     * Get number of inner-box where input is located.
     *
     * @param struct Structure that is represented.
     * @param major  Structure number of board.
     * @param minor  Structure element of structure number.
     * @return Box-number of board.
     */
    private int getBox(Structure struct, int major, int minor) {
        switch (struct) {
            case ROW:
                return (major / boxRows) * boxRows + (minor / boxCols);
            case COL:
                return (minor / boxRows) * boxRows + (major / boxCols);
            case BOX:
                return major;
            default:
                assert (false);
                return Board.UNSET_CELL;
        }
    }

    /**
     * Get number of element of inner-box, where input is located.
     *
     * @param struct Structure that is represented.
     * @param major  Structure number of board.
     * @param minor  Structure element of structure number.
     * @return Element-number of inner-box, of board.
     */
    private int getBoxMinor(Structure struct, int major, int minor) {
        switch (struct) {
            case ROW:
                return (major % boxRows) * boxCols + (minor % boxCols);
            case COL:
                return (minor % boxRows) * boxCols + (major % boxCols);
            case BOX:
                return minor;
            default:
                assert (false);
                return Board.UNSET_CELL;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxRows() {
        return boxRows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxColumns() {
        return boxCols;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumbers() {
        return numbers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCell(Structure struct, int major, int minor, int number)
            throws InvalidSudokuException {
        if (number != UNSET_CELL && (major >= 0 && major < numbers)
                && (minor >= 0 && minor < numbers)) {
            int numberIndexed = number - 1;
            int row = getRow(struct, major, minor);
            int col = getCol(struct, major, minor);

            if (!board[row][col].get(numberIndexed)) {
                throw new InvalidSudokuException("Error! Violation against "
                        + "sudoku-feature, can not set number.");
            } else {
                for (int i = 0; i < numbers; i++) {
                    if (i != numberIndexed) {
                        board[row][col].set(i, false);
                    }
                }
                isFixed[row][col] = true;
                lastCelLSet[0] = row;
                lastCelLSet[1] = col;
                int boxMajor = getBox(struct, major, minor);
                int boxMinor = getBoxMinor(struct, major, minor);
                removePossibility(Structure.ROW, major, minor, number);
                removePossibility(Structure.COL, minor, major, number);
                removePossibility(Structure.BOX, boxMajor, boxMinor, number);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getLastCellSet() {
        int[] lastCellSetClone = new int[lastCelLSet.length];

        for (int i = 0; i < lastCelLSet.length; i++) {
            lastCellSetClone[i] = lastCelLSet[i];
        }
        return lastCellSetClone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCell(Structure struct, int major, int minor) {
        assert ((major >= 0 && major < numbers)
                && (minor >= 0 && minor < numbers));

        int row = getRow(struct, major, minor);
        int col = getCol(struct, major, minor);

        if (!(isFixed[row][col])) {
            return UNSET_CELL;
        } else {
            for (int i = 0; i < numbers; i++) {
                if (board[row][col].get(i)) {
                    return i + 1;
                }
            }
        }
        return UNSET_CELL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSolution() {
        for (int row = 0; row < numbers; row++) {
            for (int col = 0; col < numbers; col++) {
                if (!isFixed[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getPossibilities(Structure struct, int major, int minor) {
        int row = getRow(struct, major, minor);
        int col = getCol(struct, major, minor);

        if (isFixed[row][col] || !((major >= 0 && major < numbers)
                && (minor >= 0 && minor < numbers))) {
            return null;
        } else {
            int[] pos = new int[board[row][col].cardinality()];
            BitSet fieldCopy = new BitSet(numbers);

            for (int j = 0; j < numbers; j++) {
                if (board[row][col].get(j)) {
                    fieldCopy.set(j, true);
                } else {
                    fieldCopy.set(j, false);
                }
            }

            for (int i = 0; i < pos.length; i++) {
                for (int k = 0; k < numbers; k++) {
                    if (fieldCopy.get(k)) {
                        pos[i] = k + 1;
                        fieldCopy.set(k, false);
                        break;
                    }
                }
            }
            return pos;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePossibility(Structure struct, int major, int minor,
                                  int number) throws InvalidSudokuException {
        int numberIndex = number - 1;

        switch (struct) {

            case ROW:
                for (int column = 0; column < numbers; column++) {
                    if (column != minor) {
                        if (board[major][column].cardinality() == 1
                                && board[major][column].get(numberIndex)) {
                            throw new InvalidSudokuException("!Error Violation "
                                    + "against sudoku-feature, can not remove "
                                    + "number. ");
                        } else {
                            board[major][column].set(numberIndex, false);
                        }
                    }
                }
                break;
            case COL:
                for (int row = 0; row < numbers; row++) {
                    if (row != minor) {
                        if (board[row][major].cardinality() == 1
                                && board[row][major].get(numberIndex)) {
                            throw new InvalidSudokuException("Error! Violation "
                                    + "against sudoku-feature, can not remove "
                                    + "number. ");
                        } else {
                            board[row][major].set(numberIndex, false);
                        }
                    }
                }
                break;
            case BOX:
                for (int element = 0; element < numbers; element++) {
                    if (element != minor) {
                        int elementY =
                                (major / boxRows) * boxRows
                                        + (element / boxCols);
                        int elementX =
                                (major % boxRows) * boxCols
                                        + (element % boxCols);

                        if (board[elementY][elementX].cardinality() == 1
                                && board[elementY][elementX].get(numberIndex)) {
                            throw new InvalidSudokuException("Error! Violation "
                                    + "against sudoku-feature, can not remove "
                                    + "number. ");
                        } else {
                            board[elementY][elementX].set(numberIndex, false);
                        }
                    }
                }
                break;
            default:
                throw new InvalidSudokuException("Error! Violation "
                        + "against sudoku-feature, can on remove "
                        + "from set sudoku structures.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board clone() {
        Board deepCopy = new SudokuBoard(boxRows, boxCols);

        for (int row = 0; row < numbers; row++) {
            for (int col = 0; col < numbers; col++) {
                if (isFixed[row][col]) {
                    int number = getCell(Structure.ROW, row, col);

                    try {
                        deepCopy.setCell(Structure.ROW, row, col, number);
                    } catch (InvalidSudokuException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return deepCopy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Board other) {
        int[] boardThis = getIntRepresentation(this);
        int[] boardOther = getIntRepresentation(other);

        for (int i = 0; i < numbers * numbers; i++) {
            if (boardThis[i] < boardOther[i]) {
                return -1;
            } else if (boardThis[i] > boardOther[i]) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Fill int[] with cell-values of board, set UNSET cells to (numbers+1).
     *
     * @param board Board to concatenated int representation.
     * @return board cell-values concatenated.
     */
    private int[] getIntRepresentation(Board board) {
        List<Integer> list = new ArrayList<>();
        int[] cellNumbers = new int[numbers * numbers];

        for (int row = 0; row < numbers; row++) {
            for (int column = 0; column < numbers; column++) {
                if (board.getCell(Structure.ROW, row, column)
                        != Board.UNSET_CELL) {
                    list.add(board.getCell(Structure.ROW, row, column));
                } else {
                    list.add(getNumbers() + 1);
                }
            }
        }

        for (int i = 0; i < numbers * numbers; i++) {
            cellNumbers[i] = list.get(i);
        }
        return cellNumbers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prettyPrint() {
        StringBuilder bob = new StringBuilder();
        for (int row = 0; row < numbers; row++) {
            for (int column = 0; column < numbers; column++) {
                if (column != 0) {
                    bob.append(" ");
                }

                if (isFixed[row][column]) {
                    for (int i = 0; i < numbers; i++) {
                        if (board[row][column].get(i)) {
                            int fixNr = i + 1;
                            bob.append(fixNr);
                        }
                    }
                } else {
                    bob.append(".");
                }
            }

            if (row + 1 < numbers) {
                bob.append("\n");
            }
        }
        return bob.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder bob = new StringBuilder();
        for (int row = 0; row < numbers; row++) {
            for (int column = 0; column < numbers; column++) {
                if (column != 0) {
                    bob.append(" ");
                }

                if (isFixed[row][column]) {
                    for (int i = 0; i < numbers; i++) {
                        if (board[row][column].get(i)) {
                            int fixNr = i + 1;
                            bob.append(fixNr);
                        }
                    }
                } else {
                    bob.append(".");
                }
            }

            if (row + 1 < numbers) {
                bob.append(" ");
            }
        }
        return bob.toString();
    }
}
