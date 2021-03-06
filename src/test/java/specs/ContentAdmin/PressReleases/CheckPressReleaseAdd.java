package specs.ContentAdmin.PressReleases;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;
import pageobjects.ContentAdmin.PressReleases.PressReleaseAdd;
import pageobjects.Dashboard.Dashboard;
import pageobjects.LoginPage.LoginPage;
import pageobjects.PageAdmin.WorkflowState;
import specs.AbstractSpec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by charleszheng on 2017-09-27.
 */

public class CheckPressReleaseAdd extends AbstractSpec {
    private static By contentAdminMenuButton, PressReleasesMenuItem;
    private static LoginPage loginPage;
    private static Dashboard dashboard;
    private static PressReleaseAdd pressReleaseAdd;

    private static String sPathToFile, sDataFileJson, sPressReleaseTitle;
    private static JSONParser parser;

    private final String DATA="getData", PAGE_NAME="Press Release";

    @BeforeTest
    public void setUp() throws Exception {
        contentAdminMenuButton = By.xpath(propUIContentAdmin.getProperty("btnMenu_ContentAdmin"));
        PressReleasesMenuItem = By.xpath(propUIContentAdmin.getProperty("btnMenu_PressReleases"));

        loginPage = new LoginPage(driver);
        dashboard = new Dashboard(driver);
        pressReleaseAdd = new PressReleaseAdd(driver);

        sPathToFile = System.getProperty("user.dir") + propUIContentAdmin.getProperty("dataPath_PressReleaseList");
        sDataFileJson = propUIContentAdmin.getProperty("json_PressReleaseData");

        parser = new JSONParser();

        loginPage.loginUser();
    }

    @BeforeMethod
    public void beforeMethod() throws Exception {
        dashboard.openPageFromMenu(contentAdminMenuButton, PressReleasesMenuItem);
    }

    @Test(dataProvider=DATA, priority=1, enabled=true)
    public void savePressRelease(JSONObject data) {
        String expectedTitleEdit = "Press Release Edit";

        sPressReleaseTitle = data.get("pressrelease_headline").toString();
        Assert.assertEquals(pressReleaseAdd.getTitle(), expectedTitleEdit, "Actual "+PAGE_NAME+" Edit page Title doesn't match to expected");
        Assert.assertEquals(pressReleaseAdd.savePressRelease(data, sPressReleaseTitle), WorkflowState.IN_PROGRESS.state(), "New "+PAGE_NAME+" didn't save properly");
    }

   @Test(dataProvider=DATA, priority=2, enabled=true)
    public void saveAndSubmitPressRelease(JSONObject data) throws InterruptedException {
        sPressReleaseTitle = data.get("pressrelease_headline").toString();
        Assert.assertEquals(pressReleaseAdd.saveAndSubmitPressRelease(data, sPressReleaseTitle), WorkflowState.FOR_APPROVAL.state(), "New " + PAGE_NAME + " doesn't submit properly (after Save And Submit)");
        Assert.assertTrue(pressReleaseAdd.checkPressRelease(data, sPressReleaseTitle), "Submitted New "+ PAGE_NAME +" data doesn't fit well to entry data (after Save and Submit)");
       Assert.assertTrue(pressReleaseAdd.previewPressRelease(data, sPressReleaseTitle), "Preview of New "+ PAGE_NAME + " didn't work properly (after Save and Submit)");
       Assert.assertFalse(pressReleaseAdd.publicPressRelease(data, sPressReleaseTitle), "New "+ PAGE_NAME + " shouldn't be shown on Public site (after Save and Submit)");

   }

   @Test(dataProvider=DATA, priority=3, enabled=true)
    public void publishPressRelease(JSONObject data) throws InterruptedException {
        sPressReleaseTitle = data.get("pressrelease_headline").toString();
        Assert.assertEquals(pressReleaseAdd.publishPressRelease(data, sPressReleaseTitle), WorkflowState.LIVE.state(), "New "+ PAGE_NAME +" doesn't publish properly (after Publish)");
        Assert.assertTrue(pressReleaseAdd.publicPressRelease(data, sPressReleaseTitle), "New "+ PAGE_NAME + " should be shown on Public site (after Publish)");
    }

    @Test(dataProvider=DATA, priority=4)
    public void revertPressRelease(JSONObject data) throws InterruptedException {
        sPressReleaseTitle = data.get("pressrelease_headline").toString();
        Assert.assertEquals(pressReleaseAdd.changeAndSubmitPressRelease(data, sPressReleaseTitle), WorkflowState.FOR_APPROVAL.state(), "Some fields of New "+ PAGE_NAME +" didn't change properly (after Change and Submit)");
        Assert.assertEquals(pressReleaseAdd.revertToLivePressRelease(sPressReleaseTitle), WorkflowState.LIVE.state(), "Couldn't revert to Live changes for New "+ PAGE_NAME);
        Assert.assertTrue(pressReleaseAdd.checkPressRelease(data, sPressReleaseTitle), "Reverted "+ PAGE_NAME +" data doesn't fit well to entry data (after Revert To Live)");
        Assert.assertTrue(pressReleaseAdd.previewPressRelease(data, sPressReleaseTitle), "Preview of Reverted "+ PAGE_NAME + " didn't work properly (after Revert to Live)");
        Assert.assertTrue(pressReleaseAdd.publicPressRelease(data, sPressReleaseTitle), "Reverted"+ PAGE_NAME + " should be shown on Public site (after Revert to Live)");
    }

