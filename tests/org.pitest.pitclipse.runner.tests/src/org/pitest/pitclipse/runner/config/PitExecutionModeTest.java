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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.WORKSPACE;

@RunWith(MockitoJUnitRunner.class)
public class PitExecutionModeTest {

    @Mock
    private PitExecutionModeVisitor<Void> visitor;

    @Test
    public void projectVisitorIsInvoked() {
        whenTheProjectExecutionModeIsVisited();
        thenTheProjectExecutionVisitorMethodIsInvoked();
    }

    @Test
    public void workspaceVisitorIsInvoked() {
        whenTheWorkspaceExecutionModeIsVisited();
        thenTheWorkspaceExecutionVisitorMethodIsInvoked();
    }

    private void whenTheProjectExecutionModeIsVisited() {
        PROJECT_ISOLATION.accept(visitor);
    }

    private void whenTheWorkspaceExecutionModeIsVisited() {
        WORKSPACE.accept(visitor);
    }

    private void thenTheProjectExecutionVisitorMethodIsInvoked() {
        verify(visitor, only()).visitProjectLevelConfiguration();
    }

    private void thenTheWorkspaceExecutionVisitorMethodIsInvoked() {
        verify(visitor, only()).visitWorkspaceLevelConfiguration();
    }
}
