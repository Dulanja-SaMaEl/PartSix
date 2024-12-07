async function loadCartItems() {
    const response = await fetch("LoadCartItems");
    if (response.ok) {
        const json = await response.json();
        console.log(json);
        loadCartProductView(json.cartList);
    } else {
        console.log("Server Error");
    }
}
const cart_product = document.getElementById("cart-product");
const cart_product_checkout_container = document.getElementById("cart-product-checkout-container");
let subtotal = 0;
function loadCartProductView(cartList) {

    const cart_product_container = document.getElementById("cart-product-container");
    cart_product_container.innerHTML = "";

    cartList.forEach(cartItem => {
        const cart_product_clone = cart_product.cloneNode(true);

        cart_product_clone.querySelector("#cart-product-img").src = "product_images/" + cartItem.product.id + "/image1.png";
        cart_product_clone.querySelector("#cart-product-name").innerHTML = cartItem.product.title;
        cart_product_clone.querySelector("#cart-product-price").innerHTML = cartItem.product.price;
        cart_product_clone.querySelector("#remove-item-from-cart").value = cartItem.id;
        let numericPrice = parseFloat(cartItem.product.price);
        cart_product_clone.querySelector("#pro1-qunt").value = cartItem.qty;
        let numericqty = Number(cartItem.qty);
        subtotal = (numericPrice) * numericqty + subtotal;
        cart_product_clone.querySelector("#cart-product-total").innerHTML = (numericPrice) * numericqty;


        cart_product_container.appendChild(cart_product_clone);
    });
    cart_product_container.appendChild(cart_product_checkout_container);
    document.getElementById("cart-total").innerHTML = subtotal;
}



document.body.addEventListener('click', async function (event) {
    if (event.target && event.target.id === 'remove-item-from-cart') {

        const response = await fetch("RemoveItemFromCart?cart_item_id=" + event.target.value);

        if (response.ok) {
            const json = await response.json();
            console.log(json);
            if (json.success) {
                loadCartItems();
            }
        } else {
            console.log("Server Error");
        }
    }
});


