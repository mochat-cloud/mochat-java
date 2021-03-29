package com.mochat.mochat.service.impl;

import java.util.List;
import java.util.Map;

/**
 * 租户业务
 */
public interface ITenantService {

    List<Map<String,Object>>  getTenantByStatus();

}
