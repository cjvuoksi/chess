/*
START CONFIG
 */
/*LOAD_GAME
Add paths from your load game class here (accessed from the game field)
    var board_path = "path.to.chess.board.array"
    var gameID = "path.to.game.id"
 */
const board_path = "game.board.chessBoard";
const game_ID_path = "gameID";

/*
Modify these values to true/false to make it so server messages fade after fade_time milliseconds
 */
const fade_error = true;
const fade_notify = true;
const fade_close = true;
const fade_time = 5000;

/*
Change this to the server port number
 */
const server_port = 8000;

/*
When you are ready to test websockets set this to true
 */
const is_ws = true;

/*
Automatically sends moves
 */
const auto_send = true;

/*
END CONFIG
 */


if (!is_ws) {
    document.getElementById("observe").remove();
}

//HTTP
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

//WEBSOCKET

//UTILS
function get(obj, path) {
    let arrayPath = path.split('.');
    return arrayPath.reduce((source, path) => source[path], obj);
}

//SETUP WS
let ws;

function wsStart() {
    if (!is_ws) {
        alert("Enable websockets to use this", true);
        return;
    }

    if (ws !== null && ws !== undefined && ws.readyState === ws.OPEN) {
        ws.close(1000, "Only one session allowed");
    } else {
        createWS();
    }
}

function createWS() {
    ws = new WebSocket(`ws://localhost:${server_port}/ws`);

    ws.onopen = () => {
        alert("New WS connection", true);
        displayWS();
    }
    ws.onmessage = onmessage;
    ws.onclose = (event) => {
        hideWS();
        alert(`Websocket connection closed: ${event.reason}`, fade_close);
        if (event.reason === "Only one session allowed") {
            createWS();
        }
    }
    ws.onerror = wsError;
}

function alert(message, timeout) {
    const alert = document.createElement("div");
    alert.className = "alert";
    alert.onclick = () => ws_alert(alert);
    alert.innerText = `✕ ${message}`;
    document.getElementById("alerts").prepend(alert);
    if (timeout) {
        setTimeout(() => ws_alert(alert), fade_time);
    }
}

function ws_alert(alert) {
    alert.style.background = "rgba(255, 0, 0, 0.0)";
    alert.style.color = "rgba(255, 0, 0, 0.0)";
    setTimeout(() => {
        alert.remove()
    }, 1000);
}

function wsError(event) {
    console.log(event);
}

function displayWS() {
    document.getElementById('ws').style.display = "block";
}

function hideWS() {
    document.getElementById('ws').style.display = "none";
}

//SETUP CHESS BOARD
let chessboard;

function loadGame(game) {
    let board;
    const id = get(game, game_ID_path);
    document.getElementById("gameIDBox").value = id ? id : document.getElementById("gameIDBox").value;
    let tmp = get(game, board_path);
    if (tmp.length === BOARD_SIZE) {
        board = tmp;
    } else {
        board = new Map()
        tmp.map((item) => {
            board.set(item[0], item[1]);
        })
    }

    chessboard = board;
    clearBoard();
    displayBoard();
}

function onmessage(event) {
    const servermessage = event.data;
    let message = JSON.parse(servermessage);

    switch (message.serverMessageType) {
        case 'LOAD_GAME':
            loadGame(message.game);
            break;
        case 'ERROR':
            // displayServerMessage(message.errorMessage);
            alert(message.errorMessage, fade_error);
            break;
        default:
            alert(message.message, fade_notify);
            break;
    }
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
    const body = request ? JSON.stringify(request, null, 2) : '';
    document.getElementById('userCommandBox').value = body;
    window.scrollBy({
        top: document.getElementById('ws_exec').getBoundingClientRect().top,
        behavior: "smooth"
    });
}

function connect(user) {
    if (!user) {
        wsStart();
    }
    if (!is_ws) {
        return;
    }
    displayWS();
    displayUserCommand("CONNECT", {
        commandType: "CONNECT",
        gameID: parseInt(document.getElementById('gameIDBox').value),
        authToken: document.getElementById('authToken').value
    })
}

const BOARD_SIZE = 8;

function displayBoard() {
    if (Array.isArray(chessboard)) {
        console.log(chessboard);
        for (let r = 1; r <= BOARD_SIZE; r++) {
            for (let c = 1; c <= BOARD_SIZE; c++) {
                let coordinates = String((r)).concat(String((c)));
                let square = document.getElementById(coordinates);
                if (square !== null) {
                    if (chessboard[r - 1][c - 1] !== null) {
                        square.innerText = getPiece(chessboard[r - 1][c - 1]);
                    }
                }
            }
        }
    } else {
        chessboard.forEach((value, key) => {
            let coordinates = String(key.row).concat(String(key.col));
            let square = document.getElementById(coordinates);
            if (square !== null) {
                square.innerText = getPiece(value);
            }
        })
    }
}

