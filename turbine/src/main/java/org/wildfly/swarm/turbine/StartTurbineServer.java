package org.wildfly.swarm.turbine;

import static com.netflix.turbine.init.TurbineInit.init;
import static com.netflix.turbine.init.TurbineInit.stop;
import static com.netflix.turbine.plugins.PluginsFactory.setInstanceDiscovery;
import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import io.fabric8.kubeflix.discovery.OpenShiftDiscovery;

public class StartTurbineServer implements ServletContextListener {

    private static final Logger logger = getLogger(StartTurbineServer.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initing Turbine server");
        setInstanceDiscovery(new OpenShiftDiscovery());
        init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Stopping Turbine server");
        stop();
    }
}
