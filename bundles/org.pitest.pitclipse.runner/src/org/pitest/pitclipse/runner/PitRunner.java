package org.pitest.pitclipse.pitrunner;

import org.pitest.mutationtest.commandline.MutationCoverageReport;
import org.pitest.pitclipse.pitrunner.client.PitClient;
import org.pitest.pitclipse.pitrunner.results.Mutations;
import org.pitest.pitclipse.pitrunner.results.mutations.RecordingMutationsDispatcher;
import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.base.Optional;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

import java.io.File;
import java.io.IOException;

import static java.lang.Integer.valueOf;
import static org.pitest.pitclipse.reloc.guava.base.Preconditions.checkArgument;

public class PitRunner {

    public static void main(String[] args) {
        validateArgs(args);
        int port = valueOf(args[0]);
        PitClient client = new PitClient(port);
        try {
            client.connect();
            System.out.println("Connected");
            Optional<PitRequest> request = client.readRequest();
            Optional<PitResults> results = request.transform(executePit());

            results.transform(sendTo(client));
        } finally {
            try {
                System.out.println("Closing server");
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Closed");
        }
    }

    private static Function<PitResults, Void> sendTo(final PitClient client) {
        return new Function<PitResults, Void>() {
            public Void apply(PitResults pitResults) {
                client.sendResults(pitResults);
                return null;
            }
        };
    }

    public static Function<PitRequest, PitResults> executePit() {
        return new Function<PitRequest, PitResults>() {
            public PitResults apply(PitRequest request) {
                System.out.println("Received request: " + request);
                String[] cliArgs = PitCliArguments.from(request.getOptions());

                MutationCoverageReport.main(cliArgs);
                File reportDir = request.getReportDirectory();
                File htmlResultFile = findResultFile(reportDir, "index.html");
                Mutations mutations = RecordingMutationsDispatcher.INSTANCE.getDispatchedMutations();

                PitResults results = PitResults.builder().withHtmlResults(htmlResultFile).withProjects(request.getProjects())
                        .withMutations(mutations).build();
                System.out.println("Sending results: " + results);
                return results;
            }
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
