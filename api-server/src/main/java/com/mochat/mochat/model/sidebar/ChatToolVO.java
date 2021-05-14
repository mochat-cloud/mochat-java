package com.mochat.mochat.model.sidebar;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChatToolVO {
    private Integer id;
    private String pageName;
    private String pageFlag;
    private String pageUrl;
}
