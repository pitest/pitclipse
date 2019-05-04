package org.pitest.pitclipse.runner.client;

import org.pitest.pitclipse.runner.PitResults;

public interface PitResultHandler {
    void handle(PitResults results);
}
