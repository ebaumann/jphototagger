/*
 * @(#)RenameTemplate.java    2010-03-01
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.data;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 *
 * @author  Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class RenameTemplate {

    // On updates change set()!
    @XmlTransient
    private Long     id;
    private String   name;
    private Integer  startNumber;
    private Integer  stepWidth;
    private Integer  numberCount;
    private String   dateDelimiter;
    private Class<?> formatClassAtBegin;
    private String   delimiter1;
    private Class<?> formatClassInTheMiddle;
    private String   delimiter2;
    private Class<?> formatClassAtEnd;
    private String   textAtBegin;
    private String   textInTheMiddle;
    private String   textAtEnd;

    public RenameTemplate() {}

    public RenameTemplate(RenameTemplate other) {
        set(other);
    }

    public void set(RenameTemplate other) {
        id                     = other.id;
        name                   = other.name;
        startNumber            = other.startNumber;
        stepWidth              = other.stepWidth;
        numberCount            = other.numberCount;
        dateDelimiter          = other.dateDelimiter;
        formatClassAtBegin     = other.formatClassAtBegin;
        delimiter1             = other.delimiter1;
        formatClassInTheMiddle = other.formatClassInTheMiddle;
        delimiter2             = other.delimiter2;
        formatClassAtEnd       = other.formatClassAtEnd;
        textAtBegin            = other.textAtBegin;
        textInTheMiddle        = other.textInTheMiddle;
        textAtEnd              = other.textAtEnd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getFormatClassAtBegin() {
        return formatClassAtBegin;
    }

    public void setFormatClassAtBegin(Class<?> formatClassAtBegin) {
        this.formatClassAtBegin = formatClassAtBegin;
    }

    public Class<?> getFormatClassAtEnd() {
        return formatClassAtEnd;
    }

    public void setFormatClassAtEnd(Class<?> formatClassAtEnd) {
        this.formatClassAtEnd = formatClassAtEnd;
    }

    public Class<?> getFormatClassInTheMiddle() {
        return formatClassInTheMiddle;
    }

    public void setFormatClassInTheMiddle(Class<?> formatClassInTheMiddle) {
        this.formatClassInTheMiddle = formatClassInTheMiddle;
    }

    public String getDateDelimiter() {
        return dateDelimiter;
    }

    public void setDateDelimiter(String dateDelimiter) {
        this.dateDelimiter = dateDelimiter;
    }

    public String getDelimiter1() {
        return delimiter1;
    }

    public void setDelimiter1(String delimiter1) {
        this.delimiter1 = delimiter1;
    }

    public String getDelimiter2() {
        return delimiter2;
    }

    public void setDelimiter2(String delimiter2) {
        this.delimiter2 = delimiter2;
    }

    public Integer getNumberCount() {
        return numberCount;
    }

    public void setNumberCount(Integer numberCount) {
        this.numberCount = numberCount;
    }

    public Integer getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(Integer startNumber) {
        this.startNumber = startNumber;
    }

    public Integer getStepWidth() {
        return stepWidth;
    }

    public void setStepWidth(Integer stepWidth) {
        this.stepWidth = stepWidth;
    }

    public String getTextAtBegin() {
        return textAtBegin;
    }

    public void setTextAtBegin(String textAtBegin) {
        this.textAtBegin = textAtBegin;
    }

    public String getTextAtEnd() {
        return textAtEnd;
    }

    public void setTextAtEnd(String textAtEnd) {
        this.textAtEnd = textAtEnd;
    }

    public String getTextInTheMiddle() {
        return textInTheMiddle;
    }

    public void setTextInTheMiddle(String textInTheMiddle) {
        this.textInTheMiddle = textInTheMiddle;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final RenameTemplate other = (RenameTemplate) obj;

        if ((this.id != other.id)
                && ((this.id == null) ||!this.id.equals(other.id))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 41 * hash + ((this.id != null)
                            ? this.id.hashCode()
                            : 0);

        return hash;
    }
}
