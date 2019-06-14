package com.ivay.ivay_app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ivay.ivay_app.service.XAppVersionUpdateService;
import com.ivay.ivay_repository.dao.master.XVersionUpdateDao;
import com.ivay.ivay_repository.model.XVersionUpdate;

@Service
public class XAppVersionUpdateImpl implements XAppVersionUpdateService{
	@Autowired
    XVersionUpdateDao xVersionUpdateDao;
	
	@Override
	public int save(XVersionUpdate xVersionUpdate) {
		return xVersionUpdateDao.save(xVersionUpdate);
	}

	@Override
	public XVersionUpdate findUpdate(String versionNumber) {
		XVersionUpdate versionUpdate=xVersionUpdateDao.findUpdate();
		if(versionUpdate!=null) {
			if(!versionNumber.equals(versionUpdate.getVersionNumber())) {
				versionUpdate.setNeedUpdate("1");
				return versionUpdate;
			}
		}
		
		return null;
	}

}
