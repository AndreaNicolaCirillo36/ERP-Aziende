import axios from './axiosSetup';

export const SupplierService = {
    
    /**
     * Recupera tutti i fornitori.
     * @returns {Promise<AxiosResponse<any>>} - Risposta dell'API contenente tutti i fornitori.
     */
    async getAllSuppliers() {
        try {
            const response = await axios.get(`/suppliers`);
            return response;
        } catch (error) {
            console.error('Errore durante il recupero di tutti i fornitori:', error);
            throw error;
        }
    },


    /**
     * Crea un nuovo fornitore.
     * @param {Object} supplierData dati del fornitore da creare
     * @returns {Promise<AxiosResponse>} risposta contenente il fornitore creato
     * @throws {Error} se si verifica un errore durante la richiesta
     */
    async createSupplier(supplierData) {
        try {
          const response = await axios.post(`/suppliers`, supplierData);
          return response;
        } catch (error) {
          console.error("Errore durante la creazione del fornitore:", error);
          throw error;
        }
      },

    /**
     * Modifica un fornitore esistente.
     * @param {string} id - L'ID del fornitore da modificare.
     * @param {Object} editSupplier - Le nuove informazioni del fornitore.
     * @returns {Promise<AxiosResponse>} Risposta contenente il fornitore modificato.
     * @throws {Error} Se si verifica un errore durante la richiesta di modifica.
     */
    async editSupplier(id, editSupplier) {
        try {
            const response = await axios.put(`/suppliers/${id}`, editSupplier);
            return response;
        } catch (error) {
            console.error('Errore durante la modifica del fornitore:', error);
            throw error;
        }
    },


    /**
     * Elimina un fornitore esistente.
     * @param {string} id - L'ID del fornitore da eliminare.
     * @returns {Promise<AxiosResponse>} Risposta contenente il fornitore eliminato.
     * @throws {Error} Se si verifica un errore durante la richiesta di eliminazione.
     */ 
    async deleteSupplier(id) {
        try {
            const response = await axios.delete(`/suppliers/${id}`);
            return response;
        } catch (error) {
            console.error("Errore durante l'eliminazione del fornitore:", error);
            throw error;
        }
    }
};