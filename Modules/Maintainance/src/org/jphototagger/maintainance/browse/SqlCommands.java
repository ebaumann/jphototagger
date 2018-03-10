package org.jphototagger.maintainance.browse;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class SqlCommands {

    @XmlElementWrapper(name = "sqlCommands")
    @XmlElement(name = "sqlCommand")
    private final List<SqlCommand> sqlCommands = new ArrayList<>();

    public List<SqlCommand> getSqlCommands() {
        return sqlCommands;
    }
}
