package testrunner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import specs.ContentAdmin.DepartmentList.CheckDepartmentList;
import specs.ContentAdmin.DownloadList.CheckDownloadList;
import specs.ContentAdmin.FaqList.CheckFaqList;
import specs.ContentAdmin.FinancialReports.CheckFinancialReports;
import specs.ContentAdmin.JobPostingList.CheckJobPostingList;
import specs.ContentAdmin.PersonList.CheckPersonList;
import specs.ContentAdmin.PressReleaseCategories.CheckPressReleaseCategories;
import specs.ContentAdmin.QuickLinkList.CheckQuickLinkList;
import specs.EmailAdmin.Compose.CheckCompose;
import specs.EmailAdmin.ManageList.CheckMailingLists;
import specs.EmailAdmin.Subscribers.CheckMailingListUsers;
import specs.SiteAdmin.AliasList.CheckAliasList;
import specs.SiteAdmin.ContentAdminEdit.CheckContentAdminEdit;
import specs.SiteAdmin.CssFileList.CheckCssFileList;
import specs.SiteAdmin.DomainList.CheckDomainList;
import specs.SiteAdmin.GlobalModuleList.CheckGlobalModuleList;
import specs.SiteAdmin.IndexContent.CheckIndexContent;
import specs.SiteAdmin.LayoutDefinitionList.CheckLayoutDefinitionList;
import specs.SiteAdmin.LinkToPageList.CheckLinkToPageList;
import specs.SiteAdmin.LookupList.CheckLookupList;
import specs.SiteAdmin.MobileLinkList.CheckMobileLinkList;
import specs.SiteAdmin.ModuleDefinitionList.CheckModuleDefinitionList;
import specs.SystemAdmin.AlertFilterList.CheckAlertFilterList;
import specs.SystemAdmin.GenericStorageList.CheckGenericStorageList;
import specs.SystemAdmin.PDFTemplateEdit.CheckPDFTemplateEdit;
import specs.SystemAdmin.SiteList.CheckSiteList;
import specs.SystemAdmin.SiteMaintenance.CheckSiteMaintenance;
import specs.SystemAdmin.UserGroupList.CheckUserGroupList;
import specs.SystemAdmin.UserList.CheckUserList;
import specs.SystemAdmin.WorkflowEmailList.CheckWorkflowEmailList;

/**
 * Created by philipsushkov on 2016-12-05.
 */


@RunWith(Suite.class)
@Suite.SuiteClasses({
    // System Admin
        CheckUserList.class,
        CheckAlertFilterList.class,
        CheckGenericStorageList.class,
        CheckPDFTemplateEdit.class,
        CheckSiteMaintenance.class,
        CheckWorkflowEmailList.class,
        CheckSiteList.class,
        CheckUserGroupList.class,

    // Site Admin
        CheckGlobalModuleList.class,
        CheckLayoutDefinitionList.class,
        CheckModuleDefinitionList.class,
        CheckCssFileList.class,
        CheckIndexContent.class,
        CheckLinkToPageList.class,
        CheckLookupList.class,
        CheckAliasList.class,
        CheckMobileLinkList.class,
        CheckDomainList.class,
        CheckContentAdminEdit.class,

    // Content Admin
        CheckFinancialReports.class,
        CheckPressReleaseCategories.class,
        CheckQuickLinkList.class,
        CheckDownloadList.class,
        CheckPersonList.class,
        CheckDepartmentList.class,
        CheckFaqList.class,
        CheckJobPostingList.class,

    // Email Admin
        CheckCompose.class,
        CheckMailingLists.class,
        CheckMailingListUsers.class
}
)

public class NavigationTest {

}