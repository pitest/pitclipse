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

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.results.Mutations.Mutation;

public class ResultsParser {

    public static final class Summary {

        private final int classes;
        private final int codeCoverage;
        private final int mutationCoverage;
        private final int generatedMutants;
        private final int killedMutants;

        private Summary(int classes, int codeCoverage, int mutationCoverage, int generatedMutants, int killedMutants) {
            this.classes = classes;
            this.codeCoverage = codeCoverage;
            this.mutationCoverage = mutationCoverage;
            this.generatedMutants = generatedMutants;
            this.killedMutants = killedMutants;
        }

        public int getClasses() {
            return classes;
        }

        public double getCodeCoverage() {
            return codeCoverage;
        }

        public double getMutationCoverage() {
            return mutationCoverage;
        }

        public int getGeneratedMutants() {
            return generatedMutants;
        }

        public int getKilledMutants() {
            return killedMutants;
        }
    }

    private static final String SUMMARY_START = "<h3>Project Summary</h3>";
    private static final String SUMMARY_END = "</table>";

    private final String html;
    private final int generatedMutants;
    private final int killedMutants;

    public ResultsParser(PitResults result) throws IOException {
        this.html = new String(Files.readAllBytes(Paths.get(result.getHtmlResultFile().getAbsolutePath())));
        List<Mutation> mutations = result.getMutations().getMutation();
        generatedMutants = mutations.size();
        killedMutants = getKilledMutants(mutations);
    }

    private int getKilledMutants(List<Mutation> mutants) {
        int tmp = 0;
        for (Mutation mutation : mutants) {
            if (mutation.isDetected()) {
                tmp++;
            }
        }
        return tmp;
    }

    private String getProjectSummary() {
        String summary = "";
        int startPos = caseInsensitveIndexOf(html, SUMMARY_START);
        if (startPos != -1) {
            int endPos = caseInsensitveIndexOf(html, SUMMARY_END, startPos);
            if (endPos != -1) {
                return html.substring(startPos, endPos + SUMMARY_END.length());
            }
        }
        return summary;
    }

    public Summary getSummary() {
        String summary = getProjectSummary();
        int classes = 0;
        int codeCoverage = 100;
        int mutationCoverage = 100;
        if (!summary.isEmpty()) {
            HtmlTable table = new HtmlTable(summary);
            List<Map<String, String>> results = table.getResults();
            if (results.size() == 1) {
                Map<String, String> mapResults = results.get(0);
                classes = parseInt(mapResults.get("Number of Classes"));
                codeCoverage = parseInt(mapResults.get("Line Coverage")
                        .replace("%", ""));
                mutationCoverage = parseInt(mapResults.get(
                        "Mutation Coverage").replace("%", ""));
            }
        }
        return new Summary(classes, codeCoverage, mutationCoverage, generatedMutants, killedMutants);
    }

    static int caseInsensitveIndexOf(String s, String searchString) {
        return caseInsensitveIndexOf(s, searchString, 0);
    }

    static int caseInsensitveIndexOf(String s, String searchString, int offset) {
        return s.toLowerCase().indexOf(searchString.toLowerCase(), offset);
    }
}
