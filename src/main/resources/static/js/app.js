const origin = 'http://localhost:8080';
const paths = {
    login: '/login',
    api: '/api/v1'
};

const $ = (selector) => document.querySelector(selector);
const $all = (selector) => document.querySelectorAll(selector);

const displayLoginPrompt = () => {
    class LoginError {
        // TODO - is there an nicer way to set a bunch of members based on the input json object?
        constructor(error) {
            this.timestamp = error.timestamp;
            this.status = error.status;
            this.error = error.error;
            this.message = error.message;
            this.path = error.path;
        }
    }

    let content = $('#content');
    content.innerHTML = '';

    let loginResultDiv = document.createElement('div');
    loginResultDiv.id = 'login_result';

    let loginForm = document.createElement('form');
    loginForm.id = 'login_form';
    loginForm.action = 'POST'; // TODO - might not need this since the submit handler POSTs

    let usernameInput = document.createElement('input');
    usernameInput.type = 'text';
    usernameInput.id = 'username';
    usernameInput.name = 'username';
    usernameInput.placeholder = 'Username...';
    usernameInput.autocomplete = 'username';

    let passwordInput = document.createElement('input');
    passwordInput.type = 'password';
    passwordInput.id = 'password';
    passwordInput.name = 'password';
    passwordInput.placeholder = 'Password...';
    passwordInput.minLength = 3;
    passwordInput.maxLength = 30;
    passwordInput.autocomplete = 'current-password';

    let loginButton = document.createElement('button')
    loginButton.id = 'login';
    loginButton.name = 'login';
    loginButton.textContent = 'Login';

    loginForm.addEventListener('submit', (event) => {
        event.preventDefault();
        fetch(origin + paths.login, {
            method: 'POST',
            headers: {
                'accepts': 'application/json',
                'content-type': 'application/json'
            },
            body: JSON.stringify({
                'username': usernameInput.value,
                'password': passwordInput.value
            })
        })
            .then(response => response.json())
            .then(json => {
                console.trace('fetch().then(response).then(json): ', json);

                // check for errors and 'handle' them
                if (json.error) {
                    throw new LoginError(json);
                }

                // TODO - make api request for greeting and insert it into the page
                // TODO - wire up future fetch calls to the api to automatically insert the authorization header with this token
                loginResultDiv.textContent = 'Login successful, token: ' + json.token;
            })
            .catch(error => {
                console.error(error);
                loginResultDiv.textContent = 'Login failed: ' + error.message;
            })
        ;
    });

    loginForm.appendChild(usernameInput);
    loginForm.appendChild(passwordInput);
    loginForm.appendChild(loginButton);

    content.appendChild(loginForm);
    content.appendChild(document.createElement('br'));
    content.appendChild(loginResultDiv);
}

// ------------------------------------------------------------------

window.onload = () => {
    displayLoginPrompt();
};
