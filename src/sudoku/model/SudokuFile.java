package sudoku.model;

import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Sudoku file class, to parse file to model.
 */
public class SudokuFile {

    /**
     * The model, created by parsing sudoku-file data.
     */
    private SudokuModel gameBoard;

    /**
     * Display dialog with set message.
     *
     * @param msg Message title.
     */
    private static void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error!",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Constructor to create model out of file-data.
     *
     * @param file File to parse data off.
     * @throws IOException Exception regarding user input and output errors.
     */
    public SudokuFile(File file) throws IOException {
        if (file.exists() && file.isFile()) {
            try {
                BufferedReader in =
                        new BufferedReader(new FileReader(file));
                String line = in.readLine();

                if (line == null) {
                    showMessage("Invalid inline file format.");
                } else {
                    String[] tokens = createTokens(line);

                    if (tokens.length != 2) {
                        showMessage("Invalid inline file format.");
                    } else {
                        gameBoard = initBoard(tokens);
                        boolean eof = false;
                        int row = 0;

                        while (!eof) {
                            String input = in.readLine();

                            if (input == null) {
                                eof = true;
                            } else {
                                addLineToBoard(input, row, gameBoard);
                                row++;
                            }
                        }
                        in.close();
                    }
                }
            } catch (FileNotFoundException e) {
                showMessage("File not found.");
            }
        } else {
            showMessage("File not found.");
        }
    }

    /**
     * Get model.
     *
     * @return Model, representing game board.
     */
    public SudokuModel getGameBoard() {
        return gameBoard;
    }

    /**
     * Takes string and splits it by whitespaces into string-tokens.
     *
     * @param input Input-string that will be split in tokens.
     * @return String-tokens of string as string-array.
     */
    private static String[] createTokens(String input) {
        return input.toLowerCase().trim().split("\\s+");
    }

    /**
     * Initialize Board, set board size.
     *
     * @param tokens Hold box-row and box-col information, transmitted by file.
     * @return Initialized Board, each cell holds all possibilities.
     */
    private static SudokuModel initBoard(String[] tokens) {
        try {
            int boxRows = Integer.parseInt(tokens[0]);
            int boxCols = Integer.parseInt(tokens[1]);
            return new SudokuModel(boxRows, boxCols);
        } catch (NumberFormatException e) {
            showMessage("No file input or file doesn't exist.");
        }
        assert (false);
        return null;
    }

    /**
     * Add one line, of file, to board.
     *
     * @param line      line of file-input that will be applied to the board.
     * @param row       Index for row to be set.
     * @param gameBoard Board to apply changes on.
     */
    private static void addLineToBoard(String line, int row,
                                       SudokuModel gameBoard) {
        String[] tokens = createTokens(line);
        try {
            for (int column = 0; column < tokens.length; column++) {
                if (".".equals(tokens[column])) {
                    gameBoard.setCell(row, column, Board.UNSET_CELL);
                } else {
                    gameBoard.setCell(row, column,
                            Integer.parseInt(tokens[column]));
                    gameBoard.setPresetCellValue(row, column, true);
                }
            }
        } catch (NumberFormatException e) {
            showMessage("Cell has to be of type integer.");
        }
    }
}
