/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

async function  loadProducts(first_result) {

    if (first_result == null) {
        const response = await fetch("LoadProducts?simple_sort=" + document.getElementById("simple-sort").value + "&firstResult=" + 0);

        if (response.ok) {
            const json = await response.json();
            console.log(json);
            updateProductView(json);
        } else {
            console.log("Server Error");
        }
    } else {
        const response = await fetch("LoadProducts?simple_sort=" + document.getElementById("simple-sort").value + "&firstResult=" + first_result);

        if (response.ok) {
            const json = await response.json();
            console.log(json);
            updateProductView(json);
        } else {
            console.log("Server Error");
        }
    }


}
var st_product = document.getElementById("product");
var st_pagination_button = document.getElementById("st-pagination-button");

var currentPage = 0;

function updateProductView(json) {
    let productContainer = document.getElementById("productContainer");
    productContainer.innerHTML = "";

    json.productList.forEach(product => {
        let product_clone = st_product.cloneNode(true);

        product_clone.querySelector("#view-product-details").href = "single-product.html?pid=" + product.id;
        product_clone.querySelector("#product-brand").innerHTML = product.model.brand.name;
        product_clone.querySelector("#product-model").innerHTML = product.title;
        product_clone.querySelector("#product-price").innerHTML = product.price;
        product_clone.querySelector("#product-img").src = "product_images/" + product.id + "/image1.png";

        productContainer.appendChild(product_clone)
    });

    //start pagination
    let st_pagination_container = document.getElementById("st-pagination-container");
    st_pagination_container.innerHTML = "";

    let product_count = json.allProductCount;
    const product_per_page = 6;

    let pages = Math.ceil(product_count / product_per_page);


    //add page buttons
    for (let i = 0; i < pages; i++) {
        let st_pagination_button_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_clone.querySelector("#page-number").innerHTML = i + 1;

        st_pagination_button_clone.addEventListener("click", e => {
            currentPage = i;
            loadProducts(i * 6);
        });

        if (i === currentPage) {
            st_pagination_button_clone.className = "active";
        } else {
            st_pagination_button_clone.className = "";
        }

        st_pagination_container.appendChild(st_pagination_button_clone);
    }

}

