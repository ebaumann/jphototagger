package org.jphototagger.importfiles.subdircreators.templates;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Container for {@link SubdirectoryTemplate}s.
 *
 * @author Elmar Baumann
 */
@XmlRootElement(name = "SubdirectoryTemplates")
@XmlAccessorType(XmlAccessType.NONE)
public final class SubdirectoryTemplates {

    @XmlElement(name = "template")
    @XmlElementWrapper(name = "templates")
    private final List<SubdirectoryTemplate> templates = new ArrayList<>();

    /**
     * @return templates for modification
     */
    public List<SubdirectoryTemplate> getTemplates() {
        return templates;
    }
}
