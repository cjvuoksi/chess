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

let ws;

function wsStart() {
    ws = new WebSocket(`ws://localhost:${location.port}/ws`);

    ws.onopen = () => {
        console.log("New WS connection");
        document.getElementById('ws').style.display = "block";
    }
    ws.onmessage = onmessage;
    ws.onclose = () => {
        console.log("WS closed");
        document.getElementById('ws').style.display = "none";
        window.alert("Websocket connection closed"); //FIXME
    }
    ws.onerror = wsError;
}

function wsError(event) {
    console.log(event);
}

let chess;

function loadGame(game) {
    const board = new Map()
    game.game.board.chessBoard.map((item) => {
        board.set(item[0], item[1]);
    })
    game.game.board.chessBoard = board;
    chess = game.game;
    console.log(chess);
    displayBoard();
}

function onmessage(event) {
    const servermessage = event.data;
    var message = JSON.parse(servermessage);
    console.log(message);

    switch (message.serverMessageType) {
        case 'LOAD_GAME':
            loadGame(message.game);
            break;
        case 'ERROR':
            displayServerMessage(message.errorMessage);
            break;
        default:
            displayServerMessage(message.message)
            break;
    }
}

function displayServerMessage(message) {
    document.getElementById("serverMessage").innerText = message;
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
        commandType: "CONNECT",
        teamColor: 'WHITE/BLACK',
        gameID: 0,
        authToken: document.getElementById('authToken').value
    })
}

const BOARD_SIZE = 8;

function displayBoard() {
    output = document.getElementById("board");
    chess.board.chessBoard.forEach((value, key, map) => {
        console.log("Key:", key, "Value:", value);
        str = String(key.row).concat(String(key.col));
        console.log("String:", str);
        console.log(`Attempting to add ${value} to location: ${str}`)
        square = document.getElementById(str)
        if (square !== null) {
            square.innerText = value;
        }
    })
    //TODO make interactive
}

function makeMove() {
    displayUserCommand("MAKEMOVE", {
        commandType: "MAKEMOVE",
    })
}

function leave() {
    displayUserCommand("LEAVE", {
        commandType: "LEAVE",
    })
}
