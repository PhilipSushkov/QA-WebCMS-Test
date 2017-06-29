package specs.Modules.PressRelease;

import com.mongodb.util.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.*;
import pageobjects.ContentAdmin.PressReleases.PressReleaseEdit;
import pageobjects.Dashboard.Dashboard;
import pageobjects.LoginPage.LoginPage;
import pageobjects.Modules.PressRelease.PressReleaseDetails;
import pageobjects.Modules.ModuleBase;
import pageobjects.Modules.PageForModules;
import pageobjects.PageAdmin.WorkflowState;
import specs.AbstractSpec;
import specs.Modules.util.ModuleFunctions;
import org.openqa.selenium.Keys;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dannyl on 2017-06-23.
 */
public class CheckPressReleaseDetails extends AbstractSpec{
    private static By pageAdminMenuButton;
    private static LoginPage loginPage;
    private static Dashboard dashboard;
    private static PageForModules pageForModules;
    private static PressReleaseDetails pressReleaseDetails;
    private static ModuleBase moduleBase;
    private static PressReleaseEdit pressReleaseEdit;
    private static JSONObject pressReleaseEditDetails;
    private static JSONArray pressReleasePreviewPageURL;
    private static String baseUrl, pressReleaseId, sectionId, languageId;

    private static String sPathToFile, sDataFileJson, sPathToModuleFile, sFileModuleJson;
    private static JSONParser parser = new JSONParser();

    private final String PAGE_DATA="pageData", PAGE_NAME="press_release_modules", MODULE_DATA="moduleData", MODULE_NAME="press_release_details", PRESS_RELEASE_DETAILS = "press_release_creation_details";

    @BeforeTest
    public void setUp() throws Exception {
        pageAdminMenuButton = By.xpath(propUIModulesPressRelease.getProperty("btnMenu_PageAdmin"));

        loginPage = new LoginPage(driver);
        dashboard = new Dashboard(driver);
        pageForModules = new PageForModules(driver);
        pressReleaseDetails = new PressReleaseDetails(driver);
        pressReleaseEdit = new PressReleaseEdit(driver);

        sPathToFile = System.getProperty("user.dir") + propUIModulesPressRelease.getProperty("dataPath_PressRelease");
        sDataFileJson = propUIModulesPressRelease.getProperty("json_pressReleaseDetailsData");
        sPathToModuleFile = System.getProperty("user.dir") + propUIModulesPressRelease.getProperty("dataPath_PressRelease");
        sFileModuleJson = propUIModulesPressRelease.getProperty("json_pressReleaseDetailsProp");
        try
        {
            Object obj = parser.parse(new FileReader("/Users/dannyl/Documents/QA-WebCMS-Test/src/test/java/pageobjects/Modules/PressRelease/json/pressReleaseDetailsData.json"));
            JSONObject jsonObject = (JSONObject) obj;
            pressReleasePreviewPageURL = (JSONArray) jsonObject.get("url_query");
           JSONObject pressReleasePreviewPageURLDetails = (JSONObject) pressReleasePreviewPageURL.get(0);
            baseUrl = pressReleasePreviewPageURLDetails.get("BaseUrl").toString();
            sectionId = pressReleasePreviewPageURLDetails.get("SectionID").toString();
            languageId = pressReleasePreviewPageURLDetails.get("LanguageID").toString();
            pressReleaseId = pressReleasePreviewPageURLDetails.get("ItemID").toString();
        }
        catch (Exception e)
        {
            System.out.println("Failed to read data.");
        }

        moduleBase = new ModuleBase(driver, sPathToModuleFile, sFileModuleJson);

        parser = new JSONParser();

        loginPage.loginUser();

    }

    @BeforeMethod
    public void beforeMethod() throws Exception {
        dashboard.openPageFromCommonTasks(pageAdminMenuButton);
    }

    @Test(dataProvider=PAGE_DATA, priority=1, enabled=false)
    public void createPressReleaseDetailsPage(JSONObject page) throws InterruptedException {
        Assert.assertEquals(pageForModules.savePage(page, MODULE_NAME), WorkflowState.IN_PROGRESS.state(), "New "+MODULE_NAME+" Page didn't save properly");
        Assert.assertEquals(pageForModules.saveAndSubmitPage(page, MODULE_NAME), WorkflowState.FOR_APPROVAL.state(), "Couldn't submit New "+MODULE_NAME+" Page properly");
        Assert.assertEquals(pageForModules.publishPage(MODULE_NAME), WorkflowState.LIVE.state(), "Couldn't publish New "+MODULE_NAME+" Page properly");
    }

    @Test(dataProvider=MODULE_DATA, priority=2, enabled=false)
    public void createPressReleaseDetailsModule(JSONObject module) throws InterruptedException {
        String sModuleNameSet = module.get("module_title").toString();
        Assert.assertEquals(moduleBase.saveModule(module, MODULE_NAME), WorkflowState.IN_PROGRESS.state(), "New "+sModuleNameSet+" Module didn't save properly");
        Assert.assertEquals(pressReleaseDetails.saveAndSubmitModule(module, sModuleNameSet), WorkflowState.FOR_APPROVAL.state(), "Couldn't submit New "+sModuleNameSet+" Module properly");
        Assert.assertEquals(moduleBase.publishModule(sModuleNameSet), WorkflowState.LIVE.state(), "Couldn't publish New "+sModuleNameSet+" Module properly");
    }

