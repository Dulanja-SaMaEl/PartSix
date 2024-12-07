var productQty;

async function loadSingleProduct() {

    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("pid")) {
        const pid = parameters.get("pid");
        const response = await fetch("LoadSingleProduct?pid=" + pid);

        if (response.ok) {
            const json = await response.json();
            const product = json.product;
            console.log(json);

            productQty = product.qty;

            document.getElementById("product-title").innerHTML = product.title;
            document.getElementById("product-description").innerHTML = product.description;
            document.getElementById("product-status").innerHTML = product.productStatus.name;
            document.getElementById("product-brand").innerHTML = product.model.brand.name;

            document.getElementById("image1").src = "product_images/" + product.id + "/image1.png";
            document.getElementById("image2").src = "product_images/" + product.id + "/image2.png";
            document.getElementById("image3").src = "product_images/" + product.id + "/image3.png";

            document.getElementById("image1_1").src = "product_images/" + product.id + "/image1.png";
            document.getElementById("image2_1").src = "product_images/" + product.id + "/image2.png";
             document.getElementById("image3_1").src = "product_images/" + product.id + "/image3.png";

            document.getElementById("add-to-cart").addEventListener("click", (e) => {
                addToCart(json.product.id, document.getElementById("pro-qunt").value);
                e.preventDefault();
            });


            loadRelatedProducts(json.relatedProductList);


        }
    } else {
        window.location = "index.html";
    }
}

function changeQuantity(amount, inputId) {
    const inputElement = document.getElementById(inputId);
    let currentValue = parseInt(inputElement.value);

    // Calculate the new value
    let newValue = currentValue + amount;

    // Ensure the value does not go below 1
    if (newValue < 1 || newValue > productQty) {
        newValue = 1;
    }

    // Update the input value
    inputElement.value = newValue;
}


var rProduct = document.getElementById("r-product");
function loadRelatedProducts(arrayList) {

    console.log(arrayList);

    let rProductContainer = document.getElementById("rProductContainer");
    rProductContainer.innerHTML = "";

    arrayList.forEach(relatedProduct => {
        let rProductClone = rProduct.cloneNode(true);

        rProductClone.querySelector("#view-r-product-details").href = "single-product.html?pid=" + relatedProduct.id;
        rProductClone.querySelector("#r-product-brand").innerHTML = relatedProduct.model.brand.name;
        rProductClone.querySelector("#r-product-model").innerHTML = relatedProduct.title;
        rProductClone.querySelector("#r-product-price").innerHTML = relatedProduct.price;
        rProductClone.querySelector("#r-product-img").src = "product_images/" + relatedProduct.id + "/image1.png";

        rProductContainer.append(rProductClone);
    });
}

async  function addToCart(p_id, pqty) {
    const response = await fetch("AddToCart?pid=" + p_id + "&pqty=" + pqty);
    if (response.ok) {
        const json = await response.json();
        console.log(json);
    } else {
        console.log("Server Error");
    }

}
