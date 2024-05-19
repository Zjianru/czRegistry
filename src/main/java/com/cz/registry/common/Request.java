package com.cz.registry.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * request info
 *
 * @author Zjianru
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request implements Serializable {
    /**
     * 请求方地址
     */
    private String url;
    /**
     * 传递参数
     */
    private Map<String, String> params = new HashMap<>();

}