    @Test(dataProvider=MODULE_DATA, priority=3, enabled=true)
    public void checkPressReleaseDetailsPreview(JSONObject module) throws InterruptedException {
    String url = "https://" + baseUrl + "PressReleaseId=" + pressReleaseId + "&LanguageId=" + languageId + "&SectionId=" + sectionId;
        ((JavascriptExecutor)driver).executeScript("window.open();");

        ArrayList<String> tabs = new ArrayList<> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        driver.get(url);
        try {
            String sModuleNameSet = module.get("module_title").toString();
            //Assert.assertTrue(moduleBase.openModulePreview(sModuleNameSet).contains(MODULE_NAME),"Did not open correct page");

            JSONArray expectedResults = (JSONArray) module.get("expected");
            for (Object expected : expectedResults) {
                String sExpected = expected.toString();
                Assert.assertTrue(ModuleFunctions.checkExpectedValue(driver, sExpected, module, sPathToModuleFile + sFileModuleJson, propUIModulesPressRelease),
                        "Did not find correct " + sExpected.split(";")[0] + " at item " + sExpected.split(";")[1]);
            }
        } finally {
            moduleBase.closeWindow();
        }
    }

    @Test(dataProvider=MODULE_DATA, priority=4, enabled=true)
    public void checkPressReleaseDetailsLive(JSONObject module) throws InterruptedException {

        try {
            Assert.assertTrue(moduleBase.openModuleLiveSite(MODULE_NAME).contains(MODULE_NAME),"Did not open correct page");

            JSONArray expectedResults = (JSONArray) module.get("expected");
            for (Object expected : expectedResults) {
                String sExpected = expected.toString();
                Assert.assertTrue(ModuleFunctions.checkExpectedValue(driver, sExpected, module, sPathToModuleFile + sFileModuleJson, propUIModulesPressRelease),
                        "Did not find correct " + sExpected.split(";")[0] + " at item " + sExpected.split(";")[1]);
            }
        } finally {
            moduleBase.closeWindow();
        }
    }

    @Test(dataProvider=MODULE_DATA, priority=5, enabled=true)
    public void removePressReleaseDetailsModule(JSONObject module) throws Exception {
        String sModuleNameSet = module.get("module_title").toString();
        Assert.assertEquals(moduleBase.setupAsDeletedModule(sModuleNameSet), WorkflowState.DELETE_PENDING.state(), "New "+sModuleNameSet+" Module didn't setup as Deleted properly");
        Assert.assertEquals(moduleBase.removeModule(module, sModuleNameSet), WorkflowState.NEW_ITEM.state(), "Couldn't remove "+sModuleNameSet+" Module. Something went wrong.");
    }

    @Test(dataProvider=PAGE_DATA, priority=6, enabled=false)
    public void removePressReleaseDetailsPage(JSONObject page) throws Exception {
        Assert.assertEquals(pageForModules.setupAsDeletedPage(MODULE_NAME), WorkflowState.DELETE_PENDING.state(), "New "+MODULE_NAME+" Page didn't setup as Deleted properly");
        Assert.assertEquals(pageForModules.removePage(page, MODULE_NAME), WorkflowState.NEW_ITEM.state(), "Couldn't remove "+MODULE_NAME+" Page. Something went wrong.");
    }

    @DataProvider
    public Object[][] moduleData() {

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(sPathToFile + sDataFileJson));
            JSONArray moduleData = (JSONArray) jsonObject.get(MODULE_NAME);
            ArrayList<Object> zoom = new ArrayList();

            for (int i = 0; i < moduleData.size(); i++) {
                JSONObject moduleObj = (JSONObject) moduleData.get(i);
                if (Boolean.parseBoolean(moduleObj.get("do_assertions").toString())) {
                    zoom.add(moduleData.get(i));
                }
            }

            Object[][] newModules = new Object[zoom.size()][1];
            for (int i = 0; i < zoom.size(); i++) {
                newModules[i][0] = zoom.get(i);
            }

            return newModules;

        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @DataProvider
    public Object[][] pageData() {

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(sPathToFile + sDataFileJson));
            JSONArray pageData = (JSONArray) jsonObject.get(PAGE_NAME);
            ArrayList<Object> zoom = new ArrayList();

            for (int i = 0; i < pageData.size(); i++) {
                JSONObject pageObj = (JSONObject) pageData.get(i);
                if (Boolean.parseBoolean(pageObj.get("do_assertions").toString())) {
                    zoom.add(pageData.get(i));
                }
            }

            Object[][] newPages = new Object[zoom.size()][1];
            for (int i = 0; i < zoom.size(); i++) {
                newPages[i][0] = zoom.get(i);
            }

            return newPages;

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
