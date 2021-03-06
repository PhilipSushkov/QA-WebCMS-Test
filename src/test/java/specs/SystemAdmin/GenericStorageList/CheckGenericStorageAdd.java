package specs.SystemAdmin.GenericStorageList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.testng.annotations.*;
import org.testng.Assert;

import org.openqa.selenium.By;
import pageobjects.PageAdmin.WorkflowState;
import pageobjects.SystemAdmin.GenericStorageList.GenericStorageAdd;
import specs.AbstractSpec;
import pageobjects.LoginPage.LoginPage;
import pageobjects.Dashboard.Dashboard;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by philipsushkov on 2017-02-15.
 */

public class CheckGenericStorageAdd extends AbstractSpec {
    private static By systemAdminMenuButton, genericStorageListMenuItem;
    private static LoginPage loginPage;
    private static Dashboard dashboard;
    private static GenericStorageAdd genericStorageAdd;

    private static String sPathToFile, sDataFileJson;
    private static JSONParser parser;

    private final String DATA="getData", STORAGE_NAME="storage_name", PAGE_NAME="Generic Storage";

    @BeforeTest
    public void setUp() throws Exception {
        systemAdminMenuButton = By.xpath(propUISystemAdmin.getProperty("btnMenu_SystemAdmin"));
        genericStorageListMenuItem = By.xpath(propUISystemAdmin.getProperty("itemMenu_GenericStorageList"));

        loginPage = new LoginPage(driver);
        dashboard = new Dashboard(driver);
        genericStorageAdd = new GenericStorageAdd(driver);

        sPathToFile = System.getProperty("user.dir") + propUISystemAdmin.getProperty("dataPath_GenericStorageList");
        sDataFileJson = propUISystemAdmin.getProperty("json_GenericStorageData");

        parser = new JSONParser();

        loginPage.loginUser();
    }

    @Test(dataProvider=DATA, priority=1)
    public void saveGenericStorage(JSONObject data) throws Exception {
        String sStorageName = data.get(STORAGE_NAME).toString();
        String expectedTitleEdit = "Generic Storage Edit";

        dashboard.openPageFromMenu(systemAdminMenuButton, genericStorageListMenuItem);
        Assert.assertEquals(genericStorageAdd.getTitle(), expectedTitleEdit, "Actual "+PAGE_NAME+" Edit page Title doesn't match to expected");

        dashboard.openPageFromMenu(systemAdminMenuButton, genericStorageListMenuItem); // Should be removed after WEB-11158 fixed
        Assert.assertEquals(genericStorageAdd.saveGenericStorage(data, sStorageName), WorkflowState.IN_PROGRESS.state(), "New " + PAGE_NAME + " didn't save properly (after Save)");
    }

    @Test(dataProvider=DATA, priority=2)
    public void saveAndSubmitGenericStorage(JSONObject data) throws Exception {
        String sStorageName = data.get(STORAGE_NAME).toString();

        dashboard.openPageFromMenu(systemAdminMenuButton, genericStorageListMenuItem);
        Assert.assertEquals(genericStorageAdd.saveAndSubmitGenericStorage(data, sStorageName), WorkflowState.FOR_APPROVAL.state(), "New " + PAGE_NAME + " doesn't submit properly (after Save And Submit)");
        Assert.assertTrue(genericStorageAdd.checkGenericStorage(data, sStorageName), "Submitted New "+ PAGE_NAME +" data doesn't fit well to entry data (after Save and Submit)");
    }

    @Test(dataProvider=DATA, priority=3)
    public void publishGenericStorage(JSONObject data) throws Exception {
        String sStorageName = data.get(STORAGE_NAME).toString();

        Assert.assertEquals(genericStorageAdd.publishGenericStorage(data, sStorageName), WorkflowState.LIVE.state(), "New "+ PAGE_NAME +" doesn't publish properly (after Publish)");
    }

    @Test(dataProvider=DATA, priority=4)
    public void revertGenericStorage(JSONObject data) throws Exception {
        String sStorageName = data.get(STORAGE_NAME).toString();

        Assert.assertEquals(genericStorageAdd.changeAndSubmitGenericStorage(data, sStorageName), WorkflowState.FOR_APPROVAL.state(), "Some fields of New "+ PAGE_NAME +" didn't change properly (after Save and Submit)");
        Assert.assertEquals(genericStorageAdd.revertToLiveGenericStorage(sStorageName), WorkflowState.LIVE.state(), "Couldn't revert to Live changes for New "+ PAGE_NAME);
        Assert.assertTrue(genericStorageAdd.checkGenericStorage(data, sStorageName), "Submitted New "+ PAGE_NAME +" data doesn't fit well to entry data (after Revert To Live)");
    }

    @Test(dataProvider=DATA, priority=5)
    public void changeAndSubmitEditGenericStorage(JSONObject data) throws Exception {
        String sStorageName = data.get(STORAGE_NAME).toString();

        Assert.assertEquals(genericStorageAdd.changeAndSubmitGenericStorage(data, sStorageName), WorkflowState.FOR_APPROVAL.state(), "Some fields of New "+ PAGE_NAME +" didn't change properly (after Save and Submit)");
        Assert.assertTrue(genericStorageAdd.checkGenericStorageCh(data, sStorageName), "Submitted New "+ PAGE_NAME +" changes don't fit well to change data (after Change And Submit)");
    }

    @Test(dataProvider=DATA, priority=6)
    public void publishEditGenericStorage(JSONObject data) throws Exception {
        String sStorageName = data.get(STORAGE_NAME).toString();
        Assert.assertEquals(genericStorageAdd.publishGenericStorage(data, sStorageName), WorkflowState.LIVE.state(), "New "+ PAGE_NAME +" doesn't publish changes properly (after Publish)");
    }

    @Test(dataProvider=DATA, priority=7)
    public void deleteGenericStorage(JSONObject data) throws Exception {
        String sStorageName = data.get(STORAGE_NAME).toString();
        Assert.assertEquals(genericStorageAdd.setupAsDeletedGenericStorage(sStorageName), WorkflowState.DELETE_PENDING.state(), "New "+ PAGE_NAME +" didn't setup as Deleted properly");
    }

    @Test(dataProvider=DATA, priority=8)
    public void removeGenericStorage(JSONObject data) throws Exception {
        String sStorageName = data.get(STORAGE_NAME).toString();
        Assert.assertEquals(genericStorageAdd.removeGenericStorage(data, sStorageName), WorkflowState.NEW_ITEM.state(), "Couldn't remove New "+ PAGE_NAME +". Something went wrong.");
    }

    @DataProvider
    public Object[][] getData() {

        try {
            FileReader readFile = new FileReader(sPathToFile + sDataFileJson);
            JSONObject jsonObject = (JSONObject) parser.parse(readFile);
            JSONArray jsonArray = (JSONArray) jsonObject.get("generic_storage");
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
        //driver.quit();
    }
}
