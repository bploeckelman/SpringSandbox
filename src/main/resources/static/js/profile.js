import * as shared from "./shared.js";
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
    auth_token = localStorage.getItem(shared.auth_token_key);
    let tokenExists = (typeof(auth_token) === 'string' && auth_token !== '');
    let hasUserInfo = await fetchUserInfo();
    return (tokenExists && hasUserInfo);
}

const fetchUserInfo = async () => {
    return fetch(shared.paths.user_profile, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${auth_token}`,
            'Accept': 'application/json'
        }
    })
        .then(response => response.json())
        .then(userJson => {
            console.debug('User info response:', userJson);
            if ('error' in userJson) {
                throw new Error(`Failed to fetch user details, response: ${JSON.stringify(userJson)}`);
            }
            user_info = userJson;
            return true;
        })
        .catch(error => {
            console.error(error);
            return false;
        });
}

const displayProfile = () => {
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

    fetch(shared.paths.greeting, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${auth_token}`,
            'Accept': 'text/plain'
        }
    })
        .then(response => response.text())
        .then(data => {
            // TODO - this probably isn't the best way to check for error responses
            //   but it's kind of contrived anyway since the api won't return plain text responses
            if (shared.isJsonString(data)) {
                let json = JSON.parse(data);
                if ('error' in json) {
                    throw new Error(json);
                }
            }
            greetingP.textContent = data;
        })
        .catch(console.error);
}

const logout = () => {
    console.log(`Logging out, redirecting to homepage`);
    localStorage.removeItem(shared.auth_token_key);
    window.location = '/';
}
