package specs.Modules.Mail;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;
import pageobjects.Dashboard.Dashboard;
import pageobjects.LoginPage.LoginPage;
import pageobjects.Modules.Mail.MailingListUnsubscribeAction;
import pageobjects.Modules.ModuleBase;
import pageobjects.Modules.PageForModules;
import pageobjects.PageAdmin.WorkflowState;
import specs.AbstractSpec;
import specs.Modules.util.ModuleFunctions;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by andyp on 2017-08-21.
 */

/*
    Things to note:
        This test behaves similarly to the Mailing List Activation module test. Other than that, not much to note.
 */

public class CheckUnsubscribeAction extends AbstractSpec {
    private static By pageAdminMenuButton;
    private static LoginPage loginPage;
    private static Dashboard dashboard;
    private static PageForModules pageForModules;
    private static MailingListUnsubscribeAction mailingListUnsubscribeAction;
    private static ModuleBase moduleBase;

    private static String sPathToFile, sDataFileJson, sPathToModuleFile, sFileModuleJson;
    private static JSONParser parser;

    private static String user, password, emailSubject;

    private static By siteEmailMenuButton,systemMessagesMenuItem, subscribersMenuItem;

    private String activationURL;

    private final String MODULE_DATA="moduleData", MODULE_NAME="mailing_list_unsubscribe_action", PAGE_DATA = "pageData", PAGE_NAME = "mail_modules";

    @BeforeTest
    public void setUp() throws Exception {
        pageAdminMenuButton = By.xpath(propUIModulesMail.getProperty("btnMenu_PageAdmin"));

        loginPage = new LoginPage(driver);
        dashboard = new Dashboard(driver);
        pageForModules = new PageForModules(driver);
        mailingListUnsubscribeAction = new MailingListUnsubscribeAction(driver);

        sPathToFile = System.getProperty("user.dir") + propUIModulesMail.getProperty("dataPath_Mail");
        sDataFileJson = propUIModulesMail.getProperty("json_MailingListUnsubscribeActionData");
        sPathToModuleFile = System.getProperty("user.dir") + propUIModulesMail.getProperty("dataPath_Mail");
        sFileModuleJson = propUIModulesMail.getProperty("json_MailingListUnsubscribeActionProp");

        siteEmailMenuButton = By.xpath(propUIEmailAdmin.getProperty("btnMenu_EmailAdmin"));
        systemMessagesMenuItem = By.xpath(propUIEmailAdmin.getProperty("btnMenu_SystemMessages"));
        subscribersMenuItem = By.xpath(propUIEmailAdmin.getProperty("btnMenu_Subscribers"));

        moduleBase = new ModuleBase(driver, sPathToModuleFile, sFileModuleJson);

        parser = new JSONParser();

        user = "test@q4websystems.com";
        password = "testing!";
        emailSubject = "Ensco Website - Unsubscribe";

        deleteMail(user,password, emailSubject);
        loginPage.loginUser();
    }

    @BeforeMethod
    public void beforeMethod() throws Exception {
        dashboard.openPageFromCommonTasks(pageAdminMenuButton);
    }

    @Test(dataProvider=PAGE_DATA, priority=1, enabled=true)
    public void createMailingListUnsubscribeActionPage(JSONObject module) throws InterruptedException {
        Assert.assertEquals(pageForModules.savePage(module, MODULE_NAME), WorkflowState.IN_PROGRESS.state(), "New "+MODULE_NAME+" Page didn't save properly");
        Assert.assertEquals(pageForModules.saveAndSubmitPage(module, MODULE_NAME), WorkflowState.FOR_APPROVAL.state(), "Couldn't submit New "+MODULE_NAME+" Page properly");
        Assert.assertEquals(pageForModules.publishPage(MODULE_NAME), WorkflowState.LIVE.state(), "Couldn't publish New "+MODULE_NAME+" Page properly");
    }

