import axios from 'axios';
import { logout, isTokenExpired } from '@/service/auth';
import AuthService from './authService';

// Creazione di una nuova instanza di Axios
const axiosInstance = axios.create({
  baseURL:'http://localhost:8080/api',
});

// Interceptor per le richieste in uscita
axiosInstance.interceptors.request.use(
  (config) => {
    const accessToken = sessionStorage.getItem('accessToken');
    if (accessToken && !isTokenExpired()) {
      config.headers['Authorization'] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor per le risposte in entrata
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const newAccessToken = await AuthService.refreshAccessToken();
        if (newAccessToken) {
          originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
          return axiosInstance(originalRequest);
        } else {
          console.error('Token scaduto. Effettuando il logout');
          logout();
        }
      } catch (refreshError) {
        console.error('Errore nel rinnovo del token:', refreshError);
        logout();
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
