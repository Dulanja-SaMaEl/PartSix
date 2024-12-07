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
import entity.Product;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author dulan
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        String pid = req.getParameter("pid");
        String qty = req.getParameter("pqty");

        if (!Validations.isInteger(pid)) {
            //Not Valid Pid
            responseJson.addProperty("message", "Invalid Product Id");
        } else if (!Validations.isInteger(qty)) {
            //Not valid qty
            responseJson.addProperty("message", "Invalid Product Quantity");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            int productId = Integer.parseInt(pid);
            int quantity = Integer.parseInt(qty);

            if (quantity <= 0) {
                //quantity must be greater than 0
                responseJson.addProperty("message", "Quantity must be greater than 0");
            } else {

                Product product = (Product) session.get(Product.class, productId);

                if (product != null) {
                    //product found

                    if (req.getSession().getAttribute("user") != null) {
                        //DB cart
                        User_Dto user_DTO = (User_Dto) req.getSession().getAttribute("user");

                        //search user
                        Criteria criteria1 = session.createCriteria(User.class);
                        criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                        User user = (User) criteria1.uniqueResult();

                        //check in db cart
                        Criteria criteria2 = session.createCriteria(Cart.class);
                        criteria2.add(Restrictions.eq("user", user));
                        criteria2.add(Restrictions.eq("product", product));

                        if (criteria2.list().isEmpty()) {
                            //Item Not Found In Cart

                            if (quantity <= product.getQty()) {
                                //Enough Stock Available

                                Cart cart = new Cart();
                                cart.setQty(quantity);
                                cart.setUser(user);
                                cart.setProduct(product);

                                session.save(cart);
                                session.beginTransaction().commit();

                                responseJson.addProperty("message", "Product Added Successfull");
                            } else {
                                //Not Enough Stock
                                responseJson.addProperty("message", "Not Enough Stock");
                            }

                        } else {
                            //Item Found In Cart
                            Cart cartItem = (Cart) criteria2.uniqueResult();

                            cartItem.setQty(cartItem.getQty() + quantity);

                            session.update(cartItem);
                            session.beginTransaction().commit();
                        }
                    } else {
                        //session cart
                        HttpSession httpSession = req.getSession();

                        if (httpSession.getAttribute("sessionCart") != null) {
                            //Session Cart Found

                            ArrayList<Cart_Dto> sessionCart = (ArrayList<Cart_Dto>) httpSession.getAttribute("sessionCart");

                            Cart_Dto foundCart_DTO = null;

                            for (Cart_Dto cart_DTO : sessionCart) {

                                if (cart_DTO.getProduct().getId() == product.getId()) {
                                    foundCart_DTO = cart_DTO;
                                    break;
                                }

                            }

                            if (foundCart_DTO != null) {
                                //product found

                                if ((foundCart_DTO.getQty() + quantity) <= product.getQty()) {
                                    //update quantity in session cart

                                    foundCart_DTO.setQty(foundCart_DTO.getQty() + quantity);

                                    responseJson.addProperty("Message", "Product quantity updated | Session Cart Found");

                                } else {
                                    // quantity not available
                                    responseJson.addProperty("Message", "Quantity Not Available | Session Cart Found");

                                }

                            } else {
                                //product not found

                                if (quantity <= product.getQty()) {
                                    //add to session cart
                                    Cart_Dto cart_DTO = new Cart_Dto();
                                    cart_DTO.setProduct(product);
                                    cart_DTO.setQty(quantity);
                                    sessionCart.add(cart_DTO);

                                    responseJson.addProperty("Message", "Product Added To Session Cart | New Product");
                                    System.out.println(cart_DTO.getProduct().getTitle());

                                } else {
                                    //quantity not available
                                    responseJson.addProperty("Message", "Quantity Not Available");

                                }

                            }

                        } else {
                            //Session Cart Not Found

                            if (quantity <= product.getQty()) {
                                //add product into session cart

                                ArrayList<Cart_Dto> sessionCart = new ArrayList<>();

                                Cart_Dto cart_DTO = new Cart_Dto();
                                cart_DTO.setProduct(product);
                                cart_DTO.setQty(quantity);
                                sessionCart.add(cart_DTO);

                                req.getSession().setAttribute("sessionCart", sessionCart);

                                responseJson.addProperty("Message", "Product Added to session cart | Session Cart Not Found");
                                System.out.println(cart_DTO.getProduct().getTitle());

                            } else {
                                //not enough stock in session cart
                                responseJson.addProperty("Message", "Not Enough Stock | Session Cart Not Found");
                            }
                        }
                    }

                }
            }
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

}
