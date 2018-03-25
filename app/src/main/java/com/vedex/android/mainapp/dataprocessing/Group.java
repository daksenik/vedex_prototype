package com.vedex.android.mainapp.dataprocessing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by user on 17.09.2017.
 */

public class Group implements Serializable {
    private int groupId;
    private String name;
    private String description;
    private String creationDate;
    private int numberOfMembers;

    private String roleName;
    private int accessLevel;

    public Group(int groupId, String name, String description, String creationDate,
                 int numberOfMembers, String roleName, int accessLevel) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.numberOfMembers = numberOfMembers;
        this.roleName = roleName;
        this.accessLevel = accessLevel;
    }

    public static Group getFromJSON(JSONObject group) {
        Group result = null;
        try {
            int id = group.getInt("group_id");
            String nm = group.getString("name");
            String desc = group.getString("description");
            String crDate = group.getString("creation_date");
            int membersNum = group.getInt("number_of_members");

            JSONObject role = group.getJSONObject("role");
            String roleNme = role.getString("name");
            int accLevel = role.getInt("access_level");

            result = new Group(id, nm, desc, crDate, membersNum, roleNme, accLevel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public String getRoleName() {
        return roleName;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void decreaseMembers() {
        numberOfMembers--;
    }
}
