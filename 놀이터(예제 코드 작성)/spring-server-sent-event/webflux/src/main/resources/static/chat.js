"use strict";

async function postData(url, data) {
  const response = await fetch(url, {
    method: 'POST',
    mode: 'cors',
    cache: 'no-cache',
    credentials: 'same-origin',
    headers: {
      'Content-Type': 'application/json'
    },
    redirect: 'follow',
    referrerPolicy: 'no-referrer',
    body: JSON.stringify(data)
  });
  return response;
}

function sendMessage() {
  const input = document.getElementById('messageInput').value;
  console.log(input)
  postData('/send-message',{ content: input, userName: window.assignedName});
}

function handleSendMessageEvent(eventData) {
  const userNameNode = document.createElement('span');
  userNameNode.innerHTML = eventData.data.userName + ': ';

  const li = document.createElement("li");
  li.appendChild(userNameNode);
  li.appendChild(document.createTextNode(eventData.data.content));

  const ul = document.getElementById("list");
  ul.appendChild(li);
}

function registerSSE(url) {
  const source = new EventSource(url);
  source.addEventListener('message', event => {
    console.log(event.data);
    handleSendMessageEvent(JSON.parse(event.data));
  })
  source.addEventListener('connect', event => {
    console.log(event.data);
  })
  source.onopen = event => console.log("Connection opened");
  source.onerror = event => console.error("Connection error");
  return source;
}

window.assignedName = `banjjoknim - ${Math.ceil(Math.random() * 10)}`
window.eventSource = registerSSE('/register-sse')

const nameSpan = document.getElementById('name')
nameSpan.innerHTML = window.assignedName;
