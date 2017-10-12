package util;
/**
 * Created by philipsushkov on 2017-06-14.
 */
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

public abstract class SendEmailLinkedList {
    public static boolean sendEmail(LinkedList<String> testingResult) {
        final String[] emailList = new String[] {"victorl@q4websystems.com", "philips@q4websystems.com", "karenl@q4inc.com"};
        final String username = "test@q4websystems.com";
        final String password = "testing!";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            for(int ii = 0; ii < emailList.length; ii++) {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("test@q4websystems.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(emailList[ii]));
                message.setSubject("Gold Price Check Report " + new Date());
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Gold Price Check Report / " + new Date() + "\n");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                Iterator<String> itr = testingResult.iterator();
                while (itr.hasNext()) {
                    messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setText(itr.next() + "\n");
                    multipart.addBodyPart(messageBodyPart);
                }
                message.setContent(multipart);
                Transport.send(message);
                System.out.println("Message is sent to: " + emailList[ii]);
            }
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
