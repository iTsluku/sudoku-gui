package sudoku.model;

import sudoku.view.SudokuObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an observable model object.
 */
public class SudokuObservable {

    /**
     * List holds observers.
     */
    private List<SudokuObserver> observers;

    /**
     * Represents if model was changed and still differs from view.
     */
    private boolean change;

    /**
     * Marks this Observable object as having been changed, the hasChanged
     * method will now return true.
     */
    protected void setChanged() {
        change = true;
    }

    /**
     * Indicates that the model has no longer changed, or that it has already
     * notified all of its observers of its most recent change, so that the
     * hasChanged method will now return false.
     */
    protected void clearChanged() {
        change = false;
    }

    /**
     * Tests if this object has changed.
     *
     * @return True if object has changed, else false.
     */
    public boolean hasChange() {
        return change;
    }

    /**
     * Construct an observable model with zero observers.
     */
    public SudokuObservable() {
        observers = new ArrayList<>();
    }

    /**
     * Get number of observers.
     *
     * @return Number of observers.
     */
    public int countObservers() {
        return observers.size();
    }

    /**
     * Adds an observer to the set of observers for this model.
     *
     * @param o Observer to add.
     */
    public void addObserver(SudokuObserver o) {
        observers.add(o);
    }

    /**
     * Remove all observers.
     */
    public void removeAllObserver() {
        observers.clear();
    }

    /**
     * Notify all of its observers.
     *
     * @param gameBoard Current model.
     */
    public void notifyObservers(SudokuModel gameBoard) {
        if (hasChange()) {
            for (SudokuObserver o : observers) {
                o.update(gameBoard);
            }
        }
    }
}
