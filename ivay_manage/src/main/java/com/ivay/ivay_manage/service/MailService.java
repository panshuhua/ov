package com.ivay.ivay_manage.service;

import com.ivay.ivay_repository.model.Mail;

import java.util.List;

public interface MailService {

	void save(Mail mail, List<String> toUser);
}
