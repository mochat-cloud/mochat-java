package com.mochat.mochat.service.contact;

import java.util.List;

public interface ICorpTagService {
    void wxUpdateTag(Integer empId, Integer contactId, List<Integer> tagIds);
    void wxDeleteTag(Integer empId, Integer contactId, List<Integer> tagIds);
    void wxUpdateTag(String userId, String externalUserID, List<Integer> tagIds);
}
