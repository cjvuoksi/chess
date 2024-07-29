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
const black_path = "blackUsername"
const white_path = "whiteUsername"
const curr_path = "game.currColor"
const game_state = "game.gameOver"
const game_winner = "game.winner"

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

let auth;
let usr; //Attach this to a side of the chess board
let state = "SO";
let gID;
let color;
let currPlayer;

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

function signIn() {
    console.log("Username", usr, "Token", auth);
    setLogin();
    document.getElementById("form").style.display = "none";
    document.getElementById("title").innerText = "Welcome " + usr + "!";
    state = "SI";
    document.getElementById("SI").style.display = "block";
    listGames();
}

function createGame() {
    let name = window.prompt("Enter game name", "Name");
    sendHTTP("/game", `{'gameName': ${name}}`, "POST", auth, (response) => {
        if (!response.ok) {
            alert(response.status + ': ' + response.statusText + '\n');
            handleError(response);
            return;
        }
        return response.json();
    }, (data) => {
        if (data !== undefined) {
            alert("Created new game!");
            listGames();
        }
    })
}

function listGames() {
    sendHTTP("/game", "", "GET", auth, handleResponse, setGames);
}

function joinWhite(event) {
    let id = event.currentTarget.dataset.id;
    color = "WHITE";
    join("WHITE", id);
}

function joinBlack(event) {
    let id = event.currentTarget.dataset.id;
    color = "BLACK";
    rotateBoard();

    join("BLACK", id);
}

function join(color, gameID) {
    gID = gameID;
    document.getElementById("bottom_player").innerText = usr;
    sendHTTP("/game", `{'playerColor': ${color}, 'gameID': ${gID}}`, "PUT", auth, handleResponse, initWS);
}

function handleResponse(response) {
    if (!response.ok) {
        alert(response.status + ': ' + response.statusText + '\n', true);
        handleError(response);
        return;
    }
    return response.json();
}

function watch(event) {
    gID = event.currentTarget.dataset.id;
    initWS(true);
}

function getGameStatus(datum) {
    console.log("Datum", datum);
    console.log("State", get(datum, game_state))
    console.log("Winner", get(datum, game_winner))
    if (get(datum, game_state)) {
        let winner = get(datum, game_winner);
        if (winner === undefined) {
            return "<i>Stalemate</i>";
        }
        winner = winner === "WHITE" ? "White" : "Black";
        return `<i>${winner} won</i>`;
    }

    return "<i>In progress</i>";
}

function setGames(data) {
    let table = document.getElementById("games");
    table.innerHTML = `
        <thead>
            <tr>
                <th scope="col">
                    Game Name
                </th>
                <th scope="col">
                    White
                </th>
                <th scope="col">
                    Black
                </th>
                <th scope="col">
                    Observe
                </th>
                <th scope="col">
                    Game Status
                </th>
            </tr>
        </thead>
    `
    for (datum of data.games) {
        let newRow = document.createElement("tr");
        newRow.innerHTML = `<td>${datum.gameName}</td>`;
        let white = document.createElement("td");
        let black = document.createElement("td");
        let observe = document.createElement("td");
        let gameStatus = document.createElement("td");
        white.innerHTML = datum.whiteUsername === undefined ? "<i>Empty</i>" : datum.whiteUsername;
        black.innerHTML = datum.blackUsername === undefined ? "<i>Empty</i>" : datum.blackUsername;
        observe.innerHTML = "<i>Observe</i>";
        gameStatus.innerHTML = getGameStatus(datum);

        white.dataset.id = datum.gameID;
        white.onclick = joinWhite;
        white.title = "Join White";
        white.style.cursor = "pointer";

        black.dataset.id = datum.gameID;
        black.onclick = joinBlack;
        black.title = "Join Black";
        black.style.cursor = "pointer";

        observe.dataset.id = datum.gameID;
        observe.onclick = watch;
        observe.title = "Watch Game";
        observe.style.cursor = "pointer";

        newRow.appendChild(white);
        newRow.appendChild(black);
        newRow.appendChild(observe);
        newRow.appendChild(gameStatus);
        if (gameStatus.innerText !== "In progress") {
            white.style.pointerEvents = "none";
            black.style.pointerEvents = "none";
        }

        table.appendChild(newRow);
    }
}

