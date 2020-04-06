const ID_TOKEN_KEY = 'id_token';

// window.localStorage is
// users browser of 5MB,
// only string data,
// one call at a time,
// only your js and hackers js can access it
//
// use IndexedDB to take full advantage of it

export const getToken = () => window.localStorage.getItem(ID_TOKEN_KEY);

export const saveToken = (token) => {
  window.localStorage.setItem(ID_TOKEN_KEY, token);
};

export const destroyToken = () => {
  window.localStorage.removeItem(ID_TOKEN_KEY);
};

export default { getToken, saveToken, destroyToken };

// https://dev.to/rdegges/please-stop-using-local-storage-1i04
// not window.localStorage but in crypto cookie
// httpOnly cookie flag
// SameSite=strict
// secure=true
//
// 1. user logs on
// 2. use their session ID extracted from cookie they send
// 3. retrieve their account details from db
