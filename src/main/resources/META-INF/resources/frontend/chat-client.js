const transcriptId = "chat-transcript";
let socket = null;
let reconnectAttempts = 0;
let reconnectTimer = null;
let botBuffer = "";

function transcriptElement() {
  return document.getElementById(transcriptId);
}

function updateStatus(text, color) {
  const status = document.getElementById("connection-status");
  if (status) {
    status.textContent = text;
    status.style.color = color || "";
  }
}

function appendTranscript(line) {
  const transcript = transcriptElement();
  if (!transcript) {
    return;
  }
  const prefix = transcript.value && !transcript.value.endsWith("\n") ? "\n" : "";
  transcript.value = `${transcript.value}${prefix}${line}\n`;
  transcript.dispatchEvent(new Event("change", { bubbles: true }));
}

function ensureAssistantPrefix() {
  const transcript = transcriptElement();
  if (!transcript) {
    return;
  }
  if (!transcript.value.endsWith("Assistant: ")) {
    const prefix = transcript.value && !transcript.value.endsWith("\n") ? "\n" : "";
    transcript.value = `${transcript.value}${prefix}Assistant: `;
  }
}

function clearTranscript() {
  const transcript = transcriptElement();
  if (transcript) {
    transcript.value = "Responses will appear here.\n";
  }
  botBuffer = "";
}

function connect() {
  const protocol = window.location.protocol === "https:" ? "wss" : "ws";
  socket = new WebSocket(`${protocol}://${window.location.host}/customer-support-agent`);

  socket.onopen = () => {
    reconnectAttempts = 0;
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
    updateStatus("Connected", "var(--lumo-success-text-color)");
  };

  socket.onmessage = (event) => {
    updateStatus("Connected", "var(--lumo-success-text-color)");
    if (event.data === "Welcome to Menthoring Knowledge base! How can I help you today?") {
      appendTranscript(`System: ${event.data}`);
      return;
    }
    botBuffer += event.data;
    const transcript = transcriptElement();
    if (transcript) {
      if (!transcript.value.includes("Assistant: ")) {
        appendTranscript("Assistant: ");
      }
      transcript.value = transcript.value.replace(/Assistant: [\s\S]*$/, `Assistant: ${botBuffer}`);
    }
  };

  socket.onclose = () => {
    updateStatus("Reconnecting...", "var(--lumo-warning-text-color)");
    if (reconnectAttempts < 30) {
      reconnectAttempts += 1;
      reconnectTimer = window.setTimeout(connect, 10000);
    } else {
      updateStatus("Connection lost", "var(--lumo-error-text-color)");
      appendTranscript("System: Connection lost - please refresh the browser.");
    }
  };

  socket.onerror = () => {
    updateStatus("Connection error", "var(--lumo-error-text-color)");
  };
}

window.customerSupportChat = {
  init() {
    if (!socket || socket.readyState === WebSocket.CLOSED) {
      connect();
    }
  },
  sendMessage(message) {
    if (socket && socket.readyState === WebSocket.OPEN) {
      botBuffer = "";
      appendTranscript(`You: ${message}`);
      ensureAssistantPrefix();
      socket.send(message);
    }
  },
  resetTranscript() {
    clearTranscript();
  }
};
