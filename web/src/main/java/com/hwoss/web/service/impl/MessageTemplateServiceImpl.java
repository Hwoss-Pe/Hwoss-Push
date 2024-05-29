package com.hwoss.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.common.constant.CommonConstant;
import com.common.constant.FuncConstant;
import com.common.enums.AuditStatus;
import com.common.enums.MessageStatus;
import com.common.enums.RespStatusEnum;
import com.common.enums.TemplateType;
import com.common.vo.BasicResultVo;
import com.hwoss.cron.xxl.pojo.XxlJobInfo;
import com.hwoss.cron.xxl.service.CronTaskService;
import com.hwoss.cron.xxl.utils.XxlJobUtils;
import com.hwoss.suport.dao.MessageTemplateDao;
import com.hwoss.suport.domain.MessageTemplate;
import com.hwoss.web.service.MessageTemplateService;
import com.hwoss.web.vo.MessageTemplateParam;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private CronTaskService cronTaskService;

    @Autowired
    private XxlJobUtils xxlJobUtils;

    @Override
    public Long count() {
        return messageTemplateDao.countByIsDeletedEquals(CommonConstant.FALSE);
    }

    /**
     * @param messageTemplate
     * @return {@link MessageTemplate }
     * 也是通过查看是否有id来决定是初始化还是进行一个更新
     */
    @Override
    public MessageTemplate saveOrUpdate(MessageTemplate messageTemplate) {
        if (Objects.isNull(messageTemplate.getId())) {
            initStatus(messageTemplate);
        } else {
//           重置模板以及一些定时任务
            resetStatus(messageTemplate);
        }
//更新修改时间
        messageTemplate.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        return messageTemplateDao.save(messageTemplate);
    }

    /**
     * @param ids 删除一个是在数据库进行一个软删除，其次如果存在定时任务也要进行删除，这个是强删除
     */
    @Override
    public void deleteByIds(List<Long> ids) {

        List<MessageTemplate> messageTemplates = messageTemplateDao.findAllById(ids);
        for (MessageTemplate messageTemplate : messageTemplates) {
            messageTemplate.setIsDeleted(CommonConstant.TRUE);
        }
        for (MessageTemplate messageTemplate : messageTemplates) {
            if (Objects.nonNull(messageTemplate.getCronTaskId()) && messageTemplate.getCronTaskId() > 0) {
                cronTaskService.deleteCronTask(messageTemplate.getCronTaskId());
            }
        }
        messageTemplateDao.saveAll(messageTemplates);
    }

    @Override
    public MessageTemplate queryById(Long id) {
        return messageTemplateDao.findById(id).orElse(null);
    }

    /**
     * @param id
     * @return {@link BasicResultVo }
     * 这里的思路是创建/更新并且启动定时器
     */
    @Override
    public BasicResultVo startCronTask(Long id) {
//        根据模板创建对应的定时器
        MessageTemplate messageTemplate = messageTemplateDao.findById(id).orElse(null);
        if (Objects.isNull(messageTemplate)) {
            return BasicResultVo.fail();
        }

        XxlJobInfo jobInfo = xxlJobUtils.getJobInfo(messageTemplate);

//      插入返回taskId，更新就不返回对应的数据
        Integer cronTaskId = messageTemplate.getCronTaskId();
        BasicResultVo basicResultVo = cronTaskService.saveCronTask(jobInfo);
//        如果是创建的，并且数据返回成功，那么taskId就会有数据
        if (Objects.isNull(cronTaskId) && Objects.nonNull(basicResultVo.getData()) && basicResultVo.getStatus().equals(RespStatusEnum.SUCCESS.getCode())) {
            cronTaskId = (Integer) basicResultVo.getData();
        }

//        启动定时任务，这里进行一个异步操作，启动后克隆出一个对象，在进行保存，对原始数据安全
        if (Objects.nonNull(cronTaskId)) {
            cronTaskService.startCronTask(cronTaskId);//这里如果启动后就修改原始数据是不太行的
//            更新messageTemplate的状态
            MessageTemplate clone = ObjectUtil.clone(messageTemplate);
            clone.setMsgStatus(MessageStatus.RUN.getCode())
                    .setCronTaskId(cronTaskId)
                    .setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
            messageTemplateDao.save(clone);
            return BasicResultVo.success();
        }

        return BasicResultVo.fail();
    }

    @Override
    public BasicResultVo stopCronTask(Long id) {
        // 修改模板状态
        MessageTemplate messageTemplate = messageTemplateDao.findById(id).orElse(null);
        if (Objects.isNull(messageTemplate)) {
            return BasicResultVo.fail();
        }
//也是采用clone出对象进行操作保证原始数据可靠
        MessageTemplate clone = ObjectUtil.clone(messageTemplate)
                .setMsgStatus(MessageStatus.STOP.getCode())
                .setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        messageTemplateDao.save(clone);
        return cronTaskService.stopCronTask(clone.getCronTaskId());
    }


    /**
     * 初始化状态信息
     *
     * @param messageTemplate
     */
    private void initStatus(MessageTemplate messageTemplate) {
//        初始化所有信息，全部设置默认，如果传就不变，不传就设置默认值
        messageTemplate.setFlowId(CharSequenceUtil.EMPTY)
                .setMsgStatus(MessageStatus.INIT.getCode()).setAuditStatus(AuditStatus.WAIT_AUDIT.getCode())
                .setCreator(CharSequenceUtil.isBlank(messageTemplate.getCreator()) ? FuncConstant.DEFAULT_CREATOR : messageTemplate.getCreator())
                .setUpdator(CharSequenceUtil.isBlank(messageTemplate.getUpdator()) ? FuncConstant.DEFAULT_UPDATOR : messageTemplate.getUpdator())
                .setTeam(CharSequenceUtil.isBlank(messageTemplate.getTeam()) ? FuncConstant.DEFAULT_TEAM : messageTemplate.getTeam())
                .setAuditor(CharSequenceUtil.isBlank(messageTemplate.getAuditor()) ? FuncConstant.DEFAULT_AUDITOR : messageTemplate.getAuditor())
                .setCreated(Math.toIntExact(DateUtil.currentSeconds()))
                .setIsDeleted(CommonConstant.FALSE);

    }

    /**
     * 1. 重置模板的状态
     * 2. 修改定时任务信息(如果存在)
     *
     * @param messageTemplate
     */
    private void resetStatus(MessageTemplate messageTemplate) {
        messageTemplate.setUpdator(messageTemplate.getUpdator())
                .setMsgStatus(MessageStatus.INIT.getCode()).setAuditStatus(AuditStatus.WAIT_AUDIT.getCode());

        // 从数据库查询并注入 定时任务 ID
        MessageTemplate dbMsg = queryById(messageTemplate.getId());
        if (Objects.nonNull(dbMsg) && Objects.nonNull(dbMsg.getCronTaskId())) {
            messageTemplate.setCronTaskId(dbMsg.getCronTaskId());
        }
        //从数据库复用这个cronId，创建新的定时任务的时候就可以复用，并且停止当前定时任务

        if (Objects.nonNull(messageTemplate.getCronTaskId()) && TemplateType.CLOCKING.getCode().equals(messageTemplate.getTemplateType())) {
            XxlJobInfo xxlJobInfo = xxlJobUtils.getJobInfo(messageTemplate);
            cronTaskService.saveCronTask(xxlJobInfo);
            cronTaskService.stopCronTask(messageTemplate.getCronTaskId());
        }
    }

    @Override
    public List<MessageTemplate> queryList(MessageTemplateParam param) {
//        传页码进来和单页数更新pageRequest
//                       页码-1
        PageRequest pageRequest = PageRequest.of(param.getPage() - 1, param.getPerPage());
        String creator = CharSequenceUtil.isBlank(param.getCreator()) ? FuncConstant.DEFAULT_CREATOR : param.getCreator();

        Page<MessageTemplate> messageTemplatePage = messageTemplateDao.findAll(new Specification<MessageTemplate>() {
            @Override
//            单体，定制查询，构建查询
            public Predicate toPredicate(@NotNull Root<MessageTemplate> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
//                增加搜索条件
                if (CharSequenceUtil.isNotBlank(param.getKeywords())) {
                    list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + param.getKeywords() + "%"));
                }
                list.add(criteriaBuilder.equal(root.get("isDeleted").as(Integer.class), CommonConstant.FALSE));
                list.add(criteriaBuilder.equal(root.get("creator").as(String.class), creator));
                Predicate[] p = new Predicate[list.size()];

                query.where(criteriaBuilder.and(list.toArray(p)));
                query.orderBy(criteriaBuilder.desc(root.get("updated")));

                return query.getRestriction();
            }
        }, pageRequest);
        List<MessageTemplate> content = messageTemplatePage.getContent();//查询的数据
        return content;
    }
}