    @Test(dataProvider=MODULE_DATA, priority=2, enabled=true)
    public void createMailingListUnsubscribeActionModule(JSONObject module) throws InterruptedException {
        String sModuleNameSet = module.get("module_title").toString();
        Assert.assertEquals(moduleBase.saveModule(module, MODULE_NAME), WorkflowState.IN_PROGRESS.state(), "New "+sModuleNameSet+" Module didn't save properly");
        Assert.assertEquals(mailingListUnsubscribeAction.saveAndSubmitModule(module, sModuleNameSet), WorkflowState.FOR_APPROVAL.state(), "Couldn't submit New "+sModuleNameSet+" Module properly");
        Assert.assertEquals(moduleBase.publishModule(sModuleNameSet), WorkflowState.LIVE.state(), "Couldn't publish New "+sModuleNameSet+" Module properly");
    }

    @Test(dataProvider=MODULE_DATA, priority=3, enabled=true)
    public void checkProperties(JSONObject module) throws InterruptedException {
        // Checks that all input properties were saved correctly
        Assert.assertEquals(mailingListUnsubscribeAction.goToModuleEditPage(module.get("module_title").toString()), WorkflowState.LIVE.state());
        JSONArray JSONArrProp = (JSONArray) module.get("properties");
        for (Object property : JSONArrProp) {
            String sProperty = property.toString();
            By propertyTextValue = By.xpath("//td[contains(@class, 'DataGridItemBorderLeft')][(text()='"+sProperty.split(";")[0]+"')]/parent::tr/td/div/input[contains(@id, 'txtStatic')]");
            By propertySelectValue = By.xpath("//td[contains(@class, 'DataGridItemBorderLeft')][(text()='" + sProperty.split(";")[0] + "')]/parent::tr/td/div/select[contains(@id, 'ddlDynamic')]/option[@selected]");
            if (!driver.findElements(propertyTextValue).isEmpty()) {
                Assert.assertEquals(driver.findElement(propertyTextValue).getAttribute("value"), sProperty.split(";")[1],
                        sProperty.split(";")[0] + " property did not save correctly");
            } else if (!driver.findElements(propertySelectValue).isEmpty()) {
                Assert.assertEquals(driver.findElement(propertySelectValue).getText(), sProperty.split(";")[1],
                        sProperty.split(";")[0] + " property did not save correctly");
            }
        }
    }

