package com.mochat.mochat.model.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ContentDTO {

    /**
     * 图片
     *
     * imageName
     * imagePath
     * imageFullPath
     */

    /**
     * 图文
     *
     * title
     * description
     * imageName
     * imagePath
     * imageFullPath
     * imageLink
     */

    /**
     * 小程序
     * <p>
     * appid
     * page
     * title
     * imagePath
     * imageFullPath
     */

    private String imageName;
    private String imagePath;
    private String imageFullPath;

    private String title;
    private String description;
    private String imageLink;

    private String appid;
    private String page;
}
