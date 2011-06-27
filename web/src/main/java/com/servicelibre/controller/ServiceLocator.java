package com.servicelibre.controller;

import org.springframework.context.ApplicationContext;
import org.zkoss.spring.SpringUtil;

import com.servicelibre.corpus.manager.DocMetadataManager;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.zk.controller.ContexteFiltreManager;
import com.servicelibre.zk.controller.FiltreManager;

public class ServiceLocator
{

    private static ApplicationContext ctx;

    static
    {

        ctx = SpringUtil.getApplicationContext();
    }

    private ServiceLocator()
    {
    }

    public static ListeManager getListeManager()
    {
        return (ListeManager) ctx.getBean("listeManager");
    }

    public static MotManager getMotManager()
    {
        return (MotManager) ctx.getBean("motManager");
    }

    public static FiltreManager getListeFiltreManager()
    {
        return (FiltreManager) ctx.getBean("listeFiltreManager");
    }

    public static ContexteFiltreManager getContexteFiltreManager()
    {
        return (ContexteFiltreManager) ctx.getBean("contexteFiltreManager");
    }
    
    public static CorpusService getCorpusService()
    {
        return (CorpusService) ctx.getBean("corpusService");
    }
    
    public static CorpusService getFormeService()
    {
        return (CorpusService) ctx.getBean("formeService");
    }
    
    public static DocMetadataManager getDocMetataManager()
    {
        return (DocMetadataManager) ctx.getBean("docMetadataManager");
    }
    
    
}
