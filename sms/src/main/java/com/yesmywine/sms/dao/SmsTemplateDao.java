package com.yesmywine.sms.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.sms.entity.SmsTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangdiandian on 2017/5/8.
 */
@Repository
public interface SmsTemplateDao extends BaseRepository<SmsTemplate,Integer> {
   SmsTemplate findByCode(String code);
}
