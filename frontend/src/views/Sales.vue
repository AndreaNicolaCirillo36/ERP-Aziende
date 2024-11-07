<script setup>
import { useRoute } from 'vue-router';
import { computed, ref, onMounted, nextTick } from 'vue';
import { ProductService } from '@/service/ProductService';
import { SaleService } from '@/service/SaleService';
import { useToast } from 'vue-toastification';
import { useConfirm } from 'primevue/useconfirm';

const route = useRoute();
const toast = useToast();
const confirm = useConfirm();
const barcode = ref('');
const product = ref(null);
const productList = ref([]);
const errorMessagePaymentMethods = ref('');
const errorMessageDiscount = ref('');
const barcodeInputRef = ref(null);
const discount = ref(0);
const paymentMethods = ref(null);
const note = ref('');
const loading = ref(false);
const paymentOptions = ref(['Carta', 'Contanti', 'Bonifico', 'Assegno']);

const focusBarcodeInput = () => {
    nextTick(() => {
        setTimeout(() => {
            if (barcodeInputRef.value && document.activeElement !== barcodeInputRef.value.$el && document.activeElement !== barcodeInputRef.value) {
                barcodeInputRef.value.$el?.focus?.() || barcodeInputRef.value.focus?.();
            }
        }, 50);
    });
};


onMounted(async () => {
    focusBarcodeInput();
    const route = useRoute();
    const saleId = route.query.saleId;

    if (saleId) {
        try {
            const response = await SaleService.getSaleById(saleId);
            if (response) {
                
                const saleData = response;

                discount.value = saleData.discount;
                paymentMethods.value = saleData.paymentMethods;
                note.value = saleData.note;

                productList.value = saleData.saleItems.map((item) => ({
                    barcode: item.product.barcode,
                    name: item.product.name,
                    sellingPrice: item.sellingPrice,
                    purchasePrice: item.purchasePrice,
                    quantity: item.quantitySold,
                    totalPrice: item.sellingPrice * item.quantitySold,
                }));

                productList.value = [...productList.value];
            }
        } catch (error) {
            console.error('Errore durante il recupero della vendita:', error);
        }
    }
});



function formatCurrency(value) {
    return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(value);
}

const totalAmount = computed(() => {
    return productList.value.reduce((acc, product) => acc + product.totalPrice, 0) - discount.value;
});

const totalProfitto = computed(() => {
    const profitWithoutDiscount = productList.value.reduce((acc, product) => acc + (product.sellingPrice - product.purchasePrice) * product.quantity, 0);
    return profitWithoutDiscount - discount.value;
});

const totalProducts = computed(() => {
    return productList.value.reduce((acc, product) => acc + product.quantity, 0);
});

const searchProduct = async () => {
    try {
        if (!/^[a-zA-Z0-9]+$/.test(barcode.value.trim())) {
            toast.error('Il barcode deve contenere solo lettere, solo numeri, o una combinazione di entrambi.');
            return;
        }


        const response = await ProductService.getProductByBarcode(barcode.value);

        if (response.status === 200 && response.data) {
            product.value = response.data;

            const existingProduct = productList.value.find(p => p.barcode === product.value.barcode);

            if (existingProduct) {
                existingProduct.quantity += 1;
                existingProduct.totalPrice = existingProduct.sellingPrice * existingProduct.quantity;
            } else {
                product.value.quantity = 1;
                product.value.totalPrice = product.value.sellingPrice;
                productList.value.push(product.value);
            }

            barcode.value = '';
            //toast.success('Prodotto aggiunto con successo!');
        } else if (response.status === 404) {
            toast.error('Prodotto non trovato.');
        }
    } catch (error) {
        const errorMsg = error.response?.status === 404 ? 'Prodotto non trovato.' : 'Errore durante la ricerca del prodotto.';
        toast.error(errorMsg);
    } finally {
        focusBarcodeInput();
    }
};

const removeProduct = (barcode) => {
    confirm.require({
        message: 'Sei sicuro di voler rimuovere questo prodotto?',
        header: 'Conferma Rimozione',
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: 'Sì, rimuovi',
        rejectLabel: 'Annulla',
        acceptClass: 'p-button-danger',
        rejectClass: 'p-button-secondary',
        accept: () => {
            const productIndex = productList.value.findIndex(p => p.barcode === barcode);
            if (productIndex !== -1) {
                productList.value.splice(productIndex, 1);
                focusBarcodeInput();
            }
        },
        reject: () => {
            focusBarcodeInput();
        }
    });
};

