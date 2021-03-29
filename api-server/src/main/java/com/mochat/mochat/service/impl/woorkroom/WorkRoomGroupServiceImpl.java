package com.mochat.mochat.service.impl.woorkroom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.model.PageModel;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.dao.entity.workroom.WorkRoomGroupEntity;
import com.mochat.mochat.dao.mapper.WorkRoomGroupMapper;
import com.mochat.mochat.service.workroom.IWorkRoomGroupService;
import com.mochat.mochat.service.workroom.IWorkRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:客户群分组
 * @author: Huayu
 * @time: 2020/12/8 14:43
 */
@Service
public class WorkRoomGroupServiceImpl extends ServiceImpl<WorkRoomGroupMapper, WorkRoomGroupEntity> implements IWorkRoomGroupService {

    @Autowired
    private IWorkRoomService workRoomServiceImpl;

    /**
     * @description:根据条件查询获得客户群列表
     * @return:
     * @author: Huayu
     * @time: 2020/12/8 16:07
     */
    @Override
    public Map<String, Object> getWorkRoomGroupList(String corpIds, Integer pageNo, Integer pageCount) {
        QueryWrapper<WorkRoomGroupEntity> workRoomGroupWrapper = new QueryWrapper<WorkRoomGroupEntity>();
        Page<WorkRoomGroupEntity> page = new Page<>();
        RequestPage requestPage = new RequestPage(pageNo, pageCount);
        ApiRespUtils.initPage(page, requestPage);
        List<Map<String, Object>> listMapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> mapList = new HashMap<String, Object>();
        Map<String, Object> listMap = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        workRoomGroupWrapper.select("id,name,corp_id,created_at");
        workRoomGroupWrapper.eq("corp_id", corpIds);
        List<WorkRoomGroupEntity> workRoomGroupEntityList = this.baseMapper.selectList(workRoomGroupWrapper);
        int totalPageNum = (workRoomGroupEntityList.size() + pageCount - 1) / pageCount;
        mapList.put("page", new PageModel(pageCount, workRoomGroupEntityList.size(), totalPageNum));
        for (WorkRoomGroupEntity workRoomGroupEntity :
                workRoomGroupEntityList) {
            listMap = new HashMap<String, Object>();
            listMap.put("workRoomGroupId", workRoomGroupEntity.getId());
            listMap.put("corpId", workRoomGroupEntity.getCorpId());
            listMap.put("workRoomGroupName", workRoomGroupEntity.getName());
            listMap.put("createdAt", format.format(workRoomGroupEntity.getCreatedAt()));
            listMapList.add(listMap);
        }
        mapList.put("list", listMapList);
        return mapList;
    }

    /**
     * @description:根据workRoomGroupId获得对象
     * @return:
     * @author: Huayu
     * @time: 2020/12/8 18:54
     */
    @Override
    public WorkRoomGroupEntity getWorkRoomGroupById(Integer workRoomGroupId) {
        return getById(workRoomGroupId);
    }

    /**
     * @description:删除分组
     * @return:
     * @author: Huayu
     * @time: 2020/12/8 19:13
     */
    @Override
    @Transactional
    public Integer deleteWorkRoomGroup(Integer workRoomGroupId) {
        Integer i = this.baseMapper.deleteById(workRoomGroupId);
        //将当前分组下的客户群移到未分组
        Integer i1 = workRoomServiceImpl.updateWorkRoomsByRoomGroupId(workRoomGroupId, 0);
        return i + i1;
    }

    /**
     * @description:验证客户群分组名称是否已经存在
     * @return:
     * @author: Huayu
     * @time: 2020/12/9 9:04
     */
    @Override
    public WorkRoomGroupEntity getWorkRoomGroupsByCorpId(Integer corpId, String name) {
        QueryWrapper<WorkRoomGroupEntity> workRoomGroupWrapper = new QueryWrapper<WorkRoomGroupEntity>();
        workRoomGroupWrapper.select("name");
        workRoomGroupWrapper.eq("corp_id", corpId);
        workRoomGroupWrapper.eq("name", name);
        return this.baseMapper.selectOne(workRoomGroupWrapper);
    }

    /**
     * @description:数据入表
     * @return:
     * @author: Huayu
     * @time: 2020/12/9 9:11
     */
    @Override
    @Transactional
    public boolean createWorkRoomGroup(WorkRoomGroupEntity workRoomGroupEntity) {
        return this.save(workRoomGroupEntity);
    }

    /**
     * @description:客户群分组管理- 更新提交.
     * @return:
     * @author: Huayu
     * @time: 2020/12/9 10:06
     */
    @Override
    @Transactional
    public Integer updateWorkRoomGroupById(Integer id, String workRoomGroupName) {
        WorkRoomGroupEntity workRoomGroupEntity = new WorkRoomGroupEntity();
        workRoomGroupEntity.setId(Integer.valueOf(id));
        workRoomGroupEntity.setName(workRoomGroupName);
        Integer i = baseMapper.updateById(workRoomGroupEntity);
        return i;
    }

    /**
     * @param roomGroupIdArr
     * @description:根据workRoomGroupIds获得对象
     * @return:
     * @author: Huayu
     * @time: 2020/12/10 18:54
     */
    @Override
    public List<WorkRoomGroupEntity> getWorkRoomGroupByIds(String roomGroupIdArr) {
        QueryWrapper<WorkRoomGroupEntity> workRoomGroupWrapper = new QueryWrapper<WorkRoomGroupEntity>();
        workRoomGroupWrapper.select("id,name");
        workRoomGroupWrapper.in("id", roomGroupIdArr);
        return null;
    }
}
