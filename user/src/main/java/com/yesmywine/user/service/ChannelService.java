
package com.yesmywine.user.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.user.entity.Channels;


/**
 * Created by hz on 2/10/17.
 */

public interface ChannelService extends BaseService<Channels, Integer> {

    Boolean synchronous(Integer id, String name, Integer synchronous, String code);
}

