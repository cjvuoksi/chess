const server_port = 8000;

let auth;
let usr;

function getCookie(name) {
    let cookie = decodeURIComponent(document.cookie);
    let index = cookie.indexOf(name + "=");

    if (index < 0) {
        return null;
    }
    cookie = cookie.substring(index + name.length + 1);
    let cookieEnd = cookie.indexOf("; ");
    if (cookieEnd > 0) {
        return cookie.substring(0, cookieEnd);
    } else {
        return cookie;
    }
}

function setCookie(name, value) {
    document.cookie = name + "=" + encodeURIComponent(value);
}

function deleteCookie(name) {
    document.cookie = name + "=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;"
}

function setAuth(auth) {
    setCookie("auth", auth);
}

function setUsr(usr) {
    setCookie("usr", usr);
}

function authenticate() {
    auth = getCookie("auth");
    // usr = getCookie("usr");
    if (auth == null) {
        window.location = "http://localhost:" + server_port + "/interactive.html";
    }

    sendHTTP("/session", {}, "GET", auth, (data) => {
        if (data === null) {
            deleteCookie("auth");
            deleteCookie("usr");
            window.location = "http://localhost:" + server_port + "/interactive.html";
            return;
        }

        usr = data.username;
        console.assert(auth === data.authToken, "Cookie auth (%s) not equal to auth: %s", auth, data.authToken);
    }, (response) => {
        if (response.ok) {
            return response.json();
        }
        return null;
    });
}

authenticate();

function dropDown() {
    dropdown({url: "http://localhost:" + server_port, text: "Non-interactive version"},
        {url: "http://localhost:" + server_port + "/account.html", text: "My Account"},
        {url: "http://localhost:" + server_port + "/manage.html", text: "Manage"},
        {url: "http://localhost:" + server_port + "/interactive.html", text: "Back to Game"})
}

function dropdown(...links) {
    let dropdown = document.createElement("nav");
    let drop = document.getElementById("dropdown");
    drop.onclick = () => {
        dropdown.remove();
        drop.onclick = dropDown;
    }
    dropdown.style.position = "absolute";
    dropdown.style.backgroundColor = "#b8b5a2";
    dropdown.style.top = drop.offsetTop + drop.offsetHeight + "px";
    dropdown.style.left = drop.offsetLeft + drop.offsetWidth + "px";


    for (link of links) {
        let item = document.createElement("a");
        item.href = link.url;
        item.innerText = link.text;
        dropdown.appendChild(item);
    }
    document.body.appendChild(dropdown);
}

//HTTP

function sendHTTP(path, params, method, authToken, data, response = handleResponse, error = catchError) {
    let body = JSON.stringify(params);
    body = body === "{}" ? undefined : body;
    fetch("http://localhost:" + server_port + path, {
        method: method,
        body: body,
        headers: {
            Authorization: authToken,
            'Content-Type': 'application/json',
        },
    })
        .then(response)
        .then(data)
        .catch(error);
}

function handleResponse(response) {
    if (!response.ok) {
        handleError(response.status);
        return;
    }
    return response.json();
}

function handleError(status) {
    switch (status) {
        case (401):
            console.log("unauthorized");
            break;
    }
}

function catchError(error) {
    console.log(error);
}

// GET ACCOUNT
function getAccount() {
    sendHTTP("/user", {}, "GET", auth, (data) => {
        document.getElementById("username").value = data.username;
        document.getElementById("email").value = data.email;
    })
}

getAccount();

// SAVE ACCOUNT
function saveChanges() {

    let username = document.getElementById("username").value;
    let email = document.getElementById("email").value;

    sendHTTP("/user", {username: username, password: null, email: email, oldPassword: null}, "PUT", auth, (data) => {
        if (data === undefined) {
            alert("Changes not saved", 2000);
            getAccount();
            return;
        }
        alert("Changes saved", 2000);
    })
}

function resetPassword() {
    let oldPassword;
    createPassMenu();
}

