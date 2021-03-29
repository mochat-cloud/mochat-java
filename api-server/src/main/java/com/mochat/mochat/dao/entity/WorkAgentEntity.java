package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

@TableName("mc_work_agent")
public class WorkAgentEntity extends Model<WorkAgentEntity> {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer corpId;
    private String wxAgentId;
    private String wxSecret;
    private String name;
    private String squareLogoUrl;
    private String description;
    private Integer close;
    private String redirectDomain;
    private Integer reportLocationFlag;
    private Integer isReportenter;
    private String homeUrl;
    private Integer type;
    private Date createdAt;
    private Date updatedAt;

    @TableLogic
    private Date deletedAt;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }


    public String getWxAgentId() {
        return getOrEmpty(wxAgentId);
    }

    public void setWxAgentId(String wxAgentId) {
        this.wxAgentId = wxAgentId;
    }


    public String getWxSecret() {
        return getOrEmpty(wxSecret);
    }

    public void setWxSecret(String wxSecret) {
        this.wxSecret = wxSecret;
    }


    public String getName() {
        return getOrEmpty(name);
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSquareLogoUrl() {
        return getOrEmpty(squareLogoUrl);
    }

    public void setSquareLogoUrl(String squareLogoUrl) {
        this.squareLogoUrl = squareLogoUrl;
    }


    public String getDescription() {
        return getOrEmpty(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getClose() {
        return close;
    }

    public void setClose(Integer close) {
        this.close = close;
    }


    public String getRedirectDomain() {
        return getOrEmpty(redirectDomain);
    }

    public void setRedirectDomain(String redirectDomain) {
        this.redirectDomain = redirectDomain;
    }


    public Integer getReportLocationFlag() {
        return reportLocationFlag;
    }

    public void setReportLocationFlag(Integer reportLocationFlag) {
        this.reportLocationFlag = reportLocationFlag;
    }


    public Integer getIsReportenter() {
        return isReportenter;
    }

    public void setIsReportenter(Integer isReportenter) {
        this.isReportenter = isReportenter;
    }


    public String getHomeUrl() {
        return getOrEmpty(homeUrl);
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }


    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    private String getOrEmpty(String str) {
        return str == null ? "" : str;
    }
}
