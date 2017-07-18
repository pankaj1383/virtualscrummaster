package com.aws.lambda.schedular.services;

import com.aws.lambda.schedular.dynamodb.pojo.DBUserInfo;
import com.aws.lambda.schedular.utils.Logger;
import com.aws.lambda.schedular.utils.Utils;
import com.sendgrid.SendGrid;

public final class EmailServiceImpl implements EmailService {

	private static final String FROM = "connect@transility.com";
	private static final String SUBJECT = "Virtual Scrum Master";

	public EmailServiceImpl() {
	}

	public boolean sendMailUsingSendGrid(DBUserInfo user) {
		try {
			Logger.log("EmailService.sendMailUsingSendGrid -> Attempting to send an email through SendGrid to "
					+ user.getEmail());

			SendGrid sendgrid = new SendGrid("TransilitySMTP", "transility123");

			SendGrid.Email email = new SendGrid.Email();
			email.setFrom(FROM);
			email.addTo(user.getEmail());
			email.setSubject(SUBJECT);

			String messageBody = Utils.readResourceFile("mail_template.html");

			messageBody = messageBody.replaceAll("USER_NAME", user.getName());
			
			messageBody = messageBody.replaceAll("ACTIVATION_KEY", user.getActivationKey());			

			email.setHtml(messageBody);

			sendgrid.send(email);
			Logger.log("EmailService.sendMailUsingSendGrid -> Email sent!");
			return true;
		} catch (Exception ex) {
			Logger.log("EmailService.sendMailUsingSendGrid -> Error message: " + ex.getMessage());
		}
		return false;
	}
}
