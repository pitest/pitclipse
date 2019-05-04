package org.pitest.pitclipse.runner.results;

import com.google.common.base.Function;

import org.pitest.mutationtest.MutationResultListener;

public interface ListenerFactory<T> extends Function<ListenerContext<T>, MutationResultListener> {

}