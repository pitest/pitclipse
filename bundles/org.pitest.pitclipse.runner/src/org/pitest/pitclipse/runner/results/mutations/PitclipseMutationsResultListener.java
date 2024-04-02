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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.pitclipse.runner.results.DetectionStatusCoverter;
import org.pitest.pitclipse.runner.results.Mutations;
import org.pitest.pitclipse.runner.results.Mutations.Mutation;
import org.pitest.pitclipse.runner.results.ObjectFactory;

/**
 * <p>Listens for mutations results sent by PIT in order to make them all available to Pitclipse.</p>
 * 
 * <p>Once PIT analysis ends, the results are dispatched as a {@link Mutations} instance.</p>
 * 
 * <p>Instances of this class are provided to PIT thanks to {@link MutationsResultListenerFactory}.</p>
 */
public class PitclipseMutationsResultListener implements MutationResultListener {

    private final MutationsDispatcher dispatcher;
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private List<Mutation> mutations = Collections.emptyList();

    public PitclipseMutationsResultListener(MutationsDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void runStart() {
        // nothing to do
    }

    @Override
    public void handleMutationResult(ClassMutationResults results) {
        for (final MutationResult result : results.getMutations()) {
            MutationDetails details = result.getDetails();
            final Mutation mutation = OBJECT_FACTORY.createMutationsMutation();
            mutation.setIndex(BigInteger.valueOf(details.getFirstIndex()));
            result.getKillingTest().ifPresent(mutation::setKillingTest);
            mutation.setLineNumber(BigInteger.valueOf(details.getLineNumber()));
            mutation.setMutatedClass(details.getClassName().asJavaName());
            mutation.setMutatedMethod(details.getMethod());
            mutation.setMutator(details.getMutator());
            mutation.setSourceFile(details.getFilename());
            mutation.setStatus(DetectionStatusCoverter.convert(result.getStatus()));
            mutation.setDetected(result.getStatus().isDetected());
            mutation.setDescription(details.getDescription());

            this.mutations = new ArrayList<>(mutations);
            this.mutations.add(mutation);
        }

    }

    @Override
    public void runEnd() {
        Mutations createdMutations = OBJECT_FACTORY.createMutations();
        createdMutations.getMutation().addAll(this.mutations);
        dispatcher.dispatch(createdMutations);
    }
}
