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

package org.pitest.pitclipse.runner.config;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.PROJECT_ISOLATION;

public class PitConfigurationTest {

    private static final String DEFAULT_MUTATORS = "DEFAULTS";
    private PitConfiguration config;

    @Test
    public void noScopeDefinedDefaultsToProject() {
        givenNoExecutionScopeIsSupplied();
        thenTheDefaultScopeIsProjectLevel();
    }

    @Test
    public void noMutatorsDefinedDefaultsToDefault() {
        givenNoMutatorsAreSupplied();
        thenTheDefaultMutatorsAreUsed();
    }

    private void givenNoExecutionScopeIsSupplied() {
        defaultConfig();
    }

    private void givenNoMutatorsAreSupplied() {
        defaultConfig();
    }

    private void thenTheDefaultScopeIsProjectLevel() {
        assertEquals(PROJECT_ISOLATION, config.getExecutionMode());
    }

    private void thenTheDefaultMutatorsAreUsed() {
        assertThat(config.getMutators(), is(equalTo(DEFAULT_MUTATORS)));
    }

    @Before
    public void cleanup() {
        config = null;
    }

    private void defaultConfig() {
        config = PitConfiguration.builder().build();
    }
}
