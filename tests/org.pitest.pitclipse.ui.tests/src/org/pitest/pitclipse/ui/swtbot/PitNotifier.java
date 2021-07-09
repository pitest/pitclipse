/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.ui.swtbot;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

public enum PitNotifier {
    INSTANCE;

    private final BlockingQueue<PitResultsView> resultQueue = new ArrayBlockingQueue<PitResultsView>(
            1);

    public PitResultsView getResults() throws InterruptedException {
        return resultQueue.poll(SWTBotPreferences.TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void notifyResults(PitResultsView resultsView)
            throws InterruptedException {
        resultQueue.offer(resultsView, SWTBotPreferences.TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void reset() {
        resultQueue.clear();
    }
}
