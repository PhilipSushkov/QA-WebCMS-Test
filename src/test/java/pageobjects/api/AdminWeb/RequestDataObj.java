package pageobjects.api.AdminWeb;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.List;

/**
 * Created by philipsushkov on 2017-08-03.
 */

public class RequestDataObj {
    private BrowserMobProxy proxy;
    private HttpClient httpClient;
    private String method, contentType, urlData;

    public RequestDataObj(BrowserMobProxy sProxy, String sMethod, String sContentType, String sUrlData) {
        this.proxy = sProxy;
        this.method = sMethod;
        this.contentType = sContentType;
        this.urlData = sUrlData;
        this.httpClient = HttpClientBuilder.create().build();
    }

    public HttpGet getHttpGet() {
        HttpGet httpGet = null;

        for (HarEntry harEntry:proxy.getHar().getLog().getEntries()) {

            HarRequest harRequest = harEntry.getRequest();
            HarResponse harResponse = harEntry.getResponse();

            List<HarNameValuePair> harListResponse = harResponse.getHeaders();
            int sInd = getIndex(harListResponse, contentType);
            
            if (harRequest.getUrl().equals(urlData)
                    && harRequest.getMethod().equals(method)
                    && harListResponse.get(sInd).getValue().contains(contentType)) {

                httpGet = new HttpGet(harRequest.getUrl());

                List<HarNameValuePair> params = harRequest.getHeaders();
                for (HarNameValuePair param:params) {
                    httpGet.setHeader(param.getName(), param.getValue());
                }

            }

        }

        return httpGet;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    private int getIndex(List<HarNameValuePair> lHarListResponse, String sCondition) {
        for (int i=0; i < lHarListResponse.size(); i++) {
            if (lHarListResponse.get(i).getValue().contains(sCondition)) {
                return i;
            }
        }
        return -1;
    }


}
