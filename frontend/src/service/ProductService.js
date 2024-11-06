import axios from './axiosSetup';

export const ProductService = {
    
    /**
     * Recupera un prodotto dal suo codice a barre.
     * @param {string} barcode codice a barre del prodotto
     * @returns {Promise<AxiosResponse<Product>>} risposta contenente il prodotto
     * @throws {Error} se il prodotto non viene trovato
     */
    async getProductByBarcode(barcode) {
        try {
            const response = await axios.get(`/products/barcode/${barcode}`);
            return response;
        } catch (error) {
            console.error('Errore durante il recupero del prodotto:', error);
            throw error;
        }
    },
    /**
     * Recupera la lista di tutti i prodotti.
     * @returns {Promise<AxiosResponse<Product[]>>} risposta contenente la lista di prodotti
     * @throws {Error} se si verifica un errore durante la richiesta
     */ 
    async getAllProducts() {
        try {
            const response = await axios.get(`/products`);
            return response;
        } catch (error) {
            console.error('Errore durante il recupero di tutti i prodotti:', error);
            throw error;
        }
    },
    /**
     * Crea un nuovo prodotto.
     * @param {Object} productData informazioni del prodotto da creare
     * @returns {Promise<AxiosResponse>} risposta contenente il prodotto creato
     * @throws {Error} se si verifica un errore durante la richiesta
     */
    async createProduct(productData) {
        try {
          const response = await axios.post(`/products`, productData);
          return response;
        } catch (error) {
          console.error("Errore durante la creazione del prodotto:", error);
          throw error;
        }
      },      
    /**
     * Modifica un prodotto esistente.
     * @param {string} id - L'ID del prodotto da modificare.
     * @param {Object} editProduct - Le nuove informazioni del prodotto.
     * @returns {Promise<AxiosResponse>} Risposta contenente il prodotto modificato.
     * @throws {Error} Se si verifica un errore durante la richiesta di modifica.
     */
    async editProduct(id, editProduct) {
        try {
            const response = await axios.put(`/products/${id}`, editProduct);
            return response;
        } catch (error) {
            console.error('Errore durante la modifica del prodotto:', error);
            throw error;
        }
    },
    /**
     * Elimina un prodotto esistente.
     * @param {string} id - L'ID del prodotto da eliminare.
     * @returns {Promise<AxiosResponse>} Risposta contenente il prodotto eliminato.
     * @throws {Error} Se si verifica un errore durante la richiesta di eliminazione.
     */
    async deleteProduct(id) {
        try {
            const response = await axios.delete(`/products/${id}`);
            return response;
        } catch (error) {
            console.error("Errore durante l'eliminazione del prodotto:", error);
            throw error;
        }
    }
};
