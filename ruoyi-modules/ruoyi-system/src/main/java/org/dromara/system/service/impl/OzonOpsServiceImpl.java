package org.dromara.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.RequiredArgsConstructor;
import org.dromara.system.domain.bo.OzonOpsQuery;
import org.dromara.system.domain.vo.OzonOpsDailyVo;
import org.dromara.system.domain.vo.OzonOpsDetailVo;
import org.dromara.system.mapper.OzonOpsDailyMapper;
import org.dromara.system.service.IOzonOpsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 「运营 / 1.运营情况」查询实现。
 * <p>
 * 表 {@code ozon_ops_daily} 在业务库 {@code ozon}，默认数据源是 {@code ry-vue}，
 * 所以必须挂 {@code @DS("ozon")}（漏写会报 {@code Table 'ry-vue.ozon_ops_daily' doesn't exist}）。
 * <p>
 * 默认只返回最近 60 天：简报一天一条，历史再多也没有浏览价值，直接砍掉避免拖慢首屏。
 */
@Service
@RequiredArgsConstructor
@DS("ozon")
public class OzonOpsServiceImpl implements IOzonOpsService {

    private static final int DEFAULT_LIMIT = 60;
    private static final int MAX_LIMIT = 366;

    private final OzonOpsDailyMapper opsDailyMapper;

    @Override
    public List<OzonOpsDailyVo> list(OzonOpsQuery query) {
        if (query == null) {
            query = new OzonOpsQuery();
        }
        Integer limit = query.getLimit();
        if (limit == null || limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        query.setLimit(Math.min(limit, MAX_LIMIT));
        if (query.getDateFrom() != null && query.getDateTo() != null
            && query.getDateFrom().isAfter(query.getDateTo())) {
            var tmp = query.getDateFrom();
            query.setDateFrom(query.getDateTo());
            query.setDateTo(tmp);
        }
        return opsDailyMapper.selectList(query);
    }

    @Override
    public OzonOpsDetailVo detail(Long id) {
        if (id == null) {
            return null;
        }
        return opsDailyMapper.selectById(id);
    }
}
