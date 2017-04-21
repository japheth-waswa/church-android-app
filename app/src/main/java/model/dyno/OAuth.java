package model.dyno;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.japhethwaswa.church.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OAuth extends Application {
//todo get passwordGrantTypeAccessToken(method)
    private static Context context = ApplicationContextProvider.getsContext();
    private static Resources res = context.getResources();
    private static String preference_file_key = res.getString(R.string.preference_file_key);
    static SharedPreferences sharedPref = context.getSharedPreferences(preference_file_key, Context.MODE_PRIVATE);
    private static String clientCredentialsAccessToken = null;

    //get token endpoint
    public static String getTokenEndPoint() {

        String baseUrl = res.getString(R.string.root_domain);
        String OAuthRelativeUrl = res.getString(R.string.oauth_token_endpoint);
        return baseUrl + OAuthRelativeUrl;
    }

    //get token test endpoint
    public static String getTokenTestEndPoint() {

        String baseUrl = res.getString(R.string.root_domain_api);
        String OAuthRelativeUrl = res.getString(R.string.token_test);
        return baseUrl + OAuthRelativeUrl;
    }

    /**==================================client_credentials grant type implementation=====================================**/
    public static String getClientCredentialsGrantTypeAccessToken(){

        //make this request if the access token is not valid(test the token with my custom endpoint) or does not exist

        //GET from preference file
        clientCredentialsAccessToken = sharedPref.getString(res.getString(R.string.preference_client_credentials_name), null);

        if(clientCredentialsAccessToken == null){
            //fetch from remote
            fetchClientCredentialsAccessToken();
        }else{
            //TEST IT TO ENSURE IT IS VALID
            testClientCredAccessToken(clientCredentialsAccessToken);
        }
        return clientCredentialsAccessToken;
    }

    private static void testClientCredAccessToken(String clientCredentialsAccessToken) {

        ANRequest request = AndroidNetworking.get(OAuth.getTokenTestEndPoint())
                .setPriority(Priority.HIGH)
                .setTag("ClientCredTokenValid")
                .addHeaders("Authorization","Bearer "+clientCredentialsAccessToken)
                .build();


        ANResponse<String> response = request.executeForString();


        if(response.isSuccess()){
            //ie return 200-it means it was not successful ie 500=success
            fetchClientCredentialsAccessToken();
        }
    }

    private static void fetchClientCredentialsAccessToken() {

        Map<String,String> bodyParams = new HashMap<>();
        bodyParams.put("grant_type","client_credentials");
        bodyParams.put("client_id",res.getString(R.string.client_credentials_client_id));
        bodyParams.put("client_secret",res.getString(R.string.client_credentials_client_secret));

        ANRequest request = AndroidNetworking.post(OAuth.getTokenEndPoint())
                      .setPriority(Priority.HIGH)
                      .setTag("ClientCredToken")
                      .addBodyParameter(bodyParams)
                      .build();


        ANResponse<JSONObject> response = request.executeForJSONObject();

        if(response.isSuccess()){
            saveClientCredentialsAccessToken(response);
        }


    }

    private static void saveClientCredentialsAccessToken(ANResponse<JSONObject> response) {

        JSONObject result  = response.getResult();
        String accessToken = null;
        String accessTokenExpires = null;
        try {
             accessToken = result.getString("access_token");
             accessTokenExpires = result.getString("expires_in");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //THEN PUT IT IN PREFERENCES
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(res.getString(R.string.preference_client_credentials_name), accessToken);
        editor.putString(res.getString(R.string.preference_client_credentials_expires), accessTokenExpires);
        editor.commit();

        clientCredentialsAccessToken = accessToken;
    }
}