    @Test(dataProvider=DATA, priority=5)
    public void changeAndSubmitPressRelease(JSONObject data) throws Exception {
        sPressReleaseTitle = data.get("pressrelease_headline").toString();
        Assert.assertEquals(pressReleaseAdd.changeAndSubmitPressRelease(data, sPressReleaseTitle), WorkflowState.FOR_APPROVAL.state(), "Some fields of New "+ PAGE_NAME +" didn't change properly (after Change and Submit)");
        Assert.assertTrue(pressReleaseAdd.checkPressReleaseCh(data, sPressReleaseTitle), "Submitted New "+ PAGE_NAME +" changes don't fit well to change data (after Change And Submit)");
        if (data.get("pressrelease_headline_ch") != null){
            Assert.assertTrue(pressReleaseAdd.previewPressReleaseCh(data, sPressReleaseTitle), "Preview of Changed "+ PAGE_NAME + "  didn't work properly (after Change and Submit)");
            Assert.assertFalse(pressReleaseAdd.publicPressReleaseCh(data, sPressReleaseTitle), "Changed "+ PAGE_NAME + " shouldn't be shown on Public site (after Change and Submit)");
        }
        else
        {
            Assert.assertTrue(pressReleaseAdd.previewPressRelease(data, sPressReleaseTitle), "Preview of Changed "+ PAGE_NAME + "  didn't work properly (after Change and Submit)");
            Assert.assertTrue(pressReleaseAdd.publicPressRelease(data, sPressReleaseTitle), "Changed "+ PAGE_NAME + " with unchanged headline should show on Public site (after Change and Submit)");
        }
    }

    @Test(dataProvider=DATA, priority=6)
    public void publishEditPressRelease(JSONObject data) throws InterruptedException {
        sPressReleaseTitle = data.get("pressrelease_headline").toString();
        Assert.assertEquals(pressReleaseAdd.publishPressRelease(data, sPressReleaseTitle), WorkflowState.LIVE.state(), "Changed "+ PAGE_NAME +" doesn't publish properly (after Publish)");
        if (data.get("pressrelease_headline_ch") != null) {
            Assert.assertTrue(pressReleaseAdd.publicPressReleaseCh(data, sPressReleaseTitle), "Changed " + PAGE_NAME + " should be shown on Public site (after Publish Edit)");
        }
        else
        {
            Assert.assertTrue(pressReleaseAdd.publicPressRelease(data, sPressReleaseTitle), "Changed " + PAGE_NAME + " should be shown on Public site (after Publish Edit)");
        }

    }

    @Test(dataProvider=DATA, priority=7)
    public void deletePressRelease(JSONObject data) throws Exception {
        sPressReleaseTitle = data.get("pressrelease_headline").toString();
        Assert.assertEquals(pressReleaseAdd.setupAsDeletedPressRelease(sPressReleaseTitle), WorkflowState.DELETE_PENDING.state(), "Changed "+ PAGE_NAME +" didn't setup as Deleted properly");
        if (data.get("pressrelease_headline_ch") != null) {
            Assert.assertTrue(pressReleaseAdd.previewPressReleaseCh(data, sPressReleaseTitle), "Deleted " + PAGE_NAME + " shouldn't be shown in Preview (after Delete)");
            Assert.assertTrue(pressReleaseAdd.publicPressReleaseCh(data, sPressReleaseTitle), "Deleted " + PAGE_NAME + " should be shown on Public pages (after Delete)");
        }
        else
        {
            Assert.assertTrue(pressReleaseAdd.previewPressRelease(data, sPressReleaseTitle), "Deleted " + PAGE_NAME + " shouldn't be shown in Preview (after Delete)");
            Assert.assertTrue(pressReleaseAdd.publicPressRelease(data, sPressReleaseTitle), "Deleted " + PAGE_NAME + " should be shown on Public pages (after Delete)");
        }
    }

    @Test(dataProvider=DATA, priority=8)
    public void removePressRelease(JSONObject data) throws Exception {
        sPressReleaseTitle = data.get("pressrelease_headline").toString();
        Assert.assertEquals(pressReleaseAdd.removePressRelease(data, sPressReleaseTitle), WorkflowState.NEW_ITEM.state(), "Couldn't remove New "+ PAGE_NAME +". Something went wrong.");
    }

    @DataProvider
    public Object[][] getData() {

        try {
            System.out.println(sPathToFile + sDataFileJson);
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(sPathToFile + sDataFileJson));
            JSONArray jsonArray = (JSONArray) jsonObject.get("press_release");
            ArrayList<Object> zoom = new ArrayList();

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject pageObj = (JSONObject) jsonArray.get(i);
                if (Boolean.parseBoolean(pageObj.get("do_assertions").toString())) {
                    zoom.add(jsonArray.get(i));
                }
            }

            Object[][] data = new Object[zoom.size()][1];
            for (int i = 0; i < zoom.size(); i++) {
                data[i][0] = zoom.get(i);
            }

            return data;

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
    }

}
