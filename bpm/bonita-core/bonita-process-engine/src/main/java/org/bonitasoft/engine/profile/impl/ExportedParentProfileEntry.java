/**
 * Copyright (C) 2012-2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.profile.impl;

import java.util.List;

/**
 * @author Zhao Na
 * @author Celine Souchet
 */
public class ExportedParentProfileEntry {

    private final String name;

    private String description;

    private String type;

    private String parentName;

    private long index;

    private String page;

    private List<ExportedProfileEntry> childProfileEntries;

    public List<ExportedProfileEntry> getChildProfileEntries() {
        return childProfileEntries;
    }

    public void setChildProfileEntries(List<ExportedProfileEntry> childProfileEntries) {
        this.childProfileEntries = childProfileEntries;
    }

    public ExportedParentProfileEntry(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (description == null ? 0 : description.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (type == null ? 0 : type.hashCode());
        result = prime * result + (page == null ? 0 : page.hashCode());
        result = prime * result + (parentName == null ? 0 : parentName.hashCode());
        result = prime * result + (int) (index ^ (index >>> 32));
        return result;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExportedParentProfileEntry other = (ExportedParentProfileEntry) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (page == null) {
            if (other.page != null) {
                return false;
            }
        } else if (!page.equals(other.page)) {
            return false;
        }
        if (index != other.index) {
            return false;
        }
        if (parentName == null) {
            if (other.parentName != null) {
                return false;
            }
        } else if (!parentName.equals(other.parentName)) {
            return false;
        }
        return true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

}
