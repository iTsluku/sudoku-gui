package sudoku.view;

import sudoku.model.SudokuModel;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.BorderFactory;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * View class, represents the game-board of the model and can be used as an
 * interface for the user to make an input.
 */
public class SudokuView extends JPanel implements SudokuObserver {

    /**
     * List contains 2DArray of labels for each sub-box.
     */
    private List<JLabel[][]> subBoxLabels;

    /**
     * 2DArray of panels, one for each sub-box.
     */
    private JPanel[][] subBoxPanels;

    /**
     * Menu to set label.
     */
    private JPopupMenu popupMenu;

    /**
     * Contains options to set label to.
     */
    private JMenuItem[] popupMenuItems;

    /**
     * Menu-item to open a new file.
     */
    private JMenuItem fileOpen;

    /**
     * Menu-item to exit the application.
     */
    private JMenuItem fileExit;

    /**
     * Menu-item to undo the last operation/selection/removal.
     */
    private JMenuItem editUndo;

    /**
     * Menu-item to suggest a value.
     */
    private JMenuItem solveSuggestValue;

    /**
     * Menu-item to solve the board.
     */
    private JMenuItem solveSolve;

    /**
     * The menu.
     */
    private JMenuBar menuBar;

    /**
     * Get menu.
     *
     * @return MenuBar for View, holding operations.
     */
    protected JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * Get Popup-menu.
     *
     * @return PopupMenu for View, to set values to cells.
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /**
     * Get sub-box panels.
     *
     * @return 2D field, holds Panels, each representing a sub-box.
     */
    public JPanel[][] getSubBoxPanels() {
        return subBoxPanels;
    }

    /**
     * Get list with sub-box labels.
     *
     * @return 2D field, holds Labels, each representing a cell.
     */
    public List<JLabel[][]> getSubBoxLabels() {
        return subBoxLabels;
    }

    /**
     * Add listener to exit menu-item.
     *
     * @param listenerForExitButton Exit the application.
     */
    public void addExitListener(ActionListener listenerForExitButton) {
        fileExit.addActionListener(listenerForExitButton);
    }

    /**
     * Add listener to open menu-item.
     *
     * @param listenerForOpenButton Open a new file.
     */
    public void addOpenListener(ActionListener listenerForOpenButton) {
        fileOpen.addActionListener(listenerForOpenButton);
    }

    /**
     * Add listener to undo menu-item.
     *
     * @param listenerForUndoButton Undo the last operation/selection/removal.
     */
    public void addUndoListener(ActionListener listenerForUndoButton) {
        editUndo.addActionListener(listenerForUndoButton);
    }

    /**
     * Add listener to suggest-value menu-item.
     *
     * @param listenerForSugValueButton Suggest a value.
     */
    public void addSugValueListener(ActionListener listenerForSugValueButton) {
        solveSuggestValue.addActionListener(listenerForSugValueButton);
    }

    /**
     * Add listener to solve menu-item.
     *
     * @param listenerForSolveButton Solve the board.
     */
    public void addSolveListener(ActionListener listenerForSolveButton) {
        solveSolve.addActionListener(listenerForSolveButton);
    }

    /**
     * Add listener to labels representing cells.
     *
     * @param listenerForInsertButton Change label text to set value.
     */
    public void addInsertListener(ActionListener listenerForInsertButton) {
        for (int nr = 0; nr < popupMenuItems.length - 1; nr++) {
            popupMenuItems[nr].addActionListener(listenerForInsertButton);
        }
    }

    /**
     * Add listener to labels representing cells.
     *
     * @param listenerForRemoveButton Change label text to set value.
     */
    public void addRemoveListener(ActionListener listenerForRemoveButton) {
        int lastMItemInd = subBoxLabels.size();
        popupMenuItems[lastMItemInd].addActionListener(listenerForRemoveButton);
    }

    /**
     * Add mouse-listener to labels representing cells.
     *
     * @param listenForCellLable React to mouse input.
     */
    public void addLableListener(MouseListener listenForCellLable) {
        int boxMajor = 0;

        for (JPanel[] subBoxPanel : subBoxPanels) {
            for (int j = 0; j < subBoxPanel.length; j++) {
                JLabel[][] currentL2D = subBoxLabels.get(boxMajor);

                for (JLabel[] jLabels : currentL2D) {
                    for (JLabel jLabel : jLabels) {
                        jLabel.addMouseListener(listenForCellLable);
                    }
                }
                boxMajor++;
            }
        }
    }

