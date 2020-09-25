package com.lyj.pay.dao;

import com.lyj.pay.pojo.PayInfo;

public interface PayInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    PayInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayInfo record);

    int updateByPrimaryKey(PayInfo record);
    //通过订单查询
    PayInfo selectByOrderNo(Long orderNo);
}