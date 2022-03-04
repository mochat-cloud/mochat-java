package com.mochat.mochat.index;

import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.CorpDataEntity;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.model.corp.CorpDataVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.ICorpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class IndexController {

    @Autowired
    private ICorpService corpServiceImpl;

    /**
     * 企业首页数据统计
     *
     * @description:
     * @return:
     * @author: Huayu
     */
    @GetMapping(value = "/corpData/index")
    public ApiRespVO index() throws Exception {
        if (AccountService.getCorpId() == null) {
            throw new CommonException("请先选择企业");
        }
        Map<String, Object> map = corpServiceImpl.handleCorpDta();
        return ApiRespUtils.ok(map);
    }


    /**
     * 首页数据统计折线图
     *
     * @description:
     * @return:
     * @author: Huayu
     */
    @GetMapping(value = "/corpData/lineChat")
    public ApiRespVO lineChat() {
        if (AccountService.getCorpId() == null) {
            throw new CommonException("请先选择企业");
        }
        List<CorpDataEntity> corpDataEntityList = corpServiceImpl.handleLineChatDta();
        List<CorpDataVO> voList = new ArrayList<>();
        CorpDataVO vo;
        for (CorpDataEntity entity : corpDataEntityList) {
            vo = new CorpDataVO();
            BeanUtils.copyProperties(entity, vo);
            vo.setDate(DateUtils.formatS1(entity.getDate().getTime()));
            voList.add(vo);
        }
        return ApiRespUtils.ok(voList);
    }
}
