/*
let stompClient = null;

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  } else {
    $("#conversation").hide();
  }
  $("#messages").html("");
}

function connect() {
  const socket = new SockJS('/ws');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, (frame) => {
    setConnected(true);
    console.log('Connected:', frame);
    stompClient.subscribe('/all/messages', (message) => {
      showMessage(JSON.parse(message.body).content);
    });
  });
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  setConnected(false);
  console.log("Disconnected");
}

function sendMessage() {
  stompClient.send("/admin", {}, JSON.stringify({'content': $("#message").val()}));
}

function showMessage(message) {
  $("#messages").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
  $("form").on('submit', (e) => {
    e.preventDefault();
  });
  // Exercise 3.
  $("#connect").click(() => {
    connect();
  });
  $("#disconnect").click(() => {
    disconnect();
  });
  $("#send").click(() => {
    sendMessage();
  });
});
*/
