package sudoku.controller;

import sudoku.model.Board;

/**
 * Thread observer class, gets informed about changes in observable objects.
 */
public interface ThreadObserver {
    /**
     * This method is called whenever the observed object is changed.
     *
     * @param firstSolution Solved board.
     * @param obj           Observable thread.
     */
    void update(Board firstSolution, ThreadObservable obj);
}
