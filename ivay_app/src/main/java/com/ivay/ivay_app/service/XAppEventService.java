package com.ivay.ivay_app.service;

import com.ivay.ivay_repository.model.XAppEvent;

import java.util.List;

public interface XAppEventService {
    XAppEvent save(XAppEvent xAppEvent);

    List<XAppEvent> listToBeUpload(String userGid);

    int delete(String gids);
}
