package com.bonitasoft.engine.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.ExecutionException;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileEntry;
import org.bonitasoft.engine.profile.ProfileEntrySearchDescriptor;
import org.bonitasoft.engine.profile.ProfileMember;
import org.bonitasoft.engine.profile.ProfileMemberSearchDescriptor;
import org.bonitasoft.engine.profile.ProfileSearchDescriptor;
import org.bonitasoft.engine.search.Order;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.test.annotation.Cover;
import org.bonitasoft.engine.test.annotation.Cover.BPMNConcept;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.bonitasoft.engine.api.ProfileAPI;

public class ProfileImportAndExportTest extends AbstractProfileTest {

    @Cover(classes = ProfileAPI.class, concept = BPMNConcept.PROFILE, keywords = { "Profile", "Export" }, story = "Export all profiles.")
    @Test
    public void exportAllProfiles() throws BonitaException, IOException {
        final Map<Long, Long> numberOfProfileMembers = getProfileAPI().getNumberOfProfileMembers(Arrays.asList(adminProfileId, userProfileId));
        assertNotNull(numberOfProfileMembers);
        assertEquals(2, numberOfProfileMembers.size());
        assertEquals(Long.valueOf(5), numberOfProfileMembers.get(adminProfileId));
        assertEquals(Long.valueOf(1), numberOfProfileMembers.get(userProfileId));

        final byte[] profilebytes = getProfileAPI().exportAllProfiles();

        final String xmlStr = new String(profilebytes);
        final String[] strs = xmlStr.split("profile name=\"");
        assertEquals(5, strs.length);
        assertEquals("Administrator", strs[1].substring(0, strs[1].indexOf('\"')));
        assertEquals("Process owner", strs[2].substring(0, strs[2].indexOf('\"')));
        // assertEquals("Process owner", strs[3].substring(0, strs[3].indexOf("\"")));
        // assertEquals("User", strs[4].substring(0, strs[4].indexOf("\"")));
        final File f = new File("AllProfiles.xml");
        if (!f.exists()) {
            f.createNewFile();
        }
        final FileOutputStream fileOutputStream = new FileOutputStream(f);
        fileOutputStream.write(profilebytes);
        fileOutputStream.flush();
        fileOutputStream.close();
        f.delete();
    }

    @Cover(classes = ProfileAPI.class, concept = BPMNConcept.PROFILE, keywords = { "Profile", "Export" }, story = "Export specified profiles.")
    @Test
    public void exportProfilesSpecified() throws BonitaException, IOException {
        final List<Long> profileIds = new ArrayList<Long>();
        profileIds.add(adminProfileId);
        profileIds.add(userProfileId);
        final Map<Long, Long> numberOfProfileMembers = getProfileAPI().getNumberOfProfileMembers(profileIds);
        assertNotNull(numberOfProfileMembers);
        assertEquals(2, numberOfProfileMembers.size());
        assertEquals(Long.valueOf(5), numberOfProfileMembers.get(adminProfileId));
        assertEquals(Long.valueOf(1), numberOfProfileMembers.get(userProfileId));

        final long[] profIds = { profileIds.get(1).longValue() };
        final byte[] profilebytes = getProfileAPI().exportProfilesWithIdsSpecified(profIds);

        final String xmlStr = new String(profilebytes);
        final String[] strs = xmlStr.split("profile name=\"");
        assertEquals(2, strs.length);
        assertEquals("User", strs[1].substring(0, strs[1].indexOf('\"')));

        final File f = new File("Profiles.xml");
        if (!f.exists()) {
            f.createNewFile();
        }
        final FileOutputStream fileOutputStream = new FileOutputStream(f);
        fileOutputStream.write(profilebytes);
        fileOutputStream.flush();
        fileOutputStream.close();
        f.delete();
    }

    @Cover(classes = ProfileAPI.class, concept = BPMNConcept.PROFILE, keywords = { "Profile", "Import", "Export" }, story = "Import and export profiles.")
    @Test
    public void importAndExport() throws BonitaException, IOException, SAXException {
        final InputStream xmlStream1 = ProfileImportAndExportTest.class.getResourceAsStream("AllProfiles.xml");
        final byte[] xmlContent = IOUtils.toByteArray(xmlStream1);
        final List<String> warningMsgs1 = getProfileAPI().importProfilesUsingSpecifiedPolicy(xmlContent, ImportPolicy.DELETE_EXISTING);
        assertEquals(0, warningMsgs1.size());

        // profilesHaveBeenImported(4);

        final byte[] profilebytes = getProfileAPI().exportAllProfiles();
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.compareXML(new String(xmlContent), new String(profilebytes));
    }

