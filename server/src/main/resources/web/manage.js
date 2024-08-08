const server_port = 8000;

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