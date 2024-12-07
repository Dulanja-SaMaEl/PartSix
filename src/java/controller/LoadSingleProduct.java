/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Model;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author dulan
 */
@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", true);

        String pid = req.getParameter("pid");

        Session session = HibernateUtil.getSessionFactory().openSession();

        Product product = (Product) session.get(Product.class, Integer.parseInt(pid));
        product.getUser().setPassword(null);

        System.out.println(product.getModel().getId());


        Criteria criteria = session.createCriteria(Product.class);
        criteria.add(Restrictions.eq("model", product.getModel()));
        criteria.add(Restrictions.ne("id", product.getId()));
        criteria.setMaxResults(4); // Optional: limit to 4 results
        List<Product> relatedProductList = criteria.list();

        responseJson.add("relatedProductList", gson.toJsonTree(relatedProductList));
        responseJson.add("product", gson.toJsonTree(product));

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

}
