<script setup>
import { ProductService } from "@/service/ProductService";
import { SupplierService } from "@/service/SupplierService";
import { FilterMatchMode } from "@primevue/core/api";
import { useToast } from "vue-toastification";
import { onMounted, ref } from "vue";

const products = ref([]);
const suppliers = ref([]);
const toast = useToast();
const dt = ref();
const productDialog = ref(false);
const deleteProductDialog = ref(false);
const deleteProductsDialog = ref(false);
const product = ref({});
const selectedProducts = ref();
const selectedSupplier = ref(null);
const submitted = ref(false);
const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
});

const getAllProducts = async () => {
  try {
    const response = await ProductService.getAllProducts();
    products.value = response.data;
  } catch (error) {
    console.error("Errore durante il recupero dei prodotti:", error);
  }
};

const getAllSuppliers = async () => {
  try {
    const response = await SupplierService.getAllSuppliers();
    suppliers.value = response.data;
  } catch (error) {
    console.error("Errore durante il recupero dei fornitori:", error);
  }
};

onMounted(() => {
  getAllProducts();
  getAllSuppliers();
});

function formatCurrency(value) {
    return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(value);
}


const calcolaRicarico = (prezzoAcquisto, prezzoVendita) => {
  if (!prezzoAcquisto || prezzoAcquisto === 0) {
    return "0%";
  }
  const ricarico = ((prezzoVendita - prezzoAcquisto) / prezzoAcquisto) * 100;
  return `${ricarico.toFixed(2)}%`;
};

function openNewEdit() {
  product.value = {};
  selectedSupplier.value = null;
  submitted.value = false;
  productDialog.value = true;
}

function hideDialogEdit() {
  productDialog.value = false;
  product.value = {};
  selectedSupplier.value = null;
  submitted.value = false;
}

const saveProduct = async () => {
  submitted.value = true;

  if (!selectedSupplier.value) {
    toast.error("Seleziona un fornitore.");
    return;
  }

  const productData = {
    barcode: product.value.barcode,
    name: product.value.name,
    supplier: {
      id: selectedSupplier.value.id,
    },
    quantity: product.value.quantity,
    purchasePrice: product.value.purchasePrice,
    sellingPrice: product.value.sellingPrice,
  };

  try {
    let response;
    if (product.value.id) {
      response = await ProductService.editProduct(
        product.value.id,
        productData
      );
      if (response.status === 200) {
        toast.success("Prodotto aggiornato con successo!");
      }
    } else {
      response = await ProductService.createProduct(productData);
      console.log("Risposta creazione prodotto:", response);
      if (response.status === 201) {
        toast.success("Prodotto creato con successo!");
        products.value.push(response.data);
      } else {
        toast.error("Errore durante la creazione del prodotto.");
      }
    }
    getAllProducts();
  } catch (error) {
    toast.error("Errore durante il salvataggio del prodotto.");
    console.error("Errore:", error);
  }

  productDialog.value = false;
};

function openEditProductDialog(prod) {
  product.value = { ...prod };
  selectedSupplier.value = suppliers.value.find(
    (supplier) => supplier.id === product.value.supplier.id
  );
  productDialog.value = true;
}

function confirmDeleteProduct(prod) {
  product.value = prod;
  deleteProductDialog.value = true;
}

const deleteProduct = async (id) => {
  try {
    const response = await ProductService.deleteProduct(id);
    if (response.status === 204) {
      toast.success("Prodotto eliminato con successo!");
      products.value = products.value.filter((product) => product.id !== id);
      product.value = null;
    } else {
      toast.error("Errore nell'eliminazione del prodotto.");
    }
  } catch (error) {
    console.error("Errore durante l'eliminazione del prodotto:", error);
    if (error.response && error.response.status === 500) {
      toast.error("Errore interno del server. Riprovare più tardi.");
    } else if (error.response) {
      toast.error(
        `Errore nell'eliminazione del prodotto: ${error.response.statusText}`
      );
    } else {
      toast.error("Errore sconosciuto nell'eliminazione del prodotto.");
    }
  } finally {
    deleteProductDialog.value = false;
  }
};

function exportCSV() {
  dt.value.exportCSV();
}

function confirmDeleteSelected() {
  deleteProductsDialog.value = true;
}

function deleteSelectedProducts() {
  if (!selectedProducts.value || selectedProducts.value.length === 0) {
    toast.error("Seleziona almeno un prodotto da eliminare.");
    return;
  }

  const deletePromises = selectedProducts.value.map((product) =>
  ProductService.deleteProduct(product.id)
);

Promise.allSettled(deletePromises)
  .then((results) => {
    results.forEach((result, index) => {
      if (result.status === 'fulfilled') {
        products.value = products.value.filter(
          (product) => product.id !== selectedProducts.value[index].id
        );
      } else {
        toast.error(`Errore durante l'eliminazione del prodotto ${selectedProducts.value[index].name}`);
      }
    });
    selectedProducts.value = null;
    deleteProductsDialog.value = false;
  })
  .catch((error) => {
    console.error("Errore durante l'eliminazione:", error);
    toast.error("Errore durante l'eliminazione dei prodotti.");
  });

}
</script>

