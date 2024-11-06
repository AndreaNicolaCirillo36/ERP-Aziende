<script setup>
import FloatingConfigurator from '@/components/FloatingConfigurator.vue';
import router from '@/router';
import { saveToken, scheduleTokenRefresh, getUsername } from '@/service/auth';
import AuthService from '@/service/authService';
import { ref } from 'vue';

// Variabili di stato per gestire username, password, caricamento e messaggi di errore
const username = ref('');
const password = ref('');
const loading = ref(false);
const errorMessage = ref('');
const validationCheckTriggered = ref(false); // Indica se i controlli di validazione sono stati effettuati

// Regex per la validazione dell'input
const alphanumericRegex = /^[a-zA-Z0-9]*$/; // Solo caratteri alfanumerici
const specialCharRegex = /[!@#$%^&*(),.?":{}|<>]/; // Almeno un carattere speciale
const uppercaseRegex = /[A-Z]/; // Almeno una lettera maiuscola
const lowercaseRegex = /[a-z]/; // Almeno una lettera minuscola
const numberRegex = /[0-9]/; // Almeno un numero

// Funzione per il login
const login = async () => {
  loading.value = true; // Impedisce azioni durante il caricamento
  errorMessage.value = '';
  validationCheckTriggered.value = true; // Attiva il controllo di validazione per fornire feedback visivo

  // Controllo della lunghezza dell'username e che contenga solo caratteri alfanumerici
  if (username.value.length < 3 || username.value.length > 50 || !alphanumericRegex.test(username.value)) {
    loading.value = false; // Ferma il caricamento se i dati sono invalidi
    return;
  }

  // Se l'username non è "admin", controlla i criteri di sicurezza della password
  if (username.value !== "admin") {
    if (
      !lowercaseRegex.test(password.value) || // Controlla presenza di lettere minuscole
      !uppercaseRegex.test(password.value) || // Controlla presenza di lettere maiuscole
      !numberRegex.test(password.value) || // Controlla presenza di numeri
      !specialCharRegex.test(password.value) || // Controlla presenza di caratteri speciali
      password.value.length < 8 // Controlla la lunghezza della password
    ) {
      loading.value = false; // Ferma il caricamento se i dati sono invalidi
      return;
    }
  }

  try {
    const response = await AuthService.login(username.value, password.value);

    if (response?.status === 200 && response.data?.accessToken) {
      loading.value = false; // Ferma il caricamento dopo la risposta positiva
      saveToken(response.data.accessToken, response.data.refreshToken); // Salva i token di accesso e di refresh
      scheduleTokenRefresh(); // Pianifica il rinnovo del token

      // Reindirizza a pagine differenti in base all'utente
      if (username.value === "admin") {
        router.replace('/addUser');
        return;
      }
      router.replace('/'); // Reindirizza alla home per utenti non admin
    } else {
      loading.value = false;
      errorMessage.value = 'Errore durante il login.';
    }
  } catch (error) {
    loading.value = false;
    if (error.response) {
      const status = error.response.status;

      // Gestione specifica degli errori di risposta del server
      if (status === 401 || status === 403) {
        errorMessage.value = 'Credenziali non valide';
      } else if (status === 500) {
        errorMessage.value = 'Errore interno del server. Si prega di riprovare più tardi.';
      } else if (status === 504) {
        errorMessage.value = 'Timeout del server. Si prega di verificare la connessione e riprovare.';
      } else {
        errorMessage.value = `Errore del server (codice ${status}). Si prega di riprovare più tardi.`;
      }
    } else {
      // Gestione degli errori di connessione al server
      errorMessage.value = 'Errore di connessione al server. Verifica la tua connessione e riprova.';
    }
  }
};
</script>

<template>
  <FloatingConfigurator />
  <div
    class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-[100vw] overflow-hidden">
    <div class="flex flex-col items-center justify-center">
      <div
        style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
        <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20" style="border-radius: 53px">
          <div class="text-center mb-8">
            <div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">Ciao da ERP Aziende!</div>
            <span class="text-muted-color font-medium">Effettua il Login</span>
          </div>

          <div>
            <!-- Campo Username -->
            <label for="username"
              class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">Username</label>
            <InputText id="username" type="text" placeholder="Username" class="w-full md:w-[30rem]" v-model="username"
              :disabled="loading" />

            <!-- Messaggi di validazione dell'username -->
            <div class="mt-4 mb-8">
              <p v-if="username.value !== 'admin'" :class="{
                'text-green-500': username.length >= 3 && username.length <= 50 && alphanumericRegex.test(username),
                'text-red-500': validationCheckTriggered &&
                  (username.length < 3 || username.length > 50 || !alphanumericRegex.test(username)),
                'text-gray-500': !validationCheckTriggered
              }">
                <i :class="{
                  'pi pi-check': username.length >= 3 && username.length <= 50 && alphanumericRegex.test(username),
                  'pi pi-times': validationCheckTriggered &&
                    (username.length < 3 || username.length > 50 || !alphanumericRegex.test(username)),
                  'pi pi-circle': !validationCheckTriggered
                }" class="mr-2"></i>
                L'username deve avere da 3 a 50 caratteri alfanumerici.
              </p>

            </div>

            <!-- Campo Password -->
            <label for="password"
              class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">Password</label>
            <Password id="password" v-model="password" placeholder="Password" :toggleMask="true" class="mb-4" fluid @keydown.enter="login"
              :feedback="false" :disabled="loading"></Password>

            <!-- Messaggi di validazione della password -->
            <div class="mt-4" v-if="username.value !== 'admin'">
              <p
                :class="{ 'text-green-500': uppercaseRegex.test(password), 'text-red-500': validationCheckTriggered && !uppercaseRegex.test(password), 'text-gray-500': !validationCheckTriggered }">
                <i :class="{ 'pi pi-check': uppercaseRegex.test(password), 'pi pi-times': validationCheckTriggered && !uppercaseRegex.test(password), 'pi pi-circle': !validationCheckTriggered }"
                  class="mr-2"></i>
                La password deve contenere almeno una lettera maiuscola.
              </p>
              <p
                :class="{ 'text-green-500': lowercaseRegex.test(password), 'text-red-500': validationCheckTriggered && !lowercaseRegex.test(password), 'text-gray-500': !validationCheckTriggered }">
                <i :class="{ 'pi pi-check': lowercaseRegex.test(password), 'pi pi-times': validationCheckTriggered && !lowercaseRegex.test(password), 'pi pi-circle': !validationCheckTriggered }"
                  class="mr-2"></i>
                La password deve contenere almeno una lettera minuscola.
              </p>
              <p
                :class="{ 'text-green-500': numberRegex.test(password), 'text-red-500': validationCheckTriggered && !numberRegex.test(password), 'text-gray-500': !validationCheckTriggered }">
                <i :class="{ 'pi pi-check': numberRegex.test(password), 'pi pi-times': validationCheckTriggered && !numberRegex.test(password), 'pi pi-circle': !validationCheckTriggered }"
                  class="mr-2"></i>
                La password deve contenere almeno un numero.
              </p>
              <p
                :class="{ 'text-green-500': password.length >= 8, 'text-red-500': validationCheckTriggered && password.length < 8, 'text-gray-500': !validationCheckTriggered }">
                <i :class="{ 'pi pi-check': password.length >= 8, 'pi pi-times': validationCheckTriggered && password.length < 8, 'pi pi-circle': !validationCheckTriggered }"
                  class="mr-2"></i>
                La password deve avere almeno 8 caratteri.
              </p>
              <p
                :class="{ 'text-green-500': specialCharRegex.test(password), 'text-red-500': validationCheckTriggered && !specialCharRegex.test(password), 'text-gray-500': !validationCheckTriggered }">
                <i :class="{ 'pi pi-check': specialCharRegex.test(password), 'pi pi-times': validationCheckTriggered && !specialCharRegex.test(password), 'pi pi-circle': !validationCheckTriggered }"
                  class="mr-2"></i>
                La password deve contenere almeno un carattere speciale.
              </p>
            </div>

            <!-- Pulsante di login -->
            <Button label="Login" class="w-full mt-6" @click="login" :loading="loading" :disabled="loading" />

            <!-- Messaggio di errore -->
            <p v-if="errorMessage" class="text-red-500 mt-4">{{ errorMessage }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pi-eye {
  transform: scale(1.6);
  margin-right: 1rem;
}

.pi-eye-slash {
  transform: scale(1.6);
  margin-right: 1rem;
}
</style>
