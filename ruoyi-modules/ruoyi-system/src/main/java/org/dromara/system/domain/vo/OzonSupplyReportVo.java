package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.OzonSupplyReport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 交货申请明细展示数据。 */
@Data
@AutoMapper(target = OzonSupplyReport.class)
public class OzonSupplyReportVo implements Serializable {
    /** 记录编号。 */
    private String id;

    /** 申请内部ID。 */
    private String orderId;

    /** 交货申请编号。 */
    private String applicationNo;

    /** 配送类型。 */
    private String deliveryType;

    /** 申请状态。 */
    private String status;

    /** 发运日期。 */
    private String shipmentDate;

    /** 发运时间段。 */
    private String shipmentTime;

    /** 存储集群。 */
    private String storageCluster;

    /** 发运点。 */
    private String dispatchPoint;

    /** 完成日期。 */
    private String completionDate;

    /** 子交货ID。 */
    private String deliveryId;

    /** 商品名称。 */
    private String productName;

    /** ItemCode。 */
    private String itemCode;

    /** SKU。 */
    private String sku;

    /** 商品流通性。 */
    private String liquidity;

    /** 商品数量。 */
    private Integer quantity;

    /** 商品体积。 */
    private String volume;

    /** Ozon详情链接。 */
    private String ozonOrderUrl;
    /** 关联产品货品图片的公开文件信息。 */
    private String attachmentJson;
}
