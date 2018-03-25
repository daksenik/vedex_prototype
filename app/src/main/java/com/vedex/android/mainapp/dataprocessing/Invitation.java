package com.vedex.android.mainapp.dataprocessing;

import com.vedex.android.mainapp.MainActivity;
import com.vedex.android.mainapp.dataprocessing.datahelpcomponents.DateParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by user on 23.09.2017.
 */

public class Invitation implements Serializable {

    String senderName;
    String senderLogin;
    String message;
    String sendingDate;

    Group group;

    public Invitation(String senderName, String senderLogin, String message, String sendingDate,
                      Group group) {
        this.senderName = senderName;
        this.senderLogin = senderLogin;
        this.message = message;
        this.sendingDate = sendingDate;
        this.group = group;
    }

    public static Invitation getFromJSON(JSONObject invitationObject) {
        Invitation result = null;

        try {
            String senderNme = invitationObject.getString("sender_name");
            String senderLgn = invitationObject.getString("login");
            String msg = invitationObject.getString("message");
            String sendingDte = DateParser.getFormattedDate(invitationObject.getString("sending_date"));

            JSONObject groupObj = invitationObject.getJSONObject("group");

            int groupId = groupObj.getInt("group_id");
            String groupName = groupObj.getString("name");
            String groupDesc = groupObj.getString("description");
            String creationDate = DateParser.getFormattedDate(groupObj.getString("creation_date"));
            int membersCount = groupObj.getInt("number_of_members");

            Group grp = new Group(groupId, groupName, groupDesc, creationDate, membersCount, "", -1);

            result = new Invitation(senderNme, senderLgn, msg, sendingDte, grp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderLogin() {
        return senderLogin;
    }

    public String getMessage() {
        return message;
    }

    public String getSendingDate() {
        return sendingDate;
    }

    public Group getGroup() {
        return group;
    }
}
