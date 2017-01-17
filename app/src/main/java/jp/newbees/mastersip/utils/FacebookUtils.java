package jp.newbees.mastersip.utils;

import android.os.Bundle;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.internal.Utility;

/**
 * Created by vietbq on 1/17/17.
 */

public class FacebookUtils {

    private FacebookUtils() {
        //Prevent init constructor
    }

    public final static void getGraphMeRequestAsync(
            final String accessToken,
            final Utility.GraphMeRequestWithCacheCallback callback) {
        GraphRequest.Callback graphCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null) {
                    callback.onFailure(response.getError().getException());
                } else {
                    callback.onSuccess(response.getJSONObject());
                }
            }
        };
        GraphRequest graphRequest = genInfoUserRequest(accessToken);
        graphRequest.setCallback(graphCallback);
        graphRequest.executeAsync();
    }

    public final static void getGraphPictureRequest(String accessToken, final Utility.GraphMeRequestWithCacheCallback callback) {
        GraphRequest.Callback graphCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null) {
                    callback.onFailure(response.getError().getException());
                } else {
                    callback.onSuccess(response.getJSONObject());
                }
            }
        };
        GraphRequest graphRequest = genPictureRequest(accessToken);
        graphRequest.setCallback(graphCallback);
        graphRequest.executeAsync();
    }

    private static GraphRequest genInfoUserRequest(
            final String accessToken) {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,middle_name,last_name,link,email,birthday,gender");
        parameters.putString("access_token", accessToken);
        GraphRequest graphRequest = new GraphRequest(
                null,
                "me",
                parameters,
                HttpMethod.GET,
                null);
        return graphRequest;
    }

    private static GraphRequest genPictureRequest(final String accessToken) {
        Bundle parameters = new Bundle();
        parameters.putInt("height", 320);
        parameters.putInt("width", 320);
        parameters.putInt("redirect", 0);
        parameters.putString("access_token", accessToken);
        GraphRequest graphRequest = new GraphRequest(
                null,
                "me/picture",
                parameters,
                HttpMethod.GET,
                null);
        return graphRequest;
    }
}
