package com.macro.mall.service;

import com.macro.mall.model.CmsTraceabilityList;
import java.util.Date;
import java.util.List;

public interface CmsTraceabilityListService {
    CmsTraceabilityList create(CmsTraceabilityList traceabilityList);

    CmsTraceabilityList update(Integer id, CmsTraceabilityList traceabilityList);

    boolean delete(Integer id);

    CmsTraceabilityList getById(Integer id);

    List<CmsTraceabilityList> listByFilters(String title, String category, Date startTime, Date endTime, int pageNum, int pageSize);
}