//SET UP MOVE MAKER
let startSquare;

for (let element of document.getElementsByClassName("square")) {
    element.addEventListener("click", (event) => {
        if (startSquare != null) {
            let end = event.target.id;
            let start = document.getElementById(startSquare);
            if ((start.innerText === "♟︎" && startSquare[0] === '2') || (start.innerText === "♙" && startSquare[0] === '7')) {
                updateMove(startSquare, end, true);
            } else {
                updateMove(startSquare, end);
            }
            unclickedSquare(start);
            startSquare = null;
        } else {
            startSquare = event.target.id;
            clickedSquare(document.getElementById(startSquare));
        }
    })
}

function clickedSquare(square) {
    if (square.classList.contains("light")) {
        square.style.background = "#96F97B";
    } else {
        square.style.background = "#6aaf58";
    }
}

function unclickedSquare(square) {
    if (square.classList.contains("light")) {
        square.style.background = "#f7f0e0";
    } else {
        square.style.background = "#b8b5a2";
    }
}

function updateMove(start, end, promo) {
    if (auto_send) {
        if (promo) {
            sendUserCommand(JSON.stringify({
                commandType: "MAKE_MOVE",
                gameID: parseInt(document.getElementById('gameIDBox').value),
                move: {startPosition: parsePosition(start), endPosition: parsePosition(end), promotionPiece: "QUEEN"},
                authToken: document.getElementById('authToken').value
            }));
        } else {
            sendUserCommand(JSON.stringify({
                commandType: "MAKE_MOVE",
                gameID: parseInt(document.getElementById('gameIDBox').value),
                move: {startPosition: parsePosition(start), endPosition: parsePosition(end)},
                authToken: document.getElementById('authToken').value
            }));
        }
    } else {
        if (promo) {
            displayUserCommand("MAKE_MOVE", {
                commandType: "MAKE_MOVE",
                gameID: parseInt(document.getElementById('gameIDBox').value),
                move: {startPosition: parsePosition(start), endPosition: parsePosition(end), promotionPiece: "QUEEN"},
                authToken: document.getElementById('authToken').value
            });
        } else {
            displayUserCommand("MAKE_MOVE", {
                commandType: "MAKE_MOVE",
                gameID: parseInt(document.getElementById('gameIDBox').value),
                move: {startPosition: parsePosition(start), endPosition: parsePosition(end)},
                authToken: document.getElementById('authToken').value
            })
        }
    }
}

function parsePosition(pos) {
    let row = parseInt(pos[0]);
    let col = parseInt(pos[1]);
    return {row: row, col: col};
}

function clearBoard() {
    let elements = document.getElementsByClassName("square");
    for (let i of elements) {
        i.innerText = "";
    }
}

function getPiece(value) {
    switch (value.type) {
        case "PAWN":
            return value.color === "BLACK" ? "♟︎" : "♙";
        case "ROOK":
            return value.color === "BLACK" ? "♜" : "♖";
        case "KNIGHT":
            return value.color === "BLACK" ? "♞" : "♘";
        case "BISHOP":
            return value.color === "BLACK" ? "♝" : "♗";
        case "KING":
            return value.color === "BLACK" ? "♚" : "♔";
        case "QUEEN":
            return value.color === "BLACK" ? "♛" : "♕";
    }
}

function resign() {
    displayUserCommand("RESIGN", {
        commandType: "RESIGN",
        authToken: document.getElementById('authToken').value,
        gameID: parseInt(document.getElementById('gameIDBox').value)
    })
}

function leave() {
    displayUserCommand("LEAVE", {
        commandType: "LEAVE",
        authToken: document.getElementById('authToken').value,
        gameID: parseInt(document.getElementById('gameIDBox').value)
    })
}

function reverseColumns() {
    let gameDOM = document.getElementById("game");
    if (gameDOM.style.flexDirection === "column-reverse") {
        gameDOM.style.flexDirection = "column";
    } else {
        gameDOM.style.flexDirection = "column-reverse";
    }
}

function reverseRows() {
    for (let row of document.getElementsByClassName("row")) {
        if (row.style.flexDirection === "row-reverse") {
            row.style.flexDirection = "row";
        } else {
            row.style.flexDirection = "row-reverse";
        }
    }
}

function rotateBoard() {
    reverseColumns();
    reverseRows();
}
