export const environment = {
  production: false,
  apiUrl: window.location.hostname.includes('onrender.com') 
    ? 'https://backend-eleuterio.onrender.com/api' 
    : 'http://localhost:8080/api'
};
