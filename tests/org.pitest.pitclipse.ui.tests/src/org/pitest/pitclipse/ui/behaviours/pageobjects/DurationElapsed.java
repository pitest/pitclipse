package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public class DurationElapsed extends DefaultCondition {
    
    private Long time;
    
    private long durationInSeconds;

    public DurationElapsed(long duration) {
        this.durationInSeconds = duration;
    }

    @Override
    public boolean test() throws Exception {
    	if (time == null) {
    		time = System.currentTimeMillis();
    	}
    	return time + 2000 <= System.currentTimeMillis();
    }

    @Override
    public String getFailureMessage() {
        return "Failed to wait for " + durationInSeconds + " s";
    }
}