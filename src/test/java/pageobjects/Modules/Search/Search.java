package pageobjects.Modules.Search;

import com.jayway.jsonpath.JsonPath;
import org.apache.xpath.operations.Bool;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pageobjects.AbstractPageObject;
import pageobjects.PageAdmin.WorkflowState;
import pageobjects.PageObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static specs.AbstractSpec.*;

/**
 * Created by andyp on 2017-07-18.
 */
public class Search extends AbstractPageObject{
    private static By workflowStateSpan, propertiesHref, commentsTxt, saveAndSubmitBtn;
    private static String sPathToModuleFile, sFileModuleJson;
    private static JSONParser parser;
    private static final long DEFAULT_PAUSE = 2500;

    public Search(WebDriver driver) {
        super(driver);

        workflowStateSpan = By.xpath(propUIPageAdmin.getProperty("select_WorkflowState"));
        commentsTxt = By.xpath(propUIPageAdmin.getProperty("txtarea_Comments"));
        propertiesHref = By.xpath(propUIModules.getProperty("href_Properties"));
        saveAndSubmitBtn = By.xpath(propUIPageAdmin.getProperty("btn_SaveAndSubmit"));

        sPathToModuleFile = System.getProperty("user.dir") + propUIModulesSearch.getProperty("dataPath_Search");
        sFileModuleJson = propUIModulesSearch.getProperty("json_SearchProp");

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
                } catch (PageObject.ElementNotFoundException e) {
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

    public String goToModuleEditPage(String moduleName) {
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToModuleFile + sFileModuleJson));
            driver.get(getModuleUrl(jsonObj, moduleName));
            findElement(propertiesHref).click();
            return findElement(workflowStateSpan).getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getModuleUrl(JSONObject obj, String moduleName) {
        String  sItemID = JsonPath.read(obj, "$.['"+moduleName+"'].url_query.ItemId");
        String  sLanguageId = JsonPath.read(obj, "$.['"+moduleName+"'].url_query.LanguageId");
        String  sSectionId = JsonPath.read(obj, "$.['"+moduleName+"'].url_query.SectionId");
        return desktopUrl.toString()+"default.aspx?ItemID="+sItemID+"&LanguageId="+sLanguageId+"&SectionId="+sSectionId;
    }

    public void searchItem(JSONObject module, String searchTerm){
        //This will search
        waitForElement(By.xpath(module.get("module_path").toString() + propUIModulesSearch.getProperty("input_SearchButton")));
        findElement(By.xpath(module.get("module_path").toString() + propUIModulesSearch.getProperty("input_SearchField"))).sendKeys(searchTerm);
        findElement(By.xpath(module.get("module_path").toString() + propUIModulesSearch.getProperty("input_SearchButton"))).click();
    }

    public Boolean checkResults(){
        By summaryResults = By.xpath("//span[div/div/h2/span[text()='Search Result']]" + propUIModulesSearch.getProperty("lbl_SearchSummaryContent"));

        waitForElement(summaryResults);
        if (!findElement(summaryResults).getText().equals("No results found")){
            return true;
        }

        return false;
    }
}

