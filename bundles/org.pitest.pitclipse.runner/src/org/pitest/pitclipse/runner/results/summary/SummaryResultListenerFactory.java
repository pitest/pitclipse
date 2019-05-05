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

package org.pitest.pitclipse.runner.results.summary;

import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.pitclipse.runner.results.Dispatcher;

import java.util.Properties;

/**
 * <p>Creates listeners used to sum up mutations detected by PIT.</p>
 * 
 * <p>This factory is directly called by Pitest when new results are computed.
 * This factory is made available to Pitest by the {@code org.pitest.pitclipse.listeners}
 * fragment.</p>
 */
public class SummaryResultListenerFactory implements MutationResultListenerFactory {
    @Override
    public String description() {
        return "Pitclipse summary result plugin";
    }

    @Override
    public MutationResultListener getListener(Properties properties, ListenerArguments args) {
        Dispatcher<SummaryResult> dispatcher = result -> { /* NO OP */ };
        return new SummaryResultListener(dispatcher, args.getCoverage());
    }

    @Override
    public String name() {
        return "PITCLIPSE_SUMMARY";
    }
}
