package pageobjects.ContentAdmin.FaqList;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang.ObjectUtils;
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

/**
 * Created by andyp on 2017-06-20.
 */
public class FaqAdd extends AbstractPageObject {
    private static By activeChk, nameInputEN, nameInputGE, questionInput, questionsListName, questionModuleTitle;
    private static By moduleTitle, saveBtn, revertBtn, cancelBtn, deleteBtn, addNewLink, publishBtn, saveAndSubmitBtn;
    private static By switchToHtml, workflowStateSpan, commentsTxt, successMsg, currentContentSpan;
    private static By textArea, radEditor, radEditorContent;
    private static String sPathToFile, sFileJson;
    private static JSONParser parser;
    private static final long DEFAULT_PAUSE = 2500;
    private final String PAGE_NAME="Faq";

    public FaqAdd(WebDriver driver) {
        super(driver);

        activeChk = By.xpath(propUIContentAdmin.getProperty("chk_Active"));
        nameInputEN = By.xpath(propUIContentAdmin.getProperty("input_FaqName"));
        nameInputGE = By.xpath(propUIContentAdmin.getProperty("input_FaqNameGerman"));

        questionInput = By.xpath(propUIContentAdmin.getProperty("input_Question"));
        questionsListName = By.xpath(propUIContentAdmin.getProperty("span_QuestionName"));
        questionModuleTitle = By.xpath(propUIContentAdmin.getProperty("header_QuestionModuleTitle"));

        moduleTitle = By.xpath(propUIContentAdmin.getProperty("spanModule_Title"));
        saveBtn = By.xpath(propUIContentAdmin.getProperty("btn_Save"));
        revertBtn = By.xpath(propUIContentAdmin.getProperty("btn_Revert"));
        cancelBtn = By.xpath(propUIContentAdmin.getProperty("btn_Cancel"));
        deleteBtn = By.xpath(propUIContentAdmin.getProperty("btn_Delete"));
        addNewLink = By.xpath(propUIContentAdmin.getProperty("input_AddNew"));
        publishBtn = By.xpath(propUIContentAdmin.getProperty("btn_Publish"));
        saveAndSubmitBtn = By.xpath(propUIContentAdmin.getProperty("btn_SaveAndSubmit"));

        switchToHtml = By.className(propUIContentAdmin.getProperty("html_SwitchTo"));
        workflowStateSpan = By.xpath(propUIContentAdmin.getProperty("select_WorkflowState"));
        commentsTxt = By.xpath(propUIContentAdmin.getProperty("txtarea_Comments"));
        successMsg = By.xpath(propUIContentAdmin.getProperty("msg_Success"));
        currentContentSpan = By.xpath(propUIContentAdmin.getProperty("span_CurrentContent"));

        textArea = By.tagName(propUIContentAdmin.getProperty("frame_Textarea"));
        radEditor = By.xpath(propUIContentAdmin.getProperty("frame_RadEditor"));
        radEditorContent = By.xpath(propUIContentAdmin.getProperty("field_RadEContent"));

        parser = new JSONParser();

        sPathToFile = System.getProperty("user.dir") + propUIContentAdmin.getProperty("dataPath_faqList");
        sFileJson = propUIContentAdmin.getProperty("json_faqList");
    }

    public String getTitle() {
        findElement(addNewLink).click();
        waitForElement(moduleTitle);
        String sTitle = getText(moduleTitle);
        findElement(cancelBtn).click();
        return sTitle;
    }

    public String getQuestionTitle() {
        findElement(addNewLink).click();
        waitForElement(questionModuleTitle);
        String sTitle = getText(questionModuleTitle);
        findElement(cancelBtn).click();
        return sTitle;
    }

