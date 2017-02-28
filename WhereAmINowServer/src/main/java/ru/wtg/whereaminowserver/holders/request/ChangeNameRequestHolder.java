package ru.wtg.whereaminowserver.holders.request;

import org.json.JSONObject;

import ru.wtg.whereaminowserver.helpers.MyToken;
import ru.wtg.whereaminowserver.helpers.MyUser;
import ru.wtg.whereaminowserver.servers.AbstractWainProcessor;
import ru.wtg.whereaminowserver.servers.MyWsServer;
import ru.wtg.whereaminowserver.interfaces.RequestHolder;

import static ru.wtg.whereaminowserver.helpers.Constants.REQUEST_CHANGE_NAME;
import static ru.wtg.whereaminowserver.helpers.Constants.USER_NAME;

/**
 * Created 1/16/17.
 */

public class ChangeNameRequestHolder implements RequestHolder {

    public static final String TYPE = REQUEST_CHANGE_NAME;

    public ChangeNameRequestHolder(AbstractWainProcessor context) {

    }


    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean perform(MyToken token, MyUser user, JSONObject request, JSONObject result) {

        if (request.has(USER_NAME)) {
            result.put(USER_NAME, request.getString(USER_NAME));
            user.setName(request.getString(USER_NAME));
        }

        return true;
    }

    @Override
    public boolean isSaveable() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }


}