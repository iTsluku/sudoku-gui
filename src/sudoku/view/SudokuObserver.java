package sudoku.view;

import sudoku.model.SudokuModel;

/**
 * Sudoku observer class, gets informed about changes in model.
 */
public interface SudokuObserver {
    /**
     * This method is called whenever the model is changed.
     *
     * @param gameBoard Model, that has change.
     */
    void update(SudokuModel gameBoard);
}
