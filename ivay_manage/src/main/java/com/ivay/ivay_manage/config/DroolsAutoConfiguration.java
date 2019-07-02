package com.ivay.ivay_manage.config;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * create by xx on 2018/1/10
 */
@Configuration
public class DroolsAutoConfiguration {

    private static final String RULES_PATH = "rules/pre/";

    private static final String RULES_MIDDLE_PATH = "rules/middle/";

    //@Bean
    //@ConditionalOnMissingBean(KieFileSystem.class)
    public KieFileSystem kieFileSystem(String path) throws IOException {
        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
        System.out.println(path + "规则条数：" + getRuleFiles(path).length);
        for (Resource file : getRuleFiles(path)) {
            kieFileSystem.write(ResourceFactory.newClassPathResource(path + file.getFilename(), "UTF-8"));
        }
        return kieFileSystem;
    }

    //@Bean
    //@ConditionalOnMissingBean(KieFileSystem.class)
//    public KieFileSystem kieFileSystemMiddle() throws IOException {
//        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
//        System.out.println("midille规则条数：" + getRuleFiles(RULES_MIDDLE_PATH).length);
//        for (Resource file : getRuleFiles(RULES_MIDDLE_PATH)) {
//        	
//        	System.out.println("midille规则路径：" + RULES_MIDDLE_PATH + file.getFilename());
//            kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_MIDDLE_PATH + file.getFilename(), "UTF-8"));
//        }        
//        return kieFileSystem;
//    }

    private Resource[] getRuleFiles(String path) throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:" + path + "**/*.*");
    }

    @Bean(name = "preKieContainer")
    @Primary
    @ConditionalOnMissingBean(KieContainer.class)
    public KieContainer kieContainer() throws IOException {
        final KieRepository kieRepository = getKieServices().getRepository();

        kieRepository.addKieModule(new KieModule() {
            @Override
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });

        KieBuilder kieBuilder = getKieServices().newKieBuilder(kieFileSystem(RULES_PATH));
        kieBuilder.buildAll();

        return getKieServices().newKieContainer(kieRepository.getDefaultReleaseId());
    }

    @Bean(name = "middleKieContainer")
    //@ConditionalOnMissingBean(KieContainer.class)
    public KieContainer kieContainerMiddle() throws IOException {
        final KieRepository kieRepository = getKieServices().getRepository();

        kieRepository.addKieModule(new KieModule() {
            @Override
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });

        KieBuilder kieBuilder = getKieServices().newKieBuilder(kieFileSystem(RULES_MIDDLE_PATH));
        kieBuilder.buildAll();

        return getKieServices().newKieContainer(kieRepository.getDefaultReleaseId());
    }

    private KieServices getKieServices() {
        return KieServices.Factory.get();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(KieBase.class)
    public KieBase kieBase() throws IOException {
        return kieContainer().getKieBase();
    }

    @ConditionalOnMissingBean(KieSession.class)
    public KieSession kieSession() throws IOException {
        return kieContainer().newKieSession();
    }

    @Bean
    @ConditionalOnMissingBean(KieSession.class)
    public KieSession kieSessionMiddle() throws IOException {
        return kieContainer().newKieSession();
    }

    @Bean
    @ConditionalOnMissingBean(StatelessKieSession.class)
    public StatelessKieSession statelessKieSession() throws IOException {
        return kieContainer().newStatelessKieSession();
    }

    @Bean
    @ConditionalOnMissingBean(KModuleBeanFactoryPostProcessor.class)
    public KModuleBeanFactoryPostProcessor kiePostProcessor() {
        return new KModuleBeanFactoryPostProcessor();
    }
}
