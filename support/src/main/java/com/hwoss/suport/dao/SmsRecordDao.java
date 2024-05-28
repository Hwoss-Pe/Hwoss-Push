package com.hwoss.suport.dao;

import com.hwoss.suport.domain.SmsRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SmsRecordDao extends JpaRepository<SmsRecord, Long> {
    /**
     * 通过日期和手机号找到发送记录
     *
     * @param phone
     * @param sendDate
     * @return
     */
    List<SmsRecord> findByPhoneAndSendDate(Long phone, Integer sendDate);
}
