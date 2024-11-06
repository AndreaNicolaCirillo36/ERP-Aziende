import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { scheduleTokenRefresh, isTokenExpired, renewAccessToken } from "@/service/auth"; // Importa la funzione per pianificare il rinnovo del token

// PrimeVue setup
import Aura from '@primevue/themes/aura'; // Importa il tema Aura
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';
import ToastService from 'primevue/toastservice';
import Button from 'primevue/button'; // Importa Button
import Dialog from 'primevue/dialog'; // Importa Dialog
import VCalendar from 'v-calendar';
import Toast from 'vue-toastification';

import 'vue-toastification/dist/index.css';
import '@/assets/styles.scss';
import '@/assets/tailwind.css';
import 'v-calendar/style.css';

// Configura le opzioni di Toastification
const options = {
    timeout: 3000,
    closeOnClick: true,
};

// Crea l'app Vue
const app = createApp(App);

app.use(router);

// Configura PrimeVue e il tema
app.use(PrimeVue, {
    theme: {
        preset: Aura,
        options: {
            darkModeSelector: '.app-dark'
        }
    }
});

// Servizi PrimeVue
app.use(ToastService);
app.use(ConfirmationService);

// Usa VCalendar e Toastification
app.use(VCalendar, {});
app.use(Toast, options);

// Registra i componenti PrimeVue
app.component('Button', Button);
app.component('Dialog', Dialog);

// Verifica se ci sono token salvati nella sessionStorage all'avvio dell'app
(async () => {
    if (sessionStorage.getItem('accessToken') && sessionStorage.getItem('refreshToken')) {
        try {
            if (isTokenExpired()) {
                console.warn('Access token prossimo alla scadenza. Avvio del rinnovo immediato.');
                await renewAccessToken(); // Avvio immediato del rinnovo se il token è prossimo alla scadenza o scaduto
            } else {
                scheduleTokenRefresh(); // Pianifica il rinnovo del token
            }
        } catch (error) {
            console.error('Errore durante il rinnovo del token all\'avvio:', error);
        }
    }
})();

// Monta l'applicazione all'elemento con id 'app'
app.mount('#app');
