package com.servicelibre.controller;

import org.springframework.context.ApplicationContext;
import org.zkoss.spring.SpringUtil;

import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;
import com.servicelibre.zk.controller.ListeMotFiltre;

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

    public static ListeMotFiltre getListeMotFiltre()
    {
        return (ListeMotFiltre) ctx.getBean("listeMotFiltre");
    }

}
