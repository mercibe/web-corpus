package com.servicelibre.zk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.util.GenericForwardComposer;

/**
 * @author benoitm
 * 
 */
public class AdminCtrl extends GenericForwardComposer implements VariableResolver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(AdminCtrl.class);

	@Override
	public Object resolveVariable(String nomVariable) throws XelException {
		// logger.debug("nomVariable = " + nomVariable);
		return null;
	}

}
