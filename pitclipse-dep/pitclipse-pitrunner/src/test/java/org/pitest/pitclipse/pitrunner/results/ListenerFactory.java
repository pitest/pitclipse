package org.pitest.pitclipse.pitrunner.results;

import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.reloc.guava.base.Function;

public interface ListenerFactory<T, D extends Dispatcher<? super T>> extends Function<D, MutationResultListener> {

}