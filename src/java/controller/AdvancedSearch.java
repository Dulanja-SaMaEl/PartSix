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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
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
@MultipartConfig
@WebServlet(name = "AdvancedSearch", urlPatterns = {"/AdvancedSearch"})
public class AdvancedSearch extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        String checkedBrandIdsJson = req.getParameter("checkedBrandIds");
        String checkedModelIdJson = req.getParameter("checkedModelIds");
        String checkedConditionIdJson = req.getParameter("checkedConditionIds");
        String checkedAvailableIdJson = req.getParameter("checkedAvailableIds");
        double starting_price = Double.parseDouble(req.getParameter("starting_price"));
        double ending_price = Double.parseDouble(req.getParameter("ending_price"));

        // Parse JSON into an array or list
        String[] checkedBrandIds = new Gson().fromJson(checkedBrandIdsJson, String[].class);
        String[] checkedModelIds = new Gson().fromJson(checkedModelIdJson, String[].class);
        String[] checkedConditionIds = new Gson().fromJson(checkedConditionIdJson, String[].class);
        String[] checkedAvailableIds = new Gson().fromJson(checkedAvailableIdJson, String[].class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria1 = session.createCriteria(Product.class);

        if (!checkedModelIdJson.equals("[]")) {
            //not empty
            Criteria criteria = session.createCriteria(Model.class);

            // Convert checkedModelIds to a list of Integer (assuming Model's id is Integer)
            List<Integer> ids = new ArrayList<>();
            for (String id : checkedModelIds) {
                ids.add(Integer.parseInt(id));
            }
            criteria.add(Restrictions.in("id", ids));
            List<Model> modelList = criteria.list();
            criteria1.add(Restrictions.in("model", modelList));

        } else if (!checkedBrandIdsJson.equals("[]")) {
            //not empty
            Criteria criteria = session.createCriteria(Brand.class);

            // Convert checkedModelIds to a list of Integer (assuming Model's id is Integer)
            List<Integer> ids = new ArrayList<>();
            for (String id : checkedBrandIds) {
                ids.add(Integer.parseInt(id));
            }
            criteria.add(Restrictions.in("id", ids));
            List<Brand> brandList = criteria.list();

            Criteria criteria2 = session.createCriteria(Model.class);
            criteria2.add(Restrictions.in("brand", brandList));

            List<Model> modelList = criteria2.list();

            criteria1.add(Restrictions.in("model", modelList));

        } else if (!checkedConditionIdJson.equals("[]")) {
            //not empty
            Criteria criteria = session.createCriteria(ProductCondition.class);

            // Convert checkedModelIds to a list of Integer (assuming Model's id is Integer)
            List<Integer> ids = new ArrayList<>();
            System.out.println(checkedConditionIds);
            for (String id : checkedConditionIds) {
                ids.add(Integer.parseInt(id));
            }

            criteria.add(Restrictions.in("id", ids));
            List<ProductCondition> productConditionList = criteria.list();
            criteria1.add(Restrictions.in("productCondition", productConditionList));
        } else if (!checkedAvailableIdJson.equals("[]")) {

            System.out.println("Hello");
            //not empty
            Criteria criteria = session.createCriteria(ProductStatus.class);

            // Convert checkedModelIds to a list of Integer (assuming Model's id is Integer)
            List<Integer> ids = new ArrayList<>();
            for (String id : checkedAvailableIds) {
                ids.add(Integer.parseInt(id));
            }

            criteria.add(Restrictions.in("id", ids));
            List<ProductStatus> productStatusList = criteria.list();
            criteria1.add(Restrictions.in("productStatus", productStatusList));
        } 
        criteria1.add(Restrictions.ge("price", starting_price));
        criteria1.add(Restrictions.le("price", ending_price));

        List<Product> productList = criteria1.list();

        responseJson.add("productList", gson.toJsonTree(productList));
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

}
