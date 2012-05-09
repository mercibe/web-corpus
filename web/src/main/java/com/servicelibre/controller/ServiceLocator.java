package com.servicelibre.controller;

import org.springframework.context.ApplicationContext;
import org.zkoss.spring.SpringUtil;

import com.servicelibre.corpus.repository.ListeRepository;
import com.servicelibre.corpus.repository.MotRepository;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.zk.controller.ContexteFiltreManager;
import com.servicelibre.zk.controller.FiltreManager;

public class ServiceLocator {

	private static ApplicationContext ctx;

	static {

		ctx = SpringUtil.getApplicationContext();
	}

	private ServiceLocator() {
	}

	public static ListeRepository getListeRepo() {
		return (ListeRepository) ctx.getBean("listeRepository");
	}

	public static MotRepository getMotRepo() {
		return (MotRepository) ctx.getBean("motRepository");
	}

	public static FiltreManager getListeFiltreManager() {
		return (FiltreManager) ctx.getBean("listeFiltreManager");
	}

	public static ContexteFiltreManager getContexteFiltreManager() {
		return (ContexteFiltreManager) ctx.getBean("contexteFiltreManager");
	}

	public static CorpusService getCorpusService() {
		return (CorpusService) ctx.getBean("corpusService");
	}

	public static CorpusService getFormeService() {
		return (CorpusService) ctx.getBean("formeService");
	}

}
