package specs.SystemAdmin.WorkflowEmailList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.testng.annotations.*;
import org.testng.Assert;

import org.openqa.selenium.By;
import pageobjects.SystemAdmin.WorkflowEmailList.WorkflowEmailAdd;
import specs.AbstractSpec;
import pageobjects.LoginPage.LoginPage;
import pageobjects.Dashboard.Dashboard;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by philipsushkov on 2017-03-06.
 */

public class CheckWorkflowEmailAdd extends AbstractSpec {
    private static By systemAdminMenuButton, workflowEmailListMenuItem;
    private static LoginPage loginPage;
    private static Dashboard dashboard;
    private static WorkflowEmailAdd workflowEmailAdd;

    private static String sPathToFile, sDataFileJson;
    private static JSONParser parser;

    private final String DATA="getData", WORKFLOW_EMAIL_NAME="description", PAGE_NAME="Workflow Email";

    @BeforeTest
    public void setUp() throws Exception {
        systemAdminMenuButton = By.xpath(propUISystemAdmin.getProperty("btnMenu_SystemAdmin"));
        workflowEmailListMenuItem = By.xpath(propUISystemAdmin.getProperty("itemMenu_WorkflowEmailList"));

        loginPage = new LoginPage(driver);
        dashboard = new Dashboard(driver);
        workflowEmailAdd = new WorkflowEmailAdd(driver);

        sPathToFile = System.getProperty("user.dir") + propUISystemAdmin.getProperty("dataPath_WorkflowEmailList");
        sDataFileJson = propUISystemAdmin.getProperty("json_WorkflowEmailData");

        parser = new JSONParser();

        loginPage.loginUser();
    }

    @BeforeMethod
    public void beforeMethod() throws Exception {
        dashboard.openPageFromMenu(systemAdminMenuButton, workflowEmailListMenuItem);
    }

    @Test(dataProvider=DATA, priority=1)
    public void saveWorkflowEmail(JSONObject data) {
        String sWorkflowEmailName = data.get(WORKFLOW_EMAIL_NAME).toString();
        String expectedTitleList = "Workflow Email List";
        String expectedTitleEdit = "Workflow Email Edit";

        Assert.assertEquals(workflowEmailAdd.getTitle(), expectedTitleEdit, "Actual "+PAGE_NAME+" Edit page Title doesn't match to expected");
        Assert.assertEquals(workflowEmailAdd.saveWorkflowEmail(data, sWorkflowEmailName), expectedTitleList, "New "+PAGE_NAME+" didn't save properly");
    }

    @Test(dataProvider=DATA, priority=2)
    public void checkWorkflowEmail(JSONObject data) {
        String sWorkflowEmailName = data.get(WORKFLOW_EMAIL_NAME).toString();
        Assert.assertTrue(workflowEmailAdd.checkWorkflowEmail(data, sWorkflowEmailName), "New "+PAGE_NAME+" doesn't fit to entry data (after Save)");
    }

    @Test(dataProvider=DATA, priority=3)
    public void editWorkflowEmail(JSONObject data) throws Exception {
        String sWorkflowEmailName = data.get(WORKFLOW_EMAIL_NAME).toString();
        Assert.assertTrue(workflowEmailAdd.editWorkflowEmail(data, sWorkflowEmailName), PAGE_NAME+" didn't change properly (after Save)");
    }

    @Test(dataProvider=DATA, priority=4)
    public void checkWorkflowEmailCh(JSONObject data) {
        String sWorkflowEmailName = data.get(WORKFLOW_EMAIL_NAME).toString();
        Assert.assertTrue(workflowEmailAdd.checkWorkflowEmailCh(data, sWorkflowEmailName), "New "+PAGE_NAME+" doesn't fit to change data (after Edit)");
    }

    @Test(dataProvider=DATA, priority=5)
    public void removeWorkflowEmail(JSONObject data) {
        String sWorkflowEmailName = data.get(WORKFLOW_EMAIL_NAME).toString();
        Assert.assertTrue(workflowEmailAdd.removeWorkflowEmail(sWorkflowEmailName), "New "+PAGE_NAME+" shouldn't be shown in "+PAGE_NAME+" List (after Delete)");
    }

    @DataProvider
    public Object[][] getData() {

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(sPathToFile + sDataFileJson));
            JSONArray jsonArray = (JSONArray) jsonObject.get("workflow_email");
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
