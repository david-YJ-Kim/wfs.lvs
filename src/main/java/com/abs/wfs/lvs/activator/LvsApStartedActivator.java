package com.abs.wfs.lvs.activator;

import com.abs.wfs.lvs.config.LvsPropertyObject;
import com.abs.wfs.lvs.config.SolaceSessionConfiguration;
import com.abs.wfs.lvs.intf.solace.InterfaceSolacePub;
import com.abs.wfs.lvs.intf.solace.InterfaceSolaceSub;
import com.abs.wfs.lvs.util.LvsDataStore;
import com.abs.wfs.lvs.util.NonStoreKeyCleaner;
import com.solacesystems.jcsmp.JCSMPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LvsApStartedActivator implements ApplicationRunner {

    @Autowired
    private Environment env;


    @Autowired
    NonStoreKeyCleaner nonStoreKeyCleaner;

    @Override
    public void run(ApplicationArguments args){


        this.initializeSolaceResources();
        log.info("Complete initialize solace resources.");


        log.info("Start Cleaner");
        this.nonStoreKeyCleaner.initializeDataStore();
        this.nonStoreKeyCleaner.executeTimer();


    }

    private void initializeSolaceResources(){

        SolaceSessionConfiguration sessionConfiguration = SolaceSessionConfiguration.createSessionConfiguration(env);

        try {
            InterfaceSolacePub interfaceSolacePub = InterfaceSolacePub.getInstance();
            interfaceSolacePub.init();
            LvsPropertyObject.getInstance().setInterfaceSolacePub(interfaceSolacePub);

        } catch (JCSMPException e) {
            throw new RuntimeException(e);
        }

        try {
            InterfaceSolaceSub interfaceSolaceSub = new InterfaceSolaceSub();
            interfaceSolaceSub.run();
            LvsPropertyObject.getInstance().setInterfaceSolaceSub(interfaceSolaceSub);

        } catch (JCSMPException e) {
            throw new RuntimeException(e);
        }

    }
}
