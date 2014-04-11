package cn.duocool.lashou.service;

import cn.duocool.lashou.dao.DaoBase;

/**
 * 每个服务层都要继承自本类
 * @author xwood
 *
 */
public abstract class ServiceBase {
	
	private DaoBase dao;

	public DaoBase getDao() {
		return dao;
	}

	public void setDao(DaoBase dao) {
		this.dao = dao;
	}
	
	
}
