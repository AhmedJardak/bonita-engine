/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 ******************************************************************************/

package com.bonitasoft.engine.business.application.model.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Elias Ricken de Medeiros
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationNode {

    @XmlAttribute(required = true)
    private String token;

    @XmlAttribute(required = true)
    private String version;

    @XmlElement
    private String displayName;

    @XmlElement
    private String description;

    @XmlElement
    private String profile;

    @XmlElement
    private String homePage;

    @XmlElement
    private String state;

    @XmlElement
    private Date creationDate;

    @XmlElement
    private String createdBy;

    @XmlElement
    private Date lastUpdateDate;

    @XmlElement
    private String updatedBy;

    @XmlElement
    private String iconPath;


    @XmlElementWrapper(name = "applicationPages")
    @XmlElement(name = "applicationPage")
    private List<ApplicationPageNode> applicationPages;

    @XmlElementWrapper(name = "applicationMenus")
    @XmlElement(name = "applicationMenu")
    private List<ApplicationMenuNode> applicationMenus;

    public ApplicationNode() {
        applicationPages = new ArrayList<ApplicationPageNode>();
        applicationMenus = new ArrayList<ApplicationMenuNode>();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ApplicationPageNode> getApplicationPages() {
        return applicationPages;
    }

    public void addApplicationPage(ApplicationPageNode applicationPage) {
        this.applicationPages.add(applicationPage);
    }

    public List<ApplicationMenuNode> getApplicationMenus() {
        return applicationMenus;
    }

    public void addApplicationMenu(ApplicationMenuNode applicationMenu) {
        this.applicationMenus.add(applicationMenu);
    }
}
