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

    const titleTag = document.getElementById("title");
    const descriptionTag = document.getElementById("description");
    const brandTag = document.getElementById("select-brand");
    const modelTag = document.getElementById("select-model");
    const priceTag = document.getElementById("price");
    const qtyTag = document.getElementById("qty");
    const conditionTag = document.getElementById("select-condition");
    const statusTag = document.getElementById("select-status");
    const image1Tag = document.getElementById("image1");
    const image2Tag = document.getElementById("image2");
    const image3Tag = document.getElementById("image3");

    const formData = new FormData();
    formData.append("title", titleTag.value);
    formData.append("description", descriptionTag.value);
    formData.append("brand", brandTag.value);
    formData.append("model", modelTag.value);
    formData.append("price", priceTag.value);
    formData.append("qty", qtyTag.value);
    formData.append("condition", conditionTag.value);
    formData.append("status", statusTag.value);
    formData.append("image1", image1Tag.files[0]);
    formData.append("image2", image2Tag.files[0]);
    formData.append("image3", image3Tag.files[0]);


    const response = await fetch("AddProduct", {
        method: "POST",
        body: formData
    }
    );

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.success) {
            console.log("New Product Added.");
            titleTag.value = "";
            descriptionTag.value = "";
            brandTag.value = 0;
            modelTag.value = 0;
//            modelTag.length = 1;
            priceTag.value = "";
            qtyTag.value = "";
            conditionTag.value = 0;
            statusTag.value = 0;
            image1Tag.value = null;
            image2Tag.value = null;
            image3Tag.value = null;
        } else {
            console.log("Something went wrong.");
        }
    } else {
        console.log("Server Error.");
    }
}
