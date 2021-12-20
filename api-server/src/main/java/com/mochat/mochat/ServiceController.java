/**
 * This file is part of MoChat.
 *
 * @link https://mo.chat
 * @document https://mochat.wiki
 * @contact group@mo.chat
 * @license https://github.com/mochat-cloud/mochat-java/blob/master/LICENSE
 */

package com.mochat.mochat;

import com.mochat.mochat.common.annotion.SkipVerityToken;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.model.ApiRespVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController extends RuntimeException {

    @RequestMapping("/health")
    @SkipVerityToken
    public ApiRespVO health() {
        return ApiRespUtils.ok();
    }

    @RequestMapping(value = "health1")
    @SkipVerityToken
    public String health1(@RequestParam(value = "id") Integer id) {
        return null;
    }

}
