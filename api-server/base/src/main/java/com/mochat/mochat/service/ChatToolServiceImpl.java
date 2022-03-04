package com.mochat.mochat.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.ChatToolEntity;
import com.mochat.mochat.dao.mapper.ChatToolMapper;
import org.springframework.stereotype.Service;

@Service
public class ChatToolServiceImpl extends ServiceImpl<ChatToolMapper, ChatToolEntity> implements IChatToolService {

}


