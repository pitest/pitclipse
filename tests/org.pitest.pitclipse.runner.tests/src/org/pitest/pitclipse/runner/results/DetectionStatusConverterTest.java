package org.pitest.pitclipse.runner.results;

import static org.junit.Assert.*;

import org.junit.Test;

import org.pitest.mutationtest.DetectionStatus;

/**
 * @author Lorenzo Bettini
 *
 */
public class DetectionStatusConverterTest {

    @Test
    public void testConvertion() {
        verifyConvertion(DetectionStatus.KILLED);
        verifyConvertion(DetectionStatus.MEMORY_ERROR);
        verifyConvertion(DetectionStatus.NON_VIABLE);
        verifyConvertion(DetectionStatus.NOT_STARTED);
        verifyConvertion(DetectionStatus.RUN_ERROR);
        verifyConvertion(DetectionStatus.STARTED);
        verifyConvertion(DetectionStatus.SURVIVED);
        verifyConvertion(DetectionStatus.TIMED_OUT);
        verifyConvertion(DetectionStatus.NO_COVERAGE);
    }

    private void verifyConvertion(DetectionStatus status) {
        // the enum elements are the same so we use the string
        // representations of the ones from PIT to create the ones
        // of Pitclipse
        assertEquals(
            org.pitest.pitclipse.runner.results.DetectionStatus
                .fromValue(status.toString()),
            DetectionStatusCoverter.convert(status)
        );
    }
}
