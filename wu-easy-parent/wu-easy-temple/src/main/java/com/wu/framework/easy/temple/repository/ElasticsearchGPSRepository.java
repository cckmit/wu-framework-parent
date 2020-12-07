package com.wu.framework.easy.temple.repository;

import com.wu.framework.easy.temple.domain.DynGpsVehRun;
import com.wu.framework.easy.temple.domain.ElasticsearchUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface ElasticsearchGPSRepository extends ElasticsearchRepository<DynGpsVehRun,Integer> {

    List<DynGpsVehRun> findAllByIndustryAndGpsVdate(String industry,String vDate);
}
