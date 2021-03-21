package sudoku.main;

import sudoku.controller.SudokuController;

/**
 * Main class for Sudoku-GUI application.
 */
public final class SudokuMain {

    /**
     * Private constructor for utility class.
     */
    private SudokuMain() {
    }

    /**
     * Start the application.
     *
     * @param args Represents command line arguments.
     */
    public static void main(String[] args) {
        new SudokuController();
    }
}
