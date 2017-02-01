package specs.PageAdmin;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Assert;

import org.openqa.selenium.By;
import pageobjects.Dashboard.Dashboard;
import pageobjects.LoginPage.LoginPage;
import pageobjects.PageAdmin.PageAdd;
import pageobjects.PageAdmin.WorkflowState;
import specs.AbstractSpec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by philipsushkov on 2017-01-24.
 */

public class CheckPageAdd extends AbstractSpec {

    private static By pageAdminMenuButton;
    private static LoginPage loginPage;
    private static Dashboard dashboard;
    private static PageAdd pageAdd;

    private static String sPathToFile, sDataFileJson;
    private static JSONParser parser;

    @BeforeTest
    public void setUp() throws Exception {
        pageAdminMenuButton = By.xpath(propUIPageAdmin.getProperty("btnMenu_PageAdmin"));

        loginPage = new LoginPage(driver);
        dashboard = new Dashboard(driver);
        pageAdd = new PageAdd(driver);

        sPathToFile = System.getProperty("user.dir") + propUIPageAdmin.getProperty("dataPath_PageAdmin");
        sDataFileJson = propUIPageAdmin.getProperty("json_CreatePageData");

        parser = new JSONParser();

        loginPage.loginUser();
    }

    @Test(dataProvider="pageData", priority=1)
    public void checkAddPage(JSONObject page) throws Exception {
        String stateProgress = WorkflowState.IN_PROGRESS.state();
        String stateApproval = WorkflowState.FOR_APPROVAL.state();
        String stateLive = WorkflowState.LIVE.state();
        String pageName = page.get("section_title").toString();

        dashboard.openPageFromCommonTasks(pageAdminMenuButton);

        Assert.assertEquals(pageAdd.createNewPage(page, pageName), stateProgress, "New Page didn't create properly");
        Assert.assertTrue(pageAdd.previewNewPage(), "Preview of New Page didn't work properly");
        Assert.assertTrue(pageAdd.publicNewPage(), "Public site shouldn't show the content of New Page when it is not published yet");

        dashboard.openPageFromCommonTasks(pageAdminMenuButton);
        Assert.assertTrue(pageAdd.listNewPage(), "New Page isn't found on Public Page List");

        Assert.assertEquals(pageAdd.saveAndSubmitNewPage(page, pageName), stateApproval, "Couldn't submit New Page");
        Assert.assertEquals(pageAdd.publishNewPage(pageName), stateLive, "Couldn't publish New Page properly");
    }

    @Test(dataProvider="pageData", priority=2)
    public void checkEditPage(JSONObject page) throws Exception {
        String stateApproval = WorkflowState.FOR_APPROVAL.state();
        String pageName = page.get("section_title").toString();

        dashboard.openPageFromCommonTasks(pageAdminMenuButton);

        Assert.assertEquals(pageAdd.changePage(page, pageName), stateApproval, "Some fields of New Page didn't changed properly");
    }

    @Test(dataProvider="pageData", priority=3)
    public void checkDeletePage(JSONObject page) throws Exception {
        String currentContent = WorkflowState.DELETE_PENDING.state();
        String workflowState = WorkflowState.NEW_ITEM.state();
        String pageName = page.get("section_title").toString();

        dashboard.openPageFromCommonTasks(pageAdminMenuButton);

        Assert.assertEquals(pageAdd.setupAsDeletedPage(pageName), currentContent, "New Page didn't setup as Deleted properly");
        Assert.assertEquals(pageAdd.removePage(pageName), workflowState, "Couldn't remove New Page. Something went wrong.");
    }

    @DataProvider
    public Object[][] pageData() {

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(sPathToFile + sDataFileJson));
            JSONArray pageData = (JSONArray) jsonObject.get("pages");
            Object[][] pages = new Object[pageData.size()][1];

            for (int i = 0; i < pageData.size(); i++) {
                pages[i][0] = pageData.get(i);
            }

            return pages;

        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @AfterTest
    public void tearDown() {
        dashboard.logoutFromAdmin();
        //Functions.ClearJSONfile(sPathToFile + sNewPagesJson);
        driver.quit();
    }

}
