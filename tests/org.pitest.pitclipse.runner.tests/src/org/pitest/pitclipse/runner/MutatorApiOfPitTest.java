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

package org.pitest.pitclipse.runner;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.pitest.mutationtest.engine.gregor.config.Mutator;
import org.pitest.pitclipse.core.Mutators;

/**
 * This test case is in place to detect whether the supplied mutators from pit
 * changed and we need to change {@link Mutators}.
 */
public class MutatorApiOfPitTest {
    @Test
    public void testTheMutatorApiOfPit() {
        assertEquals(getExpectedMutatorsAsString(), getPitMutatorsAsString());
    }

    private String getPitMutatorsAsString() {
        StringBuilder sb = new StringBuilder();
        for (String mutator : Mutator.allMutatorIds()) {
            sb.append(mutator).append('\n');
        }
        return sb.toString();
    }

    private String getExpectedMutatorsAsString() {
    	return "REMOVE_CONDITIONALS_ORDER_IF\n" 
    			+ "REMOVE_CONDITIONALS\n"
    			+ "REMOVE_CONDITIONALS_EQUAL_IF\n" 
    			+ "TRUE_RETURNS\n"
    			+ "REMOVE_CONDITIONALS_EQUAL_ELSE\n"
    			+ "VOID_METHOD_CALLS\n"
    			+ "PRIMITIVE_RETURNS\n"
    			+ "FALSE_RETURNS\n"
    			+ "NON_VOID_METHOD_CALLS\n"
    			+ "INVERT_NEGS\n"
    			+ "CONDITIONALS_BOUNDARY\n"
    			+ "REMOVE_CONDITIONALS_ORDER_ELSE\n"
    			+ "DEFAULTS\n"
    			+ "EXPERIMENTAL_SWITCH\n"
    			+ "RETURNS\n"
    			+ "EXPERIMENTAL_MEMBER_VARIABLE\n"
    			+ "NULL_RETURNS\n"
    			+ "EXPERIMENTAL_BIG_DECIMAL\n"
    			+ "MATH\n"
    			+ "EXPERIMENTAL_BIG_INTEGER\n"
    			+ "INCREMENTS\n"
    			+ "EXPERIMENTAL_ARGUMENT_PROPAGATION\n"
    			+ "EXPERIMENTAL_NAKED_RECEIVER\n"
    			+ "CONSTRUCTOR_CALLS\n"
    			+ "REMOVE_SWITCH\n"
    			+ "INLINE_CONSTS\n"
    			+ "STRONGER\n"
    			+ "REMOVE_INCREMENTS\n"
    			+ "NEGATE_CONDITIONALS\n"
    			+ "EMPTY_RETURNS\n";
    }
}