function createPassMenu() {
    let menu = document.createElement("div");
    let text = document.createElement("div");
    let cancel = document.createElement("button");
    let confirm = document.createElement("button");
    let form = document.createElement("form");
    let oldPass = document.createElement("input");
    let newPass = document.createElement("input");
    let confirmPass = document.createElement("input");

    oldPass.classList.add("menu-input");
    oldPass.type = "password";
    oldPass.required = true;
    oldPass.id = "oldPass";
    oldPass.placeholder = "Current Password";

    newPass.classList.add("menu-input");
    newPass.type = "password";
    newPass.required = true;
    newPass.id = "newPass";
    newPass.placeholder = "New Password";

    confirmPass.id = "confirmPass";
    confirmPass.classList.add("menu-input");
    confirmPass.type = "password";
    confirmPass.required = true;
    confirmPass.placeholder = "Confirm New Password";

    form.appendChild(oldPass);
    form.appendChild(newPass);
    form.appendChild(confirmPass);
    form.classList.add("menu-form");
    form.onsubmit = submitPass;
    form.id = "pass_form"

    function closeWindow() {
        menu.remove();
    }

    function moveMenu(event) {
        let x = parseInt(menu.style.left);
        let y = parseInt(menu.style.top);
        menu.style.left = x + event.movementX + "px";
        menu.style.top = y + event.movementY + "px";
    }

    menu.onmousedown = () => {
        document.addEventListener("mousemove", moveMenu);
    }

    menu.onmouseup = () => {
        document.removeEventListener("mousemove", moveMenu);
    }

    //Menu Window
    menu.classList.add("menu");
    menu.style.top = `${pos_y + 10}px`;
    menu.style.left = `${pos_x + 10}px`;
    menu.id = "password_menu";

    //Menu Text
    text.innerText = "Enter New Password";
    text.classList.add("menu-text");

    //Menu Buttons
    cancel.onclick = closeWindow;
    cancel.innerText = "Cancel"
    cancel.classList.add("menu-button");
    confirm.onclick = submitPass;
    confirm.innerText = "Confirm";
    confirm.classList.add("menu-button");

    menu.appendChild(text);
    menu.appendChild(form);
    menu.appendChild(cancel);
    menu.appendChild(confirm);
    document.body.appendChild(menu);
}

function submitPass(event) {
    event.preventDefault();
    let op = document.getElementById("oldPass");
    let np = document.getElementById("newPass");
    let cp = document.getElementById("confirmPass");

    let submit = true;
    if (op.value === "") {
        submit = false;
        op.style.outline = "red 2px solid";
    } else {
        op.style.outline = null;
    }
    if (np.value === "") {
        submit = false;
        np.style.outline = "red 2px solid";
    } else {
        np.style.outline = null;
    }
    if (cp.value === "") {
        submit = false;
        cp.style.outline = "red 2px solid";
    } else {
        cp.style.outline = null;
    }
    if (cp.value !== np.value) {
        submit = false;
        alert("Passwords don't match", 2000);
    }

    if (submit) {
        sendHTTP("/user", {password: np.value, email: null, oldPassword: op.value}, "PUT", auth, (data) => {
            if (data === null) {
                // alert failed
                alert("Failed to update password", 2000);
                return;
            }
            alert("Password updated", 2000);
            document.getElementById("password_menu").remove();
        });
    }
}


// Menus
let pos_x;
let pos_y;

document.addEventListener("mousemove", (e) => {
    pos_y = e.clientY;
    pos_x = e.clientX
})

function alert(message, timeout) {
    const alert = document.createElement("div");
    alert.className = "alert";
    alert.onclick = () => ws_alert(alert);
    alert.innerText = `✕ ${message}`;
    document.getElementById("alerts").prepend(alert);
    if (timeout) {
        setTimeout(() => ws_alert(alert), true);
    }
}

function ws_alert(alert) {
    alert.style.background = "rgba(255, 0, 0, 0.0)";
    alert.style.color = "rgba(255, 0, 0, 0.0)";
    setTimeout(() => {
        alert.remove()
    }, 1000);
}