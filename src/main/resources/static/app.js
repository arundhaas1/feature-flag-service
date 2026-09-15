/**
 * Admin page for the feature flag service.
 *
 * Talks to the same REST API any other client uses — no private endpoints — so replacing this
 * page with a framework later means rewriting only this file.
 */
(function () {
  "use strict";

  // No app-level isolation in the POC: every flag belongs to the seeded default application.
  var APP = "default";
  var TOKEN_KEY = "ff.token";
  var USER_KEY = "ff.user";

  var session = { token: null, username: null, role: null };
  var environments = [];

  var el = function (id) { return document.getElementById(id); };

  /* ---------------- session ---------------- */

  function saveSession(data) {
    session = { token: data.token, username: data.username, role: data.role };
    try {
      sessionStorage.setItem(TOKEN_KEY, data.token);
      sessionStorage.setItem(USER_KEY, JSON.stringify({ username: data.username, role: data.role }));
    } catch (ignored) {
      // Private browsing or blocked storage: the session simply will not survive a reload.
    }
  }

  function restoreSession() {
    try {
      var token = sessionStorage.getItem(TOKEN_KEY);
      var who = sessionStorage.getItem(USER_KEY);
      if (!token || !who) return false;
      var parsed = JSON.parse(who);
      session = { token: token, username: parsed.username, role: parsed.role };
      return true;
    } catch (ignored) {
      return false;
    }
  }

  function clearSession() {
    session = { token: null, username: null, role: null };
    try {
      sessionStorage.removeItem(TOKEN_KEY);
      sessionStorage.removeItem(USER_KEY);
    } catch (ignored) { /* nothing to clean up */ }
  }

  var can = {
    edit: function () { return session.role === "ADMIN" || session.role === "EDITOR"; },
    delete: function () { return session.role === "ADMIN"; }
  };

  /* ---------------- api ---------------- */

  function api(path, options) {
    var config = options || {};
    var headers = { "Accept": "application/json" };
    if (session.token) headers.Authorization = "Bearer " + session.token;
    if (config.body) headers["Content-Type"] = "application/json";

    return fetch(path, {
      method: config.method || "GET",
      headers: headers,
      body: config.body ? JSON.stringify(config.body) : undefined
    }).then(function (response) {
      // An expired or rejected token ends the session rather than failing silently.
      if (response.status === 401 && session.token) {
        clearSession();
        showLogin("Your session expired. Please sign in again.");
        throw new Error("unauthenticated");
      }
      if (response.status === 204) return null;

      return response.text().then(function (text) {
        var payload = text ? JSON.parse(text) : null;
        if (!response.ok) {
          throw new Error((payload && payload.message) || "Request failed (" + response.status + ")");
        }
        return payload;
      });
    });
  }

  /* ---------------- messages ---------------- */

  var bannerTimer = null;

  function banner(message, kind) {
    var node = el("banner");
    node.textContent = message;
    node.className = "banner " + (kind || "");
    node.hidden = false;
    window.clearTimeout(bannerTimer);
    if (kind === "good") bannerTimer = window.setTimeout(function () { node.hidden = true; }, 2500);
  }

  function hideBanner() { el("banner").hidden = true; }

  /* ---------------- views ---------------- */

  function showLogin(message) {
    el("app-view").hidden = true;
    el("login-view").hidden = false;
    var error = el("login-error");
    error.textContent = message || "";
    error.hidden = !message;
    el("password").value = "";
  }

  function showApp() {
    el("login-view").hidden = true;
    el("app-view").hidden = false;
    el("current-user").textContent = session.username;
    el("current-role").textContent = session.role;
    el("create-panel").hidden = !can.edit();
  }

  /* ---------------- data ---------------- */

  function loadEnvironments() {
    return api("/api/v1/environments").then(function (list) {
      environments = list;
      var select = el("environment");
      select.innerHTML = "";
      list.forEach(function (environment) {
        var option = document.createElement("option");
        option.value = environment.id;
        option.textContent = environment.name;
        select.appendChild(option);
      });
    });
  }

  function selectedEnvironmentId() { return Number(el("environment").value); }

  function loadFlags() {
    var environmentId = selectedEnvironmentId();
    if (!environmentId) return Promise.resolve();

    return api("/api/v1/" + APP + "/flags?environmentId=" + environmentId).then(function (flags) {
      render(flags);
    });
  }

  function render(flags) {
    var body = el("flag-rows");
    body.innerHTML = "";
    el("empty").hidden = flags.length > 0;
    el("summary").textContent = flags.length
      ? flags.filter(function (f) { return f.enabled; }).length + " of " + flags.length + " enabled"
      : "";
    flags.forEach(function (flag) { body.appendChild(row(flag)); });
  }

  function row(flag) {
    var tr = document.createElement("tr");

    var key = document.createElement("td");
    key.className = "key";
    key.textContent = flag.flagKey;

    var description = document.createElement("td");
    description.className = "muted";
    description.textContent = flag.description || "—";

    var state = document.createElement("td");
    state.appendChild(toggle(flag));

    var actions = document.createElement("td");
    actions.className = "action-col";
    if (can.delete()) actions.appendChild(deleteButton(flag));

    tr.append(key, description, state, actions);
    return tr;
  }

  function toggle(flag) {
    var label = document.createElement("label");
    label.className = "switch";

    var input = document.createElement("input");
    input.type = "checkbox";
    input.checked = flag.enabled;
    input.disabled = !can.edit();
    input.setAttribute("aria-label", "Enable " + flag.flagKey);

    var track = document.createElement("span");
    track.className = "track";

    var text = document.createElement("span");
    text.className = "state-label";
    text.textContent = flag.enabled ? "On" : "Off";

    input.addEventListener("change", function () {
      var desired = input.checked;
      input.disabled = true;
      api("/api/v1/" + APP + "/flags/" + flag.flagId, {
        method: "PATCH",
        body: { enabled: desired, environmentId: selectedEnvironmentId() }
      }).then(function () {
        text.textContent = desired ? "On" : "Off";
        input.disabled = false;
        banner(flag.flagKey + " is now " + (desired ? "on" : "off"), "good");
        loadFlags();
      }).catch(function (error) {
        // Put the switch back where it was, so the page never shows a state the server rejected.
        input.checked = !desired;
        input.disabled = false;
        if (error.message !== "unauthenticated") banner(error.message, "bad");
      });
    });

    label.append(input, track, text);
    return label;
  }

  function deleteButton(flag) {
    var button = document.createElement("button");
    button.type = "button";
    button.className = "link";
    button.textContent = "Delete";

    // Two-step inline confirm rather than window.confirm, which blocks the whole page.
    var armed = false;
    var reset = function () {
      armed = false;
      button.textContent = "Delete";
      button.classList.remove("confirming");
    };

    button.addEventListener("click", function () {
      if (!armed) {
        armed = true;
        button.textContent = "Confirm?";
        button.classList.add("confirming");
        window.setTimeout(function () { if (armed) reset(); }, 4000);
        return;
      }
      button.disabled = true;
      api("/api/v1/" + APP + "/flags/" + flag.flagId, { method: "DELETE" })
        .then(function () {
          banner(flag.flagKey + " deleted", "good");
          return loadFlags();
        })
        .catch(function (error) {
          button.disabled = false;
          reset();
          if (error.message !== "unauthenticated") banner(error.message, "bad");
        });
    });

    return button;
  }

  /* ---------------- events ---------------- */

  el("login-form").addEventListener("submit", function (event) {
    event.preventDefault();
    var button = el("login-button");
    button.disabled = true;

    api("/auth/login", {
      method: "POST",
      body: { username: el("username").value, password: el("password").value }
    }).then(function (data) {
      saveSession(data);
      button.disabled = false;
      start();
    }).catch(function (error) {
      button.disabled = false;
      showLogin(error.message === "Invalid credentials" ? "Wrong username or password." : error.message);
    });
  });

  el("logout").addEventListener("click", function () {
    clearSession();
    hideBanner();
    showLogin();
  });

  el("environment").addEventListener("change", function () {
    hideBanner();
    loadFlags().catch(function () { /* api() already surfaced it */ });
  });

  el("refresh").addEventListener("click", function () {
    hideBanner();
    loadFlags().catch(function () { /* api() already surfaced it */ });
  });

  el("create-form").addEventListener("submit", function (event) {
    event.preventDefault();
    var key = el("flag-key").value.trim();
    if (!key) return;

    // A flag exists in every environment; the admin then decides where it is switched on.
    var everyEnvironment = environments.map(function (environment) { return environment.id; });

    api("/api/v1/" + APP + "/flags", {
      method: "POST",
      body: { name: key, description: el("flag-description").value.trim(), environmentId: everyEnvironment }
    }).then(function () {
      el("flag-key").value = "";
      el("flag-description").value = "";
      banner(key + " created in all environments", "good");
      return loadFlags();
    }).catch(function (error) {
      if (error.message !== "unauthenticated") banner(error.message, "bad");
    });
  });

  /* ---------------- boot ---------------- */

  function start() {
    showApp();
    loadEnvironments()
      .then(loadFlags)
      .catch(function (error) {
        if (error.message !== "unauthenticated") banner(error.message, "bad");
      });
  }

  if (restoreSession()) start(); else showLogin();
})();