    public String saveFaq(JSONObject data, String name) {
        String name_EN, name_GE;
        Boolean active;
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonMain = new JSONObject();

        waitForElement(addNewLink);
        findElement(addNewLink).click();
        waitForElement(saveBtn);

        try {
            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
            } catch (ParseException e) {
            }

            name_EN = data.get("name_EN").toString();
            findElement(nameInputEN).clear();
            findElement(nameInputEN).sendKeys(name_EN);
            jsonObj.put("name_EN", name_EN);

            name_GE = data.get("name_GE").toString();
            findElement(nameInputGE).clear();
            findElement(nameInputGE).sendKeys(name_GE);
            jsonObj.put("name_GE", name_GE);

            // Save Active checkbox
            active = Boolean.parseBoolean(data.get("active").toString());
            jsonObj.put("active", Boolean.parseBoolean(data.get("active").toString()));
            if (active) {
                if (!Boolean.parseBoolean(findElement(activeChk).getAttribute("checked"))) {
                    findElement(activeChk).click();
                } else {
                }
            } else {
                if (!Boolean.parseBoolean(findElement(activeChk).getAttribute("checked"))) {
                } else {
                    findElement(activeChk).click();
                }
            }

            findElement(saveBtn).click();
           // Thread.sleep(DEFAULT_PAUSE);
            waitForElement(successMsg);

            jsonObj.put("workflow_state", WorkflowState.IN_PROGRESS.state());

            jsonMain.put(name, jsonObj);

            try {
                FileWriter writeFile = new FileWriter(sPathToFile + sFileJson);
                writeFile.write(jsonMain.toJSONString().replace("\\", ""));
                writeFile.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(name + ": "+PAGE_NAME+" has been saved");
            return findElement(workflowStateSpan).getText();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public Boolean saveQuestion(JSONObject data, String name) {
        String question, answer;
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonMain = new JSONObject();

        waitForElement(addNewLink);
        findElement(addNewLink).click();
        waitForElement(saveBtn);

        try {
            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
            } catch (ParseException e) {
            }

            question = data.get("question").toString();
            findElement(questionInput).clear();
            findElement(questionInput).sendKeys(question);
            jsonObj.put("question", question);

            findElement(switchToHtml).click();

            answer = data.get("answer").toString();
            driver.switchTo().frame(2);
            findElement(textArea).sendKeys(answer);
            driver.switchTo().defaultContent();
            pause(1000L);
            jsonObj.put("answer", answer);
            
            findElement(saveBtn).click();
            waitForElement(questionsListName);

            jsonMain.put(name, jsonObj);

            try {
                FileWriter writeFile = new FileWriter(sPathToFile + sFileJson);
                writeFile.write(jsonMain.toJSONString().replace("\\", ""));
                writeFile.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(name + ": Question has been created");

            return findElement(questionsListName).getText().contains(question);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public String saveAndSubmitFaq(JSONObject data, String name) throws InterruptedException {
        String workflow;
        JSONObject jsonMain = new JSONObject();
        By editBtn = By.xpath("//td[(text()='" + data.get("name_EN").toString() + "')]/parent::tr/td/input[contains(@id, 'imgEdit')][contains(@id, 'Faq')]");
        //Please don't use data with special characters ' or " in question name
        By editQuestionBtn = By.xpath("//td//input[contains(@id, 'btnEdit')][../../td/span[text()='" + data.get("question").toString()+ "']]");

        try {
            waitForElement(moduleTitle);

            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
            } catch (ParseException e) {
            }

            waitForElement(editBtn);
            findElement(editBtn).click();
            //Thread.sleep(DEFAULT_PAUSE);

            waitForElement(commentsTxt);
            findElement(commentsTxt).sendKeys(data.get("comment").toString());
            findElement(saveAndSubmitBtn).click();
            //Thread.sleep(DEFAULT_PAUSE);

            waitForElement(editBtn);
            findElement(editBtn).click();
            waitForElement(commentsTxt);

            JSONObject jsonObj = (JSONObject) jsonMain.get(name);

            jsonObj.put("workflow_state", WorkflowState.FOR_APPROVAL.state());
            jsonObj.put("deleted", "false");

            // Save Link To Page Url
            URL url = new URL(getUrl());
            String[] params = url.getQuery().split("&");
            JSONObject jsonURLQuery = new JSONObject();
            for (String param:params) {
                jsonURLQuery.put(param.split("=")[0], param.split("=")[1]);
            }
            jsonObj.put("url_query", jsonURLQuery);

            workflow = findElement(workflowStateSpan).getText();

            //Get one for Edit Question page
            findElement(editQuestionBtn).click();

            URL url2 = new URL(getUrl());
            String[] params2 = url2.getQuery().split("&");
            JSONObject jsonURLQuery2 = new JSONObject();
            for (String param:params2) {
                jsonURLQuery2.put(param.split("=")[0], param.split("=")[1]);
            }
            jsonObj.put("url_query2", jsonURLQuery2);


            jsonMain.put(name, jsonObj);

            FileWriter file = new FileWriter(sPathToFile + sFileJson);
            file.write(jsonMain.toJSONString().replace("\\", ""));
            file.flush();

            System.out.println(name+ ": "+PAGE_NAME+" has been sumbitted");
            return workflow;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Boolean checkFaq(JSONObject data, String name) throws InterruptedException {
        JSONObject jsonMain = new JSONObject();

        try {
            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
            } catch (ParseException e) {
            }

            //Causing errors
            String pageUrl = getPageUrl(jsonMain, name);
            driver.get(pageUrl);
            //Thread.sleep(DEFAULT_PAUSE);
            waitForElement(commentsTxt);
            
            // Compare field values with entry data
            try {
                if (!findElement(nameInputEN).getAttribute("value").equals(data.get("name_EN").toString())) {
                    System.out.println("Name (English) does not match data");
                    return false;
                }
            } catch (NullPointerException e) {
            }

            try {
                if (!findElement(nameInputGE).getAttribute("value").equals(data.get("name_GE").toString())) {
                    System.out.println("Name (German) does not match data");
                    return false;
                }
            } catch (NullPointerException e) {
            }
            
            try {
                if (!findElement(activeChk).getAttribute("checked").equals(data.get("active").toString())) {
                    System.out.println("Active checkbox does not match data");
                    return false;
                }
            } catch (NullPointerException e) {
            }

            String pageUrl2 = getPageUrlQuestion(jsonMain, name);
            driver.get(pageUrl2);
            waitForElement(questionInput);

            try {
                if (!findElement(questionInput).getAttribute("value").equals(data.get("question").toString())) {
                    System.out.println("Question does not match data");
                    return false;
                }
            } catch (NullPointerException e) {
            }

            try {
                driver.switchTo().frame(findElement(radEditor));
                if (!findElement(radEditorContent).getText().equals(data.get("answer").toString())) {
                    System.out.println("Answer does not match data");
                    return false;
                }
                driver.switchTo().defaultContent();
            } catch (NullPointerException e) {
            }


            System.out.println(name+ ": New "+PAGE_NAME+" has been checked");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String publishFaq(JSONObject data, String name) throws InterruptedException {
        JSONObject jsonMain = new JSONObject();
        JSONObject jsonObj = new JSONObject();

        try {
            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
                jsonObj = (JSONObject) jsonMain.get(name);
            } catch (ParseException e) {
            }

            String pageUrl = getPageUrl(jsonMain, name);
            driver.get(pageUrl);
            //Thread.sleep(DEFAULT_PAUSE);

            waitForElement(publishBtn);
            findElement(publishBtn).click();
            Thread.sleep(DEFAULT_PAUSE*2);

            driver.get(pageUrl);
            Thread.sleep(DEFAULT_PAUSE*2);
            driver.navigate().refresh();
            Thread.sleep(DEFAULT_PAUSE);

            jsonObj.put("workflow_state", WorkflowState.LIVE.state());
            jsonObj.put("deleted", "false");

            jsonMain.put(name, jsonObj);

            FileWriter file = new FileWriter(sPathToFile + sFileJson);
            file.write(jsonMain.toJSONString().replace("\\", ""));
            file.flush();

            Thread.sleep(DEFAULT_PAUSE*2);
            driver.navigate().refresh();

            System.out.println(name+ ": New "+PAGE_NAME+" has been published");
            return findElement(workflowStateSpan).getText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String revertToLiveFaq(String name) throws InterruptedException {
        JSONObject jsonMain = new JSONObject();
        JSONObject jsonObj = new JSONObject();

        try {
            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
                jsonObj = (JSONObject) jsonMain.get(name);
            } catch (ParseException e) {
            }

            String pageUrl = getPageUrl(jsonMain, name);
            driver.get(pageUrl);
            Thread.sleep(DEFAULT_PAUSE);
            waitForElement(revertBtn);

            if (jsonObj.get("workflow_state").toString().equals(WorkflowState.FOR_APPROVAL.state())) {
                findElement(revertBtn).click();
                Thread.sleep(DEFAULT_PAUSE);

                jsonObj.put("workflow_state", WorkflowState.LIVE.state());
                jsonObj.put("deleted", "false");

                jsonMain.put(name, jsonObj);

                FileWriter file = new FileWriter(sPathToFile + sFileJson);
                file.write(jsonMain.toJSONString().replace("\\", ""));
                file.flush();

                driver.get(pageUrl);
                Thread.sleep(DEFAULT_PAUSE);
                waitForElement(workflowStateSpan);

                System.out.println(name+ ": "+PAGE_NAME+" has been reverted to Live");
                return findElement(workflowStateSpan).getText();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String changeAndSubmitFaq(JSONObject data, String name) throws InterruptedException {
        JSONObject jsonMain = new JSONObject();
        JSONObject jsonObj = new JSONObject();

        try {
            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
                jsonObj = (JSONObject) jsonMain.get(name);
            } catch (ParseException e) {
            }

            // Change type
            try {
                if (!data.get("question_ch").toString().isEmpty()) {
                    //Opening question edit page
                    String pageUrl2 = getPageUrlQuestion(jsonMain, name);
                    driver.get(pageUrl2);
                    waitForElement(questionInput);

                    findElement(questionInput).clear();
                    findElement(questionInput).sendKeys(data.get("question_ch").toString());

                    jsonObj.put("question", data.get("question_ch").toString());
                }
            } catch (NullPointerException e){
            }

            try {
                if (!data.get("answer_ch").toString().isEmpty()) {
                    findElement(switchToHtml).click();
                    driver.switchTo().frame(2);
                    findElement(textArea).clear();
                    findElement(textArea).sendKeys(data.get("answer_ch").toString());
                    driver.switchTo().defaultContent();
                    jsonObj.put("answer", data.get("answer_ch").toString());

                    //Save button is in here so it only runs when question page is being changed
                    findElement(saveBtn).click();
                }
            } catch (NullPointerException e) {
            }

            String pageUrl = getPageUrl(jsonMain, name);
            driver.get(pageUrl);
            waitForElement(commentsTxt);

            try {
                if (!data.get("name_EN_ch").toString().isEmpty()) {
                    waitForElement(nameInputEN);
                    findElement(nameInputEN).clear();
                    findElement(nameInputEN).sendKeys(data.get("name_EN_ch").toString());

                    jsonObj.put("name_EN", data.get("name_EN_ch").toString());
                }
            } catch (NullPointerException e){
            }

            // Changing active
            jsonObj.put("active", Boolean.parseBoolean(data.get("active").toString()));
            try {
                // Save Active checkbox
                if (Boolean.parseBoolean(data.get("active_ch").toString())) {
                    if (!Boolean.parseBoolean(findElement(activeChk).getAttribute("checked"))) {
                        findElement(activeChk).click();
                        jsonObj.put("active", true);
                    } else {
                    }
                } else {
                    if (!Boolean.parseBoolean(findElement(activeChk).getAttribute("checked"))) {
                    } else {
                        findElement(activeChk).click();
                        jsonObj.put("active", false);
                    }
                }
            } catch (NullPointerException e) {
            }

            try {
                findElement(commentsTxt).clear();
                findElement(commentsTxt).sendKeys(data.get("comment_ch").toString());
            } catch (NullPointerException e) {
            }

            findElement(saveAndSubmitBtn).click();
            Thread.sleep(DEFAULT_PAUSE);

            jsonObj.put("workflow_state", WorkflowState.FOR_APPROVAL.state());
            jsonObj.put("deleted", "false");

            jsonMain.put(name, jsonObj);

            FileWriter file = new FileWriter(sPathToFile + sFileJson);
            file.write(jsonMain.toJSONString().replace("\\", ""));
            file.flush();

            driver.get(pageUrl);
            Thread.sleep(DEFAULT_PAUSE);
            waitForElement(workflowStateSpan);

            System.out.println(name+ ": New "+PAGE_NAME+" changes have been submitted");
            return findElement(workflowStateSpan).getText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Boolean checkFaqCh(JSONObject data, String name) throws InterruptedException {
        By editQuestionBtn = By.xpath("//input[contains(@id, 'btnEdit')]");
        JSONObject jsonMain = new JSONObject();

        try {
            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
            } catch (ParseException e) {
            }

            String pageUrl = getPageUrl(jsonMain, name);
            driver.get(pageUrl);
            waitForElement(commentsTxt);

            // Compare field values with entry data
            try {
                if (!data.get("name_EN_ch").toString().isEmpty()){
                    try {
                        if (!findElement(nameInputEN).getAttribute("value").equals(data.get("name_EN_ch").toString())) {
                            System.out.println("English name does not match changed value");
                            return false;
                        }
                    } catch (NullPointerException e) {
                    }

                    try {
                        if (!findElement(nameInputGE).getAttribute("value").equals(data.get("name_GE").toString())) {
                            System.out.println("German name does not match original value");
                            return false;
                        }
                    } catch (NullPointerException e) {
                    }

                    try {
                        System.out.println(findElement(activeChk).getAttribute("checked"));
                        //Unchecked attribute has "checked" value of null
                        if (!findElement(activeChk).getAttribute("checked").equals(data.get("active_ch").toString())) {
                            System.out.println("Active checkbox does not match changed value");
                            return false;
                        }
                    } catch (NullPointerException e) {
                    }
                }
            } catch (NullPointerException e){
            }

            // Check Questions edit page if edited
            try {
                if (!data.get("question_ch").toString().isEmpty()) {
                    waitForElementToAppear(questionsListName);

                    try {
                        if (!findElement(questionsListName).getText().equals(data.get("question_ch").toString())) {
                            return false;
                        }
                        findElement(editQuestionBtn).click();
                        waitForElement(questionInput);

                        if (!findElement(questionInput).getAttribute("value").equals(data.get("question_ch").toString())) {
                            System.out.println("Question does not match changed value");
                            return false;
                        }
                    } catch (NullPointerException e) {
                    }

                    try {
                        driver.switchTo().frame(findElement(radEditor));
                        if (!findElement(radEditorContent).getText().equals(data.get("answer_ch").toString())) {
                            System.out.println("Answer does not match changed value");
                            driver.switchTo().defaultContent();
                            return false;
                        }
                        driver.switchTo().defaultContent();
                    } catch (NullPointerException e) {
                    }
                }
            }catch (NullPointerException e){
            }

            System.out.println(name+ ": New "+PAGE_NAME+" has been checked");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String setupAsDeletedFaq(String name) throws InterruptedException {
        JSONObject jsonMain = new JSONObject();
        JSONObject jsonObj = new JSONObject();

        try {
            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
                jsonObj = (JSONObject) jsonMain.get(name);
            } catch (ParseException e) {
            }

            String pageUrl = getPageUrl(jsonMain, name);
            driver.get(pageUrl);

            waitForElement(commentsTxt);
            findElement(commentsTxt).sendKeys("Removing "+PAGE_NAME);
            findElement(deleteBtn).click();
            Thread.sleep(DEFAULT_PAUSE);

            driver.get(pageUrl);
            waitForElement(currentContentSpan);

            jsonObj.put("workflow_state", WorkflowState.FOR_APPROVAL.state());
            jsonObj.put("deleted", "true");

            jsonMain.put(name, jsonObj);

            FileWriter file = new FileWriter(sPathToFile + sFileJson);
            file.write(jsonMain.toJSONString().replace("\\", ""));
            file.flush();

            System.out.println(name+ ": "+PAGE_NAME+" set up as deleted");
            return findElement(currentContentSpan).getText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String removeFaq(JSONObject data, String name) throws InterruptedException {
        JSONObject jsonMain = new JSONObject();

        try {
            try {
                FileReader readFile = new FileReader(sPathToFile + sFileJson);
                jsonMain = (JSONObject) parser.parse(readFile);
            } catch (ParseException e) {
            }

            String pageUrl = getPageUrl(jsonMain, name);
            driver.get(pageUrl);
            Thread.sleep(DEFAULT_PAUSE);

            if (findElement(currentContentSpan).getText().equals(WorkflowState.DELETE_PENDING.state())) {

                waitForElement(commentsTxt);
                findElement(commentsTxt).sendKeys("Approving "+PAGE_NAME+" removal");
                findElement(publishBtn).click();

                Thread.sleep(DEFAULT_PAUSE*2);

                driver.get(pageUrl);
                Thread.sleep(DEFAULT_PAUSE);

                jsonMain.remove(name);

                FileWriter file = new FileWriter(sPathToFile + sFileJson);
                file.write(jsonMain.toJSONString().replace("\\", ""));
                file.flush();

                Thread.sleep(DEFAULT_PAUSE*2);
                driver.navigate().refresh();

                System.out.println(name+ ": New "+PAGE_NAME+" has been removed");
                return findElement(workflowStateSpan).getText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getPageUrl(JSONObject obj, String name) {
        String  sItemID = JsonPath.read(obj, "$.['"+name+"'].url_query.ItemID");
        String  sLanguageId = JsonPath.read(obj, "$.['"+name+"'].url_query.LanguageId");
        String  sSectionId = JsonPath.read(obj, "$.['"+name+"'].url_query.SectionId");
        return desktopUrl.toString()+"default.aspx?ItemID="+sItemID+"&LanguageId="+sLanguageId+"&SectionId="+sSectionId;
    }

    public String getPageUrlQuestion(JSONObject obj, String name) {
        String  sFaqId = JsonPath.read(obj, "$.['"+name+"'].url_query2.FaqId");
        String  sFaqQuestionId = JsonPath.read(obj, "$.['"+name+"'].url_query2.FaqQuestionId");
        String  sItemId = JsonPath.read(obj, "$.['"+name+"'].url_query2.ItemId");
        String  sLanguageId = JsonPath.read(obj, "$.['"+name+"'].url_query2.LanguageId");
        String  sSectionId = JsonPath.read(obj, "$.['"+name+"'].url_query2.SectionId");
        return desktopUrl.toString()+"default.aspx?FaqId="+sFaqId+"&FaqQuestionId="+ sFaqQuestionId + "&ItemId="+ sItemId + "&LanguageId="+sLanguageId+"&SectionId="+sSectionId;
    }
}
