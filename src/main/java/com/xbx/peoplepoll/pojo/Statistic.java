package com.xbx.peoplepoll.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author X
 * @date 2026/5/10 11:19
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Statistic {
    Map<String,Object> data = new HashMap<>();
}
