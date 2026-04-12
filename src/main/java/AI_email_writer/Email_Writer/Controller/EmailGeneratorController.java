package AI_email_writer.Email_Writer.Controller;

import AI_email_writer.Email_Writer.Entity.EmailRequest;
import AI_email_writer.Email_Writer.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email-generator")
public class EmailGeneratorController
{
    @Autowired
    private EmailService es;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest er)
    {
        String response=es.generateEmailReply(er);
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
}
