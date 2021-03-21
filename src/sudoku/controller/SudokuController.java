package sudoku.controller;

import sudoku.model.SudokuModel;
import sudoku.model.Board;
import sudoku.model.InvalidSudokuException;
import sudoku.model.SudokuFile;
import sudoku.model.SudokuBoardSolver;
import sudoku.model.EnforceCell;
import sudoku.model.EnforceNumber;
import sudoku.model.Structure;
import sudoku.view.SudokuFrame;
import sudoku.view.SudokuView;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Frame;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Controller class for Sudoku-GUI application.
 */
public class SudokuController {

    /**
     * Board model instance.
     */
    private SudokuModel gameBoard;

    /**
     * View, panel displaying model data.
     */
    private SudokuView sudokuView;

    /**
     * Frame rendering application.
     */
    private SudokuFrame sudokuFrame;

    /**
     * Last label that has been clicked on view.
     */
    private JLabel currentLabel;

    /**
     * Last label that has been clicked on view, to insert or remove a value.
     */
    private JLabel spotlightLabel;

    /**
     * Stack holds previous board states.
     */
    private Deque<SudokuModel> undoStack;

    /**
     * Thread to calculate solution for board.
     */
    private Thread firstSolutionThread;

    /**
     * States if thread has been stopped by force.
     */
    private boolean threadStopped;

    /**
     * Add Listener to view, either only the menu-bar or the menu-bar and the
     * Listeners for the labels representing each one cell.
     *
     * @param onlyMenu Sets view case.
     */
    public void addListenerToView(boolean onlyMenu) {
        sudokuView.addExitListener(new ExitListener());
        sudokuView.addOpenListener(new OpenListener());
        sudokuView.addUndoListener(new UndoListener());
        sudokuView.addSugValueListener(new SuggestValueListener());
        sudokuView.addSolveListener(new SolveListener());

        if (!onlyMenu) {
            sudokuView.addLableListener(new LableListener());
            sudokuView.addInsertListener(new InsertCellListener());
            sudokuView.addRemoveListener(new RemoveCellListener());
        }
    }

    /**
     * Construct controller to control model and view.
     */
    public SudokuController() {
        undoStack = new ArrayDeque<>();
        gameBoard = new SudokuModel(1, 1);
        sudokuFrame = new SudokuFrame();
        sudokuView = sudokuFrame.setupNewBoard(null);
        addListenerToView(true);
        threadStopped = false;
    }

    /**
     * Push current state of board to stack.
     */
    private void pushCurrentBoardState() {
        SudokuModel currentBoardState = gameBoard.clone();
        undoStack.push(currentBoardState);
    }

    /**
     * Stop all running threads.
     */
    @SuppressWarnings("deprecation")
    private void stopThreads() {
        if (firstSolutionThread != null) {
            firstSolutionThread.stop();
            firstSolutionThread = null;
            threadStopped = true;
        }
    }