<template>
  <div>
    <div class="card" :class="{ 'blur-background': productDialog || deleteProductDialog || deleteProductsDialog }">
      <Toolbar class="mb-6">
        <template #start>
          <Button label="Nuovo" icon="pi pi-plus" severity="secondary" class="mr-2" @click="openNewEdit" />
          <Button label="Elimina" icon="pi pi-trash" severity="secondary" @click="confirmDeleteSelected"
            :disabled="!selectedProducts || !selectedProducts.length" />
        </template>

        <template #end>
          <Button label="Export" icon="pi pi-upload" severity="secondary" @click="exportCSV($event)" />
        </template>
      </Toolbar>

      <DataTable ref="dt" v-model:selection="selectedProducts" :value="products" dataKey="id" :paginator="true"
        :rows="10" :filters="filters"
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
        :rowsPerPageOptions="[5, 10, 25]"
        currentPageReportTemplate="Mostra da {first} a {last} di {totalRecords} prodotti">
        <template #header>
          <div class="flex flex-wrap gap-2 items-center justify-between">
            <h4 class="m-0">Prodotti</h4>
            <IconField>
              <InputIcon>
                <i class="pi pi-search" />
              </InputIcon>
              <InputText v-model="filters['global'].value" placeholder="Search..." />
            </IconField>
          </div>
        </template>

        <Column selectionMode="multiple" style="width: 3rem" :exportable="false"></Column>
        <Column field="barcode" header="Barcode" sortable style="min-width: 12rem">
          <template #body="slotProps">
            {{ slotProps.data.barcode }}
          </template>
        </Column>
        <Column field="descrizione" header="Descrizione" sortable style="min-width: 16rem">
          <template #body="slotProps">
            {{ slotProps.data.name }}
          </template>
        </Column>
        <Column field="prezzo" header="Prezzo" sortable style="min-width: 8rem">
          <template #body="slotProps">
            {{ formatCurrency(slotProps.data.sellingPrice) }}
          </template>
        </Column>
        <Column field="pezzi" header="Pz" sortable style="min-width: 12rem">
          <template #body="slotProps">
            {{ slotProps.data.quantity }}
          </template>
        </Column>
        <Column :exportable="false" header="Azioni" style="min-width: 12rem">
          <template #body="slotProps">
            <Button icon="pi pi-pencil" outlined rounded class="mr-2" @click="openEditProductDialog(slotProps.data)" />
            <Button icon="pi pi-trash" outlined rounded severity="danger"
              @click="confirmDeleteProduct(slotProps.data)" />
          </template>
        </Column>
      </DataTable>
    </div>

    <Dialog v-model:visible="productDialog" :style="{ width: '600px' }" header="Dettagli Prodotto" :modal="true">
      <div class="flex flex-col gap-6">
        <div>
          <label for="barcode" class="block font-bold mb-3">Barcode</label>
          <Textarea id="barcode" v-model="product.barcode" required="true" rows="1" cols="5" fluid />
        </div>
        <div>
          <label for="description" class="block font-bold mb-3">Descrizione</label>
          <Textarea id="description" v-model="product.name" required="true" rows="3" cols="20" fluid />
        </div>
        <div>
          <label for="quantità" class="block font-bold mb-3">Quantità</label>
          <InputNumber id="quantità" v-model="product.quantity" required="true" rows="1" cols="5" fluid />
        </div>
        <div>
          <label for="fornitore" class="block font-bold mb-3">Fornitore</label>
          <Select id="fornitore" v-model="selectedSupplier" :options="suppliers" optionLabel="name"
            placeholder="Seleziona un fornitore" fluid></Select>
        </div>
        <div class="grid grid-cols-12 gap-4">
          <div class="col-span-6">
            <label for="prezzoAcquisto" class="block font-bold mb-3">Prezzo acquisto</label>
            <InputNumber id="prezzoAcquisto" v-model="product.purchasePrice" mode="currency" currency="EUR"
              locale="it-IT" fluid />
          </div>
          <div class="col-span-6">
            <label for="prezzoVendita" class="block font-bold mb-3">Prezzo vendita</label>
            <InputNumber id="prezzoVendita" v-model="product.sellingPrice" mode="currency" currency="EUR" locale="it-IT"
              fluid />
          </div>
        </div>
        <div>
          <label for="ricarico" class="block font-bold mb-3">Percentuale di ricarico</label>
          <InputText id="ricarico" :value="calcolaRicarico(product.purchasePrice, product.sellingPrice)
            " disabled />
        </div>
      </div>

      <template #footer>
        <Button label="Annulla" icon="pi pi-times" text @click="hideDialogEdit" />
        <Button label="Salva" icon="pi pi-check" @click="saveProduct" />
      </template>
    </Dialog>

    <Dialog v-model:visible="deleteProductDialog" :modal="true" :style="{ width: '450px' }" header="Conferma"
      :closable="false">
      <div class="flex items-center gap-4">
        <i class="pi pi-exclamation-triangle !text-3xl" />
        <span v-if="product">Eliminare <b class="text-primary">{{ product.name }}</b> ?</span>
      </div>
      <template #footer>
        <Button label="No" icon="pi pi-times" text @click="deleteProductDialog = false" />
        <Button label="Si" icon="pi pi-check" @click="deleteProduct(product.id)" />
      </template>
    </Dialog>

    <Dialog v-model:visible="deleteProductsDialog" :modal="true" :style="{ width: '450px' }" header="Conferma"
      :closable="false">
      <div class="flex items-center gap-4">
        <i class="pi pi-exclamation-triangle !text-3xl" />
        <span v-if="selectedProducts && selectedProducts.length">
          Eliminare
          <b class="text-primary">{{ selectedProducts.length }}</b> prodotti
          selezionati?
        </span>
      </div>
      <template #footer>
        <Button label="No" icon="pi pi-times" text @click="deleteProductsDialog = false" />
        <Button label="Sì" icon="pi pi-check" @click="deleteSelectedProducts" />
      </template>
    </Dialog>
  </div>
</template>

<style scoped>
.blur-background {
  filter: blur(5px);
  pointer-events: none;
  opacity: 0.8;
}
</style>