function signOut() {
    sendHTTP("/session", "", "DELETE", auth, (response) => {
        if (!response.ok) {
            alert(response.status + ': ' + response.statusText + '\n', true);
            handleError(response);
        }
        return response.json();
    }, (data) => {
        auth = null;
        usr = null;
        setLogin();
    })
}

function login(event) {
    event.preventDefault();
    let username = document.getElementById("username").value;
    let pwd = document.getElementById("pwd").value;
    console.log(username, pwd);
    sendHTTP("/session", `{'username': ${username}, 'password': ${pwd}}`, "POST", null, (response) => {
        if (!response.ok) {
            alert(response.status + ': ' + response.statusText + '\n', true);
            handleError(response);
            return;
        }
        return response.json();
    }, (data) => {
        if (data === undefined) {
            return;
        }
        auth = data.authToken;
        usr = data.username;
        signIn();
    });
    document.getElementById("login_form").reset();
}

function register(event) {
    event.preventDefault();
    let username = document.getElementById("username").value;
    let pwd = document.getElementById("pwd").value;
    let email = document.getElementById("email").value;
    sendHTTP("/user", `{'username': ${username}, 'password': ${pwd}, 'email': ${email}}`, "POST", null, (response) => {
        if (!response.ok) {
            alert(response.status + ': ' + response.statusText + '\n', true);
            handleError(response);
            return;
        }
        return response.json();
    }, (data) => {
        if (data === undefined) {
            return;
        }
        auth = data.authToken;
        usr = data.username;
        signIn();
    });
    document.getElementById("login_form").reset();
}

function sendHTTP(path, params, method, authToken, response, data) {
    params = !!params ? params : undefined;
    let errStr = '';
    fetch("http://localhost:" + server_port + path, {
        method: method,
        body: params,
        headers: {
            Authorization: authToken,
            'Content-Type': 'application/json',
        },
    })
        .then(response)
        .then(data)
        .catch((error) => {
            alert(error)
        });
}

function submitForm() {
    if (state === "SO") {
        login();
    }
    if (state === "RE") {
        register();
    }
}

function setRegister() {
    state = "RE";
    let formTitle = document.getElementById("form_title");
    let email = document.getElementById("email");
    let form = document.getElementById("login_form");
    document.getElementById("form").style.display = "block";
    form.onsubmit = register;
    formTitle.innerText = "Create a new account";
    email.type = "text";
    document.getElementById("switchButton").innerText = "Login";
}

function setLogin() {
    state = "SO";
    document.getElementById("title").innerHTML = '<span class="chess-icon">♔</span> CS 240 Chess Server';
    document.getElementById("form_title").innerText = "Login";
    document.getElementById("email").type = "hidden";
    document.getElementById("form").style.display = "block";
    document.getElementById("login_form").onsubmit = login;
    document.getElementById("SI").style.display = "none";
    document.getElementById("switchButton").innerText = "Register";
}

function switchSignIn(event) {
    if (state === "SO") {
        setRegister();
    } else {
        setLogin()
    }
}

function handleError(response) {
    switch (response.status) {
        case 401:
            if (state === "SO") {
                setRegister();
            }
            if (state === "SI") {

            }
            break;
        case 403:
            if (state === "RE") {
                alert("Username already taken");
                setRegister();
            }
    }
}

//WS

function sendUserCommand(string) {
    ws.send(string);
}

function get(obj, path) {
    let arrayPath = path.split('.');
    return arrayPath.reduce((source, path) => source[path], obj);
}

let ws;

function displayWS() {
    document.getElementById("SI").style.display = "none";
    document.getElementById("gameplay").style.display = "block";
}

function initWS(data) {
    if (data === undefined) {
        alert("Join failed");
        return;
    }
    wsStart();
}

function wsStart() {
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
        connect();
    }
    ws.onmessage = onmessage;
    ws.onclose = (event) => {
        unHighlight();
        hideWS();
        alert(`Websocket connection closed: ${event.reason}`, fade_close);
        if (event.reason === "Only one session allowed") {
            createWS();
        }
    }
    ws.onerror = wsError;
}

function wsError(event) {
    console.log(event.data);
}

