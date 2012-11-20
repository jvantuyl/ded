// EntityShape.java

package ded.model;

/** Set of possible entity shapes. */
public enum EntityShape {
    ES_NO_SHAPE                        // no outline
        ("No shape"),
    ES_RECTANGLE
        ("Rectangle"),
    ES_ELLIPSE
        ("Ellipse");
    
    /** Name of this shape as shown in the UI. */
    public final String displayName;
    
    EntityShape(String dn)
    {
        this.displayName = dn;
    }

    /** This is what JComboBox uses when it draws the items. */
    public String toString()
    {
        return this.displayName;
    }
}

// EOF