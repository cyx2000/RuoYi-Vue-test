package com.ruoyi.common.config.serializer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.DesensitizedType;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 数据脱敏序列化过滤
 *
 * @author ruoyi
 */
public class SensitiveJsonSerializer extends ValueSerializer<String>
{
    private DesensitizedType desensitizedType;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext serializers)
    {
        if (desensitization())
        {
            gen.writeString(desensitizedType.desensitizer().apply(value));
        }
        else
        {
            gen.writeString(value);
        }
    }
    /**
     * 是否需要脱敏处理
     */
    private boolean desensitization()
    {
        try
        {
            LoginUser securityUser = SecurityUtils.getLoginUser();
            // 管理员不脱敏
            return !securityUser.getUser().isAdmin();
        }
        catch (Exception e)
        {
            return true;
        }
    }
}
