// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    // Note: validate the payment and show success or failure page to the customer
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:" + error);
};


const checkout_container = document.getElementById("checkout-container");
const checkout_data_row = document.getElementById("checkout-data-row");
const city_select = document.getElementById("city-select");
let shipping_amount = 0;
let total = 0;
let subtotal = 0;

async function loadCheckoutItems() {
    const response = await fetch("LoadCheckout");
    if (response.ok) {
        const json = await response.json();
        console.log(json);


        const cityList = json.cityList;
        const cartList = json.cartList;



        cityList.forEach(city => {
            const option = document.createElement("option");
            option.innerHTML = city.name;
            option.value = city.id;

            city_select.appendChild(option);
        });


        checkout_container.innerHTML = "";




        cartList.forEach(cartItem => {
            const checkout_data_row_clone = checkout_data_row.cloneNode(true);

            checkout_data_row_clone.querySelector("#checkout-data-product").innerHTML = cartItem.product.title;
            checkout_data_row_clone.querySelector("#checkout-data-qty").innerHTML = cartItem.qty;
            let productPrice = cartItem.product.price * cartItem.qty;
            subtotal = subtotal + productPrice;
            checkout_data_row_clone.querySelector("#checkout-data-price").innerHTML = productPrice;

            checkout_container.appendChild(checkout_data_row_clone);
        });
        document.getElementById("checkout-subtotal").innerHTML = subtotal;





        document.getElementById("checkout-shipping").innerHTML = shipping_amount;

    } else {
        console.log("Server Error");
    }
}

function changeShippingCost() {

    if (city_select.value === "8") {
        //Colombo
        shipping_amount = 2;

    } else {
        //out of Colombo
        shipping_amount = 5;
    }
    total = subtotal + shipping_amount;
    document.getElementById("checkout-total").innerHTML = total;
    document.getElementById("checkout-shipping").innerHTML = shipping_amount;
}


async function checkout() {


    //get address data
    let firstName = document.getElementById("first-name");
    let lastName = document.getElementById("last-name");
    let city = document.getElementById("city-select");
    let address1 = document.getElementById("address1");
    let address2 = document.getElementById("address2");
    let postalCode = document.getElementById("postal-code");
    let mobile = document.getElementById("mobile");

    //request data (json)
    const data = {
        firstName: firstName.value,
        lastName: lastName.value,
        cityId: city.value,
        address1: address1.value,
        address2: address2.value,
        postalCode: postalCode.value,
        mobile: mobile.value
    };

    const response = await fetch("Checkout", {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json"
        }
    });
    if (response.ok) {
        const json = await response.json();
        console.log(json);
        payhere.startPayment(json.payhereJson);
    } else {

        console.log("Server Error");
    }

}