    @Test(dataProvider=MODULE_DATA, priority=4, enabled=true)
    public void checkMailingListUnsubscribeActionPreview(JSONObject module) throws InterruptedException, IOException, MessagingException {
        try {
            String sModuleNameSet = module.get("module_title").toString();

            // Setup unsubscribe email
            if(module.get("module_title").toString().equals("UnsubscribeModule")) {
                driver.get(desktopUrl.toString());
                dashboard.openPageFromMenu(siteEmailMenuButton, systemMessagesMenuItem);
                Assert.assertTrue(moduleBase.unsubscribeEmailSetup(MODULE_NAME, emailSubject), "Email was not setup successfully");
                dashboard.openPageFromMenu(siteEmailMenuButton, subscribersMenuItem);
                Assert.assertTrue(moduleBase.subscriberSave(user), "Subscriber was not setup successfully");
            }

            Assert.assertTrue(moduleBase.openModulePreview(sModuleNameSet).contains(MODULE_NAME),"Did not open correct page");

            JSONArray expectedResults = (JSONArray) module.get("expected");
            for (Object expected : expectedResults) {
                String sExpected = expected.toString();
                Assert.assertTrue(ModuleFunctions.checkExpectedValue(driver, sExpected, module, sPathToModuleFile + sFileModuleJson, propUIModulesMail),
                        "Did not find correct " + sExpected.split(";")[0] + " at item " + sExpected.split(";")[1]);
            }
            // Sign up if it is a sign up module
            if(module.get("module_title").toString().equals("UnsubscribeModule")) {
                Assert.assertEquals(mailingListUnsubscribeAction.unsubscribeFromMailingList(module), module.get("expected_message"), "Unsubscribing to email was not successful");
                activationURL = mailingListUnsubscribeAction.getUnsubscriptionURL(mailingListUnsubscribeAction.getEmailContent(user,password,emailSubject));
            } else {
                Assert.assertNotNull(activationURL, "URL was not correct");
                driver.get(activationURL);
                Assert.assertEquals(mailingListUnsubscribeAction.getUnsubscriptionMessage(module), module.get("expected_message"),"Unsubscription message is not correct");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            moduleBase.closeWindow();
        }
    }

    @Test(dataProvider=MODULE_DATA, priority=5, enabled=true)
    public void checkMailingListUnsubscribeActionLive(JSONObject module) throws InterruptedException, IOException, MessagingException {
        try {
            if(module.get("module_title").toString().equals("UnsubscribeModule")) {
                driver.get(desktopUrl.toString());
                dashboard.openPageFromMenu(siteEmailMenuButton, subscribersMenuItem);
                Assert.assertTrue(moduleBase.subscriberSave(user), "Subscriber was not setup successfully");
                deleteMail(user,password, emailSubject);
            }

            Assert.assertTrue(moduleBase.openModuleLiveSite(MODULE_NAME).contains(MODULE_NAME),"Did not open correct page");

            JSONArray expectedResults = (JSONArray) module.get("expected");
            for (Object expected : expectedResults) {
                String sExpected = expected.toString();
                Assert.assertTrue(ModuleFunctions.checkExpectedValue(driver, sExpected, module, sPathToModuleFile + sFileModuleJson, propUIModulesMail),
                        "Did not find correct " + sExpected.split(";")[0] + " at item " + sExpected.split(";")[1]);
            }
            // Sign up if it is a sign up module
            if(module.get("module_title").toString().equals("UnsubscribeModule")) {
                Assert.assertEquals(mailingListUnsubscribeAction.unsubscribeFromMailingList(module), module.get("expected_message"), "Unsubscribing to email was not successful");
                activationURL = mailingListUnsubscribeAction.getUnsubscriptionURL(mailingListUnsubscribeAction.getEmailContent(user,password,emailSubject));
            } else {
                Assert.assertNotNull(activationURL, "URL was not correct");
                driver.get(activationURL);
                Assert.assertEquals(mailingListUnsubscribeAction.getUnsubscriptionMessage(module), module.get("expected_message"),"Unsubscription message is not correct");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            moduleBase.closeWindow();
        }
    }

    @Test(dataProvider=MODULE_DATA, priority=6, enabled=false)
    public void removeMailingListUnsubscribeActionModule(JSONObject module) throws Exception {
        String sModuleNameSet = module.get("module_title").toString();
        Assert.assertEquals(moduleBase.setupAsDeletedModule(sModuleNameSet), WorkflowState.DELETE_PENDING.state(), "New "+sModuleNameSet+" Module didn't setup as Deleted properly");
        Assert.assertEquals(moduleBase.removeModule(module, sModuleNameSet), WorkflowState.NEW_ITEM.state(), "Couldn't remove "+sModuleNameSet+" Module. Something went wrong.");
    }

    @Test(dataProvider=PAGE_DATA, priority=7, enabled=false)
    public void removeMailingListUnsubscribeActionPage(JSONObject module) throws Exception {
        Assert.assertEquals(pageForModules.setupAsDeletedPage(MODULE_NAME), WorkflowState.DELETE_PENDING.state(), "New "+MODULE_NAME+" Page didn't setup as Deleted properly");
        Assert.assertEquals(pageForModules.removePage(module, MODULE_NAME), WorkflowState.NEW_ITEM.state(), "Couldn't remove "+MODULE_NAME+" Page. Something went wrong.");
    }

    private Object[][] genericProvider(String dataType) {
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(sPathToFile + sDataFileJson));
            JSONArray pageData = (JSONArray) jsonObject.get(dataType);
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

    @DataProvider
    public Object[][] moduleData() {
        return genericProvider(MODULE_NAME);
    }

    @DataProvider
    public Object[][] pageData() {
        return genericProvider(PAGE_NAME);
    }

    @AfterTest
    public void tearDown() {
        dashboard.logoutFromAdmin();
        //driver.quit();
    }
}
