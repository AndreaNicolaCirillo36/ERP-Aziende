<script setup>
import router from '@/router';
import { SaleService } from "@/service/SaleService";
import { onMounted, ref, computed } from "vue";
import { useToast } from "vue-toastification";
import Button from "primevue/button";
import Dialog from "primevue/dialog";

const monthNames = [
    "Gennaio",
    "Febbraio",
    "Marzo",
    "Aprile",
    "Maggio",
    "Giugno",
    "Luglio",
    "Agosto",
    "Settembre",
    "Ottobre",
    "Novembre",
    "Dicembre",
];

const toast = useToast();
const sales = ref([]);
const errorMessage = ref("");
const selectedYear = ref(null);
const selectedMonth = ref(null);
const selectedDay = ref(null);
const showDialog = ref(false);
const selectedSale = ref(null);
const noteIsNotEmpty = ref(false);
const deleteSaleDialog = ref(false);
const showNoSalesMessage = ref(false);

onMounted(async () => {
    try {
        setTimeout(() => {
            showNoSalesMessage.value = true;
        }, 1000);

        const allSales = await SaleService.getSalesOrderByDesc();
        sales.value = allSales;
    } catch (error) {
        errorMessage.value = "Errore durante il caricamento delle vendite.";
    }
});


function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString("it-IT", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
    });
}

function formatCurrency(value) {
    return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(value);
}

const filteredSales = computed(() => {
    if (!sales.value || sales.value.length === 0) {
        return [];
    }

    return sales.value.filter((sale) => {
        const saleDate = new Date(sale.saleDate);
        const saleYear = saleDate.getFullYear();
        const saleMonth = saleDate.getMonth() + 1;
        const saleDay = saleDate.getDate();

        return (
            (!selectedYear.value || saleYear === selectedYear.value) &&
            (!selectedMonth.value || saleMonth === selectedMonth.value) &&
            (!selectedDay.value || saleDay === selectedDay.value)
        );
    });
});

const availableYears = computed(() => {
    return [
        ...new Set(
            sales.value.map((sale) => new Date(sale.saleDate).getFullYear())
        ),
    ].sort((a, b) => b - a);
});

const availableMonths = computed(() => {
    if (!selectedYear.value) return [];
    const months = sales.value
        .filter(
            (sale) => new Date(sale.saleDate).getFullYear() === selectedYear.value
        )
        .map((sale) => new Date(sale.saleDate).getMonth() + 1);
    return [...new Set(months)].sort((a, b) => a - b);
});

const availableDays = computed(() => {
    if (!selectedYear.value || !selectedMonth.value) return [];
    const days = sales.value
        .filter((sale) => {
            const saleDate = new Date(sale.saleDate);
            return (
                saleDate.getFullYear() === selectedYear.value &&
                saleDate.getMonth() + 1 === selectedMonth.value
            );
        })
        .map((sale) => new Date(sale.saleDate).getDate());
    return [...new Set(days)].sort((a, b) => a - b);
});

function openSaleDetails(sale) {
    console.log("Vendita selezionata:", sale);
    if (sale.saleItems && sale.saleItems.length > 0) {
        console.log("Articoli nella vendita:", sale.saleItems);
    } else {
        console.log("Nessun articolo in questa vendita.");
    }

    if (sale.note && sale.note.length > 0) {
        noteIsNotEmpty.value = true;
    } else {
        noteIsNotEmpty.value = false;
    }

    selectedSale.value = sale;
    showDialog.value = true;
}

function editSale(sale) {
    router.push({
        name: 'sales',
        query: { saleId: sale.id },
    });

}

function confirmDeleteSale(sale) {
    selectedSale.value = sale;
    deleteSaleDialog.value = true;
}

const deleteSale = async (id) => {
    try {
        const response = await SaleService.deleteSale(id);
        if (response.status === 204) {
            toast.success("Vendita eliminata con successo!");
            sales.value = sales.value.filter((sale) => sale.id !== id);
            selectedSale.value = null;
        } else {
            toast.error("Errore nell'eliminazione della vendita.");
        }
    } catch (error) {
        console.error("Errore durante l'eliminazione della vendita", error);
        if (error.response && error.response.status === 500) {
            toast.error("Errore interno del server. Riprovare più tardi.");
        } else {
            toast.error("Errore nell'eliminazione della vendita.");
        }
    } finally {
        deleteSaleDialog.value = false;
        showDialog.value = false;
    }
};