    @Cover(classes = ProfileAPI.class, concept = BPMNConcept.PROFILE, keywords = { "Profile", "Import" }, story = "Import profile on other duplicate.")
    @Test
    public void importOnOtherDuplicate() throws BonitaException, IOException {
        // profile entries
        SearchOptionsBuilder builder = new SearchOptionsBuilder(0, 10);
        builder.sort(ProfileEntrySearchDescriptor.NAME, Order.ASC);
        builder.filter(ProfileEntrySearchDescriptor.PROFILE_ID, adminProfileId);
        final List<ProfileEntry> searchedProfileEntries = getProfileAPI().searchProfileEntries(builder.done()).getResult();
        assertNotNull(searchedProfileEntries);
        assertEquals(10, searchedProfileEntries.size());

        /**
         * FailAndIgnoreOnDuplicate
         */
        final InputStream xmlStreamig = ProfileImportAndExportTest.class.getResourceAsStream("failAndIgnoreOnDuplicateProfile.xml");
        final List<String> warningMsgsig = getProfileAPI().importProfilesUsingSpecifiedPolicy(IOUtils.toByteArray(xmlStreamig), ImportPolicy.IGNORE_DUPLICATES);
        assertEquals("Role with name role60 not found.", warningMsgsig.get(0));

        // check profiles
        builder = new SearchOptionsBuilder(0, 10);
        builder.sort(ProfileSearchDescriptor.ID, Order.ASC);
        final SearchResult<Profile> searchedProfilesResig = getProfileAPI().searchProfiles(builder.done());
        final List<Profile> result = searchedProfilesResig.getResult();
        Profile profile0 = result.get(0);
        final long olderId = profile0.getId();
        Profile profile1 = result.get(1);
        final long newId = profile1.getId();
        assertEquals(5L, searchedProfilesResig.getCount());
        assertEquals(adminProfileId, olderId);
        assertEquals("Administrator", profile0.getName());
        assertEquals("Administrator profile", profile0.getDescription());
        assertEquals("Team Manager", profile1.getName());
        assertEquals("Team Manager profile", profile1.getDescription());
        assertTrue(olderId < newId);

        // check new profile entry
        builder = new SearchOptionsBuilder(0, 15);
        builder.sort(ProfileEntrySearchDescriptor.NAME, Order.ASC);
        builder.filter(ProfileEntrySearchDescriptor.PROFILE_ID, newId);
        final List<ProfileEntry> searchedProfileEntriesRes2ig = getProfileAPI().searchProfileEntries(builder.done()).getResult();
        assertNotNull(searchedProfileEntriesRes2ig);
        final ProfileEntry profileEntry0 = searchedProfileEntriesRes2ig.get(0);

        assertEquals(11, searchedProfileEntriesRes2ig.size());
        assertEquals("Activity", profileEntry0.getName());
        assertEquals("Activity", profileEntry0.getDescription());
        assertEquals("folder", profileEntry0.getType());

        // check older profile entry unmodified
        builder = new SearchOptionsBuilder(0, 25);
        builder.sort(ProfileEntrySearchDescriptor.NAME, Order.ASC);
        builder.filter(ProfileEntrySearchDescriptor.PROFILE_ID, olderId);
        final List<ProfileEntry> searchedProfileEntriesRes3 = getProfileAPI().searchProfileEntries(builder.done()).getResult();
        assertNotNull(searchedProfileEntriesRes3);
        assertEquals(24, searchedProfileEntriesRes3.size());
        assertEquals(searchedProfileEntries.get(0).getName(), searchedProfileEntriesRes3.get(0).getName());
        assertEquals(searchedProfileEntries.get(0).getDescription(), searchedProfileEntriesRes3.get(0).getDescription());
        assertEquals(searchedProfileEntries.get(0).getType(), searchedProfileEntriesRes3.get(0).getType());

        // check new profile mapping
        SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
        searchOptionsBuilder.filter(ProfileMemberSearchDescriptor.PROFILE_ID, newId);
        final SearchResult<ProfileMember> searchpmRes1 = getProfileAPI().searchProfileMembers("user", searchOptionsBuilder.done());
        assertEquals(1, searchpmRes1.getCount());
        assertEquals(user4.getId(), searchpmRes1.getResult().get(0).getUserId());
        assertEquals(newId, searchpmRes1.getResult().get(0).getProfileId());

        searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
        searchOptionsBuilder.filter(ProfileMemberSearchDescriptor.PROFILE_ID, olderId);
        final SearchResult<ProfileMember> searchpmRes2 = getProfileAPI().searchProfileMembers("role", searchOptionsBuilder.done());
        assertEquals(2, searchpmRes2.getCount());

        /**
         * ReplaceOnDuplicate
         */
        // profiles
        builder = new SearchOptionsBuilder(0, 10);
        builder.sort(ProfileSearchDescriptor.ID, Order.ASC);
        final SearchResult<Profile> searchedProfilesrp = getProfileAPI().searchProfiles(builder.done());
        assertNotNull(searchedProfilesrp);
        final List<Profile> newResult = searchedProfilesrp.getResult();
        profile0 = newResult.get(0);
        profile1 = newResult.get(1);
        assertEquals(5l, searchedProfilesrp.getCount());
        assertEquals(olderId, profile0.getId());
        assertEquals("Administrator", profile0.getName());
        assertEquals("Administrator profile", profile0.getDescription());
        assertEquals(newId, profile1.getId());
        assertEquals("Team Manager", profile1.getName());
        assertEquals("Team Manager profile", profile1.getDescription());

        // profile entries
        builder = new SearchOptionsBuilder(0, 25);
        builder.sort(ProfileEntrySearchDescriptor.NAME, Order.ASC);
        builder.filter(ProfileEntrySearchDescriptor.PROFILE_ID, olderId);
        final List<ProfileEntry> searchedProfileEntriesrl = getProfileAPI().searchProfileEntries(builder.done()).getResult();
        assertNotNull(searchedProfileEntriesrl);
        assertEquals(24, searchedProfileEntriesrl.size());

        final InputStream xmlStreamrp = ProfileImportAndExportTest.class.getResourceAsStream("replaceOnDuplicateProfile.xml");
        final List<String> warningMsgsrl = getProfileAPI()
                .importProfilesUsingSpecifiedPolicy(IOUtils.toByteArray(xmlStreamrp), ImportPolicy.REPLACE_DUPLICATES);
        assertEquals("Group with path /groupPath1 not found.", warningMsgsrl.get(0));

        // check profiles
        builder = new SearchOptionsBuilder(0, 10);
        builder.sort(ProfileSearchDescriptor.ID, Order.ASC);
        final SearchResult<Profile> searchedProfilesResrl = getProfileAPI().searchProfiles(builder.done());
        final long older1 = newResult.get(0).getId();
        final long newId1 = searchedProfilesResrl.getResult().get(1).getId();
        final long newId2 = searchedProfilesResrl.getResult().get(2).getId();
        assertEquals(5l, searchedProfilesResrl.getCount());
        assertEquals(older1, searchedProfilesResrl.getResult().get(0).getId());
        assertEquals("Administrator", searchedProfilesResrl.getResult().get(0).getName());
        assertEquals("Administrator profile", searchedProfilesResrl.getResult().get(0).getDescription());
        assertEquals("User", searchedProfilesResrl.getResult().get(1).getName());
        assertEquals("User profile", searchedProfilesResrl.getResult().get(1).getDescription());
        assertEquals("Plop", searchedProfilesResrl.getResult().get(2).getName());
        assertEquals("Plop profile", searchedProfilesResrl.getResult().get(2).getDescription());

        // check new profile entry
        final SearchOptionsBuilder builderNewId1 = new SearchOptionsBuilder(0, 25);
        builderNewId1.sort(ProfileEntrySearchDescriptor.NAME, Order.ASC);
        builderNewId1.filter(ProfileEntrySearchDescriptor.PROFILE_ID, newId1);
        final List<ProfileEntry> searchedProfileEntriesRes2rl = getProfileAPI().searchProfileEntries(builderNewId1.done()).getResult();
        assertNotNull(searchedProfileEntriesRes2rl);
        assertEquals(17, searchedProfileEntriesRes2rl.size());
        assertEquals("All", searchedProfileEntriesRes2rl.get(0).getName());
        assertEquals("Processes current user can run", searchedProfileEntriesRes2rl.get(0).getDescription());
        assertEquals("link", searchedProfileEntriesRes2rl.get(0).getType());

        // check older profile entry replaced with new id
        final SearchOptionsBuilder builderNewId2 = new SearchOptionsBuilder(0, 25);
        builderNewId2.sort(ProfileEntrySearchDescriptor.NAME, Order.ASC);
        builderNewId2.filter(ProfileEntrySearchDescriptor.PROFILE_ID, newId2);
        final List<ProfileEntry> searchedProfileEntriesRes3rl = getProfileAPI().searchProfileEntries(builderNewId2.done()).getResult();
        assertNotNull(searchedProfileEntriesRes3rl);
        assertEquals(1, searchedProfileEntriesRes3rl.size());
        assertEquals("PlopEntry", searchedProfileEntriesRes3rl.get(0).getName());
        assertEquals("BPM DES", searchedProfileEntriesRes3rl.get(0).getDescription());
        assertEquals("folder", searchedProfileEntriesRes3rl.get(0).getType());

        // check new profile mapping
        final SearchOptionsBuilder builder1 = new SearchOptionsBuilder(0, 25);
        builder1.filter(ProfileEntrySearchDescriptor.PROFILE_ID, newId1);
        final SearchResult<ProfileMember> searchpmRes1rl = getProfileAPI().searchProfileMembers("user", builder1.done());
        assertEquals(0, searchpmRes1rl.getCount());

        // for group
        final SearchResult<ProfileMember> searchpmRes1Group = getProfileAPI().searchProfileMembers("group", builder1.done());
        assertEquals(1, searchpmRes1Group.getCount());
        assertEquals(group1.getId(), searchpmRes1Group.getResult().get(0).getGroupId());
        assertEquals(newId1, searchpmRes1Group.getResult().get(0).getProfileId());

        // for memebership
        final SearchResult<ProfileMember> searchpmRes1mem = getProfileAPI().searchProfileMembers("roleAndGroup", builder1.done());
        assertEquals(0, searchpmRes1mem.getCount());

        // for user
        final SearchOptionsBuilder builder2 = new SearchOptionsBuilder(0, 25);
        builder2.filter(ProfileEntrySearchDescriptor.PROFILE_ID, newId2);
        final SearchResult<ProfileMember> searchpmRes = getProfileAPI().searchProfileMembers("user", builder2.done());
        assertEquals(0, searchpmRes.getCount());

        // for role
        final SearchResult<ProfileMember> searchpmResRole = getProfileAPI().searchProfileMembers("role", builder2.done());
        assertEquals(0, searchpmResRole.getCount());

        /**
         * ExportAndImport
         */
        final byte[] xmlBytes = getProfileAPI().exportAllProfiles();
        getProfileAPI().importProfilesUsingSpecifiedPolicy(xmlBytes, ImportPolicy.DELETE_EXISTING);

        final byte[] profilebytes = xmlBytes;
        assertEquals(new String(xmlBytes), new String(profilebytes));
    }

