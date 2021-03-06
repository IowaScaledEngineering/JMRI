// PrintMoreOptionAction.java
package jmri.jmrit.operations.setup;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Swing action to load the print options.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2009
 * @version $Revision: 21656 $
 */
public class PrintMoreOptionAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = -6885400704456385145L;

    public PrintMoreOptionAction() {
        this(Bundle.getMessage("TitlePrintMoreOptions"));
    }

    public PrintMoreOptionAction(String s) {
        super(s);
    }

    PrintMoreOptionFrame f = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (f == null || !f.isVisible()) {
            f = new PrintMoreOptionFrame();
            f.initComponents();
        }
        f.setExtendedState(Frame.NORMAL);
        f.setVisible(true); // this also brings the frame into focus
    }
}

/* @(#)PrintMoreOptionAction.java */
