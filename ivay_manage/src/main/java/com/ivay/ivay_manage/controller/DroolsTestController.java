package com.ivay.ivay_manage.controller;

import com.ivay.ivay_manage.model.Address;
import com.ivay.ivay_manage.model.fact.AddressCheckResult;
import io.swagger.annotations.Api;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * create by xx on 2018/1/10
 */
@Api(tags = "drools规则测试")
@RequestMapping("/test")
@RestController
public class DroolsTestController {

//    @Resource
//    private KieSession kieSession;
    
//    @Resource
//    private StatelessKieSession statelessKieSession;
    
    @Resource
    private KieContainer kieContainer;
    
    @Resource(name="middleKieContainer")
    private KieContainer kieContainerMiddle;

    @ResponseBody
    @GetMapping("/droopsTestMiddle/{contactNum}/{contactMaxNum}/{nameDis}/{year}/{macNum}/{gpsNum}")
    public String testMiddle(@PathVariable int contactNum, @PathVariable int contactMaxNum, @PathVariable int nameDis,
    		@PathVariable int year, @PathVariable int macNum, @PathVariable int gpsNum){
    	KieSession kSession = null;
    	AddressCheckResult result = new AddressCheckResult();
    	try {
        	kSession = kieContainerMiddle.newKieSession();
            Address address = new Address();
            address.setYear(year);
            address.setNameDis(nameDis);
            address.setContactNum(contactNum);
            address.setContactMaxNum(contactMaxNum);
            address.setMacNum(macNum);
            address.setGpsNum(gpsNum);
     
            kSession.insert(address);
            kSession.insert(result);
            
            int ruleFiredCount = kSession.fireAllRules();
            System.out.println("触发了" + ruleFiredCount + "条规则");
		} finally {
			// TODO: handle finally clause
			kSession.dispose();
		}


        if(result.isContactNumResult() && result.isNameDisResult() && result.isYearResult()){
            return "借款申请审核通过";
        }
        return "很抱歉，您的借款申请未通过审核";

    }
    
    @ResponseBody
    @GetMapping("/droopsTestPre/{contactNum}/{contactMaxNum}/{nameDis}/{year}/{macNum}/{gpsNum}")
    public String testPre(@PathVariable int contactNum,  @PathVariable int contactMaxNum, @PathVariable int nameDis,
    		@PathVariable int year, @PathVariable int macNum, @PathVariable int gpsNum){
    	KieSession kSession = null;
    	AddressCheckResult result = new AddressCheckResult();
    	try {
        	kSession = kieContainer.newKieSession();
            Address address = new Address();
            address.setYear(year);
            address.setNameDis(nameDis);
            address.setContactNum(contactNum);
            address.setContactMaxNum(contactMaxNum);
            address.setMacNum(macNum);
            address.setGpsNum(gpsNum);
     
            kSession.insert(address);
            kSession.insert(result);
            
            int ruleFiredCount = kSession.fireAllRules();
            System.out.println("触发了" + ruleFiredCount + "条规则");
		} finally {
			// TODO: handle finally clause
			kSession.dispose();
		}


        if(result.isContactNumResult() && result.isNameDisResult() && result.isMacNumResult()
        		&& result.isYearResult() && result.isGpsNumResult()){
            return "test贷前审核通过";
        }
        return "很抱歉，test贷前未通过审核";

    }
    
}
