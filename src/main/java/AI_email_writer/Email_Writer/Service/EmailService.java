package AI_email_writer.Email_Writer.Service;

import AI_email_writer.Email_Writer.Entity.EmailRequest;

public interface EmailService
{
    public String generateEmailReply(EmailRequest er);
}
