package com.hwoss.business;

import com.alibaba.fastjson.JSON;
import com.common.domain.AnchorInfo;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * @author Hwoss
 * @date 2024/05/27
 * 把flink接受到字符串转化成对象的形式
 */
public class MapFunction implements FlatMapFunction<String, AnchorInfo> {
    @Override
    public void flatMap(String value, Collector<AnchorInfo> collector) throws Exception {
        AnchorInfo anchorInfo = JSON.parseObject(value, AnchorInfo.class);
        collector.collect(anchorInfo);
    }
}
