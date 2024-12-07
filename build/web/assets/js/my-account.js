/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
var modelList;
async function loadDetailsForAddProduct() {

    const response = await fetch("LoadDetailsForAddProduct");

    if (response.ok) {
        const json = await response.json();
        const brandList = json.brandList;
        modelList = json.modelList;
        const productConditionsList = json.productConditionsList;
        const productStatusesList = json.productStatusesList;

        loadSelectors("select-model", modelList, ["id", "name"]);
        loadSelectors("select-brand", brandList, ["id", "name"]);
        loadSelectors("select-condition", productConditionsList, ["id", "name"]);
        loadSelectors("select-status", productStatusesList, ["id", "name"]);
    } else {
        console.log("Server Error");
    }
}

function loadSelectors(selector, dataList, properties) {

    let selectTag = document.getElementById(selector);

    dataList.forEach(data => {
        const option = document.createElement("option");
        option.innerHTML = data[properties[1]];
        option.value = data[properties[0]];
        selectTag.appendChild(option);
    });
}

function updateModels() {

    let modelSelectTag = document.getElementById("select-model");
    modelSelectTag.length = 1;
    let selectedBrandId = document.getElementById("select-brand").value;
    modelList.forEach(model => {
        if (model.brand.id == selectedBrandId) {
            let optionTag = document.createElement("option");
            optionTag.value = model.id;
            optionTag.innerHTML = model.name;
            modelSelectTag.appendChild(optionTag);
        }
    });
}

async function addProduct() {

    var formData = new FormData();
    formData.append("title", document.getElementById("title").value);
    formData.append("description", document.getElementById("description").value);
    formData.append("brand", document.getElementById("select-brand").value);
    formData.append("model", document.getElementById("select-model").value);
    formData.append("price", document.getElementById("price").value);
    formData.append("qty", document.getElementById("qty").value);
    formData.append("condition", document.getElementById("select-condition").value);
    formData.append("status", document.getElementById("select-status").value);
    formData.append("image1", document.getElementById("image1").files[0]);
    formData.append("image2", document.getElementById("image2").files[0]);
    formData.append("image3", document.getElementById("image3").files[0]);
    


    const response = await fetch("AddProduct", {
        method: "POST",
        body: formData
    }
    );

    if (response.ok) {
        const json = await response.json();
        console.log(json);
    } else {
        console.log("Server Error");
    }
}
