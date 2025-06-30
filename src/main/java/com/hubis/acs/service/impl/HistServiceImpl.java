package com.hubis.acs.service.impl;

import com.hubis.acs.repository.dao.CommonDAO;
import com.hubis.acs.service.HistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistServiceImpl implements HistService {

    private final CommonDAO commonDAO;



}
