package org.pitest.pitclipse.runner;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.pitest.mutationtest.commandline.MutationCoverageReport;
import org.pitest.pitclipse.runner.client.PitClient;
import org.pitest.pitclipse.runner.results.Mutations;
import org.pitest.pitclipse.runner.results.mutations.RecordingMutationsDispatcher;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Integer.parseInt;

public class PitRunner {

    public static void main(String[] args) {
        validateArgs(args);
        int port = parseInt(args[0]);
        
        try (PitClient client = new PitClient(port)) {
            client.connect();
            System.out.println("Connected");
            Optional<PitRequest> request = client.readRequest();
            Optional<PitResults> results = request.transform(executePit());

            results.toJavaUtil().ifPresent(client::sendResults);
            System.out.println("Closing server");
            
        } catch (IOException e) {
            // An error occurred while closing the client
            e.printStackTrace();
        }
        System.out.println("Closed");
    }

    public static Function<PitRequest, PitResults> executePit() {
        return request -> {
            System.out.println("Received request: " + request);
            String[] cliArgs = PitCliArguments.from(request.getOptions());
            MutationCoverageReport.main(cliArgs);
            File reportDir = request.getReportDirectory();
            File htmlResultFile = findResultFile(reportDir, "index.html");
            Mutations mutations = RecordingMutationsDispatcher.INSTANCE.getDispatchedMutations();
            PitResults results = PitResults.builder()
                                           .withHtmlResults(htmlResultFile)
                                           .withProjects(request.getProjects())
                                           .withMutations(mutations)
                                           .build();
            System.out.println("Sending results: " + results);
            return results;
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
