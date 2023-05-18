/*s
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var currentReceiver = "";
var userList = new Map();
var currentUser = "";
$(document).ready(function () {

  var socket;
  $("#connect").click(() => {
    let token = $("#token").val();
    load(token)
  });
});


function loadFriend() {
  $("#user_list").html("");
  console.log(userList.size);
  for (let [key, value] of userList.entries()) {
    if (currentUser.id != key) {
      $("#user_list").append('<li class="list-group-item" onClick=setSelected("' + key + '")>' + value.username + '</li>');
    }
  }

}



function setSelected(id) {
  $("#receiver").html(userList.get(id).username);
  currentReceiver = id;
}
function load(token) {
  const socketUrl = 'http://localhost:3000';
  //const socketUrl = 'http://103.38.197.19:9080'

  socket = io(socketUrl, {
    path:'/',
    reconnection:false,
    transportOptions: {
      polling: {
      }
    },
    secure: true
  });


  socket.on("connect", function (data) {
    $("#token-container").remove();
  });


  socket.on("SUPER_ADMINISTRATOR",function(data){
    alert(data.subject);
  })
  socket.on("username", function (data) {
    $("#username").html("Hello " + data.username + " Welcome");
    if(currentUser=="") {
      subscribeToMessage(socket, data)
    }
    currentUser = data;
  });


  socket.on("user-connected", function (data) {
    console.log(data)
    userList.set(data.id, { "username": data.username, "id": data.id })
    loadFriend();
  });

  socket.on("user-disconnected",function(data){
    console.log(data);
    userList.delete(data.id);
    loadFriend();
  })


  socket.on("connected-users", function (data) {
    console.log("connected users:"+data);
    data = JSON.parse(data);
    console.log("Connected users in json:"+data);
    var map = new Map(data);
    for (let [key, value] of map.entries()) {
      userList.set(key, { 'username': value.username, "id": key })
    }
    loadFriend();
  });

  socket.on("dineshkrk24-notify",function(data){
    alert(data.message);
  });

  socket.on("message", function (data) {
    //$("#message-container").append(received(data));
  });

  $("#sent").click(() => {
    let message = $("#message").val();
    const id= uuidv4();
    console.log(id);
    socket.emit("message", { "id":id,"message": message, "receiver": currentReceiver, "sender": currentUser });
    $("#message-container").append(sent(message,id));
  });

  $("#notification-sent").click(()=>{
    let notification = $("#notification-message").val();
    let receiver = $("#notification-receiver").val();
    sendNotification(notification,receiver,socket);

  });


  function subscribeToMessage(socket,data){
    socket.on(data.username+"-message",function(data){
      console.log(data);
      $("#message-container").append(received(data));
    });

    socket.on(data.username+"delivery",function(data){
      console.log(data);
    });

    socket.on(data.username+"-notify",function(data){
      alert(data.message);
    });
  }

  function received(data) {
    console.log("received")
    return ' <div class="row justify-content-end">' +
      '<div class="row alert alert-success col-8">' +
      '<h6 class="col-4">' + data.sender.username + '</h6>' +
      '<h4 class="col-8">' + data.message + '</h4>' +
      '</div>' +
      '</div>'
  }

  function sent(data,id) {
    return '<div class="row justify-content-start">' +
      '<div class="row alert alert-info col-8" id="'+id+'">' +
      '<h4>' + data + '</h4>' +
      '</div>' +
      '</div>'
  }

  function sendNotification(message,receiver,socket){
    socket.emit("notification",{"message":message,"receiver":receiver})
  }
}

