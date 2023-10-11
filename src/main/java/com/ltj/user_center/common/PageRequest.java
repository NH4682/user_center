package com.ltj.user_center.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {


    private static final long serialVersionUID = -3725769427173689559L;
    /**
     * 页面大小
     */
    protected int pageSize;

    /**
     * 当前是第几页
     */
    protected int pageNum;
}