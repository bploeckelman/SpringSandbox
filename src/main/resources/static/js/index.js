import * as constants from './shared.js';
import { $ } from './shared.js';

let auth_token = localStorage.getItem(constants.auth_token_key);

window.onload = () => {
    // TODO - check auth_token and redirect to profile page if it's valid
    //   otherwise display the login page content
    displayLoginPrompt();
};

// ------------------------------------------------------------------

const displayLoginPrompt = () => {
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

    loginForm.append(usernameInput, passwordInput, loginButton);
    content.append(loginForm, document.createElement('br'), loginResultDiv);

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        let response = await fetch(constants.paths.login, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'username': usernameInput.value,
                'password': passwordInput.value
            })
        });

        let tokenJson = await response.json();
        console.debug('Login response: ', tokenJson);
        if (tokenJson.error) {
            console.error(new LoginError(tokenJson));
            loginResultDiv.textContent = `Login failed: ${tokenJson.message}`;
            // clear the auth token
            localStorage.removeItem(constants.auth_token_key);
            auth_token = '';
        } else {
            loginResultDiv.textContent = 'Login successful';
            // store the auth token
            auth_token = tokenJson.token;
            localStorage.setItem(constants.auth_token_key, auth_token);
            // redirect to profile page
            window.location = constants.paths.profile;
        }
    });

    class LoginError {
        constructor(options = {}) {
            Object.assign(this, options);
        }
    }
}
