/*******************************************************************************
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api;

import java.util.List;

import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;

import com.bonitasoft.engine.page.Page;
import com.bonitasoft.engine.page.PageCreator;
import com.bonitasoft.engine.page.PageNotFoundException;
import com.bonitasoft.engine.page.PageUpdater;

/**
 * This API gives access to all page features. Page is a way to add pages on portal.
 * <p>
 * Also allows to manipulate <code>Page</code>s, through creation, deletion, search.
 * </p>
 * 
 * @author Laurent Leseigneur
 * @see Page
 */
public interface PageAPI {

    /**
     * Retrieves a page from its ID.
     * 
     * @param pageId
     *            the Identifier of the page to retrieve
     * @return the found page
     * @throws PageNotFoundException
     *             if no page can be found with the provided ID.
     */
    Page getPage(final long pageId) throws PageNotFoundException;

    /**
     * Retrieves a page from its name.
     * 
     * @param name
     *            the name of the page to retrieve
     * @return the found page
     * @throws PageNotFoundException
     *             if no page can be found with the provided ID.
     */
    Page getPageByName(final String name) throws PageNotFoundException;

    /**
     * Retrieves the binary content of a page.
     * 
     * @param pageId
     *            the ID of the page to extract the content for.
     * @return
     *         the binary content of the page.
     * @throws PageNotFoundException
     *             if no page can be found with the provided ID.
     */
    byte[] getPageContent(final long pageId) throws PageNotFoundException;

    /**
     * Searches for pages with specific search criteria.
     * 
     * @param searchOptions
     *            the search options for the search. See {@link SearchOptions} for search option details.
     * @return the <code>SearchResult</code> containing
     * @throws SearchException
     *             if a problem occurs during the search.
     */
    SearchResult<Page> searchPages(final SearchOptions searchOptions) throws SearchException;

    /**
     * Creates a custom page.
     * 
     * @param pageCreator
     *            the creator object to instantiate the new page.
     * @param content
     *            the binary content of the page.
     * @return the newly created page.
     * @throws AlreadyExistsException
     *             if a page with this name already exists.
     * @throws CreationException
     *             if an error occurs during the creation.
     */
    Page createPage(final PageCreator pageCreator, final byte[] content) throws AlreadyExistsException, CreationException;

    /**
     * Updates a custom page.
     * 
     * @param pageId
     *            the Identifier of the page to update
     * @param pageUpdater
     *            the creator object to instantiate the new page.
     * @param content
     *            the binary content of the page.
     * @return the newly created page.
     * @throws UpdateException
     *             if an error occurs during the update.
     */
    Page updatePage(final long pageId, final PageUpdater pageUpdater) throws UpdateException;

    /**
     * Updates a custom page content.
     * 
     * @param pageId
     *            the Identifier of the page to update
     * @param content
     *            the binary content of the page.
     * @throws UpdateException
     *             if an error occurs during the update.
     */
    void updatePageContent(final long pageId, final byte[] getPageContent) throws UpdateException;

    /**
     * Deletes a page identified by its ID.
     * 
     * @param pageId
     *            the page identifier to delete.
     * @throws DeletionException
     *             if a problem occurs during deletion.
     */
    void deletePage(final long pageId) throws DeletionException;

    /**
     * Deletes a list of pages, given by their IDs.
     * 
     * @param pageIds
     *            a list of page identifiers to delete.
     * @throws DeletionException
     *             if a problem occurs during deletion.
     */
    void deletePages(final List<Long> pageIds) throws DeletionException;

}
