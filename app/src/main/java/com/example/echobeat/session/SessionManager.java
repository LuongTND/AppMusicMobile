package com.example.echobeat.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERID = "userId";
    private static final String KEY_GOOGLEID = "googleId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";

    private static final String KEY_ROLE_ID = "roleId";

    private static final String KEY_IMAGE = "image";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserSession(String userId,String googleId, String email, String username, int roleId,String image) {
        editor.putString(KEY_USERID, userId);
        editor.putString(KEY_GOOGLEID, googleId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_ROLE_ID, roleId);
        editor.putString(KEY_IMAGE, image);
        editor.commit();
    }
    public String getUserid() {
        return sharedPreferences.getString(KEY_USERID, null);
    }
    public String getGoogleId() {
        return sharedPreferences.getString(KEY_GOOGLEID, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    public String getImage() {
        return sharedPreferences.getString(KEY_IMAGE, null);
    }
    public int getRoleId() {
        return sharedPreferences.getInt(KEY_ROLE_ID, -1); // -1 là giá trị mặc định nếu không tìm thấy
    }
    public void updateRoleId(int roleId) {
        editor.putInt(KEY_ROLE_ID, roleId);
        //editor.apply();//thay đổi không ần kết quả ngay lập tức.
        editor.commit();//ngay lập tức
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}
