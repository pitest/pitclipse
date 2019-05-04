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

package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.SwtBotTreeHelper.expand;
import static org.pitest.pitclipse.ui.util.StepUtil.safeSleep;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.pitest.pitclipse.runner.results.DetectionStatus;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.pitest.pitclipse.ui.behaviours.steps.FilePosition;
import org.pitest.pitclipse.ui.behaviours.steps.PitMutation;

public class PitMutationsView {

    private final SWTWorkbenchBot bot;

    public PitMutationsView(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public List<PitMutation> getMutations() {
        safeSleep(1000);
        PAGES.views().closeConsole();
        SWTBotTree mutationTree = mutationTreeRoot();
        return mutationsFrom(mutationTree);
    }

    private SWTBotTree mutationTreeRoot() {
        SWTBotView mutationsView = bot.viewByTitle("PIT Mutations");
        mutationsView.show();
        SWTBotTree mutationTree = mutationsView.bot().tree();
        return mutationTree;
    }

    private ImmutableList<PitMutation> mutationsFrom(SWTBotTree mutationTree) {
        ImmutableList.Builder<PitMutation> resultBuilder = ImmutableList.builder();
        for (SWTBotTreeItem statusTreeItem : mutationTree.getAllItems()) {
            String status = normaliseLabel(expand(statusTreeItem));
            PitMutation.Builder mutationBuilder = PitMutation.builder().withStatus(DetectionStatus.valueOf(status));
            resultBuilder.addAll(statusMutations(mutationBuilder, statusTreeItem));
        }
        return resultBuilder.build();
    }

    private ImmutableList<PitMutation> statusMutations(PitMutation.Builder mutationBuilder,
            SWTBotTreeItem statusTreeItem) {
        ImmutableList.Builder<PitMutation> mutationResults = ImmutableList.builder();
        for (SWTBotTreeItem projectTreeItem : statusTreeItem.getItems()) {
            expand(projectTreeItem);
            mutationBuilder.withProject(normaliseLabel(projectTreeItem));
            mutationResults.addAll(projectMutations(mutationBuilder, projectTreeItem));
        }
        return mutationResults.build();
    }

    private ImmutableList<PitMutation> projectMutations(PitMutation.Builder mutationBuilder,
            SWTBotTreeItem projectTreeItem) {
        ImmutableList.Builder<PitMutation> mutationResults = ImmutableList.builder();
        for (SWTBotTreeItem pkgTreeItem : projectTreeItem.getItems()) {
            expand(pkgTreeItem);
            mutationBuilder.withPackage(normaliseLabel(pkgTreeItem));
            mutationResults.addAll(packageMutations(mutationBuilder, pkgTreeItem));
        }
        return mutationResults.build();
    }

    private ImmutableList<PitMutation> packageMutations(PitMutation.Builder mutationBuilder, SWTBotTreeItem pkgTreeItem) {
        ImmutableList.Builder<PitMutation> mutationResults = ImmutableList.builder();
        for (SWTBotTreeItem classTreeItem : pkgTreeItem.getItems()) {
            expand(classTreeItem);
            mutationBuilder.withClassName(normaliseLabel(classTreeItem));
            mutationResults.addAll(classMutations(mutationBuilder, classTreeItem));
        }
        return mutationResults.build();
    }

    private ImmutableList<PitMutation> classMutations(PitMutation.Builder mutationBuilder, SWTBotTreeItem classTreeItem) {
        ImmutableList.Builder<PitMutation> mutationResults = ImmutableList.builder();
        for (SWTBotTreeItem mutationTreeItem : classTreeItem.getItems()) {
            expand(mutationTreeItem);
            String mutationLabel = mutationTreeItem.getText();
            mutationBuilder.withLineNumber(lineNumberFrom(mutationLabel));
            mutationBuilder.withMutation(mutationFrom(mutationLabel));
            PitMutation mutation = mutationBuilder.build();
            mutationResults.add(mutation);
        }
        return mutationResults.build();
    }

    private int lineNumberFrom(String mutationLabel) {
        List<String> labelSplitByPattern = ImmutableList.copyOf(Splitter.on(':').split(mutationLabel));
        return labelSplitByPattern.isEmpty() ? 0 : Integer.valueOf(labelSplitByPattern.get(0));
    }

    private String mutationFrom(String mutationLabel) {
        List<String> labelSplitByPattern = ImmutableList.copyOf(Splitter.on(": ").limit(2).split(mutationLabel));
        return labelSplitByPattern.size() > 1 ? labelSplitByPattern.get(1) : "";
    }

    private static String normaliseLabel(SWTBotTreeItem treeItem) {
        String originalLabel = treeItem.getText();
        List<String> labelSplitByPattern = ImmutableList.copyOf(Splitter.on(Pattern.compile(" \\([0-9]*\\)")).split(
                originalLabel));
        return labelSplitByPattern.isEmpty() ? originalLabel : labelSplitByPattern.get(0);
    }

    public void select(PitMutation mutation) {
        MutationsTree tree = MutationsTree.from(mutationTreeRoot());
        tree.select(mutation);
    }

    private static class MutationsTree {
        private final ImmutableList<StatusTree> statuses;

        private MutationsTree(ImmutableList<StatusTree> statuses) {
            this.statuses = statuses;
        }

        public void select(PitMutation mutation) {
            String status = mutation.getStatus().toString();
            for (StatusTree statusTree : statuses)
                if (status.equals(statusTree.statusName))
                    statusTree.select(mutation);
        }

        public static MutationsTree from(SWTBotTree mutationTree) {
            ImmutableList.Builder<StatusTree> statuses = ImmutableList.builder();
            for (SWTBotTreeItem treeItem : mutationTree.getAllItems()) {
                statuses.add(StatusTree.from(treeItem));
            }
            return new MutationsTree(statuses.build());
        }
    }

    private static class StatusTree {
        private final ImmutableList<ProjectTree> projects;
        private final String statusName;

        private StatusTree(String statusName, ImmutableList<ProjectTree> projects) {
            this.statusName = statusName;
            this.projects = projects;
        }

        public void select(PitMutation mutation) {
            for (ProjectTree projectTree : projects)
                if (mutation.getProject().equals(projectTree.projectName))
                    projectTree.select(mutation);
        }

        public static StatusTree from(SWTBotTreeItem statusTree) {
            expand(statusTree);
            ImmutableList.Builder<ProjectTree> statuses = ImmutableList.builder();
            for (SWTBotTreeItem treeItem : statusTree.getItems()) {
                statuses.add(ProjectTree.from(treeItem));
            }
            String statusName = normaliseLabel(statusTree);
            return new StatusTree(statusName, statuses.build());
        }
    }

    private static class ProjectTree {
        private final ImmutableList<PackageTree> packages;
        private final String projectName;

        private ProjectTree(String projectName, ImmutableList<PackageTree> packages) {
            this.projectName = projectName;
            this.packages = packages;
        }

        public void select(PitMutation mutation) {
            for (PackageTree packageTree : packages)
                if (mutation.getPkg().equals(packageTree.packageName))
                    packageTree.select(mutation);

        }

        public static ProjectTree from(SWTBotTreeItem projectTree) {
            expand(projectTree);
            ImmutableList.Builder<PackageTree> packages = ImmutableList.builder();
            for (SWTBotTreeItem treeItem : projectTree.getItems()) {
                packages.add(PackageTree.from(treeItem));
            }
            String projectName = normaliseLabel(projectTree);
            return new ProjectTree(projectName, packages.build());
        }
    }

    private static class PackageTree {
        private final ImmutableList<ClassTree> classes;
        private final String packageName;

        private PackageTree(String packageName, ImmutableList<ClassTree> classes) {
            this.packageName = packageName;
            this.classes = classes;
        }

        public void select(PitMutation mutation) {
            for (ClassTree classTree : classes)
                if (mutation.getClassName().equals(classTree.className))
                    classTree.select(mutation);
        }

        public static PackageTree from(SWTBotTreeItem packageTree) {
            expand(packageTree);
            ImmutableList.Builder<ClassTree> classes = ImmutableList.builder();
            for (SWTBotTreeItem treeItem : packageTree.getItems()) {
                classes.add(ClassTree.from(treeItem));
            }
            String packageName = normaliseLabel(packageTree);
            return new PackageTree(packageName, classes.build());
        }
    }

    private static class ClassTree {
        private final ImmutableList<MutationTree> mutations;
        private final String className;

        private ClassTree(String className, ImmutableList<MutationTree> projects) {
            this.className = className;
            this.mutations = projects;
        }

        public void select(PitMutation mutation) {
            for (MutationTree mutationTree : mutations)
                if (mutation.getLineNumber() == mutationTree.lineNumber
                        && mutation.getMutation().equals(mutationTree.mutation))
                    mutationTree.select();
        }

        public static ClassTree from(SWTBotTreeItem classTree) {
            expand(classTree);
            ImmutableList.Builder<MutationTree> mutations = ImmutableList.builder();
            for (SWTBotTreeItem treeItem : classTree.getItems()) {
                mutations.add(MutationTree.from(treeItem));
            }
            String className = normaliseLabel(classTree);
            return new ClassTree(className, mutations.build());
        }
    }

    private static class MutationTree {
        private final int lineNumber;
        private final String mutation;
        private final SWTBotTreeItem treeItem;

        public MutationTree(SWTBotTreeItem treeItem, int lineNumber, String mutation) {
            this.treeItem = treeItem;
            this.lineNumber = lineNumber;
            this.mutation = mutation;
        }

        public void select() {
            treeItem.select().doubleClick();
        }

        public static MutationTree from(SWTBotTreeItem mutationNode) {
            String label = mutationNode.getText();
            int lineNumber = lineNumberFrom(label);
            String mutation = mutationFrom(label);
            return new MutationTree(mutationNode, lineNumber, mutation);
        }

        private static int lineNumberFrom(String mutationLabel) {
            List<String> labelSplitByPattern = ImmutableList.copyOf(Splitter.on(':').split(mutationLabel));
            return labelSplitByPattern.isEmpty() ? 0 : Integer.valueOf(labelSplitByPattern.get(0));
        }

        private static String mutationFrom(String mutationLabel) {
            List<String> labelSplitByPattern = ImmutableList.copyOf(Splitter.on(": ").limit(2).split(mutationLabel));
            return labelSplitByPattern.size() > 1 ? labelSplitByPattern.get(1) : "";
        }
    }

    public FilePosition getLastSelectedMutation() {
        SWTBotEditor editor = bot.activeEditor();
        String fileName = editor.getTitle();
        SWTBotEclipseEditor textEditor = editor.toTextEditor();
        int lineNumber = 0;
        List<String> lines = textEditor.getLines();
        String currentLine = textEditor.getTextOnCurrentLine();
        for (int i = 0; i < lines.size(); i++) {
            if (currentLine.equals(lines.get(i))) {
                lineNumber = i + 1;
                break;
            }
        }
        return FilePosition.builder().withFileName(fileName).withLineNumber(lineNumber).build();
    }
}
