package com.mochat.mochat.model.workroom;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ReqRoomAutoPullUpdateDTO {

    private Integer workRoomAutoPullId;
    private Integer isVerified;
    private String employees;
    private String tags;
    private String rooms;

}
