package org.pitest.pitclipse.core.extension.point;

/**
 * An object able to process some results.
 * 
 * @param <T> the type of expected results
 */
public interface ResultNotifier<T> {
    
    /**
     * Processes given results.
     * 
     * @param results
     *          The results to process.
     */
    void handleResults(T results);
    
}
