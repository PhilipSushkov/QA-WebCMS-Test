package pageobjects.Modules.Content;

import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pageobjects.AbstractPageObject;
import pageobjects.PageAdmin.WorkflowState;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import static specs.AbstractSpec.desktopUrl;
import static specs.AbstractSpec.propUIContentAdmin;
import static specs.AbstractSpec.propUIModules;

/**
 * Created by dannyl on 2017-07-18.
 */
public class CreateFastFact extends AbstractPageObject{
    private static By saveButton, saveAndSubmitButton, publishBtn, deleteBtn, addNewBtn, workflowStateSpan, commentsInput, currentContentSpan, yourPageUrl;
    private static By inputDescription;
    private static String sPathToFile, sFilePageJson;

    private static JSONParser parser;

    private static final long DEFAULT_PAUSE = 2500;
    private final String CONTENT_TYPE = "fast_fact";

    public CreateFastFact(WebDriver driver) {
        super(driver);



        commentsInput = By.xpath(propUIContentAdmin.getProperty("txtarea_UpdateComments"));
        saveButton = By.xpath(propUIContentAdmin.getProperty("btn_Save"));
        saveAndSubmitButton = By.xpath(propUIContentAdmin.getProperty("btn_SaveAndSubmit"));
        publishBtn = By.xpath(propUIContentAdmin.getProperty("btn_Publish"));
        deleteBtn = By.xpath(propUIContentAdmin.getProperty("btn_Delete"));
        addNewBtn = By.xpath(propUIContentAdmin.getProperty("input_AddNew"));
        workflowStateSpan = By.xpath(propUIContentAdmin.getProperty("span_WorkflowState"));
        currentContentSpan = By.xpath(propUIContentAdmin.getProperty("span_CurrentContent"));
        yourPageUrl = By.xpath(propUIContentAdmin.getProperty("span_YourPageUrl"));

        inputDescription = By.xpath(propUIContentAdmin.getProperty("input_description"));

        sPathToFile = System.getProperty("user.dir") + propUIModules.getProperty("dataPath_Content");
        sFilePageJson = propUIModules.getProperty("json_ContentProp");

        parser = new JSONParser();
    }

    public String saveFastFact(JSONObject data) {
        waitForElement(addNewBtn);
        findElement(addNewBtn).click();
        waitForElement(saveAndSubmitButton);

        findElement(inputDescription).sendKeys(data.get("description").toString());

        findElement(saveButton).click();

        // Write page parameters to json

        try {
            Thread.sleep(DEFAULT_PAUSE);

            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToFile + sFilePageJson));
            JSONObject fastFactObj = (JSONObject) jsonObj.get(CONTENT_TYPE);

            if (fastFactObj == null) {
                fastFactObj = new JSONObject();
            }

            JSONObject fastFact = new JSONObject();
            fastFact.put("workflow_state", WorkflowState.IN_PROGRESS.state());
            URL pageURL = new URL(getUrl());
            String[] params = pageURL.getQuery().split("&");
            JSONObject jsonURLQuery = new JSONObject();
            for (String param:params) {
                jsonURLQuery.put(param.split("=")[0], param.split("=")[1]);
            }
            fastFact.put("url_query", jsonURLQuery);
            fastFactObj.put(data.get("description").toString(), fastFact);
            jsonObj.put(CONTENT_TYPE, fastFactObj);

            try {
                FileWriter file = new FileWriter(sPathToFile + sFilePageJson);
                file.write(jsonObj.toJSONString().replace("\\", ""));
                file.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return findElement(workflowStateSpan).getText();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public String saveAndSubmitFastFact(JSONObject data) {
        try {

            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToFile + sFilePageJson));
            String fastFactUrl = getContentUrl(jsonObj, CONTENT_TYPE, data.get("description").toString());

            driver.get(fastFactUrl);
            waitForElement(saveAndSubmitButton);

            // ADD here
            findElement(commentsInput).sendKeys(data.get("comment").toString());
            findElement(saveAndSubmitButton).click();
            Thread.sleep(DEFAULT_PAUSE);

            driver.get(fastFactUrl);
            waitForElement(workflowStateSpan);

            JSONObject fastFactObj = (JSONObject) jsonObj.get(CONTENT_TYPE);
            JSONObject fastFact = (JSONObject) fastFactObj.get(data.get("description").toString());
            fastFact.put("workflow_state", WorkflowState.FOR_APPROVAL.state());
            fastFact.put("deleted", "false");

            fastFactObj.put(data.get("description").toString(), fastFact);
            jsonObj.put(CONTENT_TYPE, fastFactObj);

            FileWriter file = new FileWriter(sPathToFile + sFilePageJson);
            file.write(jsonObj.toJSONString().replace("\\", ""));
            file.flush();

            System.out.println("New "+CONTENT_TYPE+" has been submitted: " + data.get("description").toString());
            return  findElement(workflowStateSpan).getText();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String publishFastFact(String description) {
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToFile + sFilePageJson));
            String fastFactUrl = getContentUrl(jsonObj, CONTENT_TYPE, description);

