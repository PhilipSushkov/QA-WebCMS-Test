package specs.ContentAdmin.DownloadList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;
import pageobjects.Dashboard.Dashboard;
import pageobjects.LoginPage.LoginPage;
import pageobjects.PageAdmin.WorkflowState;
import pageobjects.ContentAdmin.DownloadList.DownloadAdd;
import specs.AbstractSpec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by victorlam on 2017-09-25.
 */

public class CheckDownloadAdd extends AbstractSpec {
    private static By contentAdminMenuButton, downloadListMenuItem;
    private static LoginPage loginPage;
    private static Dashboard dashboard;
    private static DownloadAdd downloadAdd;

    private static String sPathToFile, sDataFileJson;
    private static JSONParser parser;

    private final String DATA="getData", DOWNLOAD_NAME="download_title", PAGE_NAME="Download";

    @BeforeTest
    public void setUp() throws Exception {
        contentAdminMenuButton = By.xpath(propUIContentAdmin.getProperty("btnMenu_ContentAdmin"));
        downloadListMenuItem = By.xpath(propUIContentAdmin.getProperty("btnMenu_DownloadList"));

        loginPage = new LoginPage(driver);
        dashboard = new Dashboard(driver);
        downloadAdd = new DownloadAdd(driver);

        sPathToFile = System.getProperty("user.dir") + propUIContentAdmin.getProperty("dataPath_DownloadList");
        sDataFileJson = propUIContentAdmin.getProperty("json_DownloadData");

        parser = new JSONParser();

        loginPage.loginUser();
    }

    @BeforeMethod
    public void beforeMethod() throws Exception {
        dashboard.openPageFromMenu(contentAdminMenuButton, downloadListMenuItem);
    }

    @Test(dataProvider=DATA, priority=1)
    public void saveDownload(JSONObject data) {
        String sDownloadName = data.get(DOWNLOAD_NAME).toString();
        String expectedTitleEdit = "Download Edit";

        Assert.assertEquals(downloadAdd.getTitle(), expectedTitleEdit, "Actual "+PAGE_NAME+" Edit page Title doesn't match to expected");
        Assert.assertEquals(downloadAdd.saveDownload(data, sDownloadName), WorkflowState.IN_PROGRESS.state(), "New "+PAGE_NAME+" didn't save properly");
    }

    @Test(dataProvider=DATA, priority=2)
    public void saveAndSubmitDownload(JSONObject data) throws InterruptedException {
        String sDownloadName = data.get(DOWNLOAD_NAME).toString();

        Assert.assertEquals(downloadAdd.saveAndSubmitDownload(data, sDownloadName), WorkflowState.FOR_APPROVAL.state(), "New " + PAGE_NAME + " doesn't submit properly (after Save And Submit)");
        Assert.assertTrue(downloadAdd.checkDownload(data, sDownloadName), "Submitted New "+ PAGE_NAME +" data doesn't fit well to entry data (after Save and Submit)");
    }

    @Test(dataProvider=DATA, priority=3)
    public void publishDownload(JSONObject data) throws InterruptedException {
        String sDownloadName = data.get(DOWNLOAD_NAME).toString();
        Assert.assertEquals(downloadAdd.publishDownload(data, sDownloadName), WorkflowState.LIVE.state(), "New "+ PAGE_NAME +" doesn't publish properly (after Publish)");
    }

    @Test(dataProvider=DATA, priority=4)
    public void revertDownload(JSONObject data) throws InterruptedException {
        String sDownloadName = data.get(DOWNLOAD_NAME).toString();

        Assert.assertEquals(downloadAdd.changeAndSubmitDownload(data, sDownloadName), WorkflowState.FOR_APPROVAL.state(), "Some fields of New "+ PAGE_NAME +" didn't change properly (after Save and Submit)");
        Assert.assertEquals(downloadAdd.revertToLiveDownload(sDownloadName), WorkflowState.LIVE.state(), "Couldn't revert to Live changes for New "+ PAGE_NAME);
        Assert.assertTrue(downloadAdd.checkDownload(data, sDownloadName), "Submitted New "+ PAGE_NAME +" data doesn't fit well to entry data (after Revert To Live)");
    }

    @Test(dataProvider=DATA, priority=5)
    public void changeAndSubmitDownload(JSONObject data) throws Exception {
        String sDownloadName = data.get(DOWNLOAD_NAME).toString();

        Assert.assertEquals(downloadAdd.changeAndSubmitDownload(data, sDownloadName), WorkflowState.FOR_APPROVAL.state(), "Some fields of New "+ PAGE_NAME +" didn't change properly (after Save and Submit)");
        Assert.assertTrue(downloadAdd.checkDownloadCh(data, sDownloadName), "Submitted New "+ PAGE_NAME +" changes don't fit well to change data (after Change And Submit)");
    }

    @Test(dataProvider=DATA, priority=6)
    public void publishEditDownload(JSONObject data) throws InterruptedException {
        String sDownloadName = data.get(DOWNLOAD_NAME).toString();
        Assert.assertEquals(downloadAdd.publishDownload(data, sDownloadName), WorkflowState.LIVE.state(), "New "+ PAGE_NAME +" doesn't publish properly (after Publish)");
    }

    @Test(dataProvider=DATA, priority=7)
    public void deleteDownload(JSONObject data) throws Exception {
        String sDownloadName = data.get(DOWNLOAD_NAME).toString();
        Assert.assertEquals(downloadAdd.setupAsDeletedDownload(sDownloadName), WorkflowState.DELETE_PENDING.state(), "New "+ PAGE_NAME +" didn't setup as Deleted properly");
    }

    @Test(dataProvider=DATA, priority=8)
    public void removeDownload(JSONObject data) throws Exception {
        String sDownloadName = data.get(DOWNLOAD_NAME).toString();
        Assert.assertEquals(downloadAdd.removeDownload(data, sDownloadName), WorkflowState.NEW_ITEM.state(), "Couldn't remove New "+ PAGE_NAME +". Something went wrong.");
    }

    @DataProvider
    public Object[][] getData() {

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(sPathToFile + sDataFileJson));
            JSONArray jsonArray = (JSONArray) jsonObject.get("download");
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
