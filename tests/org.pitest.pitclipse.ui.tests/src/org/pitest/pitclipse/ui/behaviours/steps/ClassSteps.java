package org.pitest.pitclipse.ui.behaviours.steps;

import org.pitest.pitclipse.ui.behaviours.pageobjects.ConcreteClassContext;

import static org.junit.Assert.assertTrue;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public final class ClassSteps {

    private ConcreteClassContext concreteClassContext = null;

    @When("a class {word} in package {word} is created in project {word}")
    public void classIsCreated(String className, String packageName, String projectName) {
        createClass(className, packageName, projectName);
    }

    @Given("a bad test for class {word} in package {word} is created in project {word}")
    public void createClassWithBadTest(String className, String packageName, String projectName) {
        String testClass = className + "Test";
        createClass(testClass, packageName, projectName);
        selectClass(testClass, packageName, projectName);
        createMethod("@Test public void badTest() {" + className + " x = new " + className + "(); x.f(1);}");
        createClass(className, packageName, projectName);
        selectClass(className, packageName, projectName);
        createMethod("public int f(int i) {ArrayList<Object> pointless = new ArrayList<Object>(); if (pointless.size() == 1) return i + 1; else return 0;}");
    }

    @Given("a bad test for class {word} is created in the default package in project {word}")
    public void createClassWithBadTestInDefaultPackage(String className, String projectName) {
        String packageName = "";
        String testClass = className + "Test";
        createClass(testClass, packageName, projectName);
        selectClass(testClass, packageName, projectName);
        createMethod("@Test public void badTest() {" + className + " x = new " + className + "(); x.f(1);}");
        createClass(className, packageName, projectName);
        selectClass(className, packageName, projectName);
        createMethod("public int f(int i) {ArrayList<Object> pointless = new ArrayList<Object>(); if (pointless.size() == 1) return i + 1; else return 0;}");
    }

    @Then("package {word} exists in project {word}")
    public void verifyPackageExists(String packageName, String projectName) {
        assertTrue("Package: " + packageName + " not found",
                PAGES.getPackageExplorer().doesPackageExistInProject(packageName, projectName));
    }

    @Then("class {word} exists in package {word} in project {word}")
    public void verifyClassExists(String className, String packageName, String projectName) {
        assertTrue("Class: " + className + " not found",
                PAGES.getPackageExplorer().doesClassExistInProject(className, packageName, projectName));
    }

    @Given("the class {word} in package {word} in project {word} is selected")
    public void selectClass(String className, String packageName, String projectName) {
        concreteClassContext = new ConcreteClassContext.Builder().withProjectName(projectName)
                .withPackageName(packageName).withClassName(className).build();
    }

    @When("a method {string} is created")
    public void createMethod(String method) {
        concreteClassContext = new ConcreteClassContext.Builder().clone(concreteClassContext).withMethod(method)
                .build();
        PAGES.getBuildProgress().listenForBuild();
        PAGES.getPackageExplorer().openClass(concreteClassContext);
        PAGES.getAbstractSyntaxTree().addMethod(concreteClassContext);
        PAGES.getSourceMenu().organizeImports();
        PAGES.getSourceMenu().format();
        PAGES.getFileMenu().saveAll();
        PAGES.getBuildProgress().waitForBuild();
    }

    @When("the class is renamed to {word}")
    public void renameClass(String newClassName) {
        PAGES.getBuildProgress().listenForBuild();
        PAGES.getPackageExplorer().selectClass(concreteClassContext.getClassName(),
                concreteClassContext.getPackageName(), concreteClassContext.getProjectName());
        PAGES.getRefactorMenu().renameClass(newClassName);
        PAGES.getBuildProgress().waitForBuild();
    }

    @Given("the package {word} in project {word} is selected")
    public void selectPackage(String packageName, String projectName) {
        concreteClassContext = new ConcreteClassContext.Builder().withProjectName(projectName)
                .withPackageName(packageName).build();
    }

    @When("the package is renamed to {word}")
    public void renamePackage(String packageName) {
        PAGES.getBuildProgress().listenForBuild();
        PAGES.getPackageExplorer().selectPackage(concreteClassContext);
        PAGES.getRefactorMenu().renamePackage(packageName);
        PAGES.getBuildProgress().waitForBuild();
    }

    private static void createClass(String className, String packageName, String projectName) {
        PAGES.getBuildProgress().listenForBuild();
        PAGES.getPackageExplorer().selectPackageRoot(projectName, "src");
        // Cannot use the Package explorer right click context menu
        // to create a class due to SWTBot bug 261360
        PAGES.getFileMenu().createClass(packageName, className);
        PAGES.getBuildProgress().waitForBuild();
    }
}