            findVisibleElement(publishBtn).click();
            Thread.sleep(DEFAULT_PAUSE);

            driver.get(fastFactUrl);
            waitForElement(workflowStateSpan);

            JSONObject fastFactObj = (JSONObject) jsonObj.get(CONTENT_TYPE);
            JSONObject fastFact = (JSONObject) fastFactObj.get(description);

            fastFact.put("workflow_state", WorkflowState.LIVE.state());
            fastFact.put("deleted", "false");

            fastFactObj.put(description, fastFact);
            jsonObj.put(CONTENT_TYPE, fastFactObj);

            FileWriter file = new FileWriter(sPathToFile + sFilePageJson);
            file.write(jsonObj.toJSONString().replace("\\", ""));
            file.flush();

            Thread.sleep(DEFAULT_PAUSE*2);
            driver.navigate().refresh();

            System.out.println("New "+CONTENT_TYPE+" has been published: " + description);
            return  findElement(workflowStateSpan).getText();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String setupAsDeletedFastFact(String description) {
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToFile + sFilePageJson));
            String fastFactUrl = getContentUrl(jsonObj, CONTENT_TYPE, description);

            driver.get(fastFactUrl);
            waitForElement(commentsInput);
            findElement(commentsInput).sendKeys("Removing the "+CONTENT_TYPE);

            findElement(deleteBtn).click();
            Thread.sleep(DEFAULT_PAUSE);
            driver.get(fastFactUrl);

            JSONObject fastFactObj = (JSONObject) jsonObj.get(CONTENT_TYPE);
            JSONObject fastFact = (JSONObject) fastFactObj.get(description);

            fastFact.put("workflow_state", WorkflowState.FOR_APPROVAL.state());
            fastFact.put("deleted", "true");

            fastFactObj.put(description, fastFact);
            jsonObj.put(CONTENT_TYPE, fastFactObj);

            FileWriter file = new FileWriter(sPathToFile + sFilePageJson);
            file.write(jsonObj.toJSONString().replace("\\", ""));
            file.flush();

            System.out.println(CONTENT_TYPE+" has been set up as deleted: " + description);
            return  findElement(currentContentSpan).getText();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String removeFastFact(String description) {
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(sPathToFile + sFilePageJson));
            String fastFactUrl = getContentUrl(jsonObj, CONTENT_TYPE, description);

            driver.get(fastFactUrl);
            waitForElement(commentsInput);

            if (findElement(currentContentSpan).getText().equals(WorkflowState.DELETE_PENDING.state())) {

                findElement(commentsInput).sendKeys("Approving the " + CONTENT_TYPE + " removal");
                findElement(publishBtn).click();

                JSONObject fastFactObj = (JSONObject) jsonObj.get(CONTENT_TYPE);
                fastFactObj.remove(description);
                jsonObj.put(CONTENT_TYPE, fastFactObj);

                FileWriter file = new FileWriter(sPathToFile + sFilePageJson);
                file.write(jsonObj.toJSONString().replace("\\", ""));
                file.flush();

                Thread.sleep(DEFAULT_PAUSE * 2);
                driver.get(fastFactUrl);

                System.out.println(description + ": " + CONTENT_TYPE + " has been removed");
                return findElement(workflowStateSpan).getText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getContentUrl(JSONObject obj,String type, String contentName) {
        String  sItemID = JsonPath.read(obj, "$.['"+type+"'].['"+contentName+"'].url_query.ItemID");
        String  sLanguageId = JsonPath.read(obj, "$.['"+type+"'].['"+contentName+"'].url_query.LanguageId");
        String  sSectionId = JsonPath.read(obj, "$.['"+type+"'].['"+contentName+"'].url_query.SectionId");
        return desktopUrl.toString()+"default.aspx?ItemID="+sItemID+"&LanguageId="+sLanguageId+"&SectionId="+sSectionId;
    }
}
