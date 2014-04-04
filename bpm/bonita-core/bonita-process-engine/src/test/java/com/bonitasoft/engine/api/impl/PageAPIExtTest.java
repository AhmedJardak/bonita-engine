package com.bonitasoft.engine.api.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.commons.exceptions.SObjectModificationException;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.persistence.QueryOptions;
import org.bonitasoft.engine.recorder.model.EntityUpdateDescriptor;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.sessionaccessor.SessionAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bonitasoft.engine.api.impl.transaction.page.SearchPages;
import com.bonitasoft.engine.page.Page;
import com.bonitasoft.engine.page.PageCreator;
import com.bonitasoft.engine.page.PageNotFoundException;
import com.bonitasoft.engine.page.PageService;
import com.bonitasoft.engine.page.PageUpdater;
import com.bonitasoft.engine.page.PageUpdater.PageUpdateField;
import com.bonitasoft.engine.page.SPage;
import com.bonitasoft.engine.page.SPageUpdateBuilder;
import com.bonitasoft.engine.page.SPageUpdateContentBuilder;
import com.bonitasoft.engine.search.descriptor.SearchEntitiesDescriptor;
import com.bonitasoft.engine.service.TenantServiceAccessor;

@RunWith(MockitoJUnitRunner.class)
public class PageAPIExtTest {

    @Mock
    PageService pageService;

    @Mock
    PageAPIExt pageAPIExt;

    @Mock
    TenantServiceAccessor tenantServiceAccessor;

    @Mock
    SPage sPage;

    @Mock
    Page page;

    @Mock
    SessionAccessor sessionAccessor;

    @Mock
    QueryOptions queryOptions;

    @Mock
    SearchPages searchPages;

    @Mock
    private PageUpdater pageUpdater;

    private final long userId = 1;

    @Mock
    private SPageUpdateBuilder sPageUpdateBuilder;

    @Mock
    private SPageUpdateContentBuilder sPageUpdateContentBuilder;

    @Before
    public void before() throws Exception {

        pageAPIExt = spy(new PageAPIExt());

        doReturn(tenantServiceAccessor).when(pageAPIExt).getTenantAccessor();
        doReturn(pageService).when(tenantServiceAccessor).getPageService();

        doReturn(1l).when(sPage).getId();
        doReturn("name").when(sPage).getName();
        doReturn("display name").when(sPage).getDisplayName();
        doReturn("description").when(sPage).getDescription();
        doReturn(2l).when(sPage).getInstalledBy();
        doReturn(3l).when(sPage).getInstallationDate();
        doReturn(4l).when(sPage).getLastModificationDate();

    }

    @Test
    public void testGetPage() throws Exception {
        // given
        final long pageId = 123;
        doReturn(sPage).when(pageService).getPage(pageId);

        // when
        pageAPIExt.getPage(pageId);

        // then
        verify(pageService, times(1)).getPage(pageId);
    }

    @Test
    public void testGetPageByName() throws Exception {
        // given
        final String pageName = "name";
        doReturn(sPage).when(pageService).getPageByName(pageName);

        // when
        pageAPIExt.getPageByName(pageName);

        // then
        verify(pageService, times(1)).getPageByName(pageName);
    }

    @Test(expected = PageNotFoundException.class)
    public void testGetPageByNameNotFound() throws Exception {
        // given
        // when
        pageAPIExt.getPageByName("unknown");

        // then: exception
    }

    @Test
    public void testGetPageContent() throws Exception {
        // given
        final long pageId = 123;

        // when
        pageAPIExt.getPageContent(pageId);

        // then
        verify(pageService, times(1)).getPageContent(pageId);
    }

    @Test
    public void testSearchPages() throws Exception {
        doReturn(searchPages).when(pageAPIExt).getSearchPages(any(SearchOptions.class), any(SearchEntitiesDescriptor.class), any(PageService.class));

        // when
        final SearchOptions searchOptions = new SearchOptionsBuilder(0, 5).searchTerm("search").done();
        pageAPIExt.searchPages(searchOptions);

        // then
        verify(searchPages, times(1)).execute();
    }

