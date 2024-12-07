async function signIn() {

    const user_dto = {
        email: document.getElementById("email-login").value,
        password: document.getElementById("pwd-login").value,
        verification: document.getElementById("vc").value
    };

    const response = await fetch(
            "SignIn",
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

function changeVerify() {
    const vcElement = document.getElementById("vc");
    if (document.getElementById("verify-check").checked) {
        vcElement.readOnly = true; // Enable read-only
    } else {
        vcElement.readOnly = false; // Disable read-only
    }
}