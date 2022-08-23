package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
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
        final SWTBotView view = bot.viewByTitle(viewTitle);
        final boolean condition = view != null
            && view.isActive();
        if (!condition) {
            System.out.println("*** " + viewTitle + " not yet active...");
        }
        return condition;
    }
    
    @Override
    public String getFailureMessage() {
        return "The view '" + viewTitle + "' did not open";
    }
}