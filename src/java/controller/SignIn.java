/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Cart_Dto;
import dto.User_Dto;
import entity.Cart;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author dulan
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        User_Dto user_Dto = gson.fromJson(req.getReader(), User_Dto.class);

        if (user_Dto.getEmail().isEmpty()) {
            System.out.println(user_Dto.getEmail());
            responseJson.addProperty("message", "Empty Email");
        } else if (user_Dto.getPassword().isEmpty()) {
            System.out.println(user_Dto.getPassword());
            responseJson.addProperty("message", "Empty password");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.and(
                    Restrictions.eq("email", user_Dto.getEmail()), Restrictions.eq("password", user_Dto.getPassword()
            )));

            if (criteria1.list().isEmpty()) {
                //not found
                responseJson.addProperty("message", "User not found");
            } else {
                //found

                User user = (User) criteria1.uniqueResult();

                if (user.getVerification().equals("verified")) {
                    //verified

                    User_Dto user_Dto1 = new User_Dto();
                    user_Dto1.setEmail(user.getEmail());
                    user_Dto1.setUsername(user.getUsername());
                    user_Dto.setPassword(null);

                    HttpSession session1 = req.getSession();

                    req.getSession().setAttribute("user", user_Dto1);
                    responseJson.addProperty("message", "User found");
                    responseJson.addProperty("success", true);

                    if (req.getSession().getAttribute("sessionCart") != null) {

                        ArrayList<Cart_Dto> cart_Dtos = (ArrayList<Cart_Dto>) req.getSession().getAttribute("sessionCart");

                        Criteria criteria2 = session.createCriteria(Cart.class);
                        criteria2.add(Restrictions.eq("user", user));
                        List<Cart> cartList = criteria2.list();

                        if (cartList.isEmpty()) {
                            //DB cart is empty

                            //add each session product into cart
                            for (Cart_Dto cart_Dto : cart_Dtos) {
                                Cart cart = new Cart();
                                cart.setProduct(cart_Dto.getProduct());
                                cart.setQty(cart_Dto.getQty());
                                cart.setUser(cart_Dto.getUser());

                                session.save(cart);
                            }

                        } else {
                            //DB cart is not empty
                            for (Cart_Dto cart_Dto : cart_Dtos) {

                                boolean isFoundInDbCart = false;
                                for (Cart cart : cartList) {

                                    if (cart_Dto.getProduct().getId() == cart.getProduct().getId()) {
                                        //item found in db cart
                                        isFoundInDbCart = true;

                                        if (cart_Dto.getQty() + cart.getQty() <= cart.getProduct().getQty()) {
                                            //Quantity Can be Added
                                            cart.setQty(cart_Dto.getQty());
                                            session.update(cart);
                                        } else {
                                            //quantity not available 
                                            //add max amount that can add
                                            cart.setQty(cart.getProduct().getQty());
                                            session.update(cart);
                                        }
                                    }
                                }
                                if (!isFoundInDbCart) {
                                    Cart cart = new Cart();
                                    cart.setProduct(cart_Dto.getProduct());
                                    cart.setQty(cart_Dto.getQty());
                                    cart.setUser(user);
                                    session.save(cart);
                                }
                            }

                        }
                    }
                } else {
                    //not verified
                    
                  
                    
                    if (user.getVerification().equals(user_Dto.getVerification()) ) {
                        //user passed

                        user.setVerification("verified");
                        session.save(user);

                        User_Dto user_Dto1 = new User_Dto();
                        user_Dto1.setEmail(user.getEmail());
                        user_Dto1.setUsername(user.getUsername());
                        user_Dto.setPassword(null);

                        HttpSession session1 = req.getSession();

                        req.getSession().setAttribute("user", user_Dto1);
                        responseJson.addProperty("message", "User found");
                        responseJson.addProperty("success", true);

                        if (req.getSession().getAttribute("sessionCart") != null) {

                            ArrayList<Cart_Dto> cart_Dtos = (ArrayList<Cart_Dto>) req.getSession().getAttribute("sessionCart");

                            Criteria criteria2 = session.createCriteria(Cart.class);
                            criteria2.add(Restrictions.eq("user", user));
                            List<Cart> cartList = criteria2.list();

                            if (cartList.isEmpty()) {
                                //DB cart is empty

                                //add each session product into cart
                                for (Cart_Dto cart_Dto : cart_Dtos) {
                                    Cart cart = new Cart();
                                    cart.setProduct(cart_Dto.getProduct());
                                    cart.setQty(cart_Dto.getQty());
                                    cart.setUser(cart_Dto.getUser());

                                    session.save(cart);
                                }

                            } else {
                                //DB cart is not empty
                                for (Cart_Dto cart_Dto : cart_Dtos) {

                                    boolean isFoundInDbCart = false;
                                    for (Cart cart : cartList) {

                                        if (cart_Dto.getProduct().getId() == cart.getProduct().getId()) {
                                            //item found in db cart
                                            isFoundInDbCart = true;

                                            if (cart_Dto.getQty() + cart.getQty() <= cart.getProduct().getQty()) {
                                                //Quantity Can be Added
                                                cart.setQty(cart_Dto.getQty());
                                                session.update(cart);
                                            } else {
                                                //quantity not available 
                                                //add max amount that can add
                                                cart.setQty(cart.getProduct().getQty());
                                                session.update(cart);
                                            }
                                        }
                                    }
                                    if (!isFoundInDbCart) {
                                        Cart cart = new Cart();
                                        cart.setProduct(cart_Dto.getProduct());
                                        cart.setQty(cart_Dto.getQty());
                                        cart.setUser(user);
                                        session.save(cart);
                                    }
                                }

                            }
                        }
                    } else {
                        //invalid verification ID
                        responseJson.addProperty("message", "User Need To Be Verified");
                    }
                }

            }
            session.beginTransaction().commit();
            session.close();

        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

}
