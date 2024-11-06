import { jwtDecode } from 'jwt-decode';
import router from '../router';
import AuthService from '@/service/authService';

/**
 * Salva il token di accesso e il token di refresh nel sessionStorage.
 * @param {string} accessToken - Il token di accesso.
 * @param {string} refreshToken - Il token di refresh.
 */
export function saveToken(accessToken, refreshToken) {
  try {
    console.log('savetoken')
    const decoded = jwtDecode(accessToken);
    const expirationTime = decoded.exp * 1000;
    console.log(`Token expiry time in ms: ${expirationTime}`);
    sessionStorage.setItem('accessToken', accessToken);
    sessionStorage.setItem('refreshToken', refreshToken);
    sessionStorage.setItem('tokenExpiry', expirationTime.toString()); 
    console.log('access token = ' + accessToken)
    console.log('refresh token = ' + refreshToken)
    console.log('Access Token e Refresh Token salvati correttamente.');
  } catch (error) {
    console.error('Errore nel salvataggio dei token:', error);
  }
}


/**
 * Verifica se il token di accesso è scaduto.
 * @returns {boolean} - Ritorna true se il token è scaduto, altrimenti false.
 */
export function isTokenExpired() {
  const tokenExpiry = sessionStorage.getItem('tokenExpiry');
  if (!tokenExpiry) return true;
  return new Date().getTime() > Number(tokenExpiry);
}

/**
 * Rimuove il token dal sessionStorage (logout o scadenza).
 */
export function clearToken() {
  sessionStorage.removeItem('accessToken');
  sessionStorage.removeItem('refreshToken');
  sessionStorage.removeItem('tokenExpiry');
}

/**
 * Esegue il logout e reindirizza l'utente alla pagina di login.
 */
export function logout() {
  clearToken();
  router.replace('/auth/login');
}

/**
 * Ottiene il tempo di scadenza del token.
 * @returns {number|null} - Ritorna il tempo di scadenza del token in millisecondi, o null se non trovato.
 */
export function getTokenExpirationTime() {
  const tokenExpiry = sessionStorage.getItem('tokenExpiry'); 
  console.log('Scadenza token =', tokenExpiry);
  if (!tokenExpiry) {
    console.error('Scadenza del token non trovata');
    return null;
  }
  return parseInt(tokenExpiry, 10);
}

/**
 * Estrae il ruolo dell'utente dal token di accesso.
 * @returns {string|null} - Il ruolo dell'utente, o null se non è possibile estrarlo.
 */
export function getUserRole() {
  const accessToken = sessionStorage.getItem('accessToken');
  if (!accessToken) {
    console.error('Token mancante');
    return null;
  }

  try {
    const decoded = jwtDecode(accessToken);
    console.log('Ruolo estratto da access token =', decoded.role);
    return decoded.role || null;
  } catch (error) {
    console.error('Errore nel decodificare il token:', error);
    return null;
  }
}

export function getUsername() {
  const accessToken = sessionStorage.getItem('accessToken');
  if (!accessToken) {
    console.error('Token mancante');
    return null;
  }

  try {
    const decoded = jwtDecode(accessToken);
    console.log('Username estratto da access token =', decoded.sub);
    return decoded.sub || null;
  } catch (error) {
    console.error('Errore nel decodificare il token:', error);
    return null;
  }
}

/**
 * Verifica se il refresh token è valido.
 * @returns {boolean} - Ritorna true se il token di refresh è valido, altrimenti false.
 */
export function isRefreshTokenValid() {
  const refreshToken = sessionStorage.getItem('refreshToken');
  if (refreshToken) {
    try {
      const decoded = jwtDecode(refreshToken);
      const isValid = decoded.exp * 1000 > new Date().getTime();
      console.log(`Refresh token valid: ${isValid}`);
      return isValid;
    } catch (error) {
      console.error('Errore durante la verifica del refresh token:', error);
      return false;
    }
  }
  return false;
}


/**
 * Pianifica il rinnovo del token di accesso poco prima che scada.
 */
let refreshTimer = null;

export function scheduleTokenRefresh() {
  console.log('Eseguendo scheduleTokenRefresh');

  if (refreshTimer) {
    clearTimeout(refreshTimer);
    refreshTimer = null;
  }

  const tokenExpiry = sessionStorage.getItem('tokenExpiry');
  if (tokenExpiry) {
    const currentTime = new Date().getTime();
    const timeUntilExpiry = Number(tokenExpiry) - currentTime - 2 * 60 * 1000;

    if (timeUntilExpiry <= 0) {
      console.warn('Token prossimo alla scadenza. Avvio immediato del rinnovo preventivo.');
      renewAccessToken();
    } else {
      refreshTimer = setTimeout(async () => {
        await renewAccessToken();
      }, timeUntilExpiry);
    }
  } else {
    console.error('Scadenza del token non trovata, eseguendo logout.');
    logout();
  }
}

/**
 * Rinnova il token di accesso utilizzando il refresh token.
 */
export async function renewAccessToken() {
  try {
      console.log('Tentativo di rinnovo preventivo del token.');
      const newAccessToken = await AuthService.refreshAccessToken();
      if (newAccessToken) {
          console.log('Token rinnovato correttamente.');
          scheduleTokenRefresh();
      } else {
          console.error('Rinnovo del token fallito. Effettuando il logout.');
          logout();
      }
  } catch (error) {
      console.error('Errore durante il rinnovo del token:', error);
      logout();
  }
}
