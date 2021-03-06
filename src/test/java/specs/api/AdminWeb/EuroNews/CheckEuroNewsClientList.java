package specs.api.AdminWeb.EuroNews;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pageobjects.api.AdminWeb.Auth;
import pageobjects.api.AdminWeb.EuroNews.EuroNewsClientList;
import pageobjects.api.AdminWeb.LeftMainMenu;
import pageobjects.api.AdminWeb.ResponseDataObj;
import specs.ApiAbstractSpec;

import com.jayway.jsonpath.JsonPath;

import java.io.*;
import java.util.ArrayList;


/**
 * Created by philipsushkov on 2017-07-31.
 */

public class CheckEuroNewsClientList extends ApiAbstractSpec {
    private static Auth auth;
    private static LeftMainMenu leftMainMenu;
    private static EuroNewsClientList euroNews;
    private static final String EURO_NEWS = "Euro News";
    private static String sPathToFile;
    private static JSONParser parser;
    private final String DATA_JSON_CONTENT="getDataJsonContent", REQUESTS="requests", JSON_CONTENT = "application/json";

    @BeforeTest
    public void setUp() throws InterruptedException {
        auth = new Auth(driver);
        leftMainMenu = new LeftMainMenu(driver);
        sPathToFile = System.getProperty("user.dir") + propAPI.getProperty("dataPath_EuroNewsClientList");

        parser = new JSONParser();

        // Authorization
        System.out.println("Start Google Authorization");
        auth.getGoogleAuthPage();

        // Open Web Section
        System.out.println("Open Web Section");
        auth.getWebSection();

        euroNews = new EuroNewsClientList(driver, proxy);

        // Open Euro News Client List page in Web Section
        leftMainMenu.getEuroNewsClientListPage(EURO_NEWS);
    }

    @Test(priority=1)
    public void checkEuroNewsClient() throws InterruptedException {
        final String expectedTitle = "Euro News Client List";

        // Check elements after the first load
        Assert.assertNotNull(euroNews.getUrl());
        Assert.assertEquals(euroNews.getTitle(), expectedTitle, "Actual Euro News Client List page Title doesn't match to expected");
        Assert.assertNotNull(euroNews.getSearchInput(), "Search field doesn't exist");
        Assert.assertNotNull(euroNews.getClientsDataTable(), "Clients Data table doesn't exist");
        Assert.assertNotNull(euroNews.getPage4Href(), "Page #4 link doesn't exist");
        Assert.assertNotNull(euroNews.getWidgetContent(), "Widget Content doesn't exist");
        Assert.assertNotNull(euroNews.getCellDataSpan(), "Cell Data spans don't exist");

        // Click the 4th page / dataset
        euroNews.clickPage4Href();

        // Use Search functionality
        euroNews.clickSearch();
        //euroNews.removeSearchWord();

    }

    @Test(priority=2)
    public void checkHarFile() throws InterruptedException {
        Assert.assertNotNull(euroNews.getHar(), "HAR file didn't create");
    }

    @Test(dataProvider=DATA_JSON_CONTENT, priority=3)
    public void checkResponseData(JSONObject data) throws InterruptedException, IOException, ParseException {
        String sApiRequestName =data.get("api_request_name").toString();

        ResponseDataObj responseDataObj = euroNews.getResponseData(data);
        Assert.assertNotNull(responseDataObj, "Api request isn't found in network traffic logs");

        Assert.assertTrue(responseDataObj.getResponseCode() == Integer.valueOf(JsonPath.read(data, "$.expected.response_code")), "Response Code value of "+sApiRequestName+" is not the same as expected. Actual: "+responseDataObj.getResponseCode()+". Expected: "+JsonPath.read(data, "$.expected.response_code"));
        Assert.assertTrue(responseDataObj.getResponseTime() < Integer.valueOf(JsonPath.read(data, "$.expected.response_time_ms")), "Response Time value of "+sApiRequestName+" exceeded the expected. Actual: "+responseDataObj.getResponseTime()+". Expected: "+JsonPath.read(data, "$.expected.response_time_ms"));
        Assert.assertNotNull(responseDataObj.getJsonResponse(), "JSON Response Data doesn't exist");
    }

    @Test(dataProvider=DATA_JSON_CONTENT, priority=4)
    public void checkSchemaValidation(JSONObject data) throws InterruptedException, IOException, ParseException {
        Assert.assertTrue(euroNews.getSchemaValidation(data), "Actual JSON schema doesn't match expected one");
    }

    @DataProvider
    public Object[][] genericProvider(String sType, String sContentType) {
        String sDataFileJson = propAPI.getProperty("json_EuroNewsClientListData");

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(sPathToFile + sDataFileJson));
            JSONArray jsonArray = (JSONArray) jsonObject.get(sType);
            ArrayList<Object> zoom = new ArrayList();

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject pageObj = (JSONObject) jsonArray.get(i);
                if (Boolean.parseBoolean(pageObj.get("do_assertions").toString()) && pageObj.get("content_type").toString().equals(sContentType)) {
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

    @DataProvider
    public Object[][] getDataJsonContent() {
        return genericProvider(REQUESTS, JSON_CONTENT);
    }

    @AfterTest
    public void tearDown() {
    }


}
