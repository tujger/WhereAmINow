package com.edeqa.waytous.holders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.edeqa.waytous.MainActivity;
import com.edeqa.waytous.State;
import com.edeqa.waytous.abstracts.AbstractView;
import com.edeqa.waytous.abstracts.AbstractViewHolder;
import com.edeqa.waytous.helpers.IntroRule;
import com.edeqa.waytous.helpers.MyUser;
import com.edeqa.waytous.holders.FabViewHolder;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;

import static com.edeqa.waytous.State.EVENTS.ACTIVITY_RESULT;
import static com.edeqa.waytous.State.EVENTS.PREPARE_FAB;
import static com.edeqa.waytous.holders.MessagesHolder.WELCOME_MESSAGE;


/**
 * Created 12/03/16.
 */
public class FacebookViewHolder extends AbstractViewHolder {

    public static final String TYPE = "facebook";

    private CallbackManager callbackManager;
    private FabViewHolder fab;
    private String welcomeMessage;

    public FacebookViewHolder(MainActivity context) {
        super(context);
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        AppEventsLogger.activateApp(context);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean dependsOnEvent() {
        return true;
    }

    @Override
    public boolean dependsOnUser() {
        return false;
    }

    @Override
    public AbstractView create(MyUser myUser) {
        return null;
    }

    @Override
    public boolean onEvent(String event, Object object) {
        switch (event) {
            case PREPARE_FAB:
                fab = (FabViewHolder) object;
                if(State.getInstance().tracking_active()) {
                    fab.add(R.string.share_to_facebook, R.drawable.ic_facebook_white).setOnClickListener(onClickListener);
                }
                break;
            case ACTIVITY_RESULT:
                Bundle m = (Bundle) object;
                if(m != null){
                    int requestCode = m.getInt("requestCode");
                    if(CallbackManagerImpl.RequestCodeOffset.Share.toRequestCode() == requestCode) {
                        int resultCode = m.getInt("resultCode");
                        Intent data = m.getParcelable("data");
                        callbackManager.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;
            case WELCOME_MESSAGE:
                if(object != null) {
                    welcomeMessage = (String) object;
                }
                break;

        }
        return true;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fab.close(true);
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                String message = String.format(context.getString(R.string.click_here_to_follow_me_using_s), context.getString(R.string.app_name));
                if(welcomeMessage != null && welcomeMessage.length() > 0){
                    message = welcomeMessage;
                }

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(String.format(context.getString(R.string.follow_me_with_s), context.getString(R.string.app_name)))
                        .setContentDescription(message)
                        .setContentUrl(Uri.parse(State.getInstance().getTracking().getTrackingUri()))
                        .setImageUrl(Uri.parse(context.getString(R.string.logo_link)))
                        .build();

                ShareDialog shareDialog = new ShareDialog(context);
                // this part is optional
                callbackManager = CallbackManager.Factory.create();
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        System.out.println("Facebook onsuccess");
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("Facebook oncancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        System.out.println("Facebook onerror");

                    }
                });
                System.out.println("CALLBACKMANAGER:"+callbackManager);
                shareDialog.show(linkContent);
            }
        }
    };

    @Override
    public ArrayList<IntroRule> getIntro() {

        ArrayList<IntroRule> rules = new ArrayList<>();
        rules.add(new IntroRule().setEvent(PREPARE_FAB).setId("facebook_intro").setViewId(R.string.share_to_facebook).setTitle("Here you can").setDescription("Share this group to Facebook."));

        return rules;
    }

}