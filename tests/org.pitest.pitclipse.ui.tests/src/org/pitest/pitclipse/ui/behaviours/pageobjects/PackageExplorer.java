/*******************************************************************************
 * Copyright 2012-2021 Phil Glover and contributors
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

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.junit.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.SwtBotTreeHelper.selectAndExpand;
import static org.pitest.pitclipse.ui.util.VerifyUtil.isNotNull;

import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PackageExplorer {

    private static final String PACKAGE_EXPLORER = "Package Explorer";
    private final SWTWorkbenchBot bot;

    public PackageExplorer(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public List<String> getProjectsInWorkspace() {
        Builder<String> builder = ImmutableList.builder();
        SWTBotTreeItem[] treeItems = bot.viewByTitle(PACKAGE_EXPLORER).bot().tree().getAllItems();
        for (SWTBotTreeItem swtBotTreeItem : treeItems) {
            builder.add(swtBotTreeItem.getText());
        }
        return builder.build();
    }

    public void selectProject(String projectName) {
        getProject(projectName);
    }

    private void openProject(SWTBotTreeItem project) {
        selectAndExpand(project);
    }

    private SWTBotTreeItem getProject(String projectName) {
        // You need to have the focus on the explorer, otherwise the project doesn't get
        // selected properly
        bot.viewByTitle(PACKAGE_EXPLORER).show();
        SWTBotTreeItem[] treeItems = bot.viewByTitle(PACKAGE_EXPLORER).bot().tree().getAllItems();
        for (SWTBotTreeItem treeItem : treeItems) {
            if (projectName.equals(treeItem.getText())) {
                openProject(treeItem);
                return treeItem;
            }
        }
        fail("Project: " + projectName + " couldn't be found");
        return null; // Never reached
    }

    public boolean doesPackageExistInProject(String packageName, String projectName) {
        SWTBotTreeItem project = getProject(projectName);
        return isNotNull(getPackageFromProject(project, packageName));
    }

    private SWTBotTreeItem getPackageFromProject(SWTBotTreeItem project, String packageName) {
        for (SWTBotTreeItem srcDir : project.getItems()) {
            for (SWTBotTreeItem pkg : selectAndExpand(srcDir).getItems()) {
                String text = pkg.getText();
                if (packageName.equals(text) || (isDefaultPackage(packageName) && isDefaultPackageLabel(text))) {
                    return pkg;
                }
            }
        }
        return null;
    }

    private boolean isDefaultPackageLabel(String label) {
        return "(default package)".equals(label);
    }

    private boolean isDefaultPackage(String packageName) {
        return isNullOrEmpty(packageName);
    }

    private SWTBotTreeItem getClassFromPackage(SWTBotTreeItem pkg, String className) {
        String fileName = className + ".java";
        for (SWTBotTreeItem clazz : pkg.getItems()) {
            if (fileName.equals(clazz.getText())) {
                return clazz;
            }
        }
        return null;
    }

    public void selectClass(String className, String packageName, String projectName) {
        SWTBotTreeItem project = getProject(projectName);
        SWTBotTreeItem pkg = selectAndExpand(getPackageFromProject(project, packageName));
        SWTBotTreeItem classItem = getClassFromPackage(pkg, className);
        
        if (classItem == null) {
            throw new NoSuchElementException("class " + className + 
                                             " not found in package " + packageName + 
                                             " of project " + projectName);
        }
        selectAndExpand(classItem);
    }

    public boolean doesClassExistInProject(String className, String packageName, String projectName) {
        SWTBotTreeItem project = getProject(projectName);
        SWTBotTreeItem pkg = selectAndExpand(getPackageFromProject(project, packageName));
        return isNotNull(getClassFromPackage(pkg, className));
    }

    public void openClass(ClassContext context) {
        SWTBotTreeItem pkg = selectAndExpand(getPackage(context));
        SWTBotTreeItem classItem = getClassFromPackage(pkg, context.getClassName());
        
        if (classItem == null) {
            throw new NoSuchElementException("class " + context.getClassName() + 
                                             " not found in package " + context.getPackageName() + 
                                             " of project " + context.getProjectName());
        }
        SWTBotTreeItem clazz = selectAndExpand(classItem);
        clazz.doubleClick();
    }

    public void selectPackage(PackageContext context) {
        getPackage(context).select();
    }

    private SWTBotTreeItem getPackage(PackageContext context) {
        SWTBotTreeItem project = getProject(context.getProjectName());
        return getPackageFromProject(project, context.getPackageName());
    }

    public SWTBotTreeItem selectPackageRoot(PackageContext context) {
        return selectPackageRoot(context.getProjectName(), context.getSourceDir());
    }

    public SWTBotTreeItem selectPackageRoot(String projectName, String src) {
        SWTBotTreeItem project = getProject(projectName);
        for (SWTBotTreeItem srcDir : project.getItems()) {
            SWTBotTreeItem item = selectAndExpand(srcDir);
            if (item.getText().equals(src)) {
                return srcDir;
            }
        }
        return null;

    }
}