    /**
     * Make menu.
     */
    private void makeMenu() {
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu solveMenu = new JMenu("Solve");
        // Initialize sub-menus and items.
        fileOpen = new JMenuItem("Open");
        KeyStroke keyStrokeToOpen = KeyStroke.getKeyStroke(KeyEvent.VK_O,
                KeyEvent.CTRL_DOWN_MASK);
        fileOpen.setAccelerator(keyStrokeToOpen);
        fileOpen.setMnemonic(KeyEvent.VK_O);
        fileExit = new JMenuItem("Exit");
        KeyStroke keyStrokeToExit = KeyStroke.getKeyStroke(KeyEvent.VK_X,
                KeyEvent.CTRL_DOWN_MASK);
        fileExit.setAccelerator(keyStrokeToExit);
        fileExit.setMnemonic(KeyEvent.VK_X);
        editUndo = new JMenuItem("Undo");
        KeyStroke keyStrokeToUndo = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                KeyEvent.CTRL_DOWN_MASK);
        editUndo.setAccelerator(keyStrokeToUndo);
        editUndo.setMnemonic(KeyEvent.VK_Z);
        solveSuggestValue = new JMenuItem("Suggest Value");
        KeyStroke keyStrokeToSuggestedValue =
                KeyStroke.getKeyStroke(KeyEvent.VK_V,
                        KeyEvent.CTRL_DOWN_MASK);
        solveSuggestValue.setAccelerator(keyStrokeToSuggestedValue);
        solveSuggestValue.setMnemonic(KeyEvent.VK_V);
        solveSolve = new JMenuItem("Solve");
        KeyStroke keyStrokeToSolve =
                KeyStroke.getKeyStroke(KeyEvent.VK_A,
                        KeyEvent.CTRL_DOWN_MASK);
        solveSolve.setAccelerator(keyStrokeToSolve);
        solveSolve.setMnemonic(KeyEvent.VK_A);
        // Add menu-items to menu.
        fileMenu.add(fileOpen);
        fileMenu.addSeparator();
        fileMenu.add(fileExit);
        editMenu.add(editUndo);
        solveMenu.add(solveSuggestValue);
        solveMenu.addSeparator();
        solveMenu.add(solveSolve);
        // Add menus to menu-bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(solveMenu);
    }

    /**
     * Construct view, display model and interface for user input.
     *
     * @param gameBoard The model zu represent.
     */
    public SudokuView(SudokuModel gameBoard) {
        if (gameBoard == null) {
            makeMenu();
        } else {
            gameBoard.addObserver(this);

            if (menuBar == null) {
                makeMenu();
            }
            int boxRows = gameBoard.getBoxRows();
            int boxCols = gameBoard.getBoxColumns();
            setLayout(new GridLayout(boxCols, boxRows));
            subBoxLabels = new ArrayList<>(gameBoard.getNumbers());
            subBoxPanels = new JPanel[boxCols][boxRows];
            setLabelCells(gameBoard, boxRows, boxCols, subBoxPanels,
                    subBoxLabels);

            if (popupMenu == null) {
                addPopupMenu();
            }
        }
    }

    /**
     * Set label settings representing a cell.
     *
     * @param gameBoard    The model, simple board.
     * @param boxRows      Inner box-row length.
     * @param boxCols      Inner box-col length.
     * @param subBoxPanels 2DArray of panels, one for each sub-box.
     * @param subBoxLabels List contains 2DArray of labels for each sub-box.
     */
    private void setLabelCells(SudokuModel gameBoard, int boxRows, int boxCols,
                               JPanel[][] subBoxPanels,
                               List<JLabel[][]> subBoxLabels) {
        int boxMajor = 0;
        for (int i = 0; i < subBoxPanels.length; i++) {
            for (int j = 0; j < subBoxPanels[i].length; j++) {
                subBoxPanels[i][j] = new JPanel();
                add(subBoxPanels[i][j]);
                subBoxPanels[i][j].setLayout(new GridLayout(boxRows, boxCols));
                int boxMinor = 0;
                subBoxLabels.add(new JLabel[boxRows][boxCols]);
                JLabel[][] currentL2D = subBoxLabels.get(boxMajor);

                for (int k = 0; k < currentL2D.length; k++) {
                    for (int l = 0; l < currentL2D[k].length; l++) {
                        int row = (boxMajor / boxRows) * boxRows
                                + (boxMinor / boxCols);
                        int col = (boxMajor % boxRows) * boxCols
                                + (boxMinor % boxCols);
                        currentL2D[k][l] =
                                new JLabel(String.valueOf(gameBoard.getCell(row,
                                        col)), JLabel.CENTER);
                        setDefaultCellLabelSettings(currentL2D[k][l]);

                        if (gameBoard.getPresetCellValue(row, col)) {
                            currentL2D[k][l].setForeground(Color.RED);
                        }

                        if (currentL2D[k][l].getText().equals("-1")) {
                            currentL2D[k][l].setText("");
                            currentL2D[k][l].setForeground(Color.BLACK);
                        }
                        subBoxPanels[i][j].add(currentL2D[k][l]);
                        currentL2D[k][l].setBorder(
                                BorderFactory.createLineBorder(Color.BLACK));
                        boxMinor++;
                    }
                }
                subBoxPanels[i][j].setBorder(BorderFactory.createLineBorder(
                        Color.BLACK));
                boxMajor++;
            }
        }
    }

    /**
     * Add popup-menu.
     */
    private void addPopupMenu() {
        final int boardSize = subBoxLabels.size();
        popupMenu = new JPopupMenu();
        popupMenuItems = new JMenuItem[boardSize + 1];

        for (int nr = 0; nr < popupMenuItems.length - 1; nr++) {
            int nrIndexed = nr + 1;
            popupMenuItems[nr] = new JMenuItem(String.valueOf(nrIndexed));
            popupMenu.add(popupMenuItems[nr]);
        }
        popupMenuItems[boardSize] = new JMenuItem("remove");
        popupMenu.add(popupMenuItems[boardSize]);
        setComponentPopupMenu(popupMenu);
    }

    /**
     * Set default settings for label.
     *
     * @param label Label to set some default settings for.
     */
    private void setDefaultCellLabelSettings(JLabel label) {
        label.setPreferredSize(new Dimension(50, 50));
        label.setOpaque(true);
        label.setFont(new Font("Serif", Font.PLAIN, 21));
        label.setBackground(Color.WHITE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(SudokuModel gameBoard) {
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
                        jLabel.setText(String.valueOf(
                                gameBoard.getCell(row, col)));

                        if (jLabel.getText().equals("-1")) {
                            jLabel.setText("");
                        }
                        boxMinor++;
                    }
                }
                boxMajor++;
            }
        }
    }
}