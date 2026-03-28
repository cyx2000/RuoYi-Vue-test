package com.ruoyi.common.core.db.parameter;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class NamedSqlParameterSource extends MapSqlParameterSource {

    public NamedSqlParameterSource() {

    }

    public void setDefaultOrderByStr(String inOrderByStr) {
        this.addValue("useToOrderByAppendStr", inOrderByStr);
    }

    public Object getDefaultOrderByStr() {
        return this.getValue("useToOrderByAppendStr");
    }

}
