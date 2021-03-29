package com.mochat.mochat.model.wm;

import lombok.Data;

@Data
public class CorpMsgTO {
    private int corpId;
    private String wxCorpId;
    private String chatSecret;
    private String chatRsaKey;
}
