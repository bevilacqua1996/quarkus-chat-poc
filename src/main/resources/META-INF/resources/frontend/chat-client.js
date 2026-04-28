const transcriptId = "chat-transcript";
let socket = null;
let reconnectAttempts = 0;
let reconnectTimer = null;
let botBuffer = "";
let assistantBubble = null;
let assistantRenderScheduled = false;

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function renderInlineMarkdown(text) {
  let output = escapeHtml(text);
  output = output.replace(/`([^`]+)`/g, "<code>$1</code>");
  output = output.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>");
  output = output.replace(/\*([^*]+)\*/g, "<em>$1</em>");
  output = output.replace(/\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g, '<a href="$2" target="_blank" rel="noopener noreferrer">$1</a>');
  return output;
}

function renderMarkdown(markdown) {
  const normalized = String(markdown || "").replace(/\r\n/g, "\n").trim();
  if (!normalized) {
    return "";
  }

  const blocks = normalized.split(/\n{2,}/);
  return blocks.map((block) => {
    const lines = block.split("\n");
    const firstLine = lines[0];

    if (/^#{1,6}\s+/.test(firstLine)) {
      const level = firstLine.match(/^#+/)[0].length;
      const text = firstLine.replace(/^#{1,6}\s+/, "");
      return `<h${level}>${renderInlineMarkdown(text)}</h${level}>`;
    }

    if (firstLine.startsWith("```")) {
      const code = lines.slice(1, lines[lines.length - 1] === "```" ? -1 : undefined).join("\n");
      return `<pre><code>${escapeHtml(code)}</code></pre>`;
    }

    if (/^(\s*[-*]\s+)/.test(firstLine)) {
      const items = lines
        .map((line) => line.replace(/^\s*[-*]\s+/, "").trim())
        .filter(Boolean)
        .map((item) => `<li>${renderInlineMarkdown(item)}</li>`)
        .join("");
      return `<ul>${items}</ul>`;
    }

    if (/^\s*\d+\.\s+/.test(firstLine)) {
      const items = lines
        .map((line) => line.replace(/^\s*\d+\.\s+/, "").trim())
        .filter(Boolean)
        .map((item) => `<li>${renderInlineMarkdown(item)}</li>`)
        .join("");
      return `<ol>${items}</ol>`;
    }

    return `<p>${lines.map((line) => renderInlineMarkdown(line)).join("<br>")}</p>`;
  }).join("");
}

function transcriptElement() {
  return document.getElementById(transcriptId);
}

function bubbleBaseStyles(role) {
  const palette = {
    system: "background: var(--vaadin-background-container); color: var(--vaadin-text-color-secondary);",
    user: "background: color-mix(in srgb, var(--aura-blue) 18%, transparent); color: var(--vaadin-text-color); align-self: flex-end;",
    assistant: "background: var(--aura-surface-color-solid); color: var(--vaadin-text-color); border: 1px solid var(--vaadin-border-color-secondary);",
  };

  return [
    "max-width: min(72ch, 100%);",
    "padding: var(--vaadin-gap-s) var(--vaadin-gap-m);",
    "border-radius: var(--aura-base-radius, var(--vaadin-radius-m));",
    "box-shadow: 0 1px 3px rgb(0 0 0 / 0.08);",
    "overflow-wrap: anywhere;",
    palette[role] || palette.assistant,
  ].join(" ");
}

function bubbleHtml(role, markdown) {
  return `<div style="${bubbleBaseStyles(role)}">${renderMarkdown(markdown)}</div>`;
}

function clearTranscript() {
  const transcript = transcriptElement();
  if (transcript) {
    transcript.innerHTML = "";
  }
  botBuffer = "";
  assistantBubble = null;
  assistantRenderScheduled = false;
}

function appendBubble(role, markdown) {
  const transcript = transcriptElement();
  if (!transcript) {
    return null;
  }

  const bubble = document.createElement("div");
  bubble.setAttribute("data-role", role);
  bubble.setAttribute("style", bubbleBaseStyles(role));
  bubble.innerHTML = renderMarkdown(markdown);
  transcript.appendChild(bubble);
  transcript.scrollTop = transcript.scrollHeight;
  return bubble;
}

function ensureAssistantBubble() {
  if (!assistantBubble) {
    assistantBubble = appendBubble("assistant", "");
  }
  return assistantBubble;
}

function flushAssistantBubble() {
  assistantRenderScheduled = false;
  const bubble = ensureAssistantBubble();
  if (!bubble) {
    return;
  }

  bubble.innerHTML = renderMarkdown(botBuffer || " ");
  const transcript = transcriptElement();
  if (transcript) {
    transcript.scrollTop = transcript.scrollHeight;
  }
}

function scheduleAssistantBubbleUpdate() {
  if (assistantRenderScheduled) {
    return;
  }

  assistantRenderScheduled = true;
  window.requestAnimationFrame(flushAssistantBubble);
}

function updateStatus(text, color) {
  const status = document.getElementById("connection-status");
  if (status) {
    status.textContent = text;
    const palette = {
      connected: "var(--aura-green-text)",
      disconnected: "var(--aura-red-text)",
      reconnecting: "var(--aura-blue-text)",
      connecting: "var(--aura-blue-text)",
    };
    status.style.setProperty("--status-color", palette[color] || palette.connecting);
  }
}

function appendTranscript(line) {
  appendBubble("system", line);
}

function connect() {
  const protocol = window.location.protocol === "https:" ? "wss" : "ws";
  socket = new WebSocket(`${protocol}://${window.location.host}/customer-support-agent`);

  socket.onopen = () => {
    reconnectAttempts = 0;
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
    updateStatus("Connected", "connected");
  };

  socket.onmessage = (event) => {
    updateStatus("Connected", "connected");
    if (event.data === "Welcome to Menthoring Knowledge base! How can I help you today?") {
      appendTranscript(event.data);
      return;
    }
    botBuffer += event.data;
    scheduleAssistantBubbleUpdate();
  };

  socket.onclose = () => {
    updateStatus("Reconnecting...", "reconnecting");
    if (reconnectAttempts < 30) {
      reconnectAttempts += 1;
      reconnectTimer = window.setTimeout(connect, 10000);
    } else {
      updateStatus("Connection lost", "disconnected");
      appendTranscript("System: Connection lost - please refresh the browser.");
    }
  };

  socket.onerror = () => {
    updateStatus("Connection error", "disconnected");
  };
}

window.customerSupportChat = {
  init() {
    clearTranscript();
    if (!socket || socket.readyState === WebSocket.CLOSED) {
      connect();
    }
  },
  sendMessage(message) {
    if (socket && socket.readyState === WebSocket.OPEN) {
      botBuffer = "";
      appendBubble("user", message);
      assistantBubble = null;
      socket.send(message);
    }
  },
  resetTranscript() {
    clearTranscript();
  }
};
