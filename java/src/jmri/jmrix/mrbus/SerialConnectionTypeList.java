package jmri.jmrix.mrbus;

/**
 * Returns a list of valid MRBus Connection Types
 * <P>
 * @author Bob Jacobsen Copyright (C) 2010
 * @author Kevin Dickerson Copyright (C) 2010
 * @version	$Revision$
 *
 */
public class SerialConnectionTypeList implements jmri.jmrix.ConnectionTypeList {

    public static final String MRBUS = "MRBus";

    @Override
    public String[] getAvailableProtocolClasses() {
        return new String[]{
            "jmri.jmrix.mrbus.serialdriver.ConnectionConfig"
        };
    }

    @Override
    public String[] getManufacturers() {
        return new String[]{MRBUS};
    }

}
