package com.ivay.ivay_manage;

import com.alibaba.fastjson.JSONObject;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.XCollectionRecordService;
import com.ivay.ivay_repository.model.XCollectionRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @ClassName XCollectionRecordServiceImplTest
 * @Description XCollectionRecordServiceImplTest
 * @Author Ryan
 * @Date 2019/7/10 18:23
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class XCollectionRecordServiceImplTest {

    @Autowired
    private XCollectionRecordService xCollectionRecordService;

    @Test
    public void list(){
        PageTableRequest request = new PageTableRequest();
        request.setOffset(0);
        request.setLimit(10);
        PageTableResponse response = xCollectionRecordService.list(request);
        System.out.println(JSONObject.toJSONString(response));
    }

    @Test
    public void save(){
        XCollectionRecord x = new XCollectionRecord();
        x.setCollectorId(3);
        x.setUpdateTime(new Date());
        x.setUpdateTime(new Date());
        x.setCollectionTime(new Date());
        x.setCollectionReason("无法联系");
        x.setCollectionPhone("12347683276");
        x.setRemark("备注信息");
        x.setTaskId(3665);

        xCollectionRecordService.save(x);
    }


}
