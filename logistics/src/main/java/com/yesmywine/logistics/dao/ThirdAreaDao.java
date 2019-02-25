package com.yesmywine.logistics.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.logistics.entity.ThirdArea;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangdiandian on 2017/7/21.
 */
@Repository
public interface ThirdAreaDao extends BaseRepository<ThirdArea,Integer> {
    ThirdArea findByAreaIdAndThirdAreaName(Integer areaId,String thirdAreaName);
    List<ThirdArea> findByAreaId(Integer areaId);
}
