# ERP Aziende

ERP Aziende è un sistema ERP (Enterprise Resource Planning) sviluppato per la gestione delle operazioni aziendali. Questo progetto include un backend in Java/Spring Boot e un frontend sviluppato con Vue.js utilizzando il tema Sakaivue adattato per le esigenze aziendali.

## Indice

- [Introduzione](#introduzione)
- [Caratteristiche](#caratteristiche)
- [Tecnologie Utilizzate](#tecnologie-utilizzate)
- [Installazione](#installazione)
  - [Prerequisiti](#prerequisiti)
  - [Setup](#setup)
- [Come Usare](#come-usare)
- [API Documentation](#api-documentation)
- [Contribuire](#contribuire)
- [Licenza](#licenza)

## Introduzione

⚠️ **Attenzione**: Questo progetto è attualmente **in fase di sviluppo** e non è ancora completo. Nuove funzionalità e miglioramenti sono in corso di implementazione.

ERP Aziende è una soluzione ERP semplice e modulare progettata per aiutare le piccole e medie imprese a gestire le loro operazioni quotidiane, tra cui la gestione dei prodotti, fornitori e vendite.

Il progetto è suddiviso in due parti principali:

- **Backend**: Sviluppato con Java 17 e Spring Boot, offre tutte le funzionalità del server, gestione della logica di business e delle connessioni al database.
- **Frontend**: Sviluppato con Vue.js, offre un'interfaccia utente moderna e reattiva.

## Caratteristiche

- **Gestione Prodotti**: CRUD completo per la gestione dei prodotti aziendali.
- **Gestione Fornitori**: Funzionalità per aggiungere e modificare i fornitori dei prodotti.
- **Autenticazione JWT**: Autenticazione sicura utilizzando JSON Web Token.
- **Interfaccia Utente Intuitiva**: Utilizzo del tema Sakaivue con componenti personalizzati per un'interfaccia moderna.
- **Creazione Utente di Default**: Al primo avvio, se non esiste nessun utente, viene creato un utente di default con username `admin` e password `admin123`. L'utente sarà eliminato dopo la creazione di un nuovo utente amministratore.

## Tecnologie Utilizzate

- **Backend**: Java 17, Spring Boot, JPA/Hibernate, MySQL
- **Frontend**: Vue.js 3, Vite, PrimeVue
- **Autenticazione**: JWT (JSON Web Token)
- **Integrazione API**: Axios per le chiamate API

## Installazione

### Prerequisiti

Assicurati di avere installato:

- [Node.js](https://nodejs.org/)  
- [Java JDK](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) (v17)
- [MySQL](https://dev.mysql.com/downloads/installer/) (o un altro database compatibile)
- [Maven](https://maven.apache.org/)

### Setup

#### Backend

1. Fai un fork del progetto originale dal repository su GitHub.
2. Clona il tuo fork del repository:
   ```bash
   git clone https://github.com/<tuo-username>/ERP-Aziende.git
   ```
3. Vai alla directory del backend:
   ```bash
   cd ERP-Aziende/backend
   ```
4. Modifica il file `application.properties` per configurare le tue credenziali del database.
5. Compila e avvia il backend:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

#### Frontend

1. Vai alla directory del frontend:
   ```bash
   cd ../frontend
   ```
2. Installa le dipendenze:
   ```bash
   npm install
   ```
3. Avvia il server di sviluppo:
   ```bash
   npm run dev
   ```

## Come Usare

Una volta avviati il backend e il frontend:

- Vai su `http://localhost:5173` nel tuo browser.
- Usa le credenziali predefinite (`admin`/`admin123`) per autenticarti. Alla prima autenticazione, ti verrà chiesto di creare un nuovo utente amministratore, dopo di che l'utente di default verrà eliminato per motivi di sicurezza.

## API Documentation

La documentazione del backend è disponibile utilizzando **Swagger**. Per visualizzare la documentazione completa delle API:

- Vai su `http://localhost:8080/swagger-ui.html` per esplorare gli endpoint disponibili.

## Contribuire

Siamo aperti ai contributi! Se desideri contribuire:

1. Fai un fork del progetto.
2. Crea un nuovo branch:
   ```bash
   git checkout -b feature/nome-feature
   ```
3. Fai i tuoi cambiamenti e commit:
   ```bash
   git commit -m "Aggiunta di una nuova funzionalità"
   ```
4. Pusha il branch:
   ```bash
   git push origin feature/nome-feature
   ```
5. Apri una Pull Request.

## Licenza

**Frontend**: Il frontend del progetto è distribuito sotto licenza MIT. Consulta il file [frontend/LICENSE](frontend/LICENSE) per maggiori dettagli.

**Backend**: Il backend del progetto è distribuito sotto licenza GNU General Public License v3 Consulta il file [backend/LICENSE](backend/LICENSE) per maggiori dettagli.

⚠️ **Nota**: Questo progetto è attualmente **in fase di sviluppo** e non è ancora completo.
