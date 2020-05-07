package com.wjjasd.google.menumemo;

import java.io.Serializable;

public class MemoData implements Serializable {

    private String tableNo;
    private String menu;

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
