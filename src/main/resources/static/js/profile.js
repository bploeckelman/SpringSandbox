import * as constants from "./shared.js";
import { $ } from './shared.js';

let auth_token = '';
let user_info = {};

window.onload = async () => {
    loadAuth().then(isAuthenticated => {
        if (isAuthenticated) {
            console.log('Authentication token found, displaying profile');
            displayProfile();
        } else {
            console.log('No authentication token available, redirecting to login page');
            window.location = '/';
        }
    });
};

// ------------------------------------------------------------------

const loadAuth = async () => {
    auth_token = localStorage.getItem(constants.auth_token_key);
    let tokenExists = (typeof(auth_token) === 'string' && auth_token !== '');
    let hasUserInfo = await fetchUserInfo();
    return (tokenExists && hasUserInfo);
}

const fetchUserInfo = async () => {
    let response = await fetch(constants.paths.user_profile, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${auth_token}`,
            'Accept': 'application/json'
        }
    });

    let user = await response.json();
    console.debug('User info response:', user);

    if ('error' in user) {
        console.error(new Error(`Failed to fetch user details, response: ${JSON.stringify(user)}`));
        return false;
    }

    user_info = user;
    return true;
}

const displayProfile = async () => {
    $('#logout_button').onclick = logout;

    let content = $('#content');
    content.innerHTML = '';

    let userInfoDiv = document.createElement('div');
    userInfoDiv.id = 'user_info';

    const createInfoP = (headingText, contentText) => {
        let p = document.createElement('p');
        let strong = document.createElement('strong');
        let content = document.createElement('span');
        strong.textContent = headingText;
        content.textContent = contentText;
        p.append(strong, content);
        return p;
    }

    userInfoDiv.append(
        createInfoP('Id: ', user_info.id),
        createInfoP('Name: ', user_info.username),
        createInfoP('Roles: ', user_info.roles),
        createInfoP('Created: ', new Date(user_info.created)),
        createInfoP('Modified: ', new Date(user_info.lastModified))
    );

    let greetingP = document.createElement('p');

    content.append(greetingP, userInfoDiv);

    let response = await fetch(constants.paths.greeting, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${auth_token}`,
            'Accept': 'text/plain'
        }
    });

    let greeting = await response.text();
    // TODO - this probably isn't the best way to check for error responses
    //   but it's kind of contrived anyway since the api won't return plain text responses
    if (constants.isJsonString(greeting)) {
        let json = JSON.parse(greeting);
        if ('error' in json) {
            console.error(new Error(`Failed to fetch greeting, response: ${JSON.stringify(json)}`));
        }
    }
    greetingP.textContent = greeting;
}

const logout = () => {
    console.log(`Logging out, redirecting to homepage`);
    localStorage.removeItem(constants.auth_token_key);
    window.location = '/';
}
