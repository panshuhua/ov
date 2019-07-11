package com.ivay.ivay_repository.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserName
 * @Description 用户名字列表
 * @Author Ryan
 * @Date 2019/7/10 20:31
 */
@Data
public class UserName implements Serializable {

    private static final long serialVersionUID = -184878138990564168L;
    private Integer id;
    private String username;
}
