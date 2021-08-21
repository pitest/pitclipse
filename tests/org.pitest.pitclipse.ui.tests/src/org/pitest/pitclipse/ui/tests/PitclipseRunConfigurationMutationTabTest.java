/*******************************************************************************
 * Copyright 2021 Jonas Kutscha and contributors
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
package org.pitest.pitclipse.ui.tests;

import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.core.Mutators;

/**
 * @author Jonas Kutscha
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipseRunConfigurationMutationTabTest extends AbstractPitclipseSWTBotTest {
    private static final String TEST_PROJECT = "org.pitest.pitclipse.testprojects.twoclasses";
    private static final String TEST_CONFIG_NAME = "Testing Config";
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_TEST_CLASS = "FooTest";

    private static final int COVERAGE = 40;
    private static final int TESTED_CLASSES = 2;

    @BeforeClass
    public static void initialSetup() throws CoreException {
        importTestProject(TEST_PROJECT);
        PAGES.getRunMenu().createRunConfiguration(TEST_CONFIG_NAME,
                TEST_PROJECT,
                FOO_BAR_PACKAGE + '.' + FOO_TEST_CLASS);
    }

    @AfterClass
    public static void removeConfig() {
        PAGES.getRunMenu().removeConfig(TEST_CONFIG_NAME);
    }

    @After
    public void clearConsole() {
        PAGES.getConsole().clear();
    }

    @Test
    public void pressMutatorGroupButtons() { // NOSONAR
        PAGES.getRunMenu().setMutatorGroup(TEST_CONFIG_NAME, Mutators.OLD_DEFAULTS);
        PAGES.getRunMenu().setMutatorGroup(TEST_CONFIG_NAME, Mutators.DEFAULTS);
        PAGES.getRunMenu().setMutatorGroup(TEST_CONFIG_NAME, Mutators.STRONGER);
        PAGES.getRunMenu().setMutatorGroup(TEST_CONFIG_NAME, Mutators.ALL);
        PAGES.getRunMenu().setMutatorGroup(TEST_CONFIG_NAME, Mutators.DEFAULTS);
    }

    @Test
    public void selectNoMutator() { // NOSONAR
        PAGES.getRunMenu().setOneCustomMutator(TEST_CONFIG_NAME, Mutators.NEGATE_CONDITIONALS);
        PAGES.getRunMenu().toggleCustomMutator(TEST_CONFIG_NAME, Mutators.NEGATE_CONDITIONALS);
        // should switch back to previous selected mutator
        PAGES.getRunMenu().runPitWithConfiguration(TEST_CONFIG_NAME);
        coverageReportGenerated(TESTED_CLASSES, COVERAGE, 0, 2, 0);
        // check that mutator was selected as only mutator
        mutatorIs(Mutators.NEGATE_CONDITIONALS);
        mutationsAre(   "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    7 | negated conditional\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    7 | negated conditional");
    }

    @Test
    public void useOldDefaultsMutatorsGroup() { // NOSONAR
        // set OLD_DEFAULTS mutators
        PAGES.getRunMenu().setMutatorGroup(TEST_CONFIG_NAME, Mutators.OLD_DEFAULTS);
        // run test and confirm result is as expected
        PAGES.getRunMenu().runPitWithConfiguration(TEST_CONFIG_NAME);
        coverageReportGenerated(TESTED_CLASSES, COVERAGE, 0, 8, 0);
        mutationsAre(   "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    7 | negated conditional\n" +
                        "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    10 | replaced return of integer sized value with (x == 0 ? 1 : 0)\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    7 | negated conditional\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    8 | Replaced integer addition with subtraction\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    8 | replaced return of integer sized value with (x == 0 ? 1 : 0)\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    10 | replaced return of integer sized value with (x == 0 ? 1 : 0)\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    8 | Replaced integer addition with subtraction\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    8 | replaced return of integer sized value with (x == 0 ? 1 : 0)");
    }

    @Test
    public void useDefaultMutatorsGroup() { // NOSONAR
        // set DEFAULTS mutators
        PAGES.getRunMenu().setMutatorGroup(TEST_CONFIG_NAME, Mutators.DEFAULTS);
        // run test and confirm result is as expected
        PAGES.getRunMenu().runPitWithConfiguration(TEST_CONFIG_NAME);
        coverageReportGenerated(TESTED_CLASSES, COVERAGE, 0, 6, 0);
        mutationsAre(   "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    7 | negated conditional\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    7 | negated conditional\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    8 | Replaced integer addition with subtraction\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    8 | replaced int return with 0 for foo/bar/Bar::f\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    8 | Replaced integer addition with subtraction\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    8 | replaced int return with 0 for foo/bar/Foo::f");
    }

    @Test
    public void useStrongerMutatorsGroup() { // NOSONAR
        // now set STRONGER mutators
        PAGES.getRunMenu().setMutatorGroup(TEST_CONFIG_NAME, Mutators.STRONGER);
        // run test and confirm result is as expected
        PAGES.getRunMenu().runPitWithConfiguration(TEST_CONFIG_NAME);
        coverageReportGenerated(TESTED_CLASSES, COVERAGE, 0, 8, 0);
        mutationsAre(   "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    7 | negated conditional\n" +
                        "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    7 | removed conditional - replaced equality check with false\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    7 | negated conditional\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    7 | removed conditional - replaced equality check with false\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    8 | Replaced integer addition with subtraction\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    8 | replaced int return with 0 for foo/bar/Bar::f\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    8 | Replaced integer addition with subtraction\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    8 | replaced int return with 0 for foo/bar/Foo::f");
    }

    @Test
    public void checkOneMutant() { // NOSONAR
        PAGES.getRunMenu().setOneCustomMutator(TEST_CONFIG_NAME, Mutators.NEGATE_CONDITIONALS);
        // run test and confirm result is as expected
        PAGES.getRunMenu().runPitWithConfiguration(TEST_CONFIG_NAME);
        coverageReportGenerated(TESTED_CLASSES, COVERAGE, 0, 2, 0);
        // check that mutator was selected as only mutator
        mutatorIs(Mutators.NEGATE_CONDITIONALS);
        mutationsAre(   "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    7 | negated conditional\n" +
                        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    7 | negated conditional");
    }

    /**
     * This test will probably fail, if mutators of pit get changed
     */
    @Test
    public void useAllMutatorsGroup() { // NOSONAR
        // now set ALL mutators group
        PAGES.getRunMenu().setMutatorGroup(TEST_CONFIG_NAME, Mutators.ALL);
        // run test and confirm result is as expected
        PAGES.getRunMenu().runPitWithConfiguration(TEST_CONFIG_NAME);
        coverageReportGenerated(TESTED_CLASSES, COVERAGE, 1, 84, 1);
        mutationsAre(getAllMutantsResult());
    }

    /**
     * This test will probably fail, if mutators of pit get changed
     */
    @Test
    public void checkAllMutants() { // NOSONAR
        PAGES.getRunMenu().checkAllMutators(TEST_CONFIG_NAME);
        // run test and confirm result is as expected
        PAGES.getRunMenu().runPitWithConfiguration(TEST_CONFIG_NAME);
        coverageReportGenerated(TESTED_CLASSES, COVERAGE, 1, 84, 1);
        mutationsAre(getAllMutantsResult());
    }

    private String getAllMutantsResult(){
        return "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | Substituted 1 with -1\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | Substituted 1 with -1\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | Substituted 1 with 0\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | Substituted 1 with 0\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | Substituted 1 with 0\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | Substituted 1 with 2\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | negated conditional\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | not equal to equal\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | not equal to greater or equal\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | not equal to greater than\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | not equal to less or equal\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | not equal to less than\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | removed call to java/util/ArrayList::size\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | removed conditional - replaced equality check with false\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 7 | removed conditional - replaced equality check with true\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 10 | Substituted 0 with -1\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 10 | Substituted 0 with -1\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 10 | Substituted 0 with 1\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 10 | Substituted 0 with 1\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 10 | Substituted 0 with 1\n" +
                "SURVIVED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 10 | replaced return of integer sized value with (x == 0 ? 1 : 0)\n" +
                "KILLED | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 6 | removed call to java/util/ArrayList::<init>\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 6 | removed call to java/util/ArrayList::<init>\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | Substituted 1 with -1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | Substituted 1 with -1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | Substituted 1 with 0\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | Substituted 1 with 0\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | Substituted 1 with 0\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | Substituted 1 with 2\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | negated conditional\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | not equal to equal\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | not equal to greater or equal\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | not equal to greater than\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | not equal to less or equal\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | not equal to less than\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | removed call to java/util/ArrayList::size\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | removed conditional - replaced equality check with false\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 7 | removed conditional - replaced equality check with true\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Decremented (--a) integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Decremented (a--) integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Incremented (++a) integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Incremented (a++) integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Negated integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Replaced integer addition with division\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Replaced integer addition with modulus\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Replaced integer addition with multiplication\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Replaced integer addition with subtraction\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Replaced integer addition with subtraction\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Replaced integer operation by second member\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Replaced integer operation with first member\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Substituted 1 with -1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Substituted 1 with -1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Substituted 1 with 0\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Substituted 1 with 0\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Substituted 1 with 0\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | Substituted 1 with 2\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | replaced int return with 0 for foo/bar/Bar::f\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 8 | replaced return of integer sized value with (x == 0 ? 1 : 0)\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 10 | Substituted 0 with -1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 10 | Substituted 0 with -1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 10 | Substituted 0 with 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 10 | Substituted 0 with 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 10 | Substituted 0 with 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Bar | 10 | replaced return of integer sized value with (x == 0 ? 1 : 0)\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Decremented (--a) integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Decremented (a--) integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Incremented (++a) integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Incremented (a++) integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Negated integer local variable number 1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Replaced integer addition with division\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Replaced integer addition with modulus\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Replaced integer addition with multiplication\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Replaced integer addition with subtraction\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Replaced integer addition with subtraction\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Replaced integer operation by second member\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Replaced integer operation with first member\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Substituted 1 with -1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Substituted 1 with -1\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Substituted 1 with 0\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Substituted 1 with 0\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Substituted 1 with 0\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | Substituted 1 with 2\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | replaced int return with 0 for foo/bar/Foo::f\n" +
                "NO_COVERAGE | " + TEST_PROJECT + " |foo.bar | foo.bar.Foo | 8 | replaced return of integer sized value with (x == 0 ? 1 : 0)";
    }
}
