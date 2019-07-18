package com.ivay.ivay_manage;

import com.ivay.ivay_manage.service.XCollectionCalculateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName XCollectionCalculateServiceImplTest
 * @Description TODO
 * @Author Ryan
 * @Date 2019/7/18 9:26
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class XCollectionCalculateServiceImplTest {

    @Autowired
    private XCollectionCalculateService xCollectionCalculateService;

    @Test
    public void saveCollectionCalculate(){
        xCollectionCalculateService.saveCollectionCalculate(null);
    }
}
