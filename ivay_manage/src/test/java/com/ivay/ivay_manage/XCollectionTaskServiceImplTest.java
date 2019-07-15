package com.ivay.ivay_manage;

import com.alibaba.fastjson.JSONObject;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_manage.service.UserService;
import com.ivay.ivay_manage.service.XCollectionTaskService;
import com.ivay.ivay_repository.dto.CollectionTaskInfo;
import com.ivay.ivay_repository.dto.UserName;
import com.ivay.ivay_repository.model.SysUser;
import com.ivay.ivay_repository.model.XCollectionTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @ClassName XCollectionTaskServiceImplTest
 * @Description XCollectionTaskServiceImplTest
 * @Author Ryan
 * @Date 2019/7/9 14:19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class XCollectionTaskServiceImplTest {

    @Autowired
    private XCollectionTaskService collectionTaskService;
    @Autowired
    private UserService userService;

    @Test
    public void saveCollectionTaskBatch(){
        collectionTaskService.saveCollectionTaskBatch();
    }

    @Test
    public void list(){
        PageTableRequest request = new PageTableRequest();
        request.setOffset(0);
        request.setLimit(10);
        CollectionTaskInfo collectionTaskInfo = new CollectionTaskInfo();
        //collectionTaskInfo.setOverdueLevel("M1");
        //collectionTaskInfo.setCollectionStatus((byte)1);
        //collectionTaskInfo.setPhone("9888888888");
        PageTableResponse response = collectionTaskService.list(10,1, collectionTaskInfo);
        System.out.println("==========================");
        System.out.println(JSONObject.toJSONString(response));
    }

    @Test
    public void updateCollector(){

        //collectionTaskService.updateCollector(3, 3663);
    }

    @Test
    public void nameList(){

        List<UserName> sysUserList = userService.getUserNames();
        System.out.println(JSONObject.toJSONString(sysUserList));
    }

    @Test
    public void getCollectionListByUserGidTest(){
        CollectionTaskInfo collectionTaskInfo = new CollectionTaskInfo();
        PageTableResponse response = collectionTaskService.getCollectionListByUserGid(10,1,collectionTaskInfo);
        System.out.println(JSONObject.toJSONString(response));
    }

    @Test
    public void getCollectionsRepayList(){
        CollectionTaskInfo collectionTaskInfo = new CollectionTaskInfo();
        System.out.println(JSONObject.toJSONString(collectionTaskService.getCollectionsRepayList(10,1,collectionTaskInfo)));
    }
}