    @Test
    public void testCreatePage() throws Exception {
        // given
        final PageCreator pageCreator = new PageCreator("name", "content.zip");
        final byte[] content = "content".getBytes();

        doReturn(userId).when(pageAPIExt).getUserIdFromSessionInfos();
        doReturn(sPage).when(pageAPIExt).constructPage(pageCreator, userId);
        doReturn(page).when(pageAPIExt).convertToPage(any(SPage.class));

        // when
        pageAPIExt.createPage(pageCreator, content);

        // then
        verify(pageService, times(1)).addPage(sPage, content);
    }

    @Test
    public void testUpdatePage() throws Exception {
        final Map<PageUpdateField, String> map = new HashMap<PageUpdater.PageUpdateField, String>();
        doReturn(userId).when(pageAPIExt).getUserIdFromSessionInfos();
        doReturn(sPage).when(pageAPIExt).constructPage(any(PageUpdater.class), anyLong());
        doReturn(page).when(pageAPIExt).convertToPage(any(SPage.class));
        doReturn(sPageUpdateBuilder).when(pageAPIExt).getPageUpdateBuilder();
        doReturn(map).when(pageUpdater).getFields();

        // given
        map.put(PageUpdateField.DISPLAY_NAME, "displayname");
        map.put(PageUpdateField.NAME, "name");
        map.put(PageUpdateField.DESCRIPTION, "description");
        map.put(PageUpdateField.CONTENT_NAME, "content.zip");

        // when
        pageAPIExt.updatePage(1, pageUpdater);

        // then
        verify(pageService, times(1)).updatePage(anyLong(), any(EntityUpdateDescriptor.class));
    }

    @Test(expected = UpdateException.class)
    public void testUpdatePageWithEmplyUpdateFileShouldThrowExceptions() throws Exception {
        doReturn(userId).when(pageAPIExt).getUserIdFromSessionInfos();
        doReturn(sPage).when(pageAPIExt).constructPage(any(PageUpdater.class), anyLong());
        doReturn(page).when(pageAPIExt).convertToPage(any(SPage.class));

        // given
        final Map<PageUpdateField, String> map = new HashMap<PageUpdater.PageUpdateField, String>();
        doReturn(map).when(pageUpdater).getFields();

        // when
        pageAPIExt.updatePage(1, pageUpdater);

        // then
        verify(pageService, times(1)).updatePage(anyLong(), any(EntityUpdateDescriptor.class));
    }

    @Test
    public void testUpdatePageContent() throws Exception {

        doReturn(sPageUpdateBuilder).when(pageAPIExt).getPageUpdateBuilder();
        doReturn(sPageUpdateContentBuilder).when(pageAPIExt).getPageUpdateContentBuilder();

        // given
        final byte[] content = "content".getBytes();
        final long pageId = 1;

        // when
        pageAPIExt.updatePageContent(pageId, content);

        // then
        verify(pageService, times(1)).updatePageContent(anyLong(), any(EntityUpdateDescriptor.class));
    }

    @Test(expected = UpdateException.class)
    public void testUpdatePageContentWithWrongZip() throws Exception {
        final SObjectModificationException exception = new SObjectModificationException("message");
        doReturn(sPageUpdateBuilder).when(pageAPIExt).getPageUpdateBuilder();
        doReturn(sPageUpdateContentBuilder).when(pageAPIExt).getPageUpdateContentBuilder();
        doThrow(exception).when(pageService).updatePageContent(anyLong(), any(EntityUpdateDescriptor.class));

        // given
        final byte[] content = "bad".getBytes();
        final long pageId = 1;

        // when
        pageAPIExt.updatePageContent(pageId, content);

        // then
        // exception
    }

    @Test
    public void testDeletePage() throws Exception {
        // given
        final long pageId = 123;

        // when
        pageAPIExt.deletePage(pageId);

        // then
        verify(pageService, times(1)).deletePage(pageId);
    }

    @Test
    public void testDeletePages() throws Exception {
        // given
        final List<Long> pageIds = new ArrayList<Long>();
        for (int pageId = 0; pageId < 10; pageId++) {
            pageIds.add(new Long(pageId));
        }

        // when
        pageAPIExt.deletePages(pageIds);

        // then
        verify(pageService, times(pageIds.size())).deletePage(anyLong());
    }

    @Test(expected = AlreadyExistsException.class)
    public void testCheckPageAlreadyExists() throws Exception {
        // given
        doReturn(sPage).when(pageService).getPageByName("name");

        // when
        pageAPIExt.checkPageAlreadyExists("name", tenantServiceAccessor);

        // then
        // AlreadyExistsException

    }

}
