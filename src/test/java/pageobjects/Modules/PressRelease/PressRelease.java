package pageobjects.Modules.PressRelease;

import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import pageobjects.AbstractPageObject;
import pageobjects.PageAdmin.WorkflowState;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static specs.AbstractSpec.*;
import static specs.AbstractSpec.desktopUrl;

/**
 * Created by philipsushkov on 2017-06-12.
 */

public class PressRelease extends AbstractPageObject {
    private static By addNewModuleBtn, backBtn, moduleTitleInput, moduleDefinitionSelect, includeLegacyModulesChk;
    private static By publishBtn, saveBtn, workflowStateSpan, currentContentSpan, propertiesHref, previewLnk;
    private static By commentsTxt, deleteBtn, saveAndSubmitBtn, regionNameSelect;
    private static By livePageTitle, liveModuleTitleSpan, livePressReleaseModulePageMenuBtn;
    private static String sPathToPageFile, sFilePageJson, sPathToModuleFile, sFileModuleJson;
    private static JSONParser parser;
    private static final long DEFAULT_PAUSE = 2500;

    public PressRelease(WebDriver driver) {
        super(driver);

        addNewModuleBtn = By.xpath(propUIModules.getProperty("btn_AddNewModule"));
        moduleTitleInput = By.xpath(propUIModules.getProperty("input_ModuleTitle"));
        moduleDefinitionSelect = By.xpath(propUIModules.getProperty("select_ModuleDefinition"));
        includeLegacyModulesChk = By.xpath(propUIModules.getProperty("chk_IncludeLagacyModules"));
        regionNameSelect = By.xpath(propUIModules.getProperty("select_RegionName"));
        workflowStateSpan = By.xpath(propUIPageAdmin.getProperty("select_WorkflowState"));
        commentsTxt = By.xpath(propUIPageAdmin.getProperty("txtarea_Comments"));
        currentContentSpan = By.xpath(propUIPageAdmin.getProperty("span_CurrentContent"));
        propertiesHref = By.xpath(propUIModules.getProperty("href_Properties"));
        previewLnk = By.xpath(propUIModules.getProperty("lnk_Preview"));

        saveBtn = By.xpath(propUIPageAdmin.getProperty("btn_Save"));
        deleteBtn = By.xpath(propUIPageAdmin.getProperty("btn_Delete"));
        publishBtn = By.xpath(propUIPageAdmin.getProperty("btn_Publish"));
        backBtn = By.xpath(propUIPageAdmin.getProperty("btn_Back"));
        saveAndSubmitBtn = By.xpath(propUIPageAdmin.getProperty("btn_SaveAndSubmit"));

        livePageTitle = By.xpath(propUIModules.getProperty("page_Title"));
        liveModuleTitleSpan = By.xpath(propUIModules.getProperty("span_LiveModuleTitle"));
        livePressReleaseModulePageMenuBtn = By.xpath(propUIModules.getProperty("btnMenu_LivePressReleaseModulePage"));

        sPathToPageFile = System.getProperty("user.dir") + propUIModules.getProperty("dataPath_Modules");
        sFilePageJson = propUIModules.getProperty("json_PagesProp");
        sPathToModuleFile = System.getProperty("user.dir") + propUIModulesPressRelease.getProperty("dataPath_PressRelease");
        sFileModuleJson = propUIModulesPressRelease.getProperty("json_PressReleaseProp");

        parser = new JSONParser();
    }


    public String saveAndSubmitModule(JSONObject modulesDataObj, String moduleName) throws InterruptedException {

        try {
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToModuleFile + sFileModuleJson));
            JSONArray jsonArrProp = (JSONArray) modulesDataObj.get("properties");

            String moduleUrl = getModuleUrl(jsonObj, moduleName);
            driver.get(moduleUrl);
            Thread.sleep(DEFAULT_PAUSE);

            waitForElement(commentsTxt);

            JSONObject module = (JSONObject) jsonObj.get(moduleName);

            findElement(propertiesHref).click();
            Thread.sleep(DEFAULT_PAUSE);

            for (int i=0; i<jsonArrProp.size(); i++) {
                try {
                    By propertyTextValue = By.xpath("//td[contains(@class, 'DataGridItemBorderLeft')][(text()='" + jsonArrProp.get(i).toString().split(";")[0] + "')]/parent::tr/td/div/input[contains(@id, 'txtStatic')]");
                    findElement(propertyTextValue).clear();
                    findElement(propertyTextValue).sendKeys(jsonArrProp.get(i).toString().split(";")[1]);
                } catch (ElementNotFoundException e) {
                    By propertyDropdownValue = By.xpath("//td[contains(@class, 'DataGridItemBorderLeft')][(text()='" + jsonArrProp.get(i).toString().split(";")[0] + "')]/parent::tr/td/div/select[contains(@id, 'ddlDynamic')]");
                    findElement(propertyDropdownValue).sendKeys(jsonArrProp.get(i).toString().split(";")[1]);
                }
            }

            findElement(commentsTxt).sendKeys(modulesDataObj.get("comment").toString());
            findElement(saveAndSubmitBtn).click();
            Thread.sleep(DEFAULT_PAUSE);

            driver.get(moduleUrl);
            Thread.sleep(DEFAULT_PAUSE);

            module.put("properties", jsonArrProp);
            module.put("workflow_state", WorkflowState.FOR_APPROVAL.state());

            jsonObj.put(moduleName, module);

            FileWriter file = new FileWriter(sPathToModuleFile + sFileModuleJson);
            file.write(jsonObj.toJSONString().replace("\\", ""));
            file.flush();

