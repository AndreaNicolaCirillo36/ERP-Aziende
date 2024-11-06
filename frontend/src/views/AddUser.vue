<script setup>
import router from '@/router';
import AuthService from '@/service/authService';
import { logout } from '@/service/auth';
import { ref, onMounted } from 'vue';

// Variabili di stato per gestire i campi del form, caricamento, messaggi di errore/successo
const username = ref('');
const password = ref('');
const role = ref('');
const loading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const formDisabled = ref(false);
const defaultUser = ref(false); // Indica se l'utente corrente è il default "admin"

// Opzioni per il dropdown dei ruoli
const dropdownRoles = ref([
  { name: 'Admin', code: 'ADMIN' },
  { name: 'User', code: 'USER' }
]);

// Controlla se esiste un utente di default (ad esempio "admin") all'avvio della pagina
onMounted(async () => {
  try {
    const response = await AuthService.defaultUser(); // Richiama il service per verificare l'utente di default
    const isDefaultUserPresent = response.data;

    if (isDefaultUserPresent) {
      defaultUser.value = true; // Se l'utente di default è presente, abilita la visualizzazione del messaggio di attenzione
    } else {
      defaultUser.value = false;
    }
  } catch (error) {
    router.push("/auth/error"); // Reindirizza alla pagina di errore se la chiamata fallisce
  }
});

// Funzione per aggiungere un utente
const addUser = async () => {
  loading.value = true; // Impedisce altre azioni durante il caricamento
  errorMessage.value = '';
  successMessage.value = '';

  // Verifica che tutti i campi del form siano compilati
  if (!username.value || !password.value || !role.value) {
    errorMessage.value = "Tutti i campi sono obbligatori!";
    loading.value = false;
    return;
  }

  try {
    const response = await AuthService.addUser(username.value, password.value, role.value.code);

    if (response && response.status === 201) {
      successMessage.value = 'Utente registrato con successo. Sarai reindirizzato alla Home';
      formDisabled.value = true; // Disabilita il form dopo la registrazione

      // Se l'utente di default è presente, effettua il logout dopo la registrazione
      if (defaultUser.value === true) {
        logout();
        return;
      }

      // Altrimenti reindirizza l'utente alla homepage dopo 3 secondi
      setTimeout(() => {
        router.replace('/');
      }, 3000);
    } else {
      errorMessage.value = "Errore durante la creazione dell'utente.";
    }
  } catch (error) {
    // Gestione degli errori HTTP specifici
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      errorMessage.value = 'Non hai i permessi per creare un Utente';
    } else if (error.response && error.response.status === 400) {
      errorMessage.value = 'Richiesta errata.';
    } else {
      errorMessage.value = 'Errore di connessione al server';
    }
  } finally {
    loading.value = false; // Ferma il caricamento, indipendentemente dall'esito dell'operazione
  }
};
</script>

<template>
  <div :class="['bg-surface-50', 'dark:bg-surface-950', 'flex', 'items-center', 'justify-center', 'px-4']"
    style="min-height: 80vh;">
    <div class="flex flex-col items-center justify-center w-full max-w-[600px] mx-auto">
      <div
        style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)"
        class="w-full">
        <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20" style="border-radius: 53px">
          <div class="text-center mb-8">
            <div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">Aggiungi un utente</div>
            <span class="text-muted-color font-medium">Presta attenzione al ruolo che selezioni!</span>
            <p v-if="defaultUser" class="text-red-500 font-semibold mt-4">⚠️ Questo è il primo accesso, crea un utente
              con ruolo Admin. L'utente di default "admin" verrà eliminato.</p>
          </div>
          <div>
            <!-- Campo Username -->
            <label for="username"
              class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">Username</label>
            <InputText id="username" type="text" placeholder="Username" class="w-full mb-4" v-model="username"
              :disabled="formDisabled || loading" />

            <!-- Campo Password -->
            <label for="password"
              class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">Password</label>
            <Password id="password" v-model="password" placeholder="Password" :toggleMask="true" class="mb-4" fluid
              :feedback="false" :disabled="formDisabled || loading"></Password>

            <!-- Campo Ruolo -->
            <label for="role" class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">Ruolo</label>
            <Select id="role" v-if="defaultUser" v-model="role"
              :options="[dropdownRoles.find(role => role.code === 'ADMIN')]" optionLabel="name"
              placeholder="Seleziona Ruolo" class="w-full mb-8" :disabled="formDisabled || loading"></Select>
            <Select id="role" v-else v-model="role" :options="dropdownRoles" optionLabel="name"
              placeholder="Seleziona Ruolo" class="w-full mb-8" :disabled="formDisabled || loading"></Select>

            <!-- Pulsante Aggiungi -->
            <Button label="Aggiungi" class="w-full" @click="addUser" :loading="loading"
              :disabled="formDisabled || loading" />

            <!-- Messaggio di Errore -->
            <p v-if="errorMessage" class="text-red-500 mt-4">{{ errorMessage }}</p>

            <!-- Messaggio di Successo -->
            <p v-if="successMessage" class="text-green-500 mt-4">{{ successMessage }}</p>
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
