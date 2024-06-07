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

function observeGame() {
    //FIXME
}

//SETUP WS
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

//SETUP CHESS BOARD
let chess;

function loadGame(game) {
    const board = new Map()
    document.getElementById("gameIDBox").value = game.gameID;
    game.game.board.chessBoard.map((item) => {
        board.set(item[0], item[1]);
    })
    game.game.board.chessBoard = board;
    chess = game.game;
    clearBoard();
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
        gameID: parseInt(document.getElementById('gameIDBox').value),
        authToken: document.getElementById('authToken').value
    })
}

const BOARD_SIZE = 8;

function displayBoard() {
    output = document.getElementById("board");
    chess.board.chessBoard.forEach((value, key, map) => {
        str = String(key.row).concat(String(key.col));
        square = document.getElementById(str)
        if (square !== null) {
            square.innerText = getPiece(value);
        }
    })
    //TODO make interactive
}

//SET UP MOVE MAKER
let init;
let end;

for (let element of document.getElementsByClassName("square")) {
    element.addEventListener("click", (event) => {
        if (init != null && end == null) {
            end = event.target.id;
            updateMove(init, end);
            init = null;
            end = null;
        } else {
            console.log(event);
            end = null;
            init = event.target.id;
        }
    })
}

function updateMove(start, end) {
    displayUserCommand("MAKE_MOVE", {
        commandType: "MAKE_MOVE",
        gameID: parseInt(document.getElementById('gameIDBox').value),
        move: {startPosition: parsePosition(start), endPosition: parsePosition(end)},
        authToken: document.getElementById('authToken').value
    })
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

let new_chess = {
    "board": {
        "chessBoard": new Map([
            [
                {
                    "row": 2,
                    "col": 1
                },
                {
                    "type": "PAWN",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 8,
                    "col": 7
                },
                {
                    "type": "KNIGHT",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 2,
                    "col": 2
                },
                {
                    "type": "PAWN",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 8,
                    "col": 8
                },
                {
                    "type": "ROOK",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 4,
                    "col": 4
                },
                {
                    "type": "PAWN",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 2,
                    "col": 3
                },
                {
                    "type": "PAWN",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 2,
                    "col": 5
                },
                {
                    "type": "PAWN",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 2,
                    "col": 6
                },
                {
                    "type": "PAWN",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 2,
                    "col": 7
                },
                {
                    "type": "PAWN",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 2,
                    "col": 8
                },
                {
                    "type": "PAWN",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 7,
                    "col": 1
                },
                {
                    "type": "PAWN",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 7,
                    "col": 2
                },
                {
                    "type": "PAWN",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 7,
                    "col": 3
                },
                {
                    "type": "PAWN",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 7,
                    "col": 4
                },
                {
                    "type": "PAWN",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 7,
                    "col": 5
                },
                {
                    "type": "PAWN",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 7,
                    "col": 6
                },
                {
                    "type": "PAWN",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 7,
                    "col": 7
                },
                {
                    "type": "PAWN",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 1,
                    "col": 1
                },
                {
                    "type": "ROOK",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 7,
                    "col": 8
                },
                {
                    "type": "PAWN",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 1,
                    "col": 2
                },
                {
                    "type": "KNIGHT",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 1,
                    "col": 3
                },
                {
                    "type": "BISHOP",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 1,
                    "col": 4
                },
                {
                    "type": "QUEEN",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 1,
                    "col": 5
                },
                {
                    "type": "KING",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 1,
                    "col": 6
                },
                {
                    "type": "BISHOP",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 1,
                    "col": 7
                },
                {
                    "type": "KNIGHT",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 1,
                    "col": 8
                },
                {
                    "type": "ROOK",
                    "color": "WHITE"
                }
            ],
            [
                {
                    "row": 8,
                    "col": 1
                },
                {
                    "type": "ROOK",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 8,
                    "col": 2
                },
                {
                    "type": "KNIGHT",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 8,
                    "col": 3
                },
                {
                    "type": "BISHOP",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 8,
                    "col": 4
                },
                {
                    "type": "QUEEN",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 8,
                    "col": 5
                },
                {
                    "type": "KING",
                    "color": "BLACK"
                }
            ],
            [
                {
                    "row": 8,
                    "col": 6
                },
                {
                    "type": "BISHOP",
                    "color": "BLACK"
                }
            ]
        ])
    },
    "enPassant": {
        "row": 4,
        "col": 4
    },
    "whiteKingMoved": false,
    "whiteQueenRookMoved": false,
    "whiteKingRookMoved": false,
    "blackKingMoved": false,
    "blackQueenRookMoved": false,
    "blackKingRookMoved": false,
    "currColor": "BLACK",
    "gameOver": false
}

chess = new_chess;

displayBoard();