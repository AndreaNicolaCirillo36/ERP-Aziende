import axios from './axiosSetup';
import { saveToken, logout, isRefreshTokenValid } from '../service/auth';

const AuthService = {

  /**
   * Effettua il login dell'utente e restituisce la risposta del server.
   * @param {string} username - Il nome utente.
   * @param {string} password - La password dell'utente.
   * @return {Promise<AxiosResponse>} - La risposta del server.
   */
  async login(username, password) {
    return axios.post('/auth/login', {
      username,
      password
    });
  },


  /**
   * Aggiunge un nuovo utente al sistema.
   * @param {string} username - Il nome utente del nuovo utente.
   * @param {string} password - La password del nuovo utente.
   * @param {string} role - Il ruolo assegnato al nuovo utente.
   * @return {Promise<AxiosResponse>} - La risposta del server dopo il tentativo di registrazione.
   */
  async addUser(username, password, role) {    
    return axios.post('/users/register', {
      username,
      password,
      role
    });
  },

  /**
   * Ottiene le informazioni dell'utente predefinito.
   * @return {Promise<AxiosResponse>} - La risposta del server contenente i dettagli dell'utente predefinito.
   */
  async defaultUser() {    
    return axios.get('/users/defaultUser');
  },

 
/**
 * Rinnova il token di accesso utilizzando il token di refresh.
 * 
 * Questo metodo tenta di ottenere un nuovo token di accesso utilizzando
 * il refresh token memorizzato. Se il refresh token è valido, invia
 * una richiesta al server per ottenere un nuovo token di accesso. Se
 * il rinnovo ha successo, salva il nuovo token di accesso e ritorna
 * il token rinnovato. In caso di fallimento o se il refresh token non è
 * valido, esegue il logout dell'utente.
 *
 * @returns {Promise<string|null>} - Il nuovo token di accesso se il rinnovo
 * ha successo, altrimenti null.
 */
  async refreshAccessToken() {
    const refreshToken = sessionStorage.getItem('refreshToken');
    if (refreshToken) {
      try {
        if (isRefreshTokenValid()) {
          const response = await axios.post('/auth/refresh-token', { refreshToken });
          if (response.status === 200) {
            const { accessToken } = response.data;
            saveToken(accessToken, refreshToken);
            console.log("Access token rinnovato.");
            return accessToken;
          }
        } else {
          logout();
          return;
        }
      } catch (error) {
        logout();
        return;
      }
    }
    return null;
  }
};

export default AuthService;