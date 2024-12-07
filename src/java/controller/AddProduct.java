/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.User_Dto;
import entity.Brand;
import entity.Model;
import entity.Product;
import entity.ProductCondition;
import entity.ProductStatus;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author dulan
 */
@MultipartConfig
@WebServlet(name = "AddProduct", urlPatterns = {"/AddProduct"})
public class AddProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String brand = req.getParameter("brand");
        String model = req.getParameter("model");
        String price = req.getParameter("price");
        String qty = req.getParameter("qty");
        String condition = req.getParameter("condition");
        String status = req.getParameter("status");

        Part image1 = req.getPart("image1");
        Part image2 = req.getPart("image2");
        Part image3 = req.getPart("image3");

        Session session = HibernateUtil.getSessionFactory().openSession();

        if (!Validations.isInteger(brand)) {
            responseJson.addProperty("message", "Invalid Brand");

        } else if (!Validations.isInteger(model)) {
            responseJson.addProperty("message", "Invalid Model");

        } else if (!Validations.isInteger(condition)) {
            responseJson.addProperty("message", "Invalid Condition");

        } else if (title.isEmpty()) {
            responseJson.addProperty("message", "Please Fill Title");
        } else if (description.isEmpty()) {
            responseJson.addProperty("message", "Please Fill Description");

        } else if (price.isEmpty()) {
            responseJson.addProperty("message", "Please Fill Price");

        } else if (!Validations.isDouble(price)) {
            responseJson.addProperty("message", "Invalid Price Type");

        } else if (Double.parseDouble(price) <= 0) {
            responseJson.addProperty("message", "Price must be greater than 0");

        } else if (qty.isEmpty()) {
            responseJson.addProperty("message", "Please Fill Quantity");

        } else if (status.isEmpty()) {
            responseJson.addProperty("message", "Please Fill Status");

        } else if (!Validations.isInteger(qty)) {
            responseJson.addProperty("message", "Invalid Quantity");

        } else if (Integer.parseInt(qty) <= 0) {
            responseJson.addProperty("message", "Quantity must be greater than 0");

        } else if (image1.getSubmittedFileName() == null) {
            responseJson.addProperty("message", "Please Submit Image 1");

        } else if (image2.getSubmittedFileName() == null) {
            responseJson.addProperty("message", "Please Submit Image 2");

        } else if (image3.getSubmittedFileName() == null) {
            responseJson.addProperty("message", "Please Submit Image 3");
        } else {

            Model productModel = (Model) session.get(Model.class, Integer.parseInt(model));
            ProductCondition productCondition = (ProductCondition) session.get(ProductCondition.class, Integer.parseInt(condition));
            ProductStatus productStatus = (ProductStatus) session.get(ProductStatus.class, Integer.parseInt(status));

            User_Dto user_DTO = (User_Dto) req.getSession().getAttribute("user");
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria.uniqueResult();

            Product product = new Product();
            product.setModel(productModel);
            product.setTitle(title);
            product.setDescription(description);
            product.setPrice(Double.parseDouble(price));
            product.setQty(Integer.parseInt(qty));
            product.setDatetime(new Date());
            product.setProductCondition(productCondition);
            product.setProductStatus(productStatus);
            product.setUser(user);

            int pid = (int) session.save(product);
            session.beginTransaction().commit();

            String applicationPath = getServletContext().getRealPath("");
            String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");
            
            System.out.println(newApplicationPath);

            File folder = new File(newApplicationPath + File.separator + "product_images" + File.separator + pid);
            folder.mkdir();

            File file1 = new File(folder, "image1.png");
            InputStream inputStream = image1.getInputStream();
            Files.copy(inputStream, file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

            File file2 = new File(folder, "image2.png");
            InputStream inputStream2 = image2.getInputStream();
            Files.copy(inputStream2, file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

            File file3 = new File(folder, "image3.png");
            InputStream inputStream3 = image3.getInputStream();
            Files.copy(inputStream3, file3.toPath(), StandardCopyOption.REPLACE_EXISTING);

        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

}
