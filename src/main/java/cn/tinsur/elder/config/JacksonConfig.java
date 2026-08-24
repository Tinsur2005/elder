package cn.tinsur.elder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson全局序列化配置
 * 解决雪花算法Long类型ID返回前端JS精度丢失问题
 * 将所有Long类型序列化为字符串返回
 */
@Configuration
public class JacksonConfig {

    /**
     * 自定义ObjectMapper对象，替换SpringBoot默认的Jackson序列化实例
     * @Primary 优先使用本Bean，覆盖系统默认ObjectMapper
     * @ConditionalOnMissingBean 容器中不存在ObjectMapper才创建，避免重复覆盖
     * @param builder Jackson2ObjectMapperBuilder Spring提供的构建器
     * @return ObjectMapper 自定义序列化对象
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 创建ObjectMapper实例，关闭xml映射
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 创建自定义模块，用于注册自定义序列化器
        SimpleModule simpleModule = new SimpleModule();

        // 注册序列化器：Long对象序列化为字符串
        // ToStringSerializer.instance：直接调用对象toString()输出字符串
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);

        // 将自定义模块注册到ObjectMapper，序列化时生效
        objectMapper.registerModule(simpleModule);

        return objectMapper;
    }
}