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
    }
};