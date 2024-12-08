package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Brand;
import entity.Model;
import entity.Product;
import entity.ProductCondition;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        //get request json
        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        //search all products
        Criteria criteria1 = session.createCriteria(Product.class);

        //add category filter
        //category selected
        String brandName = requestJsonObject.get("brand").getAsString();

        if (!brandName.equals("Sort by Brand")) {

            //get brand list from Db
            Criteria criteria2 = session.createCriteria(Brand.class);
            criteria2.add(Restrictions.eq("name", brandName));
            List<Brand> brandList = criteria2.list();

            //get models by category from DB
            Criteria criteria3 = session.createCriteria(Model.class);
            criteria3.add(Restrictions.in("brand", brandList));
            List<Model> modelList = criteria3.list();

            //filter products by model list from DB
            criteria1.add(Restrictions.in("model", modelList));
        }

        //brand selected
        String modelName = requestJsonObject.get("model").getAsString();

        if (!modelName.equals("Sort by Model")) {

            //get brand from Db
            Criteria criteria4 = session.createCriteria(Model.class);
            criteria4.add(Restrictions.eq("name", modelName));
            Model modelResult = (Model) criteria4.uniqueResult();

            //filter products by brand from DB
            criteria1.add(Restrictions.eq("model", modelResult));

        }

        //color selected
        String conditionName = requestJsonObject.get("condition").getAsString();

        if (!conditionName.equals("Sort by Condition")) {

            //get color from Db
            Criteria criteria5 = session.createCriteria(ProductCondition.class);
            criteria5.add(Restrictions.eq("name", conditionName));
            ProductCondition conditionResult = (ProductCondition) criteria5.uniqueResult();

            //filter products by color from DB
            criteria1.add(Restrictions.eq("productCondition", conditionResult));

        }

        //text selected
        String searchText = requestJsonObject.get("searchText").getAsString();

        if (!searchText.isEmpty()) {
            //filter products by text from DB
            criteria1.add(Restrictions.like("title", searchText, MatchMode.ANYWHERE));
        }

        //get all product count
        responseJsonObject.addProperty("allProductCount", criteria1.list().size());

        //set product range
        int firstResult = requestJsonObject.get("firstResult").getAsInt();
        criteria1.setFirstResult(firstResult);
        criteria1.setMaxResults(2);

        //get product list
        List<Product> productList = criteria1.list();
        System.out.println(productList.size());

        //get product list
        for (Product product : productList) { 
            product.setUser(null);
        }

        responseJsonObject.addProperty("success", true);
        responseJsonObject.add("productList", gson.toJsonTree(productList));

        //send response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));

    }

}
