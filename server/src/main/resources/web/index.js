function submit() {
    document.getElementById('response').value = '';
    const method = document.getElementById('method').value;
    const endpoint = document.getElementById('handleBox').value;
    const requestBody = document.getElementById('requestBox').value;
    const authToken = document.getElementById('authToken').value;

    if (endpoint && method) {
        send(endpoint, requestBody, method, authToken);
    }

    return false;
}

function send(path, params, method, authToken) {
    params = !!params ? params : undefined;
    let errStr = '';
    fetch(path, {
        method: method,
        body: params,
        headers: {
            Authorization: authToken,
            'Content-Type': 'application/json',
        },
    })
        .then((response) => {
            if (!response.ok) errStr = response.status + ': ' + response.statusText + '\n';
            if (response.ok && path === '/game' && method === 'PUT') {
                wsStart()
            }
            return response.json();
        })
        .then((data) => {
            document.getElementById('authToken').value = data.authToken || authToken || 'none';
            document.getElementById('response').innerText = errStr + JSON.stringify(data, null, 2);

        })
        .catch((error) => {
            document.getElementById('response').innerText = error;
        });
}

let ws;

function wsStart() {
    ws = new WebSocket('ws://localhost:8080/ws');

    ws.onopen = () => {
        console.log("New WS connection");
        document.getElementById('ws').style.display = "block";
    }
    ws.onmessage = onmessage;
    ws.onclose = () => {
        console.log("WS closed");
        document.getElementById('ws').style.display = "none";
    }
    ws.onerror = wsError;
}

function wsError(event) {
    console.log(event);
}

function onmessage(event) {
    const servermessage = event.data;
    console.log(event.data);
}

function submitUserCommand() {
    const type = document.getElementById('typeBox').value;
    const command = document.getElementById('userCommandBox').value;

    if (type && command) {
        sendUserCommand(command)
    }
}

function sendUserCommand(command) {
    ws.send(command);
}

function displayRequest(method, endpoint, request) {
    document.getElementById('method').value = method;
    document.getElementById('handleBox').value = endpoint;
    const body = request ? JSON.stringify(request, null, 2) : '';
    document.getElementById('requestBox').value = body;
    window.scrollBy({
        top: document.getElementById('execute').getBoundingClientRect().top,
        behavior: "smooth"
    });
}

function clearAll() {
    displayRequest('DELETE', '/db', null);
}

function register() {
    displayRequest('POST', '/user', {username: 'username', password: 'password', email: 'email'});
}

function login() {
    displayRequest('POST', '/session', {username: 'username', password: 'password'});
}

function logout() {
    displayRequest('DELETE', '/session', null);
}

function gamesList() {
    displayRequest('GET', '/game', null);
}

function createGame() {
    displayRequest('POST', '/game', {gameName: 'gameName'});
}

function joinGame() {
    displayRequest('PUT', '/game', {playerColor: 'WHITE/BLACK/empty', gameID: 0});
}

function displayUserCommand(type, request) {
    document.getElementById('typeBox').value = type;
    document.getElementById('endBox').value = "/ws";
    const body = request ? JSON.stringify(request, null, 2) : '';
    document.getElementById('userCommandBox').value = body;
    window.scrollBy({
        top: document.getElementById('ws_exec').getBoundingClientRect().top,
        behavior: "smooth"
    });
}

function connect() {
    displayUserCommand("CONNECT", {
        type: "CONNECT",
        teamColor: 'WHITE/BLACK',
        gameID: 0,
        authToken: document.getElementById('authToken').value
    })
}

function makeMove() {
    displayUserCommand("MAKEMOVE",)
}