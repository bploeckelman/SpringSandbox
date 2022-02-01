export const origin = 'http://localhost:8080';
export const api_base_path = '/api/v1';
export const paths = {
    login: '/login',
    profile: '/profile',
    greeting: `${api_base_path}/greeting`,
    user_profile: `${api_base_path}/profile`
};

export const $ = (selector) => document.querySelector(selector);
export const $all = (selector) => document.querySelectorAll(selector);

export const auth_token_key = 'auth_token';

export const isJsonString = (str) => {
    try {
        let json = JSON.parse(str);
        return (typeof json === 'object');
    } catch (e) {
        return false;
    }
}
