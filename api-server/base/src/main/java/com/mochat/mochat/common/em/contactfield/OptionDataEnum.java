package com.mochat.mochat.common.em.contactfield;

public enum OptionDataEnum {

    /**
     * @Name("phone")
     * @Label("手机号")
     * @TypeEnum("PHONE")
     * @Options("")
     */
    PHONE("1","手机号"),

    /**
     * @Name("name")
     * @Label("姓名")
     * @TypeEnum("TEXT")
     * @Options("")
     */
    NAME("2","姓名"),

    /**
     * @Name("gender")
     * @Label("性别")
     * @TypeEnum("RADIO")
     * @Options("男,女,未知")
     */
    GENDER("3","性别","男,女,未知"),

    /**
     * @Name("birthday")
     * @Label("生日")
     * @TypeEnum("DATE")
     * @Options("")
     */
    BIRTHDAY("4","生日"),

    /**
     * @Name("age")
     * @Label("年龄")
     * @TypeEnum("NUMBER")
     * @Options("")
     */
    AGE("5","年龄"),

    /**
     * @Name("QQ")
     * @Label("QQ")
     * @TypeEnum("TEXT")
     * @Options("")
     */
    QQ("6","QQ"),

    /**
     * @Name("email")
     * @Label("邮箱")
     * @TypeEnum("EMAIL")
     * @Options("")
     */
    EMAIL("7","邮箱"),

    /**
     * @Name("hobby")
     * @Label("爱好")
     * @TypeEnum("CHECKBOX")
     * @Options("游戏,阅读,音乐,运动,动漫,旅行,家居,曲艺,宠物,美食,娱乐,电影,电视剧,健康养生,数码,其他")
     */
    HOBBY("8","爱好","游戏,阅读,音乐,运动,动漫,旅行,家居,曲艺,宠物,美食,娱乐,电影,电视剧,健康养生,数码,其他"),

    /**
     * @Name("education")
     * @Label("学历")
     * @TypeEnum("SELECT")
     * @Options("博士,硕士,大学,大专,高中,初中,小学,其他")
     */
    EDUCATION("9","学历","博士,硕士,大学,大专,高中,初中,小学,其他"),

    /**
     * @Name("annualIncome")
     * @Label("年收入")
     * @TypeEnum("SELECT")
     * @Options("5万以下,5万-15万,15万-30万,30万以上,50-100万,100万-200万,200万-500万,500万-1000万,1000万-5000万")
     */
    ANNUAL_INCOME("10","年收入","5万以下,5万-15万,15万-30万,30万以上,50-100万,100万-200万,200万-500万,500万-1000万,1000万-5000万"),

    /**
     * @Name("industryBusiness")
     * @Label("行业")
     * @TypeEnum("SELECT")
     * @Options("IT/互联网/通信/电子,金融/投资/财会/保险,广告/媒体/出版/艺术,市场/销售/客服,人力资源/行政/高级管理,建筑/房产/物业,采购/贸易/物流/交通,咨询/法律/认证,生产/制造,生物/制药/医疗/护理,教育/培训/翻译/公务员,科研/环保/农业/能源,服务业,其他")
     */
    INDUSTRY_BUSINESS("11","行业","IT/互联网/通信/电子,金融/投资/财会/保险,广告/媒体/出版/艺术,市场/销售/客服,人力资源/行政/高级管理,建筑/房产/物业,采购/贸易/物流/交通,咨询/法律/认证,生产/制造,生物/制药/医疗/护理,教育/培训/翻译/公务员,科研/环保/农业/能源,服务业,其他"),

    /**
     * @Name("company")
     * @Label("公司")
     * @TypeEnum("TEXT")
     * @Options("")
     */
    COMPANY("12","公司"),

    /**
     * @Name("area")
     * @Label("区域")
     * @TypeEnum("TEXT")
     * @Options("")
     */
    AREA("13","区域"),

    /**
     * @Name("address")
     * @Label("地址")
     * @TypeEnum("TEXT")
     * @Options("")
     */
    ADDRESS("14","地址"),

    /**
     * @Name("idCard")
     * @Label("身份证")
     * @TypeEnum("TEXT")
     * @Options("")
     */
    ID_CARD("15","身份证"),

    /**
     * @Name("picture")
     * @Label("图片")
     * @TypeEnum("PICTURE")
     * @Options("")
     */
    PICTURE("16","图片");

    private String id;
    private String desc;
    private String option_desc;

    OptionDataEnum(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    OptionDataEnum(String id, String desc,String option_desc) {
        this.id = id;
        this.desc = desc;
        this.option_desc = option_desc;
    }


    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public String getOption_desc() {
        return option_desc;
    }
}
