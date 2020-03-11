package org.pitest.pitclipse.launch.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import java.util.function.Function;

class LambdaLabelProvider<T> extends ColumnLabelProvider {
    
    private final Function<T, String> labelProvider;

    public LambdaLabelProvider(Function<T, String> labelProvider) {
        super();
        this.labelProvider = labelProvider;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public String getText(Object element) {
        try {
            return labelProvider.apply((T) element);
        }
        catch (ClassCastException e) {
            return "";
        }
    }

}