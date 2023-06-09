package com.groot.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatDetailDTO {
    private Long userPK;
    private String nickName;
    private String profile;
    private boolean receive;
}
