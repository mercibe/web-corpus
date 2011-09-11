package com.servicelibre.zk.controller;

import java.util.Collections;
import java.util.List;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Toolbarbutton;

/**
 * Démontre MVC: Autowire UI objects to data members
 * 
 * @author benoitm
 * 
 */
public class IndexCtrl extends GenericForwardComposer implements
VariableResolver {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3190337135508498208L;

	//private static Logger logger = LoggerFactory.getLogger(IndexCtrl.class);

	Toolbarbutton boutonFermerTousLesOnglets;
	Tabs corpusTabs;
	
	
	public void onClick$boutonFermerTousLesOnglets(Event event) {
		// Fermer tous les onglets fermables
		Tab tab = (Tab)corpusTabs.getLastChild();
		while(tab.isClosable()) {
			tab.close();
			tab = (Tab)corpusTabs.getLastChild();
		}
	}
	
	@Override
	public Object resolveVariable(String arg0) throws XelException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