const saveSale = async () => {
    loading.value = true;

    if (productList.value.length < 1) {
        toast.error('Aggiungere almeno un prodotto per effettuare la vendita.');
        loading.value = false;
        return;
    }
    if (discount.value < 0) {
        toast.error('Il valore dello sconto non può essere negativo.');
        loading.value = false;
        return;
    }
    if (!paymentMethods.value) {
        toast.error('Seleziona un metodo di pagamento.');
        loading.value = false;
        return;
    }

    const saleData = {
        discount: discount.value,
        paymentMethods: paymentMethods.value,
        note: note.value,
        saleItems: productList.value.map(product => ({
            product: { barcode: product.barcode },
            quantitySold: product.quantity,
            sellingPrice: product.sellingPrice,
            purchasePrice: product.purchasePrice
        }))
    };

    try {
        let response;
        const saleId = route.query.saleId;

        if (saleId) {
            response = await SaleService.updateSale(saleId, saleData);
        } else {
            response = await SaleService.saveSale(saleData);
        }

        if (response.status === 201 || response.status === 200) {
            toast.success('Vendita salvata con successo!');
            productList.value = [];
            discount.value = 0;
            paymentMethods.value = null;
            note.value = '';
            focusBarcodeInput();
        } else {
            toast.error('Errore nel salvataggio della vendita.');
        }
    } catch (error) {
        if (error.response && error.response.status === 500) {
            toast.error('Errore interno del server. Riprovare più tardi.');
        } else {
            toast.error('Errore durante il salvataggio della vendita.');
        }
    } finally {
        loading.value = false;
    }


};

</script>

<template>
    <ConfirmDialog />
    <div class="grid grid-cols-12 gap-8">
        <div class="col-span-12 lg:col-span-6 xl:col-span-4">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Totale</span>
                        <div class="text-surface-900 dark:text-primary font-medium text-xl">{{
                            formatCurrency(totalAmount) }}</div>
                    </div>
                    <div class="flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-border"
                        style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-euro text-orange-500 !text-xl"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-4">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Profitto</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{
                            formatCurrency(totalProfitto) }}</div>
                    </div>
                    <div class="flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-border"
                        style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-euro text-green-500 !text-xl"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-4">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Nr. Articoli</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ totalProducts }}</div>
                    </div>
                    <div class="flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-border"
                        style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-shopping-cart text-blue-500 !text-xl"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-span-12 xl:col-span-8">
            <div class="card">
                <div class="font-semibold text-xl mb-4">Vendita</div>
                <DataTable :value="productList" :rows="50" :paginator="true" responsiveLayout="scroll">
                    <Column style="width: 15%" header="Barcode">
                        <template #body="slotProps">
                            {{ slotProps.data.barcode }}
                        </template>
                    </Column>
                    <Column field="descrizione" header="Descrizione" style="width: 50%">
                        <template #body="slotProps">
                            {{ slotProps.data.name }}
                        </template>
                    </Column>
                    <Column field="prezzo" header="Prezzo" style="width: 10%">
                        <template #body="slotProps">
                            {{ formatCurrency(slotProps.data.sellingPrice) }}
                        </template>
                    </Column>
                    <Column field="pz" header="Pz" style="width: 10%">
                        <template #body="slotProps">
                            {{ slotProps.data.quantity }}
                        </template>
                    </Column>
                    <Column field="totale" header="Totale" style="width: 10%">
                        <template #body="slotProps">
                            {{ formatCurrency(slotProps.data.totalPrice) }}
                        </template>
                    </Column>
                    <Column header="Rimuovi" style="width: 5%">
                        <template #body="slotProps">
                            <Button icon="pi pi-times" severity="danger" rounded
                                @click="removeProduct(slotProps.data.barcode)" />
                        </template>
                    </Column>
                </DataTable>
            </div>
        </div>

        <div class="col-span-12 xl:col-span-4">
            <InputGroup>
                <InputText id="barcode-input" v-model="barcode" ref="barcodeInputRef" @keydown.enter="searchProduct"
                    placeholder="Inserisci Barcode" :disabled="loading" />
                <Button label="Cerca" text @click="searchProduct" class="text-primary" :disabled="loading" />
            </InputGroup>

            <div class="grid grid-cols-2 gap-4 mt-6">
                <div>
                    <h3 class="mt-6 text-lg">Totale</h3>
                    <InputText :value="formatCurrency(totalAmount)" readonly="true" class="w-full"
                        :disabled="loading" />
                </div>

                <div>
                    <h3 class="mt-6 text-lg">Abbuono in €</h3>
                    <InputText type="number" v-model.number="discount" class="w-full" :disabled="loading" />
                    <p v-if="errorMessageDiscount" class="text-red-500 mt-2">{{ errorMessageDiscount }}</p>
                </div>
            </div>

            <h3 class="mt-6 text-lg"><i class="pi pi-wallet"></i> Metodi di Pagamento</h3>
            <div class="flex items-center gap-16 mt-2">
                <div v-for="method in paymentOptions" :key="method">
                    <label class="flex items-center cursor-pointer">
                        <RadioButton :name="method" :value="method" v-model="paymentMethods" :disabled="loading" />
                        <span class="leading-none ml-2">{{ method }}</span>
                    </label>
                </div>

            </div>
            <p v-if="errorMessagePaymentMethods" class="text-red-500 mt-2">{{ errorMessagePaymentMethods }}</p>

            <Textarea placeholder="Note" :autoResize="true" v-model="note" rows="4" class="w-full mt-6"
                :disabled="loading" />

            <Button label="Salva vendita" raised class="w-full mt-6" :loading="loading" :disabled="loading"
                @click="saveSale" />

        </div>
    </div>
</template>