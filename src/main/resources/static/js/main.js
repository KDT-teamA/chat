//이 코드는 클라이언트 측에서 사용자와 서버 간의 웹소켓 통신을 처리하며,
//사용자가 입력한 메시지를 서버로 보내고 서버로부터 받은 메시지를 화면에 표시
'use strict';

//HTML 엘리먼트들을 변수에 할당
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

//변수 초기화
var stompClient = null; // WebSocket을 통해 서버와 통신할 때 사용될 Stomp 클라이언트
var username = null; //사용자 이름을 저장하는 변수

//사용자의 아바타 색상을 지정하는 데 사용되는 색상 배열
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

//사용자가 입력한 이름을 가져와서 WebSocket에 연결
function connect(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

//서버와의 연결이 설정되면 실행되며, 공용 토픽(/topic/public)을 구독하고,
//사용자가 채팅에 참여했음을 서버에 알림
function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}

//연결 중에 오류가 발생하면 호출되며, 사용자에게 오류 메시지를 표시
function onError(error) {
    connectingElement.textContent = '서버에 연결할 수 없습니다. 다시 시도하려면 이 페이지를 새로고침하세요!';
    connectingElement.style.color = 'red';
}

//사용자가 메시지를 입력하고 전송 버튼을 클릭하면 실행되며, 입력된 메시지를 서버에 전송
function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

//서버로부터 메시지를 수신하면 실행되며, 받은 메시지를 화면에 표시
function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 입장하셨습니다!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 퇴장하셨습니다!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

//사용자의 이름을 해싱하여 배열에서 해당 사용자에게 할당된 아바타 색상을 반환
function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

//사용자 이름을 제출하거나 메시지를 전송할 때 connect() 및 sendMessage() 함수가 호출되도록 이벤트 리스너를 등록
usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
