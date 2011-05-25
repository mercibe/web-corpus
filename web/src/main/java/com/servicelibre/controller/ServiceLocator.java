package com.servicelibre.controller;

import org.springframework.context.ApplicationContext;
import org.zkoss.spring.SpringUtil;

import com.servicelibre.corpus.manager.ListeManager;

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
        return (ListeManager) ctx.getBean("listeManager", ListeManager.class);
    }
}
