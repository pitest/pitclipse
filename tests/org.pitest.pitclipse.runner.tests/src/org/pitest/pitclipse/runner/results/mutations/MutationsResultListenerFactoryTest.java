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

package org.pitest.pitclipse.runner.results.mutations;

import org.junit.Test;
import org.pitest.mutationtest.ListenerArguments;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MutationsResultListenerFactoryTest {

    private final MutationsResultListenerFactory factory = new MutationsResultListenerFactory();

    @Test
    public void theListenerDescribesItselfSensibly() {
        assertThat(factory.description(), is(not(nullValue())));
        assertThat(factory.name(), is(equalTo("PITCLIPSE_MUTATIONS")));
    }

    @Test
    public void factoryProducesExpectedListener() {
        assertThat(factory.getListener(someProperties(), someArgs()),
                is(instanceOf(PitclipseMutationsResultListener.class)));
    }

    private ListenerArguments someArgs() {
        return null;
    }
    
    private Properties someProperties() { 
        return null;
    }
}
