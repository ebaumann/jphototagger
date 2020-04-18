package org.jphototagger.importfiles.subdircreators.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Template for creating a subdirectory which may contain subdirectories.
 *
 * @author Elmar Baumann
 */
@XmlAccessorType(XmlAccessType.NONE)
public final class SubdirectoryTemplate {

    @XmlElement(name = "template")
    private String template;

    @XmlElement(name = "displayName")
    private String displayName;

    @XmlElement(name = "position")
    private int position;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