    /**
     * Apply view change by user-input to game-board.
     */
    private void updateGameboard() {
        List<JLabel[][]> subBoxLabels = sudokuView.getSubBoxLabels();
        JPanel[][] subBoxPanels = sudokuView.getSubBoxPanels();
        int boxRows = gameBoard.getBoxRows();
        int boxCols = gameBoard.getBoxColumns();
        int boxMajor = 0;

        for (JPanel[] subBoxPanel : subBoxPanels) {
            for (int j = 0; j < subBoxPanel.length; j++) {
                int boxMinor = 0;
                JLabel[][] currentL2D = subBoxLabels.get(boxMajor);

                for (JLabel[] jLabels : currentL2D) {
                    for (JLabel jLabel : jLabels) {
                        int row = (boxMajor / boxRows) * boxRows
                                + (boxMinor / boxCols);
                        int col = (boxMajor % boxRows) * boxCols
                                + (boxMinor % boxCols);

                        if (!String.valueOf(gameBoard.getCell(row,
                                col)).equals(jLabel.getText())) {
                            if (!((String.valueOf(gameBoard.getCell(row,
                                    col)).equals(String.valueOf(
                                    Board.UNSET_CELL)))
                                    && (jLabel.getText().equals("")))) {

                                try {
                                    int cellValueView;

                                    if (jLabel.getText().equals("")) {
                                        cellValueView = Board.UNSET_CELL;
                                    } else {
                                        cellValueView = Integer.parseInt(
                                                jLabel.getText());
                                    }
                                    gameBoard.setCell(row, col, cellValueView);
                                    return;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        boxMinor++;
                    }
                }
                boxMajor++;
            }
        }
    }

    /**
     * Display dialog, saying that the currently displayed board is not
     * solvable.
     */
    private void displayUnsolvable() {
        JOptionPane.showMessageDialog(null,
                "Sudoku unsolvable.",
                "Error!", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Display dialog, saying that the currently displayed board is solvable.
     */
    private void displaySolvable() {
        JOptionPane.showInternalMessageDialog(null,
                "Sudoku solved!", "Message",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Apply changes to spotlight and last clicked label.
     */
    private void applyPopupLabelClickedChanges() {
        sudokuView.getPopupMenu().setVisible(false);
        spotlightLabel.setFont(new Font("Serif", Font.PLAIN, 21));
        spotlightLabel.setBackground(Color.WHITE);
        currentLabel.setFont(new Font("Serif", Font.PLAIN, 21));
    }

    /**
     * Get Sudoku-board solver, filled with enforce number and enforce cell
     * strategy.
     *
     * @return Sudoku-board solver.
     */
    private SudokuBoardSolver getSudokuBoardSolver() {
        SudokuBoardSolver solver = new SudokuBoardSolver();
        solver.addSaturator(new EnforceCell());
        solver.addSaturator(new EnforceNumber());
        return solver;
    }


    /**
     * Listener class, to handle value insertions triggered by popup-menu
     * label interaction in view.
     */
    class InsertCellListener implements ActionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (firstSolutionThread == null) {
                currentLabel.setText(actionEvent.getActionCommand());
                applyPopupLabelClickedChanges();
                pushCurrentBoardState();
                updateGameboard();

                if (gameBoard.checkAllCellsSet()) {
                    try {
                        Board sudokuBoard = gameBoard.convertToSudokuBoard();
                        displaySolvable();
                    } catch (InvalidSudokuException e) {
                        displayUnsolvable();
                    }
                }
            }
        }
    }

    /**
     * Listener class, to handle value removal triggered by popup-menu
     * label interaction in view.
     */
    class RemoveCellListener implements ActionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (firstSolutionThread == null) {
                currentLabel.setText("");
                applyPopupLabelClickedChanges();
                pushCurrentBoardState();
                updateGameboard();
            }
        }
    }

    /**
     * Listener class, to handle exit menu-bar-item interaction in view and
     * keystrokes.
     */
    class ExitListener implements ActionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            stopThreads();

            for (Frame frame : Frame.getFrames()) {
                frame.dispose();
            }
        }
    }

    /**
     * Listener class, to handle open menu-bar-item interaction in view and
     * keystrokes.
     */
    class OpenListener implements ActionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            stopThreads();
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Sudoku Files", "sud", "txt");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(chooser.getParent());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    SudokuFile sudokuFile =
                            new SudokuFile(chooser.getSelectedFile());
                    SudokuModel updatedBoard = sudokuFile.getGameBoard();

                    if (updatedBoard != null) {
                        updateGameBoard(updatedBoard);
                        gameBoard.removeAllObserver();
                        sudokuView = sudokuFrame.setupNewBoard(gameBoard);
                        addListenerToView(false);
                        undoStack.clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Apply new board settings to current model.
     *
     * @param updatedBoard Board to update to.
     */
    private void updateGameBoard(SudokuModel updatedBoard) {
        gameBoard.setBoxRows(updatedBoard.getBoxRows());
        gameBoard.setBoxCols(updatedBoard.getBoxCols());
        gameBoard.setNumbers(updatedBoard.getNumbers());
        gameBoard.setPresetCell(updatedBoard.getPresetCell());
        gameBoard.setGameBoard(updatedBoard.getGameBoard());
    }

    /**
     * Listener class, to handle undo menu-bar-item interaction in view and
     * keystrokes.
     */
    class UndoListener implements ActionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (!undoStack.isEmpty() && firstSolutionThread == null) {
                SudokuModel prevState = undoStack.pop();
                int boardLength = gameBoard.getNumbers();

                for (int row = 0; row < boardLength; row++) {
                    for (int col = 0; col < boardLength; col++) {
                        int currentStateCell = gameBoard.getCell(row, col);
                        int prevStateCell = prevState.getCell(row, col);

                        if (currentStateCell != prevStateCell) {
                            gameBoard.setCell(row, col, prevStateCell);
                        }
                    }
                }
            }
        }
    }

    /**
     * Listener class, to handle suggested-value menu-bar-item interaction in
     * view and
     * keystrokes.
     */
    class SuggestValueListener implements ActionListener, ThreadObserver {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (firstSolutionThread == null) {