            System.out.println(moduleName+ ": New "+moduleName+" has been submitted");
            return findElement(workflowStateSpan).getText();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String publishModule(String moduleName) throws InterruptedException {
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToModuleFile + sFileModuleJson));

            String moduleUrl = getModuleUrl(jsonObj, moduleName);
            driver.get(moduleUrl);
            Thread.sleep(DEFAULT_PAUSE);

            waitForElement(publishBtn);
            findElement(publishBtn).click();
            Thread.sleep(DEFAULT_PAUSE*2);

            driver.get(moduleUrl);
            Thread.sleep(DEFAULT_PAUSE);

            JSONObject moduleObj = (JSONObject) jsonObj.get(moduleName);

            moduleObj.put("workflow_state", WorkflowState.LIVE.state());

            jsonObj.put(moduleName, moduleObj);

            FileWriter file = new FileWriter(sPathToModuleFile + sFileModuleJson);
            file.write(jsonObj.toJSONString().replace("\\", ""));
            file.flush();


            Thread.sleep(DEFAULT_PAUSE*2);
            driver.navigate().refresh();

            System.out.println("New "+moduleName+" has been published");
            return findElement(workflowStateSpan).getText();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String setupAsDeletedModule(String moduleName) throws InterruptedException {
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToModuleFile + sFileModuleJson));

            String moduleUrl = getModuleUrl(jsonObj, moduleName);
            driver.get(moduleUrl);
            Thread.sleep(DEFAULT_PAUSE);

            waitForElement(commentsTxt);
            findElement(commentsTxt).sendKeys("Removing the module");
            findElement(deleteBtn).click();

            Thread.sleep(DEFAULT_PAUSE);

            driver.get(moduleUrl);
            waitForElement(currentContentSpan);

            JSONObject moduleObj = (JSONObject) jsonObj.get(moduleName);

            moduleObj.put("workflow_state", WorkflowState.FOR_APPROVAL.state());

            jsonObj.put(moduleName, moduleObj);

            FileWriter file = new FileWriter(sPathToModuleFile + sFileModuleJson);
            file.write(jsonObj.toJSONString().replace("\\", ""));
            file.flush();

            System.out.println(moduleName+ ": Module set up as deleted");
            return findElement(currentContentSpan).getText();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String removeModule(JSONObject modulesDataObj, String moduleName) throws InterruptedException {
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToModuleFile + sFileModuleJson));

            String moduleUrl = getModuleUrl(jsonObj, moduleName);
            driver.get(moduleUrl);
            Thread.sleep(DEFAULT_PAUSE);

            if (findElement(currentContentSpan).getText().equals(WorkflowState.DELETE_PENDING.state())) {

                waitForElement(commentsTxt);
                findElement(commentsTxt).sendKeys("Approving the module removal");
                findElement(publishBtn).click();

                Thread.sleep(DEFAULT_PAUSE*2);

                driver.get(moduleUrl);
                Thread.sleep(DEFAULT_PAUSE);

                jsonObj.remove(moduleName);

                FileWriter file = new FileWriter(sPathToModuleFile + sFileModuleJson);
                file.write(jsonObj.toJSONString().replace("\\", ""));
                file.flush();

                Thread.sleep(DEFAULT_PAUSE*2);
                driver.navigate().refresh();

                System.out.println(moduleName+ ": Module has been removed");
                return findElement(workflowStateSpan).getText();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String openModulePreview(String moduleName) {
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToModuleFile + sFileModuleJson));
            String moduleURL = getModuleUrl(jsonObj, moduleName);
            driver.get(moduleURL);

            findElement(previewLnk).click();

            Thread.sleep(DEFAULT_PAUSE);

            ArrayList<String> tabs = new ArrayList<> (driver.getWindowHandles());
            driver.switchTo().window(tabs.get(1));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return driver.getTitle();
    }

    public String openModuleLiveSite(String moduleName) {
        try {
            ((JavascriptExecutor)driver).executeScript("window.open();");

            Thread.sleep(DEFAULT_PAUSE);

            ArrayList<String> tabs = new ArrayList<> (driver.getWindowHandles());
            driver.switchTo().window(tabs.get(1));

            JSONObject jsonObj = (JSONObject)  parser.parse(new FileReader(sPathToPageFile + sFilePageJson));
            String moduleURL = JsonPath.read(jsonObj, "$.['"+moduleName+"'].your_page_url");
            driver.get(moduleURL);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return driver.getTitle();
    }

    public void closeWindow() {
        try {
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());

            driver.switchTo().window(tabs.get(1)).close();
            Thread.sleep(DEFAULT_PAUSE);
            driver.switchTo().window(tabs.get(0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getPageUrl(JSONObject obj, String moduleName) {
        String  sItemID = JsonPath.read(obj, "$.['"+moduleName+"'].url_query.ItemID");
        String  sLanguageId = JsonPath.read(obj, "$.['"+moduleName+"'].url_query.LanguageId");
        String  sSectionId = JsonPath.read(obj, "$.['"+moduleName+"'].url_query.SectionId");
        return desktopUrl.toString()+"default.aspx?ItemID="+sItemID+"&LanguageId="+sLanguageId+"&SectionId="+sSectionId;
    }

    private String getModuleUrl(JSONObject obj, String moduleName) {
        String  sItemID = JsonPath.read(obj, "$.['"+moduleName+"'].url_query.ItemId");
        String  sLanguageId = JsonPath.read(obj, "$.['"+moduleName+"'].url_query.LanguageId");
        String  sSectionId = JsonPath.read(obj, "$.['"+moduleName+"'].url_query.SectionId");
        return desktopUrl.toString()+"default.aspx?ItemID="+sItemID+"&LanguageId="+sLanguageId+"&SectionId="+sSectionId;
    }
}

