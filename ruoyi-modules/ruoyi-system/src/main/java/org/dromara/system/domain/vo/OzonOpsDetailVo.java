package org.dromara.system.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单日运营简报**全文**。
 * <p>
 * 在摘要字段之上多带 {@code contentMd}（原文）与 {@code contentHtml}（预渲染 HTML）。
 * 前端默认渲染 {@code contentHtml}；「查看原文」展示 {@code contentMd}。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OzonOpsDetailVo extends OzonOpsDailyVo {
    /** 简报全文（markdown 原文）。 */
    private String contentMd;
    /** 简报全文（预渲染 HTML，前端直接 v-html 渲染）。 */
    private String contentHtml;
}
