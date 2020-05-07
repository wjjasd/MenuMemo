package com.wjjasd.google.menumemo;

import io.realm.RealmObject;

public class MemoVo extends RealmObject {
    public String tableNo;
    public String memo;

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


}