function hideWS() {
    document.getElementById('gameplay').style.display = "none";
    signIn();
}

function onmessage(event) {
    const serverMessage = event.data;
    let message = JSON.parse(serverMessage);

    switch (message.serverMessageType) {
        case 'LOAD_GAME':
            loadGame(message.game);
            break;
        case 'ERROR':
            alert(message.errorMessage, fade_error);
            break;
        case 'MOVES':
            console.log(message);
            highlight(message.moves);
            break;
        default:
            parseMessage(message.message);
            alert(message.message, fade_notify);
            break;
    }
}

let last_move;

function parseMessage(message) {

    if (message.includes("joined as black")) {
        loadNames(message.split(' ')[0], null);
    }
    if (message.includes("joined as white")) {
        loadNames(null, message.split(' ')[0]);
    }

    if (message.includes("->")) {
        last_move = message;
        loadMove();
    }
}

function loadMove() {
    if (move_start !== null && move_start !== undefined) {
        unClickedSquare(move_start);
        unClickedSquare(move_end);
    }
    if (currPlayer !== color) {
        return;
    }
    let start_pos = last_move.charAt(1).concat(String(last_move.charCodeAt(0) - 96));
    let end_pos = last_move.slice(4);
    console.log(end_pos);
    end_pos = end_pos.charAt(1).concat(String(end_pos.charCodeAt(0) - 96));
    console.log("Start", start_pos, "End", end_pos);
    move_start = document.getElementById(start_pos);
    move_end = document.getElementById(end_pos);
    moveHighlight(move_start);
    moveHighlight(move_end);
}

function moveHighlight(square) {
    if (square.classList.contains("light")) {
        square.style.background = "#f9f37b";
    } else {
        square.style.background = "#af9658";
    }
}

let move_start;
let move_end;

function connect() {
    ws.send(`{"commandType": "CONNECT", "gameID": ${gID}, "authToken": ${auth}}`);
}

const BOARD_SIZE = 8;

let chessboard;

function loadGame(game) {
    let board;
    gID = get(game, game_ID_path);
    let tmp = get(game, board_path);
    if (tmp.length === BOARD_SIZE) {
        board = tmp;
    } else {
        board = new Map()
        tmp.map((item) => {
            board.set(item[0], item[1]);
        })
    }

    // Move this to load game I think
    let black = get(game, black_path);
    let white = get(game, white_path);
    let curr = get(game, curr_path);
    loadCurr(curr);
    currPlayer = curr;

    loadNames(black, white);

    chessboard = board;
    clearBoard();
    displayBoard();
    loadMove();
}

function loadCurr(curr) {
    if (curr === board_state) {
        document.getElementById("top_player").classList.remove("currPlayer");
        document.getElementById("bottom_player").classList.add("currPlayer");
    } else {
        document.getElementById("top_player").classList.add("currPlayer");
        document.getElementById("bottom_player").classList.remove("currPlayer");
    }
}

function loadNames(black, white) {
    let top = document.getElementById("top_player");
    let bottom = document.getElementById("bottom_player");
    if (board_state === "WHITE") {
        top.innerText = black === null ? top.innerText : black;
        bottom.innerText = white === null ? bottom.innerText : white;

    } else {
        top.innerText = white === null ? top.innerText : white;
        bottom.innerText = black === null ? bottom.innerText : black;
    }
}

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
            if (currHighlights.includes(event.target)) {
                if ((start.innerText === "♟︎" && startSquare[0] === '2') || (start.innerText === "♙" && startSquare[0] === '7')) {
                    setPromo(startSquare, end);
                } else {
                    updateMove(startSquare, end);
                }
            }
            unClickedSquare(start);
            unHighlight();
            startSquare = null;
            loadMove();
        } else {
            startSquare = event.target.id;
            getValidMoves();
            clickedSquare(document.getElementById(startSquare));
        }
    })
}

function getValidMoves() {
    sendUserCommand(JSON.stringify({
        commandType: "HIGHLIGHT",
        gameID: gID,
        move: {startPosition: parsePosition(startSquare), endPosition: null},
        authToken: auth
    }));
}

let currHighlights = new Array();

