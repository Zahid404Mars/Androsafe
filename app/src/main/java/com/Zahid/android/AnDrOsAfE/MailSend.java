package com.Zahid.android.AnDrOsAfE;

/**
 * @author jahid hasan
 * MailSend class to access MailJet and send mail
 */

import android.util.Log;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Timestamp;

public class MailSend {
    static String HTMLContent="Temp",base64send;
    String timestamp=String.valueOf(new Timestamp(System.currentTimeMillis())); //Get the current time-stamp
    MailSend(String base64){

        //This uses a pre-defined HTML template for body of the e-mail
        HTMLContent="<p style=\"text-align: center;\"><span style=\"font-size: 24px;\"><strong><span style=\"color: rgb(255, 0, 0);\">Mongol Intrusion Alert</span></strong></span></p>\n" +
                "<p style=\"text-align: center;\"><u>A Wrong PIN Was Entered On Your Phone</u></p>\n" +
                "<p style=\"text-align: center;\">Device:"+SecurityService.deviceDetails+"</p>\n" +
                "<p style=\"text-align: center;\">Time Stamp:"+timestamp+"</p>\n" +
                "<p style=\"text-align: center;\">&nbsp;Intruders Image has been attached.</p>\n" +
                "<p style=\"text-align: center;\"><br></p>";
        base64send=base64;
        try {
            if(SecurityService.senderEmail!=null){
                SendMail(HTMLContent);
            }
        } catch (Exception e) {
            Log.d("AnDrOsAfE.MailSend",e.getMessage());
        }
    }


    static void SendMail(String HTML) throws JSONException, MailjetSocketTimeoutException, MailjetException {
        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;
        client = new MailjetClient("0de27e064f6445b1e0d94207f3b716b1", "3c25dfc4ee751c53ddf9e0ddbc16abc5", new ClientOptions("v3.1"));
        //The request contains various JSON objects and JSON key-value pairs
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", "jahidhasanakondo@gmail.com")
                                        .put("Name", "AnDrOsAfEAdmin"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", SecurityService.senderEmail)
                                                .put("Name", "Client")))
                                .put(Emailv31.Message.SUBJECT, "Greetings from AnDrOsAfE")
                                .put(Emailv31.Message.TEXTPART, "Incorrect Pin Alert")
                                .put(Emailv31.Message.HTMLPART, HTMLContent)
                                .put(Emailv31.Message.ATTACHMENTS, new JSONArray()
                                        .put(new JSONObject()
                                                .put("ContentType", "image/bmp")
                                                .put("Filename", "Intruder.bmp")
                                                .put("Base64Content", base64send)))));
        response = client.post(request);  //2XX: OK,  4XX: Not-OK
        Log.d("response", String.valueOf(response.getStatus()));
        Log.d("response", String.valueOf(response.getData()));
        Log.d("mailSent to",SecurityService.senderEmail);
    }
}
