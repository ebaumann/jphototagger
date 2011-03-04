package org.jphototagger.program.image.metadata.xmp;

import com.adobe.xmp.options.PropertyOptions;

import org.jphototagger.program.database.metadata.mapping.XmpColumnXmpDataTypeMapping.XmpValueType;

/**
 *
 *
 * @author Elmar Baumann
 */
public enum ArrayName {
    LR_HIERARCHICAL_SUBJECTS("lr:hierarchicalSubject", Namespace.LIGHTROOM, XmpValueType.BAG_TEXT),
    ;

    private final String name;
    private final Namespace namesapce;
    private final XmpValueType valueType;

    private ArrayName(String name, Namespace namesapce, XmpValueType valueType) {
        this.name = name;
        this.namesapce = namesapce;
        this.valueType = valueType;
    }

    public Namespace getNamesapce() {
        return namesapce;
    }

    public String getName() {
        return name;
    }

    public XmpValueType getValueType() {
        return valueType;
    }

    public PropertyOptions getArrayPropertyOptions() {
        if (valueType.equals(XmpValueType.BAG_TEXT)) {
            return new PropertyOptions().setArray(true);
        } else if (valueType.equals(XmpValueType.SEQ_PROPER_NAME)) {
            return new PropertyOptions().setArrayOrdered(true);
        } else if (valueType.equals(XmpValueType.LANG_ALT)) {
            return new PropertyOptions().setArrayAlternate(true);
        } else {
            assert false : valueType;

            return null;
        }
    }
}
