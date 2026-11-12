package org.dromara.system.service;

import org.dromara.system.domain.bo.OzonOpsQuery;
import org.dromara.system.domain.vo.OzonOpsDailyVo;
import org.dromara.system.domain.vo.OzonOpsDetailVo;

import java.util.List;

/**
 * 「运营 / 1.运营情况」查询服务。
 */
public interface IOzonOpsService {

    /** 简报日期列表（倒序），不含正文。 */
    List<OzonOpsDailyVo> list(OzonOpsQuery query);

    /** 单日简报全文；id 不存在时返回 null。 */
    OzonOpsDetailVo detail(Long id);
}
