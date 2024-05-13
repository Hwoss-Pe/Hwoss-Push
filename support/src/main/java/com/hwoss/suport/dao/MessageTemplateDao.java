package com.hwoss.suport.dao;

import com.hwoss.suport.domain.MessageTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * @author Hwoss
 * @date 2024/05/11
 * 采用jpa的方式，先去分别继承简单查询和复杂查询，这里面的value不是实体就是主键
 * 实现后特点是在实体类里面做好表的映射，然后可以通过方法名进行映射查找
 */
public interface MessageTemplateDao extends JpaRepository<MessageTemplate, Long>, JpaSpecificationExecutor<MessageTemplate> {
    /**
     * @param deleted
     * @param pageable 这个是自定义好的分页查询功能，这里默认是第0页20条数据，可改配置文件
     * @return {@link List }<{@link MessageTemplate }>
     */
    List<MessageTemplate> findAllByIsDeletedEqualsOrderByUpdatedDesc(Integer deleted, Pageable pageable);

    /**
     * @param deleted 0：未删除 1：删除
     * @return {@link Long }
     * 统计未被删除的条数
     */
    Long countByIsDeletedEquals(Integer deleted);
}
