// OptionFrame.java
package jmri.jmrit.operations.setup;

import jmri.jmrit.operations.OperationsFrame;

/**
 * Frame for user edit of setup options
 *
 * @author Dan Boudreau Copyright (C) 2010, 2011, 2012, 2013
 * @version $Revision$
 */
public class OptionFrame extends OperationsFrame {

    /**
     *
     */
    private static final long serialVersionUID = 6966221440054475425L;

    public OptionFrame() {
        super(Bundle.getMessage("TitleOptions"), new OptionPanel());
    }

    @Override
    public void initComponents() {
        super.initComponents();

        // build menu
        addHelpMenu("package.jmri.jmrit.operations.Operations_SettingsOptions", true); // NOI18N

        initMinimumSize();
    }

//    private static final Logger log = LoggerFactory.getLogger(OptionFrame.class);
}
