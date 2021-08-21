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
        return "INVERT_NEGS\n"
                + "RETURN_VALS\n"
                + "INLINE_CONSTS\n"
                + "MATH\n"
                + "VOID_METHOD_CALLS\n"
                + "NEGATE_CONDITIONALS\n"
                + "CONDITIONALS_BOUNDARY\n"
                + "INCREMENTS\n"
                + "REMOVE_INCREMENTS\n"
                + "NON_VOID_METHOD_CALLS\n"
                + "CONSTRUCTOR_CALLS\n"
                + "REMOVE_CONDITIONALS_EQ_IF\n"
                + "REMOVE_CONDITIONALS_EQ_ELSE\n"
                + "REMOVE_CONDITIONALS_ORD_IF\n"
                + "REMOVE_CONDITIONALS_ORD_ELSE\n"
                + "REMOVE_CONDITIONALS\n"
                + "TRUE_RETURNS\n"
                + "FALSE_RETURNS\n"
                + "PRIMITIVE_RETURNS\n"
                + "EMPTY_RETURNS\n"
                + "NULL_RETURNS\n"
                + "RETURNS\n"
                + "EXPERIMENTAL_MEMBER_VARIABLE\n"
                + "EXPERIMENTAL_SWITCH\n"
                + "EXPERIMENTAL_ARGUMENT_PROPAGATION\n"
                + "EXPERIMENTAL_NAKED_RECEIVER\n"
                + "EXPERIMENTAL_BIG_INTEGER\n"
                + "AOR_1\n"
                + "AOR_2\n"
                + "AOR_3\n"
                + "AOR_4\n"
                + "ABS\n"
                + "AOD1\n"
                + "AOD2\n"
                + "CRCR1\n"
                + "CRCR2\n"
                + "CRCR3\n"
                + "CRCR4\n"
                + "CRCR5\n"
                + "CRCR6\n"
                + "OBBN1\n"
                + "OBBN2\n"
                + "OBBN3\n"
                + "ROR1\n"
                + "ROR2\n"
                + "ROR3\n"
                + "ROR4\n"
                + "ROR5\n"
                + "UOI1\n"
                + "UOI2\n"
                + "UOI3\n"
                + "UOI4\n"
                + "REMOVE_SWITCH\n"
                + "OLD_DEFAULTS\n"
                + "STRONGER\n"
                + "ALL\n"
                + "DEFAULTS\n"
                + "AOR\n"
                + "AOD\n"
                + "CRCR\n"
                + "OBBN\n"
                + "ROR\n"
                + "UOI\n";
    }
}
