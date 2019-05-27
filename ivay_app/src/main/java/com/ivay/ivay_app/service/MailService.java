package com.ivay.ivay_app.service;

import com.ivay.ivay_app.model.Mail;

import java.util.List;

public interface MailService {

	void save(Mail mail, List<String> toUser);
}
