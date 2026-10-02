package org.dromara.system.service.impl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.oss.client.DefaultOssClientImpl;
import org.dromara.common.oss.client.OssClient;
import org.dromara.common.oss.config.OssClientConfig;
import org.dromara.common.oss.model.Options;
import org.dromara.common.oss.model.PutObjectResult;
import org.dromara.common.oss.properties.OssProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * Ozon 业务附件使用的对象存储客户端。
 * 只服务单一存储桶，密钥由环境变量注入，不读写 sys_oss_config，避免影响系统级文件存储配置。
 */
@Slf4j
@Component
public class OzonCosClient {
    /** 访问站点。 */
    @Value("${ozon.cos.endpoint:cos.ap-guangzhou.myqcloud.com}")
    private String endpoint;
    /** 存储区域。 */
    @Value("${ozon.cos.region:ap-guangzhou}")
    private String region;
    /** 存储桶。 */
    @Value("${ozon.cos.bucket:}")
    private String bucket;
    /** 访问密钥 ID。 */
    @Value("${ozon.cos.secret-id:}")
    private String secretId;
    /** 访问密钥。 */
    @Value("${ozon.cos.secret-key:}")
    private String secretKey;
    /** 惰性初始化的客户端，构造代价高且只在首次上传时需要。 */
    private volatile OssClient client;
    /** 是否已配置可用的存储桶与密钥。 */
    public boolean enabled(){
        return StringUtils.isNotBlank(bucket)&&StringUtils.isNotBlank(secretId)&&StringUtils.isNotBlank(secretKey);
    }
    /** 上传对象并返回公开访问地址。 */
    public PutObjectResult upload(String key,byte[] data,String contentType){
        OssClient target=instance();
        Options options=Options.builder();
        if(StringUtils.isNotBlank(contentType))options.setContentType(contentType);
        try{
            return target.upload(key,data,options);
        }catch(Exception e){
            log.error("Ozon 附件上传失败: {}",key,e);
            throw new ServiceException("附件上传失败，请稍后重试");
        }
    }
    private OssClient instance(){
        if(!enabled())throw new ServiceException("附件存储未配置，请联系管理员");
        OssClient current=client;
        if(current!=null)return current;
        synchronized(this){
            if(client==null){
                OssProperties properties=new OssProperties();
                properties.setEndpoint(endpoint);
                properties.setRegion(region);
                properties.setBucketName(bucket);
                properties.setAccessKey(secretId);
                properties.setSecretKey(secretKey);
                properties.setIsHttps("Y");
                OssClient created=new DefaultOssClientImpl("ozon-business",OssClientConfig.formProperties(properties));
                created.initialize();
                client=created;
            }
            return client;
        }
    }
}
