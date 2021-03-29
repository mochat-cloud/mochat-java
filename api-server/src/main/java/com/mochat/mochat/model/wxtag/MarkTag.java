package com.mochat.mochat.model.wxtag;

/**
 * @author zhaojinjian
 * @ClassName MarkTag.java
 * @Description TODO
 * @createTime 2020/12/7 15:57
 */
public class MarkTag {
    private String userid;
    private String external_userid;
    private String [] add_tag;
    private String [] remove_tag;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getExternal_userid() {
        return external_userid;
    }

    public void setExternal_userid(String external_userid) {
        this.external_userid = external_userid;
    }

    public String[] getAdd_tag() {
        return add_tag;
    }

    public void setAdd_tag(String[] add_tag) {
        this.add_tag = add_tag;
    }

    public String[] getRemove_tag() {
        return remove_tag;
    }

    public void setRemove_tag(String[] remove_tag) {
        this.remove_tag = remove_tag;
    }
}
