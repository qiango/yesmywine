package com.yesmywine.user.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.user.entity.BeanCenterFlow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ${shuang} on 2017/3/29.
 */
public interface BeanCenterFlowDao extends BaseRepository<BeanCenterFlow,Integer> {


    @Query("SELECT sum(beans) as beansAcount,channelId  FROM BeanCenterFlow WHERE createTime<=STR_TO_DATE( :endDate,'%Y-%m-%d') and createTime>STR_TO_DATE( :startDate,'%Y-%m-%d') GROUP BY channelId")
    List findAccout(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
