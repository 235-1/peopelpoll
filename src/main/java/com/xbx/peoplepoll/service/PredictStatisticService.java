package com.xbx.peoplepoll.service;

import com.xbx.peoplepoll.pojo.LabelCount;
import org.springframework.stereotype.Service;
import java.util.List;

public interface PreStatisticService {

    List<LabelCount> getLabelCount();
}
