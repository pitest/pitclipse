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

package org.pitest.pitclipse.core;

public enum MutatorGroups {
    
    DEFAULTS("defaultMutators", "&Default Mutators", ""), 
    STRONGER("strongerMutators", "&Stronger Mutators", ""), 
    ALL("allMutators", "&All Mutators", "");

    private final String label;
    private final String id;
    private final String description;

    private MutatorGroups(String id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
}
