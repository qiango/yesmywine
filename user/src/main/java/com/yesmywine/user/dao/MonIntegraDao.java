package com.yesmywine.user.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.user.entity.Channels;
import com.yesmywine.user.entity.MonIntegra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Created by hz on 3/27/17.
 */
@Repository
public interface MonIntegraDao extends BaseRepository<MonIntegra,Integer> {

    Page<MonIntegra> findByChannels(Channels channels, Pageable pageable);
     MonIntegra findByChannels(Channels channels);
}
