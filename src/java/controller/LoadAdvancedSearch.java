/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Brand;
import entity.Model;
import entity.Product;
import entity.ProductCondition;
import entity.ProductStatus;
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

/**
 *
 * @author dulan
 */
@WebServlet(name = "LoadAdvancedSearch", urlPatterns = {"/LoadAdvancedSearch"})
public class LoadAdvancedSearch extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        

        //get models
        Criteria criteria1 = session.createCriteria(Model.class);
        List<Model> modelList = criteria1.list();

        //get brands
        Criteria criteria2 = session.createCriteria(Brand.class);
        List<Brand> brandList = criteria2.list();

        //get product conditions
        Criteria criteria3 = session.createCriteria(ProductCondition.class);
        List<ProductCondition> productConditionsList = criteria3.list();

        //get product status
        Criteria criteria4 = session.createCriteria(ProductStatus.class);
        List<ProductStatus> productStatusesList = criteria4.list();
        
         //get products
        Criteria criteria5 = session.createCriteria(Product.class);
        List<Model> productList = criteria5.list();
        
        responseJson.add("productList", gson.toJsonTree(productList));
        responseJson.add("modelList", gson.toJsonTree(modelList));
        responseJson.add("brandList", gson.toJsonTree(brandList));
        responseJson.add("productConditionsList", gson.toJsonTree(productConditionsList));
        responseJson.add("productStatusesList", gson.toJsonTree(productStatusesList));
        responseJson.addProperty("success", true);

        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

   

}