                try {
                    Board sudokuBoard
                            = gameBoard.convertToSudokuBoard().clone();
                    SudokuBoardSolver solverFirst = getSudokuBoardSolver();
                    FirstSolutionThread fstSolThread
                            = new FirstSolutionThread(solverFirst, sudokuBoard);
                    fstSolThread.addThreadObserver(this);
                    Thread t1 = new Thread(fstSolThread);
                    firstSolutionThread = t1;
                    t1.start();
                } catch (InvalidSudokuException e) {
                    if (!threadStopped) {
                        displayUnsolvable();
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Board firstSolution, ThreadObservable obj) {
            // Check if thread got stopped by controller.
            if (firstSolutionThread != null) {
                if (firstSolution == null) {
                    displayUnsolvable();
                } else {
                    int[] lastCelLSet = firstSolution.getLastCellSet();
                    int row = lastCelLSet[0];
                    int col = lastCelLSet[1];
                    int cellValue = firstSolution.getCell(Structure.ROW, row,
                            col);
                    pushCurrentBoardState();
                    gameBoard.setCell(row, col, cellValue);
                }

                if (gameBoard.checkAllCellsSet()) {
                    displaySolvable();
                }
                obj.removeAllObservers();
                firstSolutionThread = null;
            }
        }
    }

    /**
     * Listener class, to handle solver menu-bar-item interaction in view and
     * keystrokes.
     */
    class SolveListener implements ActionListener, ThreadObserver {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (firstSolutionThread == null && !gameBoard.checkAllCellsSet()) {
                try {
                    Board sudokuBoard
                            = gameBoard.convertToSudokuBoard().clone();
                    SudokuBoardSolver solverFirst = getSudokuBoardSolver();
                    FirstSolutionThread fstSolThread
                            = new FirstSolutionThread(solverFirst, sudokuBoard);
                    fstSolThread.addThreadObserver(this);
                    Thread t1 = new Thread(fstSolThread);
                    firstSolutionThread = t1;
                    t1.start();
                } catch (InvalidSudokuException e) {
                    if (!threadStopped) {
                        displayUnsolvable();
                        threadStopped = false;
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Board firstSolution, ThreadObservable obj) {
            // Check if thread got stopped by controller.
            if (firstSolutionThread != null) {
                if (firstSolution == null) {
                    displayUnsolvable();
                } else {
                    pushCurrentBoardState();

                    for (int row = 0; row < gameBoard.getNumbers(); row++) {
                        for (int col = 0; col < gameBoard.getNumbers(); col++) {
                            if (gameBoard.getCell(row, col) == -1) {
                                int cellValue
                                        = firstSolution.getCell(Structure.ROW,
                                        row, col);
                                gameBoard.setCell(row, col, cellValue);
                            }
                        }
                    }
                }
                obj.removeAllObservers();
                firstSolutionThread = null;
            }
        }
    }

    /**
     * Mouse Listener class, to apply changes to labels on panel in view.
     */
    class LableListener implements MouseListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (firstSolutionThread != null) {
                JPopupMenu popup = sudokuView.getPopupMenu();

                if (popup.isVisible()) {
                    popup.setVisible(false);
                    spotlightLabel.setFont(new Font("Serif",
                            Font.PLAIN, 21));
                    spotlightLabel.setBackground(Color.WHITE);
                }
            } else {
                currentLabel = (JLabel) mouseEvent.getComponent();
                JPopupMenu popup = sudokuView.getPopupMenu();
                popup.setLocation((int) mouseEvent.getLocationOnScreen().getX(),
                        (int) mouseEvent.getLocationOnScreen().getY());
                popup.pack();

                if (!currentLabel.getForeground().equals(Color.RED)) {
                    if (spotlightLabel != null) {
                        spotlightLabel.setFont(new Font("Serif",
                                Font.PLAIN, 21));
                        spotlightLabel.setBackground(Color.WHITE);
                    }
                    spotlightLabel = currentLabel;
                    spotlightLabel.setBackground(Color.YELLOW);
                    currentLabel.setFont(new Font("Serif",
                            Font.PLAIN, 42));
                }

                if (popup.isVisible()) {
                    popup.setVisible(false);
                    spotlightLabel.setFont(new Font("Serif",
                            Font.PLAIN, 21));
                    spotlightLabel.setBackground(Color.WHITE);
                } else {
                    if (!currentLabel.getForeground().equals(Color.RED)) {
                        popup.setVisible(true);
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            JLabel label = (JLabel) mouseEvent.getComponent();

            if (!label.getBackground().equals(Color.YELLOW)) {
                label.setBackground(Color.LIGHT_GRAY);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            JLabel label = (JLabel) mouseEvent.getComponent();

            if (!label.getBackground().equals(Color.YELLOW)) {
                label.setBackground(Color.WHITE);
            }
        }
    }
}
