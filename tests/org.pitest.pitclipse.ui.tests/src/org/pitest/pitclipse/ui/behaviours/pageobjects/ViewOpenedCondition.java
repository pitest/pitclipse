package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

class ViewOpenedCondition extends DefaultCondition {
    
    private SWTWorkbenchBot bot;
    private String viewTitle;

    public ViewOpenedCondition(SWTWorkbenchBot bot, String viewTitle) {
        this.bot = bot;
        this.viewTitle = viewTitle;
    }

    @Override
    public boolean test() throws Exception {
        return bot.viewByTitle(viewTitle).isActive();
    }

    @Override
    public String getFailureMessage() {
        return "The view '" + viewTitle + "' did not open";
    }
}