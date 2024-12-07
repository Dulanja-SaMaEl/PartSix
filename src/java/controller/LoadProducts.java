/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

/**
 *
 * @author dulan
 */
@WebServlet(name = "LoadProducts", urlPatterns = {"/LoadProducts"})
public class LoadProducts extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        
        String simple_sort = req.getParameter("simple_sort");
        //set product range
        int firstResult = Integer.parseInt(req.getParameter("firstResult"));
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Criteria criteria1 = session.createCriteria(Product.class);
        //get all product count
        responseJson.addProperty("allProductCount", criteria1.list().size());
        
        if (simple_sort.equals("default")) {
            criteria1.addOrder(Order.asc("title"));
            responseJson.addProperty("success", true);
        } else if (simple_sort.equals("newest")) {
            criteria1.addOrder(Order.asc("datetime"));
            responseJson.addProperty("success", true);
        } else if (simple_sort.equals("oldest")) {
            criteria1.addOrder(Order.desc("datetime"));
            responseJson.addProperty("success", true);
        } else if (simple_sort.equals("highest")) {
            criteria1.addOrder(Order.desc("price"));
            responseJson.addProperty("success", true);
        } else if (simple_sort.equals("lowest")) {
            criteria1.addOrder(Order.asc("price"));
            responseJson.addProperty("success", true);
        }
        
        criteria1.setFirstResult(firstResult);
        criteria1.setMaxResults(6);
        List<Product> productList = criteria1.list();
        responseJson.add("productList", gson.toJsonTree(productList));
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }
    
}
