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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
        assertThat(getPitMutatorsAsString(), equalTo(getExpectedMutatorsAsString()));
    }

    private String getPitMutatorsAsString() {
        StringBuilder sb = new StringBuilder();
        for (String mutator : Mutator.allMutatorIds()) {
            sb.append(mutator).append('\n');
        }
        return sb.toString();
    }

    private String getExpectedMutatorsAsString() {
        return "INVERT_NEGS\nRETURN_VALS\nINLINE_CONSTS\nMATH\nVOID_METHOD_CALLS\n"
                + "NEGATE_CONDITIONALS\nCONDITIONALS_BOUNDARY\nINCREMENTS\n"
                + "REMOVE_INCREMENTS\nNON_VOID_METHOD_CALLS\nCONSTRUCTOR_CALLS\n"
                + "REMOVE_CONDITIONALS_EQ_IF\nREMOVE_CONDITIONALS_EQ_ELSE\n"
                + "REMOVE_CONDITIONALS_ORD_IF\nREMOVE_CONDITIONALS_ORD_ELSE\n"
                + "REMOVE_CONDITIONALS\nTRUE_RETURNS\nFALSE_RETURNS\n"
                + "PRIMITIVE_RETURNS\nEMPTY_RETURNS\nNULL_RETURNS\n"
                + "RETURNS\nEXPERIMENTAL_MEMBER_VARIABLE\n"
                + "EXPERIMENTAL_SWITCH\nEXPERIMENTAL_ARGUMENT_PROPAGATION\n"
                + "EXPERIMENTAL_NAKED_RECEIVER\nEXPERIMENTAL_BIG_INTEGER\n"
                + "AOR_1\nAOR_2\nAOR_3\nAOR_4\nABS\nAOD1\nAOD2\nCRCR1\nCRCR2\nCRCR3\nCRCR4\n"
                + "CRCR5\nCRCR6\nOBBN1\nOBBN2\nOBBN3\nROR1\nROR2\nROR3\nROR4\nROR5\nUOI1\n"
                + "UOI2\nUOI3\nUOI4\nREMOVE_SWITCH\nOLD_DEFAULTS\nSTRONGER\nALL\nDEFAULTS\n"
                + "AOR\nAOD\nCRCR\nOBBN\nROR\nUOI\n";
    }
}
