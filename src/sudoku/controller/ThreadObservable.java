package sudoku.controller;

import sudoku.model.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an observable thread object.
 */
public class ThreadObservable {
    /**
     * List holds observers.
     */
    protected List<ThreadObserver> observers;

    /**
     * Construct an observable thread with zero observers.
     */
    public ThreadObservable() {
        observers = new ArrayList<>();
    }

    /**
     * Remove all observers.
     */
    public void removeAllObservers() {
        observers.clear();
    }

    /**
     * Add observer to observable thread.
     *
     * @param o Observer, that is to be added.
     */
    public void addThreadObserver(ThreadObserver o) {
        observers.add(o);
    }

    /**
     * Notify all of its observers.
     *
     * @param gameBoard First solution, calculated by thread.
     * @param obj       Observable thread object.
     */
    public void notifyThreadObservers(Board gameBoard, ThreadObservable obj) {
        if (!observers.isEmpty()) {
            for (int i = 0; i < observers.size(); i++) {
                observers.get(i).update(gameBoard, obj);
            }
        }
    }
}
