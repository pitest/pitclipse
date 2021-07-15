package org.pitest.pitclipse.ui.util;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * 
 * @author Jonas Kutscha
 *
 */
public class ViewUtil {
    /**
     * Opens the view in the given node and returns its handle, identified with the id.
     * @param bot      used for the menus
     * @param viewName name of the view seen, in "Show View" shell
     * @param nodeName name of the node, where the view can be found, in "Show View" shell
     * @param viewId   the view id which identifies the view
     * @return the handle of the view
     */
    public static SWTBotView openViewById(SWTWorkbenchBot bot, String viewName, String nodeName, String viewId) {
        bot.menu("Window").menu("Show View").menu("Other...").click();
        SWTBotShell shell = bot.shell("Show View");
        shell.activate();
        bot.tree().expandNode(nodeName).select(viewName);
        bot.button("Open").click();
        return bot.viewById(viewId);
    }
}
