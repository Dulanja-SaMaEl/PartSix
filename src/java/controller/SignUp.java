/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.User_Dto;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Mail;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author dulan
 */
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        
        User_Dto user_Dto = gson.fromJson(req.getReader(), User_Dto.class);
        
        if (user_Dto.getUsername().isEmpty()) {
            responseJson.addProperty("message", "Username can not be empty");
        } else if (user_Dto.getEmail().isEmpty()) {
            responseJson.addProperty("message", "Email can not be empty");
        } else if (!Validations.isEmailValid(user_Dto.getEmail())) {
            responseJson.addProperty("message", "Email not valid");
        } else if (user_Dto.getPassword().isEmpty() || user_Dto.getPasswordc().isEmpty()) {
            responseJson.addProperty("message", "Password or Verified Password can not be empty");
        } else if (!user_Dto.getPassword().equals(user_Dto.getPasswordc())) {
            responseJson.addProperty("message", "Password not matched");
        } else if (!Validations.isPasswordValid(user_Dto.getPassword().toString())) {
            
            responseJson.addProperty("message", "Passwords not valid");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_Dto.getEmail()));
            
            if (!criteria1.list().isEmpty()) {
                responseJson.addProperty("message", "User Already Exists in this email");
            } else {
                
                int code = (int) (Math.random() * 100000);
                
                User user = new User();
                
                user.setEmail(user_Dto.getEmail());
                user.setPassword(user_Dto.getPassword());
                user.setUsername(user_Dto.getUsername());
                user.setVerification(String.valueOf(code));
                
                String content = "<html lang=\"en\">\n"
                        + "<head>\n"
                        + "    <meta charset=\"UTF-8\">\n"
                        + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                        + "    <title>Verify Your Email </title>\n"
                        + "</head>\n"
                        + "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;\">\n"
                        + "    <table align=\"center\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">\n"
                        + "        <tr>\n"
                        + "            <td style=\"text-align: center;\">\n"
                        + "                <h2 style=\"color: #333333;\">Verify Your Email</h2>\n"
                        + "                <p style=\"color: #666666;\">Thank you for signing up with Smart Trade!</p>\n"
                        + "                <p style=\"color: #666666;\">Please use the verification code below to complete your registration:</p>\n"
                        + "                <h3 style=\"color: #007bff; font-size: 24px; margin: 20px 0;\">" + code + "</h3>\n"
                        + "                <p style=\"color: #666666;\">If you didn’t request this, you can safely ignore this email.</p>\n"
                        + "                <p style=\"color: #666666;\">Contact Us on Facebook <b style=\"color: #007bff;\">(DULANJA ABEYSINGHE)</b></p>\n"
                        + "            </td>\n"
                        + "        </tr>\n"
                        + "    </table>\n"
                        + "</body>\n"
                        + "</html>";
                
                Thread sendMailThread = new Thread() {
                    @Override
                    public void run() {
                        Mail.sendMail(user_Dto.getEmail(), "Verification Smart Trade Account", content);
                    }
                };
                
                sendMailThread.start();
                session.save(user);
                session.beginTransaction().commit();
                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "Successfully Added");
            }
            
            session.close();
        }
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }
    
}
