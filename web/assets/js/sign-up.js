// Initialize Awesome Notifications
const notifier = new AWN({
    position: "top-right" // Set position to top-right
});
async function signUp() {

    const user_dto = {
        username: document.getElementById("username").value,
        email: document.getElementById("email").value,
        password: document.getElementById("pwd").value,
        passwordc: document.getElementById("pwdc").value
    };


    const response = await fetch(
            "SignUp",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();
        

        if (json.success) {
            window.location = "index.html";
        } else {
             notifier.warning(json.message);
        }
    } else {
         notifier.alert("Server Error");
    }
}