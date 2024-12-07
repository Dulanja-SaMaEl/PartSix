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
        console.log(json);

        if (json.success) {
            window.location = "index.html";
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Please try agin later";
    }
}