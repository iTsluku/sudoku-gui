package sudoku.view;

import sudoku.model.SudokuModel;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import java.awt.Dimension;
import java.awt.Container;

/**
 * Frame class, renders content to display.
 */
public class SudokuFrame extends JFrame {

    /**
     * Board, view representation of model.
     */
    private SudokuView boardPanel;

    /**
     * Construct frame to render view-content.
     */
    public SudokuFrame() {
        super("Sudoku");
        boardPanel = new SudokuView(null);
        Container c = getContentPane();
        c.add(boardPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setJMenuBar(boardPanel.getMenuBar());
        setMinimumSize(new Dimension(200, 200));
        setVisible(true);
    }

    /**
     * Setup new view in relation to new board dimensions.
     *
     * @param gameBoard The new model.
     * @return The new view.
     */
    public SudokuView setupNewBoard(SudokuModel gameBoard) {
        if (gameBoard == null) {
            return boardPanel;
        }
        Container c = getContentPane();

        if (boardPanel != null) {
            JPopupMenu popupMenu = boardPanel.getComponentPopupMenu();

            if (popupMenu != null) {
                c.remove(popupMenu);
            }
            c.remove(boardPanel);
        }
        boardPanel = new SudokuView(gameBoard);
        c.add(boardPanel);
        setContentPane(c);
        pack();
        return boardPanel;
    }
}
