/**
 * Copyright (C) 2013 BonitaSoft S.A.
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
package org.bonitasoft.engine.api.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.bonitasoft.engine.builder.BuilderFactory;
import org.bonitasoft.engine.io.IOUtil;
import org.bonitasoft.engine.service.TenantServiceAccessor;
import org.bonitasoft.engine.theme.ThemeService;
import org.bonitasoft.engine.theme.builder.SThemeBuilder;
import org.bonitasoft.engine.theme.builder.SThemeBuilderFactory;
import org.bonitasoft.engine.theme.exception.SThemeCreationException;
import org.bonitasoft.engine.theme.model.STheme;
import org.bonitasoft.engine.theme.model.SThemeType;

/**
 * @author Celine Souchet
 * 
 */
public class PlatformAPIImplDelegate {

    private static final String BONITA_PORTAL_THEME_DEFAULT = "bonita-portal-theme";

    private static final String BONITA_MOBILE_THEME_DEFAULT = "bonita-mobile-theme";

    private static final String ZIP = ".zip";

    private final String portalDefaultThemeFilename;

    private final String mobileDefaultThemeFilename;

    private final File unzippedCssPortalThemeFolder;

    public PlatformAPIImplDelegate() {
        this(BONITA_PORTAL_THEME_DEFAULT, BONITA_MOBILE_THEME_DEFAULT);
    }

    public PlatformAPIImplDelegate(final String portalDefaultThemeFilename, final String mobileDefaultThemeFilename) {
        super();
        this.portalDefaultThemeFilename = portalDefaultThemeFilename;
        this.mobileDefaultThemeFilename = mobileDefaultThemeFilename;

        try {
            final InputStream inputStream = getResourceAsStream(portalDefaultThemeFilename + "-css" + ZIP);
            unzippedCssPortalThemeFolder = IOUtil.createTempDirectory(portalDefaultThemeFilename + "-css");
            IOUtil.unzipToFolder(inputStream, unzippedCssPortalThemeFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanUnzippedFolder() throws IOException {
        IOUtil.deleteDir(unzippedCssPortalThemeFolder);
    }

    public void createDefaultThemes(final TenantServiceAccessor tenantServiceAccessor) throws IOException, SThemeCreationException {
        createDefaultMobileTheme(tenantServiceAccessor);
        createDefaultPortalTheme(tenantServiceAccessor);
    }

    private void createDefaultPortalTheme(final TenantServiceAccessor tenantServiceAccessor) throws IOException, SThemeCreationException {
        final ThemeService themeService = tenantServiceAccessor.getThemeService();
        final byte[] defaultThemeZip = getFileContent(portalDefaultThemeFilename + ZIP);
        final byte[] defaultThemeCss = IOUtil.getAllContentFrom(new File(unzippedCssPortalThemeFolder, "bonita.css"));

        if (defaultThemeZip != null && defaultThemeCss != null) {
            final STheme sTheme = createTheme(defaultThemeZip, defaultThemeCss, SThemeType.PORTAL);
            themeService.createTheme(sTheme);
        }
    }

    private void createDefaultMobileTheme(final TenantServiceAccessor tenantServiceAccessor) throws IOException, SThemeCreationException {
        final ThemeService themeService = tenantServiceAccessor.getThemeService();
        final byte[] defaultThemeZip = getFileContent(mobileDefaultThemeFilename + ZIP);
        if (defaultThemeZip != null) {
            final STheme sTheme = createTheme(defaultThemeZip, null, SThemeType.MOBILE);
            themeService.createTheme(sTheme);
        }
    }

    // default visibility for testing
    STheme createTheme(final byte[] defaultThemeZip, final byte[] defaultThemeCss, final SThemeType type) {
        final long lastUpdateDate = System.currentTimeMillis();
        final SThemeBuilder sThemeBuilder = BuilderFactory.get(SThemeBuilderFactory.class).createNewInstance(defaultThemeZip,
                true, type, lastUpdateDate);
        if (SThemeType.PORTAL.equals(type)) {
            sThemeBuilder.setCSSContent(defaultThemeCss);
        }
        return sThemeBuilder.done();
    }

    byte[] getFileContent(final String fileName) throws IOException {
        final InputStream inputStream = getResourceAsStream(fileName);

        if (inputStream == null) {
            // no file
            return null;
        }
        try {
            return IOUtils.toByteArray(inputStream);
        } finally {
            inputStream.close();
        }
    }

    private InputStream getResourceAsStream(final String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }
}
