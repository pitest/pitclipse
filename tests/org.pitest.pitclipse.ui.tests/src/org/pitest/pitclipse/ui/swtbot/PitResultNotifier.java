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

import static org.junit.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import java.io.IOException;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.ui.swtbot.ResultsParser.Summary;

/**
 * @author Jonas Kutscha
 */
public class PitResultNotifier implements ResultNotifier<PitResults> {
    private static final String FAIL_MESSAGE = "Pit result was not ready for retrieval. Console output was: ";

    public enum PitSummary {
        INSTANCE;
        private Summary summary;
        private boolean finishedWithoutResult = false;

        /**
         * @return how many classes where in scope or -1, if no result was generated
         */
        public int getClasses() {
            try {
                return finishedWithoutResult ? -1 : summary.getClasses();
            } catch (NullPointerException e) {
                // if the result is not ready
                fail(getFailMessage());
                return -1;
            }
        }

        /**
         * @return code coverage in percent or -1, if no result was generated
         */
        public double getCodeCoverage() {
            try {
                return finishedWithoutResult ? -1 : summary.getCodeCoverage();
            } catch (NullPointerException e) {
                // if the result is not ready
                fail(getFailMessage());
                return -1;
            }
        }

        /**
         * @return mutation coverage in percent or -1, if no result was generated
         */
        public double getMutationCoverage() {
            try {
                return finishedWithoutResult ? -1 : summary.getMutationCoverage();
            } catch (NullPointerException e) {
                // if the result is not ready
                fail(getFailMessage());
                return -1;
            }
        }

        public int getKilledMutants() {
            try {
                return finishedWithoutResult ? -1 : summary.getKilledMutants();
            } catch (NullPointerException e) {
                // if the result is not ready
                fail(getFailMessage());
                return -1;
            }
        }

        public int getGeneratedMutants() {
            try {
                return finishedWithoutResult ? -1 : summary.getGeneratedMutants();
            } catch (NullPointerException e) {
                // if the result is not ready
                fail(getFailMessage());
                return -1;
            }
        }

        /**
         * Reset the current summary.<br>
         * <b>Needs</b> to be called before a new run, because it is crucial for the
         * waiting mechanism.
         */
        public void resetSummary() {
            summary = null;
            finishedWithoutResult = false;
        }

        /**
         * Should be called, if the pit is done but has no result.
         */
        private void finishedWithoutResult() {
            finishedWithoutResult = true;
        }

        /**
         * Waits for PIT to finish or until {@link SWTBotPreferences#TIMEOUT} is up.
         * @see {@link #waitForPitToFinish(long)}
         */
        public void waitForPitToFinish() {
            waitForPitToFinish(0);
        }

        /**
         * Waits for PIT to finish.<br>
         * <b>Mandatory</b> to call {@link #resetSummary()} before.
         * @param timeOut after the wait stops, if not set use SWTBotPreferences.TIMEOUT
         */
        public void waitForPitToFinish(long timeOut) {
            SWTWorkbenchBot bot = new SWTWorkbenchBot();
            bot.waitUntil(new DefaultCondition() {
                @Override
                public boolean test() {
                    return summary != null || finishedWithoutResult;
                }
                @Override
                public String getFailureMessage() {
                    return "No summary was generated after the specified time.";
                }
            }, (timeOut > 0) ? timeOut : SWTBotPreferences.TIMEOUT);
        }

        private void setSummary(Summary summary) {
            this.summary = summary;
        }

        private String getFailMessage() {
            return FAIL_MESSAGE + PAGES.getConsole().getText();
        }
    }

    @Override
    public void handleResults(PitResults results) {
        try {
            // file only exists, if mutations were done
            if (results.getHtmlResultFile() != null) {
                PitSummary.INSTANCE.setSummary(new ResultsParser(results).getSummary());
            } else {
                // no result exists, notify summary
                PitSummary.INSTANCE.finishedWithoutResult();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
