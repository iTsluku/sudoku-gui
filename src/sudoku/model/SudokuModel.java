package sudoku.model;

/**
 * Model class, holds information about game-board.
 */
public class SudokuModel extends SudokuObservable {

    /**
     * Board holding set values for cells.
     */
    private int[][] gameBoard;

    /**
     * 2DArray holds information about cells being preset and not changeable
     * by user-input ( exception: opening a new sudoku-file).
     */
    private boolean[][] presetCell;

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
     * Get 2DArray with information about cells being preset or not.
     *
     * @return 2D field, holds information about cells being preset or not.
     */
    public boolean[][] getPresetCell() {
        return presetCell;
    }

    /**
     * Set 2DArray with information about cells being preset or not.
     *
     * @param presetCell 2D field, holds information about cells
     *                   being preset or not.
     */
    public void setPresetCell(boolean[][] presetCell) {
        this.presetCell = presetCell;
    }

    /**
     * Set specific cell in 2DArray {@code presetCell}.
     *
     * @param row   Row index of cell.
     * @param col   Column index of cell.
     * @param value Value to set cell to.
     */
    protected void setPresetCellValue(int row, int col, boolean value) {
        presetCell[row][col] = value;
    }

    /**
     * Get specific cell in 2DArray {@code presetCell}.
     *
     * @param row Row index of cell.
     * @param col Column index of cell.
     * @return Value of cell with given coordinates.
     */
    public boolean getPresetCellValue(int row, int col) {
        return presetCell[row][col];
    }

    /**
     * Get model.
     *
     * @return 2D field, holds information about cell values.
     */
    public int[][] getGameBoard() {
        return gameBoard;
    }

    /**
     * Set model.
     *
     * @param gameBoard The model, simple game board.
     */
    public void setGameBoard(int[][] gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Set the number of rows in a box.
     *
     * @param boxRows Number of rows per box.
     */
    public void setBoxRows(int boxRows) {
        this.boxRows = boxRows;
    }

    /**
     * Get the number columns in a box.
     *
     * @return Number of columns per box.
     */
    public int getBoxCols() {
        return boxCols;
    }

    /**
     * Set the number columns in a box.
     *
     * @param boxCols Number of columns per box.
     */
    public void setBoxCols(int boxCols) {
        this.boxCols = boxCols;
    }

    /**
     * Set numbers, product of {@code boxCols} and {@code boxRows}.
     *
     * @param numbers Board length for row-/col-/box elements.
     */
    public void setNumbers(int numbers) {
        this.numbers = numbers;
    }

    /**
     * Check if every cell holds a value.
     *
     * @return True, if all cells are set, else false.
     */
    public boolean checkAllCellsSet() {
        for (int[] ints : gameBoard) {
            for (int anInt : ints) {
                if (anInt == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Convert simple Model to smart-sudoku-board.
     *
     * @return Board instance of SudokuModel.
     * @throws InvalidSudokuException Exception if one likes to set a number
     *                                which is not possible.
     */
    public Board convertToSudokuBoard() throws InvalidSudokuException {
        Board sudokuBoard = new SudokuBoard(boxRows, boxCols);

        for (int row = 0; row < numbers; row++) {
            for (int col = 0; col < numbers; col++) {
                sudokuBoard.setCell(Structure.ROW, row, col,
                        gameBoard[row][col]);
            }
        }
        return sudokuBoard;
    }

    /**
     * Construct Model with given number of rows and columns in a box.
     *
     * @param boxRows Number rows in a box.
     * @param boxCols Number columns in a box.
     */
    public SudokuModel(int boxRows, int boxCols) {
        super();
        this.boxRows = boxRows;
        this.boxCols = boxCols;
        numbers = boxRows * boxCols;
        gameBoard = new int[numbers][numbers];
        presetCell = new boolean[numbers][numbers];
        initializeEmptyBoard();

    }

    /**
     * Gets the number of rows in a box.
     *
     * @return The number of rows per box.
     */
    public int getBoxRows() {
        return boxRows;
    }

    /**
     * Gets the number columns in a box.
     *
     * @return The number of columns per box.
     */
    public int getBoxColumns() {
        return boxCols;
    }

    /**
     * Gets the number of cells in each structure.
     * <p>
     * The result must be identical to the product of {@link #getBoxRows()} and
     * {@link #getBoxColumns()}.
     * <p>
     * This function is only for convenience.
     *
     * @return The number of cells in each structure.
     */
    public int getNumbers() {
        return numbers;
    }

    /**
     * Gets the content of a cell.
     *
     * @param row The row coordinate of the cell.
     * @param col The column coordinate of the cell.
     * @return The content of the cell.
     */
    public int getCell(int row, int col) {
        return gameBoard[row][col];
    }

    /**
     * Specifies the content of a cell.
     *
     * @param row    Row coordinate of the cell.
     * @param col    Column coordinate of the cell.
     * @param number The number to which the cell is fixed.
     */
    public void setCell(int row, int col, int number) {
        setChanged();
        gameBoard[row][col] = number;

        if (countObservers() != 0) {
            notifyObservers(this);
        }
        clearChanged();
    }

    /**
     * Deep copy of model.
     *
     * @return Deep copy of this instance.
     */
    @Override
    public SudokuModel clone() {
        SudokuModel deepCopy = new SudokuModel(boxRows, boxCols);

        for (int row = 0; row < numbers; row++) {
            for (int col = 0; col < numbers; col++) {
                deepCopy.setCell(row, col, gameBoard[row][col]);
                deepCopy.setPresetCellValue(row, col, presetCell[row][col]);
            }
        }
        return deepCopy;
    }

    /**
     * Initialize board, every cell holds all possibilities.
     */
    private void initializeEmptyBoard() {
        for (int row = 0; row < numbers; row++) {
            for (int column = 0; column < numbers; column++) {
                setCell(row, column, Board.UNSET_CELL);
            }
        }
    }
}
