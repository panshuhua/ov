package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dao.MailDao;
import com.ivay.ivay_app.model.Mail;
import com.ivay.ivay_app.service.MailService;
import com.ivay.ivay_app.service.SendMailSevice;
import com.ivay.ivay_app.utils.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private SendMailSevice sendMailSevice;
    @Autowired
    private MailDao mailDao;

    @Override
    @Transactional
    public void save(Mail mail, List<String> toUser) {
        mail.setUserId(UserUtil.getLoginUser().getId());
        mailDao.save(mail);

        toUser.forEach(u -> {
            int status = 1;
            try {
                sendMailSevice.sendMail(u, mail.getSubject(), mail.getContent());
            } catch (Exception e) {
                log.error("发送邮件失败", e);
                status = 0;
            }

            mailDao.saveToUser(mail.getId(), u, status);
        });

    }

}