    @Cover(classes = ProfileAPI.class, concept = BPMNConcept.PROFILE, keywords = { "Profile", "Import" }, story = "Import profiles and delete existing.")
    @Test
    public void importProfilesDeleteExisting() throws BonitaException, IOException {
        final InputStream xmlStream1 = ProfileImportAndExportTest.class.getResourceAsStream("AllProfiles.xml");
        final List<String> warningMsgs1 = getProfileAPI().importProfilesUsingSpecifiedPolicy(IOUtils.toByteArray(xmlStream1), ImportPolicy.DELETE_EXISTING);
        assertEquals(0, warningMsgs1.size());

        // check current status: profiles and its attributes
        SearchOptionsBuilder builder = new SearchOptionsBuilder(0, 10);
        builder.sort(ProfileSearchDescriptor.ID, Order.ASC);
        final SearchResult<Profile> searchedProfiles = getProfileAPI().searchProfiles(builder.done());
        final long olderid1 = searchedProfiles.getResult().get(0).getId();
        final long olderid2 = searchedProfiles.getResult().get(1).getId();
        final long olderid3 = searchedProfiles.getResult().get(2).getId();
        final long olderid4 = searchedProfiles.getResult().get(3).getId();
        assertEquals(4, searchedProfiles.getResult().size());
        assertEquals(4l, searchedProfiles.getCount());
        assertEquals("Administrator", searchedProfiles.getResult().get(0).getName());
        assertEquals("Team Manager", searchedProfiles.getResult().get(1).getName());
        assertEquals("Process owner", searchedProfiles.getResult().get(2).getName());
        assertEquals("User", searchedProfiles.getResult().get(3).getName());
        assertEquals("Administrator profile", searchedProfiles.getResult().get(0).getDescription());
        assertEquals("Team Manager profile", searchedProfiles.getResult().get(1).getDescription());
        assertEquals("Process owner profile", searchedProfiles.getResult().get(2).getDescription());
        assertEquals("User profile", searchedProfiles.getResult().get(3).getDescription());

        // check profile entries and their attributes
        for (final long i : Arrays.asList(olderid1, olderid2, olderid3, olderid4)) {
            builder = new SearchOptionsBuilder(0, 10);
            builder.sort(ProfileEntrySearchDescriptor.PROFILE_ID, Order.ASC);
            builder.filter(ProfileEntrySearchDescriptor.PROFILE_ID, i);
            assertNotNull(getProfileAPI().searchProfileEntries(builder.done()).getResult());
        }

        final InputStream xmlStream = ProfileImportAndExportTest.class.getResourceAsStream("deleteExistingProfile.xml");
        final List<String> warningMsgs = getProfileAPI().importProfilesUsingSpecifiedPolicy(IOUtils.toByteArray(xmlStream), ImportPolicy.DELETE_EXISTING);
        assertEquals(0, warningMsgs.size());

        // check profiles
        builder = new SearchOptionsBuilder(0, 10);
        builder.sort(ProfileSearchDescriptor.ID, Order.ASC);
        final SearchResult<Profile> searchedProfilesRes = getProfileAPI().searchProfiles(builder.done());
        final long newId1 = searchedProfilesRes.getResult().get(0).getId();
        assertTrue(newId1 > olderid4);
        assertEquals(1l, searchedProfilesRes.getCount());
        assertEquals("Team Manager", searchedProfilesRes.getResult().get(0).getName());
        assertEquals("TM profile", searchedProfilesRes.getResult().get(0).getDescription());

        // check profileEntries
        for (final long i : Arrays.asList(olderid1, olderid2, olderid3, olderid4)) {
            builder = new SearchOptionsBuilder(0, 10);
            builder.sort(ProfileEntrySearchDescriptor.NAME, Order.ASC);
            builder.filter(ProfileEntrySearchDescriptor.PROFILE_ID, i);
            assertTrue(getProfileAPI().searchProfileEntries(builder.done()).getCount() == 0);
        }

        builder = new SearchOptionsBuilder(0, 10);
        builder.sort(ProfileEntrySearchDescriptor.NAME, Order.ASC);
        builder.filter(ProfileEntrySearchDescriptor.PROFILE_ID, newId1);
        final List<ProfileEntry> searchedProfileEntriesRes2 = getProfileAPI().searchProfileEntries(builder.done()).getResult();
        assertNotNull(searchedProfileEntriesRes2);
        assertEquals(1, searchedProfileEntriesRes2.size());
        assertEquals("Home", searchedProfileEntriesRes2.get(0).getName());
        assertEquals("My team activitys dashboard", searchedProfileEntriesRes2.get(0).getDescription());
        assertEquals("CurrentUserTeamTasksDashboard", searchedProfileEntriesRes2.get(0).getType());

        // check profile mapping
        final SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
        searchOptionsBuilder.filter(ProfileMemberSearchDescriptor.PROFILE_ID, newId1);
        final SearchResult<ProfileMember> searchpms = getProfileAPI().searchProfileMembers("user", searchOptionsBuilder.done());
        assertEquals(2, searchpms.getCount());
        assertEquals(user1.getId(), searchpms.getResult().get(0).getUserId());
        assertEquals(user2.getId(), searchpms.getResult().get(1).getUserId());
        assertEquals(newId1, searchpms.getResult().get(0).getProfileId());
        assertEquals(newId1, searchpms.getResult().get(1).getProfileId());

        for (final long i : Arrays.asList(olderid1, olderid2, olderid3, olderid4)) {
            final SearchOptionsBuilder searchOptionsBuilderI = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
            searchOptionsBuilderI.filter(ProfileMemberSearchDescriptor.PROFILE_ID, i);
            final SearchResult<ProfileMember> searchpms1 = getProfileAPI().searchProfileMembers("user", searchOptionsBuilderI.done());
            assertEquals(0, searchpms1.getCount());
        }
    }

    @Cover(classes = ProfileAPI.class, concept = BPMNConcept.PROFILE, keywords = { "Profile", "Export", "Wrong parameter" }, story = "Execute profile export  with wrong parameter", jira = "ENGINE-586")
    @Test(expected = ExecutionException.class)
    public void exportProfilesWithIdsSpecifiedWithWrongParameter() throws Exception {
        final long[] profileIds = { 541646L };
        getProfileAPI().exportProfilesWithIdsSpecified(profileIds);
    }

}