function highlight(moves) {
    for (move of moves) {
        let coordinates = String(move.endPosition.row).concat(String(move.endPosition.col));
        let square = document.getElementById(coordinates);
        clickedSquare(square);
        currHighlights.push(square);
    }
}

function unHighlight() {
    for (square of currHighlights) {
        unClickedSquare(square);
    }
    currHighlights = new Array();
}

function clickedSquare(square) {
    if (square.classList.contains("light")) {
        square.style.background = "#96F97B";
    } else {
        square.style.background = "#6aaf58";
    }
}

function unClickedSquare(square) {
    if (square.classList.contains("light")) {
        square.style.background = "#f7f0e0";
    } else {
        square.style.background = "#b8b5a2";
    }
}

let promoPiece = "QUEEN"

function setPromo(start, end) {
    console.log("Set Promo");
    const ctxMenu = document.createElement('div');
    ctxMenu.style.position = 'absolute';
    ctxMenu.classList.add("dialog");
    ctxMenu.style.zIndex = 100;
    ctxMenu.style.top = `${pos_y}px`;
    ctxMenu.style.left = `${pos_x}px`;

    const menuItems = [
        {
            text: 'Queen', action: () => {
                promoPiece = "QUEEN";
                ctxMenu.remove();
                updateMove(start, end, true);
            }
        },
        {
            text: 'Rook', action: () => {
                promoPiece = "ROOK";
                ctxMenu.remove();
                updateMove(start, end, true);
            }
        },
        {
            text: 'Bishop', action: () => {
                promoPiece = "BISHOP";
                ctxMenu.remove();
                updateMove(start, end, true);
            }
        },
        {
            text: 'Knight', action: () => {
                promoPiece = "KNIGHT";
                ctxMenu.remove();
                updateMove(start, end, true);
            }
        }
    ];

    menuItems.forEach((item) => {
        const menuItem = document.createElement('p');
        menuItem.classList.add("promo");
        menuItem.textContent = item.text;
        menuItem.addEventListener('click', item.action);
        ctxMenu.appendChild(menuItem);
    });

    document.body.appendChild(ctxMenu);
    ctxMenu.style.display = 'block';

    console.log("CTX MENU", ctxMenu);
}

let pos_x;
let pos_y;

document.addEventListener("mousemove", (e) => {
    pos_y = e.clientY;
    pos_x = e.clientX
})

function updateMove(start, end, promo) {
    if (move_start !== null && move_start !== undefined) {
        unClickedSquare(move_start);
        unClickedSquare(move_end);
    }
    if (promo) {
        sendUserCommand(JSON.stringify({
            commandType: "MAKE_MOVE",
            gameID: gID,
            move: {startPosition: parsePosition(start), endPosition: parsePosition(end), promotionPiece: promoPiece},
            authToken: auth
        }));
    } else {
        sendUserCommand(JSON.stringify({
            commandType: "MAKE_MOVE",
            gameID: gID,
            move: {startPosition: parsePosition(start), endPosition: parsePosition(end)},
            authToken: auth
        }));
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
    sendUserCommand(JSON.stringify({
        commandType: "RESIGN",
        authToken: auth,
        gameID: gID
    }))
}

function leave() {
    sendUserCommand(JSON.stringify({
        commandType: "LEAVE",
        authToken: auth,
        gameID: gID
    }));
    signIn();
}

let board_state = "WHITE";

function reverseColumns() {
    let gameDOM = document.getElementById("game");
    if (gameDOM.style.flexDirection === "column-reverse") {
        gameDOM.style.flexDirection = "column"; //White bottom
    } else {
        gameDOM.style.flexDirection = "column-reverse";
    }
}

function reverseRows() {
    for (let row of document.getElementsByClassName("row")) {
        if (row.style.flexDirection === "row-reverse") {
            row.style.flexDirection = "row"; //White Bottom
        } else {
            row.style.flexDirection = "row-reverse";
        }
    }
}

function rotateBoard() {
    board_state = board_state === "WHITE" ? "BLACK" : "WHITE";
    let top = document.getElementById("top_player");
    let bottom = document.getElementById("bottom_player");
    let tmp = top.innerText;
    top.innerText = bottom.innerText;
    bottom.innerText = tmp;
    loadCurr(currPlayer);
    reverseColumns();
    reverseRows();
}


//Base it off of board_state
