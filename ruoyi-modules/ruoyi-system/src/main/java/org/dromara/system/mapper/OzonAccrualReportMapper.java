package org.dromara.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.system.domain.OzonAccrualReport;
import org.dromara.system.domain.bo.OzonReportQuery;
import org.dromara.system.domain.vo.OzonAccrualReportVo;
import org.dromara.system.domain.vo.OzonAccrualSummaryVo;
import org.dromara.system.domain.vo.OzonProductSalesTrendVo;
import java.util.List;
import org.dromara.system.domain.vo.OzonProductSaleVo;
import org.dromara.system.domain.vo.OzonSalesProductVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/** 订单费用明细及按编号归集查询。 */
public interface OzonAccrualReportMapper extends BaseMapperPlus<OzonAccrualReport, OzonAccrualReportVo> {
    /** 聚合后筛选日期，确保同一费用编号的所有正负金额完整参与合计。 */
    Page<OzonAccrualSummaryVo> selectSummary(Page<OzonAccrualSummaryVo> page, @Param("q") OzonReportQuery query);
    /** 复用原视图筛选后的全部费用编号，按产品和月份汇总销售额。 */
    List<OzonProductSalesTrendVo> selectSalesTrend(@Param("q") OzonReportQuery query);
    /** 当前费用筛选范围内的产品及逐笔销售统计。 */
    List<OzonSalesProductVo> selectSalesProducts(@Param("q") OzonReportQuery query);
    /** 单产品逐笔销售，不对同一天的多笔销售再聚合。 */
    List<OzonProductSaleVo> selectSalesTransactions(@Param("q") OzonReportQuery query);
}
