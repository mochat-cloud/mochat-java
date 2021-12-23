package com.mochat.mochat.controller;

import com.mochat.mochat.common.annotion.SkipVerityToken;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.common.em.channel.ReqStatisticsIndexEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Slf4j
@RestController
public class TestController {

    @Data
    public static class Vo {
        @Length(max = 2, message = "姓名不能为空")
        @NotBlank(message = "姓名不能为空")
        private String name;

        @NotBlank(message = "密码不能为空")
        private String pwd;

        ReqStatisticsIndexEnum en = ReqStatisticsIndexEnum.DAY;
    }

    @GetMapping("a")
    @SkipVerityToken
    public ApiRespVO a(@Size(min = 3, max = 10) String tagId) {
        return ApiRespUtils.ok(tagId);
    }

    @GetMapping("b")
    @SkipVerityToken
    public ApiRespVO b(@Size(min = 3, max = 10) @RequestParam(value = "tagId", defaultValue = "0") String tagId) {
        return ApiRespUtils.ok(tagId);
    }

    @GetMapping("c")
    @SkipVerityToken
    public ApiRespVO c(@Validated Vo vo) {
        return ApiRespUtils.ok(vo);
    }

}
