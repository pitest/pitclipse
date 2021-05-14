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

package org.pitest.pitclipse.runner;

import com.google.common.base.Function;
import java.util.Optional;
import com.google.common.collect.ImmutableList;

import org.pitest.mutationtest.commandline.MutationCoverageReport;
import org.pitest.pitclipse.runner.client.PitClient;
import org.pitest.pitclipse.runner.results.Mutations;
import org.pitest.pitclipse.runner.results.mutations.RecordingMutationsDispatcher;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Integer.parseInt;

/**
 * Executes PIT.
 */
public class PitRunner {

    public static void main(String[] args) {
        validateArgs(args);
        int port = parseInt(args[0]);
        
        try (PitClient client = new PitClient(port)) {
            client.connect();
            Optional<PitRequest> request = client.readRequest();
            Optional<PitResults> results = request.map(executePit());

            results.ifPresent(client::sendResults);
        } catch (IOException e) {
            // An error occurred while closing the client
            e.printStackTrace();
        }
    }

    public static Function<PitRequest, PitResults> executePit() {
        return request -> {
            String[] cliArgs = PitCliArguments.from(request.getOptions());
            MutationCoverageReport.main(cliArgs);
            File reportDir = request.getReportDirectory();
            File htmlResultFile = findResultFile(reportDir, "index.html");
            Mutations mutations = RecordingMutationsDispatcher.INSTANCE.getDispatchedMutations();
            return PitResults.builder()
                   .withHtmlResults(htmlResultFile)
                   .withProjects(request.getProjects())
                   .withMutations(mutations)
                   .build();
        };
    }

    private static void validateArgs(String[] args) {
        checkArgument(args.length == 1);
    }

    private static ImmutableList<File> safeListFiles(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            return ImmutableList.copyOf(files);
        } else {
            return ImmutableList.of();
        }
    }

    private static File findResultFile(File reportDir, String fileName) {
        ImmutableList<File> files = safeListFiles(reportDir);
        for (File file : files) {
            if (fileName.equals(file.getName())) {
                return file;
            }
        }
        for (File file : files) {
            if (file.isDirectory()) {
                File result = findResultFile(file, fileName);
                if (null != result) {
                    return result;
                }
            }
        }
        return null;
    }

}
