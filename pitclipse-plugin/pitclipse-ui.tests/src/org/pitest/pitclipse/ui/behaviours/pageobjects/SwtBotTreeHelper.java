package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class SwtBotTreeHelper {

    public static SWTBotTreeItem selectAndExpand(SWTBotTreeItem item) {
        return expand(item.select().click());
    }

    public static SWTBotTreeItem expand(SWTBotTreeItem item) {
        if (item.isExpanded())
            return item;
        else
            return item.expand();
    }

    private SwtBotTreeHelper() {
    }
}
