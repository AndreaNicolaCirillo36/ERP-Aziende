import axios from './axiosSetup';

export const SaleService = {

/**
 * Recupera tutti i record di vendita dal server.
 * 
 * Effettua una richiesta GET all'endpoint /sales per recuperare i dati.
 * Restituisce i dati delle vendite se la richiesta ha successo.
 * Registra un messaggio di errore e lancia un errore se la richiesta fallisce.
 *
 * @returns {Promise<Object[]>} Una promessa che si risolve in un array di record di vendita.
 * @throws Genera un errore se la richiesta di rete fallisce.
 */
    async getSales() {
        try {
            const response = await axios.get('http://localhost:8080/sales');
            return response.data;
        } catch (error) {
            console.error('Errore durante il recupero delle vendite:', error);
            throw error;
        }
    },


    /**
     * Recupera un singolo record di vendita dal server in base al suo id.
     * 
     * Effettua una richiesta GET all'endpoint /sales/:id per recuperare i dati.
     * Restituisce i dati della vendita se la richiesta ha successo.
     * Registra un messaggio di errore e lancia un errore se la richiesta fallisce.
     * 
     * @param {number} id - L'id della vendita da recuperare.
     * @returns {Promise<Object>} Una promessa che si risolve nel record della vendita.
     * @throws Genera un errore se la richiesta di rete fallisce.
     */
    async getSaleById(id) {
        try {
            const response = await axios.get(`/sales/${id}`);
            return response.data;
        } catch (error) {
            console.error('Errore durante il recupero della vendita:', error);
            throw error;
        }
    },    

    /**
     * Recupera tutti i record di vendita dal server, ordinati in ordine decrescente rispetto alla loro data di creazione.
     * 
     * Effettua una richiesta GET all'endpoint /sales/orderByDesc per recuperare i dati.
     * Restituisce i dati delle vendite se la richiesta ha successo.
     * Registra un messaggio di errore e lancia un errore se la richiesta fallisce.
     * 
     * @returns {Promise<Object[]>} Una promessa che si risolve in un array di record di vendita, ordinati in ordine decrescente rispetto alla loro data di creazione.
     * @throws Genera un errore se la richiesta di rete fallisce.
     */
    async getSalesOrderByDesc() {
        try {
            const response = await axios.get('/sales/orderByDesc');
            return response.data;
        } catch (error) {
            console.error('Errore durante il recupero delle vendite:', error);
            throw error;
        }
    },

    /**
     * Recupera l'ultimo record di vendita dal server.
     * 
     * Effettua una richiesta GET all'endpoint /sales/latest per recuperare i dati.
     * Restituisce l'ultimo record di vendita se la richiesta ha successo.
     * Registra un messaggio di errore e lancia un errore se la richiesta fallisce.
     * 
     * @returns {Promise<Object>} Una promessa che si risolve nell'ultimo record di vendita.
     * @throws Genera un errore se la richiesta di rete fallisce.
     */
    async getSalesLatest() {
        try {
            const response = await axios.get('/sales/latest');
            return response.data;
        } catch (error) {
            console.error('Errore durante il recupero delle ultime vendite:', error);
            throw error;
        }
    },

    /**
     * Recupera tutti i record di vendita dal server creati oggi.
     * 
     * Effettua una richiesta GET all'endpoint /sales/today per recuperare i dati.
     * Restituisce i dati delle vendite se la richiesta ha successo.
     * Registra un messaggio di errore e lancia un errore se la richiesta fallisce.
     * 
     * @returns {Promise<Object[]>} Una promessa che si risolve in un array di record di vendita creati oggi.
     * @throws Genera un errore se la richiesta di rete fallisce.
     */
    async getSalesToday() {
        try {
            const response = await axios.get('/sales/today');
            return response.data;
        } catch (error) {
            console.error('Errore durante il recupero delle vendite di oggi:', error);
            throw error;
        }
    },

    /**
     * Recupera tutti i record di vendita dal server creati nel mese corrente.
     * 
     * Effettua una richiesta GET all'endpoint /sales/current-month per recuperare i dati.
     * Restituisce i dati delle vendite se la richiesta ha successo.
     * Registra un messaggio di errore e lancia un errore se la richiesta fallisce.
     * 
     * @returns {Promise<Object[]>} Una promessa che si risolve in un array di record di vendita creati nel mese corrente.
     * @throws Genera un errore se la richiesta di rete fallisce.
     */
    async getSalesCurrentMonth() {
        try {
            const response = await axios.get('/sales/current-month');
            return response.data;
        } catch (error) {
            console.error('Errore durante il recupero delle vendite del mese corrente:', error);
            throw error;
        }
    },

    /**
     * Salva un nuovo record di vendita sul server.
     * 
     * Esegue una richiesta POST all'endpoint /sales con i dati della vendita.
     * Restituisce la risposta del server se la richiesta ha successo.
     * Registra un messaggio di errore e lancia un errore se la richiesta fallisce.
     * 
     * @param {Object} saleData - I dati della vendita da salvare.
     * @returns {Promise<Object>} Una promessa che si risolve nella risposta del server.
     * @throws Genera un errore se la richiesta di rete fallisce.
     */
    async saveSale(saleData) {
        try {
            const response = await axios.post('/sales', saleData);
            return response;
        } catch (error) {
            console.error('Errore durante il salvataggio della vendita:', error);
            throw error;
        }
    },

    /**
     * Aggiorna un record di vendita esistente sul server.
     * 
     * Effettua una richiesta PUT all'endpoint /sales/:id con i dati di vendita forniti.
     * Restituisce la risposta del server se la richiesta ha successo.
     * Registra un messaggio di errore e genera un errore se la richiesta fallisce.
     * 
     * @param {number} saleId - L'id della vendita da aggiornare.
     * @param {Object} saleData - I dati aggiornati della vendita.
     * @returns {Promise<Object>} Una promessa che si risolve nella risposta del server.
     * @throws Genera un errore se la richiesta di rete fallisce.
     */
    async updateSale(saleId, saleData) {
        try {
            const response = await axios.put(`/sales/${saleId}`, saleData);
            return response;
        } catch (error) {
            console.error('Errore durante l\'aggiornamento della vendita:', error);
            throw error;
        }
    },
    
/**
 * Elimina un record di vendita dal server in base al suo id.
 * 
 * Effettua una richiesta DELETE all'endpoint /sales/:id per eliminare la vendita.
 * Restituisce la risposta del server se la richiesta ha successo.
 * Registra un messaggio di errore e lancia un errore se la richiesta fallisce.
 * 
 * @param {number} saleId - L'id della vendita da eliminare.
 * @returns {Promise<Object>} Una promessa che si risolve nella risposta del server.
 * @throws Genera un errore se la richiesta di rete fallisce.
 */
    async deleteSale(saleId) {
        try {
            const response = await axios.delete(`/sales/${saleId}`);
            return response;
        } catch (error) {
            console.error("Errore durante l'eliminazione della vendita:", error);
            throw error;
        }
    }
};