</script>

<template>
    <div :class="{ 'blur-background': showDialog || deleteSaleDialog }" class="grid grid-cols-12 gap-8">
        <div class="col-span-12 xl:col-span-12">
            <div v-if="showNoSalesMessage && sales.length === 0" class="flex justify-center items-center h-10 mb-6">
                <p class="text-red-500 text-xl">Nessuna vendita disponibile al momento.</p>
            </div>

            <div class="card">

                <div class="flex justify-between items-center mb-4">
                    <div class="font-semibold text-xl">Vendite</div>

                    <div class="flex space-x-4">
                        <div>
                            <select id="year" v-model="selectedYear" class="custom-select border rounded">
                                <option :value="null">Anno</option>
                                <option v-for="year in availableYears" :key="year" :value="year">
                                    {{ year }}
                                </option>
                            </select>
                        </div>

                        <div>
                            <select id="month" v-model="selectedMonth" class="custom-select border rounded"
                                :disabled="!selectedYear">
                                <option :value="null">Mese</option>
                                <option v-for="month in availableMonths" :key="month" :value="month">
                                    {{ monthNames[month - 1] }}
                                </option>
                            </select>
                        </div>

                        <div>
                            <select id="day" v-model="selectedDay" class="custom-select border rounded"
                                :disabled="!selectedMonth">
                                <option :value="null">Giorno</option>
                                <option v-for="day in availableDays" :key="day" :value="day">
                                    {{ day }}
                                </option>
                            </select>
                        </div>
                    </div>
                </div>

                <p v-if="errorMessage" class="text-red-500 mt-2">{{ errorMessage }}</p>
                <DataTable :value="filteredSales" :rows="8" :paginator="true" responsiveLayout="scroll">
                    <Column style="width: 10%" header="Nr.">
                        <template #body="slotProps">
                            {{ slotProps.data.id }}
                        </template>
                    </Column>
                    <Column field="date" header="Data" style="width: 15%">
                        <template #body="slotProps">
                            {{ formatDateTime(slotProps.data.saleDate) }}
                        </template>
                    </Column>
                    <Column field="totale" header="Tot. Parz." style="width: 15%">
                        <template #body="slotProps">
                            {{
                                formatCurrency(
                                    slotProps.data.totalPrice + slotProps.data.discount
                                )
                            }}
                        </template>
                    </Column>
                    <Column field="abbuono" header="Abbuono" style="width: 15%">
                        <template #body="slotProps">
                            {{ formatCurrency(slotProps.data.discount) }}
                        </template>
                    </Column>
                    <Column field="profitto" header="Profitto" style="width: 15%">
                        <template #body="slotProps">
                            {{ formatCurrency(slotProps.data.netProfit) }}
                        </template>
                    </Column>
                    <Column field="totale" header="Totale" style="width: 15%">
                        <template #body="slotProps">
                            {{ formatCurrency(slotProps.data.totalPrice) }}
                        </template>
                    </Column>
                    <Column style="width: 10%" header="Pz.">
                        <template #body="slotProps">
                            {{ slotProps.data.totalProducts }}
                        </template>
                    </Column>
                    <Column style="width: 5%" header="Dettagli">
                        <template #body="slotProps">
                            <Button icon="pi pi-search" type="button" class="p-button-text"
                                @click="openSaleDetails(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>
            </div>
        </div>
    </div>

    <Dialog header="Dettagli vendita" v-model:visible="showDialog" :closable="true"
        :style="deleteSaleDialog ? 'width: 50vw; filter: blur(5px); opacity: 0.8; pointer-events: none;' : 'width: 50vw;'">
        <div v-if="selectedSale">
            <table class="table-auto w-full border-collapse mb-4">
                <tbody>
                    <tr>
                        <td class="font-bold border px-4 py-2">Data</td>
                        <td class="border px-4 py-2">
                            {{ formatDateTime(selectedSale.saleDate) }}
                        </td>
                    </tr>
                    <tr>
                        <td class="font-bold border px-4 py-2">Numero Prodotti</td>
                        <td class="border px-4 py-2">{{ selectedSale.totalProducts }}</td>
                    </tr>
                    <tr>
                        <td class="font-bold border px-4 py-2">Metodo di pagamento</td>
                        <td class="border px-4 py-2">{{ selectedSale.paymentMethods }}</td>
                    </tr>
                    <tr>
                        <td class="font-bold border px-4 py-2">Importo</td>
                        <td class="border px-4 py-2">
                            {{ formatCurrency(selectedSale.totalPrice + selectedSale.discount) }}
                        </td>
                    </tr>
                    <tr>
                        <td class="font-bold border px-4 py-2">Abbuono</td>
                        <td class="border text-orange-500 px-4 py-2">
                            {{ formatCurrency(selectedSale.discount) }}
                        </td>
                    </tr>
                    <tr>
                        <td class="font-bold border px-4 py-2">Profitto</td>
                        <td class="border px-4 py-2">
                            {{ formatCurrency(selectedSale.netProfit) }}
                        </td>
                    </tr>
                    <tr>
                        <td class="font-bold border px-4 py-2">Totale</td>
                        <td class="border text-green-500 px-4 py-2">
                            {{ formatCurrency(selectedSale.totalPrice) }}
                        </td>
                    </tr>
                    <tr v-if="noteIsNotEmpty">
                        <td class="font-bold border px-4 py-2">Note</td>
                        <td class="border px-4 py-2">{{ selectedSale.note }}</td>
                    </tr>
                </tbody>
            </table>

            <h3 class="text-xl font-semibold mb-4 mt-6">Articoli Venduti</h3>
            <table class="table-auto w-full border-collapse">
                <thead>
                    <tr>
                        <th class="border px-4 py-2 text-left">Descrizione</th>
                        <th class="border px-4 py-2 text-left">Barcode</th>
                        <th class="border px-4 py-2 text-left">Quantità</th>
                        <th class="border px-4 py-2 text-left">Prezzo Unitario</th>
                        <th class="border px-4 py-2 text-left">Prezzo Totale</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in selectedSale.saleItems" :key="item.id">
                        <td class="border px-4 py-2">{{ item.product.name }}</td>
                        <td class="border px-4 py-2">{{ item.product.barcode }}</td>
                        <td class="border px-4 py-2">{{ item.quantitySold }}</td>
                        <td class="border px-4 py-2">
                            {{ formatCurrency(item.product.sellingPrice) }}
                        </td>
                        <td class="border px-4 py-2">
                            {{ formatCurrency(item.sellingPrice) }}
                        </td>
                    </tr>
                </tbody>
            </table>
            <div class="text-right mt-6">
                <Button label="Elimina" class="p-button p-button-danger"  style="margin-right: 5px;" @click="confirmDeleteSale(selectedSale)" />
                <Button label="Modifica" class="p-button p-button-primary"
                    @click="editSale(selectedSale)" />
            </div>
        </div>
    </Dialog>

    <Dialog v-model:visible="deleteSaleDialog" :modal="true" :style="{ width: '450px' }" header="Conferma"
        :closable="false">
        <div class="flex items-center gap-4">
            <i class="pi pi-exclamation-triangle !text-3xl" />
            <span v-if="selectedSale">
                Eliminare la vendita nr. <b class="text-primary">{{ selectedSale.id }}</b> del
                <b class="text-primary">{{ formatDateTime(selectedSale.saleDate) }}</b>?
            </span>
        </div>
        <template #footer>
            <Button label="No" icon="pi pi-times" text @click="deleteSaleDialog = false" />
            <Button label="Si" icon="pi pi-check" @click="deleteSale(selectedSale.id)" />
        </template>
    </Dialog>

</template>

<style scoped>
.blur-background {
    filter: blur(5px);
    pointer-events: none;
    opacity: 0.8;
}
</style>
