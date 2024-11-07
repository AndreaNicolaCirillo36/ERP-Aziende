<script setup>
import '@/assets/layout/Calendar.css';
import router from '@/router';
import { SaleService } from '@/service/SaleService';
import { onMounted, ref } from 'vue';

const sales = ref([]);
const salesToday = ref([]);
const nrSalesToday = ref(0);
const incassoToday = ref(0);
const profittoToday = ref(0);
const salesMonth = ref([]);
const nrSalesMonth = ref(0);
const incassoMonth = ref(0);
const profittoMonth = ref(0);
const errorMessage = ref('');

onMounted(async () => {
    try {
        const latestSales = await SaleService.getSalesLatest();
        sales.value = latestSales;

        const todaySales = await SaleService.getSalesToday();
        salesToday.value = todaySales;
        nrSalesToday.value = todaySales.length;
        incassoToday.value = parseFloat(todaySales.reduce((total, sale) => total + sale.totalPrice, 0).toFixed(2));
        profittoToday.value = parseFloat(todaySales.reduce((total, sale) => total + sale.netProfit, 0).toFixed(2));

        const monthSales = await SaleService.getSalesCurrentMonth();
        salesMonth.value = monthSales;
        nrSalesMonth.value = monthSales.length;
        incassoMonth.value = parseFloat(monthSales.reduce((total, sale) => total + sale.totalPrice, 0).toFixed(2));
        profittoMonth.value = parseFloat(monthSales.reduce((total, sale) => total + sale.netProfit, 0).toFixed(2));
    } catch (error) {
        errorMessage.value = 'Errore durante il caricamento delle vendite.';
    }
});

function goToSalePage() {
    router.push('/sales');
}

function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('it-IT', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}


function formatCurrency(value) {
    return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(value);
}

const attrsCalendar = ref([
    {
        highlight: true,
        dates: new Date()
    },
]);

</script>

<template>
    <div class="grid grid-cols-12 gap-8">
        <div class="col-span-12 lg:col-span-6 xl:col-span-4">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Vendite odierne</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ nrSalesToday }}</div>
                    </div>
                    <div class="flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-border"
                        style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-shopping-cart text-blue-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-primary font-medium">{{ nrSalesMonth }} </span>
                <span class="text-muted-color"> questo mese</span>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-4">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Incasso</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">€ {{ incassoToday }}</div>
                    </div>
                    <div class="flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-border"
                        style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-euro text-orange-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-primary font-medium">€ {{ incassoMonth }} </span>
                <span class="text-muted-color"> questo mese</span>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-4">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Profitto</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">€ {{ profittoToday }}</div>
                    </div>
                    <div class="flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-border"
                        style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-euro text-green-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-primary font-medium">€ {{ profittoMonth }} </span>
                <span class="text-muted-color"> questo mese</span>
            </div>
        </div>

        <div class="col-span-12 xl:col-span-8">
            <div class="card">
                <div class="font-semibold text-xl mb-4">Ultime Vendite</div>
                <p v-if="errorMessage" class="text-red-500 mt-2">{{ errorMessage }}</p>
                <DataTable :value="sales" :rows="6" :paginator="true" responsiveLayout="scroll">
                    <Column style="width: 10%" header="Nr.">
                        <template #body="slotProps">
                            {{ slotProps.data.id }}
                        </template>
                    </Column>
                    <Column field="date" header="Data" style="width: 25%">
                        <template #body="slotProps">
                            {{ formatDateTime(slotProps.data.saleDate) }}
                        </template>
                    </Column>
                    <Column field="importo" header="Importo" style="width: 15%">
                        <template #body="slotProps">
                            {{ formatCurrency(slotProps.data.totalPrice + slotProps.data.discount) }}
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
                    <Column field="totaleFinale" header="Totale" style="width: 15%">
                        <template #body="slotProps">
                            {{ formatCurrency(slotProps.data.totalPrice) }}
                        </template>
                    </Column>
                    <Column style="width: 5%" header="Pz.">
                        <template #body="slotProps">
                            {{ slotProps.data.totalProducts }}
                        </template>
                    </Column>
                </DataTable>
            </div>

        </div>
        <div class="col-span-12 xl:col-span-4">
            <div class="card flex justify-center items-center ring-green-500">
                <VCalendar is-dark="dark" color="green" :attributes="attrsCalendar" transparent borderless
                    :min-date="new Date()" />
            </div>
            <Button label="Banco" icon="pi pi-shopping-cart" class="w-full"
                :style="{ height: '90px', fontSize: '20px' }" @click="goToSalePage" />
        </div>
    </div>
</template>