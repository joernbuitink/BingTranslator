import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Joern Buitink
 * Date: 15.06.14
 * Time: 14:49
 * http://www.steinchenwelt.de
 */
public class Translator {


    public final static String CLIENT_ID = "CLIENT_ID";
    public final static String CLIENT_SECRET = "CLIENT_SECRET";
    public final static String DATAMARKET_ACCESS_URL = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
    public final static String DATAMARKET_DETECT_URL = "http://api.microsofttranslator.com/v2/Http.svc/Detect?text=%s";
    public final static String DATAMARKET_TRANSLATE_URL = "http://api.microsofttranslator.com/v2/Http.svc/Translate?text=%s&from=%s&to=%s";
    public final static String DATAMARKET_LANGUAGES_URL = /*"http://api.microsofttranslator.com/v2/Http.svc/GetLanguagesForTranslate";*/ "http://api.microsofttranslator.com/v2/Http.svc/GetLanguageNames?locale=en";

    private String accessToken;

    public static void main(String[] args) throws Exception {
        Translator translator = new Translator();
        translator.obtainAccessToken();
        //System.out.println(translator.translate("Deutschland"));
        translator.getLanguages();
    }

    public void detect(String toDetect) {
        System.out.println(sendGet(String.format(DATAMARKET_DETECT_URL,toDetect)));
    }

    public String translate(String toTranslate, String from, String to) throws UnsupportedEncodingException {
        if (from == null) {
            from = "en";
        }
        return sendGet(String.format(DATAMARKET_TRANSLATE_URL,URLEncoder.encode(toTranslate,"UTF-8"),from, to)).replace("<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\">","").replace("</string>", "");
    }

    public void getLanguages() {
        System.out.println(sendGet(DATAMARKET_LANGUAGES_URL));
    }

    public void obtainAccessToken() {

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        urlParameters.add(new BasicNameValuePair("client_id", CLIENT_ID));
        urlParameters.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
        urlParameters.add(new BasicNameValuePair("scope", "http://api.microsofttranslator.com"));

        try {
            String response = sendPost(new UrlEncodedFormEntity(urlParameters), DATAMARKET_ACCESS_URL);
            JSONObject json = new JSONObject(response);
            if (json.has("access_token")) {
                System.out.println(response);
                this.accessToken = "Bearer "+ json.get("access_token");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private String sendPost(HttpEntity entity, String url) {
        try {
            HttpPost post = new HttpPost(url);

            if (accessToken!=null) {

                post.addHeader("Authorization", this.accessToken);

            }
            post.setEntity(entity);

            return sendRequest(post);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String sendGet(String url) {
        try {
            HttpGet get = new HttpGet(url);

            if (accessToken!=null) {
                get.addHeader("Authorization", this.accessToken);
            }

            return sendRequest(get);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String sendRequest(HttpUriRequest request) {

        try {
            DefaultHttpClient client = new DefaultHttpClient();

            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
