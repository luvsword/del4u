package com.kaist.delforyou.activity;

/**
 * Created by luvsword on 2016-07-31.
 */

import java.io.Serializable;

public class UserData implements Serializable {
    public String fName, lName;
    public String email;

    public UserData() {}

    public UserData(String fName, String lName, String email) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
    }
}
