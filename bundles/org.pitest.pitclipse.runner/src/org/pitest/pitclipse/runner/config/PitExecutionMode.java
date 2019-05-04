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

public enum PitExecutionMode {
    PROJECT_ISOLATION("containingProject", "&Project containing test only") {
        @Override
        public <T> T accept(PitExecutionModeVisitor<T> visitor) {
            return visitor.visitProjectLevelConfiguration();
        }
    },
    WORKSPACE("allProjects", "&All projects in workspace") {
        @Override
        public <T> T accept(PitExecutionModeVisitor<T> visitor) {
            return visitor.visitWorkspaceLevelConfiguration();
        }
    };

    private final String label;
    private final String id;

    PitExecutionMode(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }

    public abstract <T> T accept(PitExecutionModeVisitor<T> visitor);
}
