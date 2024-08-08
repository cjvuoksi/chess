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
    usr = getCookie("usr");
    if (auth == null) {
        window.location = "http://localhost:" + server_port + "/interactive.html";
    }
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

    })
}