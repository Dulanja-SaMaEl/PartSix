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
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author dulan
 */
@WebServlet(name = "LoadCartItems", urlPatterns = {"/LoadCartItems"})
public class LoadCartItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        HttpSession httpSession = req.getSession();

        Session session = HibernateUtil.getSessionFactory().openSession();

        ArrayList<Cart_Dto> cart_Dtos = new ArrayList<>();

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        if (httpSession.getAttribute("user") != null) {
            //db cart
            User_Dto user_dto = (User_Dto) httpSession.getAttribute("user");

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_dto.getEmail()));
            User user = (User) criteria1.uniqueResult();

            Criteria criteria2 = session.createCriteria(Cart.class);
            criteria2.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria2.list();

            for (Cart cart : cartList) {

                Cart_Dto cart_DTO = new Cart_Dto();

                cart_DTO.setId(cart.getId());
                cart_DTO.setProduct(cart.getProduct());
                cart_DTO.setQty(cart.getQty());

                cart_Dtos.add(cart_DTO);
            }
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "success");

        } else {
            //session cart
            System.out.println("session cart");

            if (httpSession.getAttribute("sessionCart") != null) {
                System.out.println("cart not empty");
                cart_Dtos = (ArrayList<Cart_Dto>) httpSession.getAttribute("sessionCart");

                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "success");
            } else {
                //cart empty
            }
        }

        responseJson.add("cartList", gson.toJsonTree(cart_Dtos));
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

}
