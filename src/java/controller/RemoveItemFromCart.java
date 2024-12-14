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
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author dulan
 */
@WebServlet(name = "RemoveItemFromCart", urlPatterns = {"/RemoveItemFromCart"})
public class RemoveItemFromCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        HttpSession httpSession = req.getSession();

        String cartItemId = req.getParameter("cart_item_id");

        if (!cartItemId.isEmpty()) {

            Session session = HibernateUtil.getSessionFactory().openSession();

            if (httpSession.getAttribute("user") != null) {
                //user logged in

                User_Dto user_Dto = (User_Dto) httpSession.getAttribute("user");

                // Retrieve user from the database
                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", user_Dto.getEmail()));
                User user = (User) criteria1.uniqueResult();

                if (user != null) {
                    // Find the cart item for this user and product
                    Criteria criteria2 = session.createCriteria(Cart.class);
                    criteria2.add(Restrictions.eq("user", user));
                    criteria2.add(Restrictions.eq("product.id", Integer.parseInt(cartItemId)));

                    Cart cartItem = (Cart) criteria2.uniqueResult();

                    if (cartItem != null) {
                        session.delete(cartItem);

                        responseJson.addProperty("success", true);
                        responseJson.addProperty("message", "Product removed from the cart.");
                    } else {
                        responseJson.addProperty("message", "Product not found in the cart.");
                    }
                } else {
                    responseJson.addProperty("message", "User not found.");
                }

            } else {
                //user not logged in (sessionCart)

                ArrayList<Cart_Dto> sessionCart = (ArrayList<Cart_Dto>) httpSession.getAttribute("sessionCart");

                if (sessionCart != null) { // Ensure the sessionCart is not null
                    Cart_Dto itemToRemove = null;

                    for (Cart_Dto cart_DTO : sessionCart) {
                        System.out.println(cartItemId);
                        if (cart_DTO.getProduct().getId() == Integer.parseInt(cartItemId)) {
                            itemToRemove = cart_DTO; // Mark the item for removal
                            break; // Exit the loop once the item is found
                        }
                    }

                    if (itemToRemove != null) {
                        sessionCart.remove(itemToRemove); // Remove the item outside the loop
                        httpSession.setAttribute("sessionCart", sessionCart); // Update the session
                        responseJson.addProperty("success", true);
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
