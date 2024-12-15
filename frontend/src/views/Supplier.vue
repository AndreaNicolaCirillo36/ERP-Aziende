<script setup>
import { SupplierService } from "@/service/SupplierService";
import { FilterMatchMode } from "@primevue/core/api";
import { useToast } from "vue-toastification";
import { onMounted, ref } from "vue";

const suppliers = ref([]);
const toast = useToast();
const dt = ref();
const supplierDialog = ref(false);
const deleteSupplierDialog = ref(false);
const deleteSuppliersDialog = ref(false);
const supplier = ref({});
const selectedSuppliers = ref();
const submitted = ref(false);
const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
});

const getAllSuppliers = async () => {
  try {
    const response = await SupplierService.getAllSuppliers();
    suppliers.value = response.data;
  } catch (error) {
    console.error("Errore durante il recupero dei fornitori:", error);
  }
};

onMounted(() => {
  getAllSuppliers();
});

function openNewEdit() {
    supplier.value = {};
  submitted.value = false;
  supplierDialog.value = true;
}

function hideDialogEdit() {
  supplierDialog.value = false;
  supplier.value = {};
  submitted.value = false;
}

const saveSupplier = async () => {
  submitted.value = true;

  const supplierData = {
    name: supplier.value.name,
    address: supplier.value.address,
    phoneNumber: supplier.value.phoneNumber
  };

  try {
    let response;
    if (supplier.value.id) {
      response = await SupplierService.editSupplier(
        supplier.value.id,
        supplierData
      );
      if (response.status === 200) {
        toast.success("Fornitore aggiornato con successo!");
      }
    } else {
      response = await SupplierService.createSupplier(supplierData);
      console.log("Risposta creazione fornitore:", response);
      if (response.status === 201) {
        toast.success("Fornitore creato con successo!");
        suppliers.value.push(response.data);
      } else {
        toast.error("Errore durante la creazione del fornitore.");
      }
    }
    getAllSuppliers();
  } catch (error) {
    toast.error("Errore durante il salvataggio del fornitore.");
    console.error("Errore:", error);
  }

  supplierDialog.value = false;
};

function openEditSupplierDialog(prod) {
  supplier.value = { ...prod };
  supplierDialog.value = true;
}

function confirmDeleteSupplier(prod) {
  supplier.value = prod;
  deleteSupplierDialog.value = true;
}

const deleteSupplier = async (id) => {
  try {
    const response = await SupplierService.deleteSupplier(id);
    if (response.status === 204) {
      toast.success("Fornitore eliminato con successo!");
      suppliers.value = suppliers.value.filter((supplier) => supplier.id !== id);
      supplier.value = null;
    } else {
      toast.error("Errore nell'eliminazione del fornitore.");
    }
  } catch (error) {
    console.error("Errore durante l'eliminazione del fornitore:", error);
    if (error.response && error.response.status === 500) {
      toast.error("Errore interno del server. Riprovare più tardi.");
    } else if (error.response) {
      toast.error(
        `Errore nell'eliminazione del fornitore: ${error.response.statusText}`
      );
    } else {
      toast.error("Errore sconosciuto nell'eliminazione del fornitore.");
    }
  } finally {
    deleteSupplierDialog.value = false;
  }
};

function exportCSV() {
  dt.value.exportCSV();
}

function confirmDeleteSelected() {
  deleteSuppliersDialog.value = true;
}

function deleteSelectedSuppliers() {
  if (!selectedSuppliers.value || selectedSuppliers.value.length === 0) {
    toast.error("Seleziona almeno un fornitore da eliminare.");
    return;
  }

  const deletePromises = selectedSuppliers.value.map((supplier) =>
  SupplierService.deleteSupplier(supplier.id)
);

Promise.allSettled(deletePromises)
  .then((results) => {
    results.forEach((result, index) => {
      if (result.status === 'fulfilled') {
        suppliers.value = suppliers.value.filter(
          (supplier) => supplier.id !== selectedSuppliers.value[index].id
        );
      } else {
        toast.error(`Errore durante l'eliminazione del fornitore ${selectedSuppliers.value[index].name}`);
      }
    });
    toast.success("Fornitori eliminati con successo!");
    selectedSuppliers.value = null;
    deleteSuppliersDialog.value = false;
  })
  .catch((error) => {
    console.error("Errore durante l'eliminazione:", error);
    toast.error("Errore durante l'eliminazione dei fornitore.");
  });

}
</script>

<template>
  <div>
    <div class="card" :class="{ 'blur-background': supplierDialog || deleteSupplierDialog || deleteSuppliersDialog }">
      <Toolbar class="mb-6">
        <template #start>
          <Button label="Nuovo" icon="pi pi-plus" severity="secondary" class="mr-2" @click="openNewEdit" />
          <Button label="Elimina" icon="pi pi-trash" severity="secondary" @click="confirmDeleteSelected"
            :disabled="!selectedSuppliers || !selectedSuppliers.length" />
        </template>

        <template #end>
          <Button label="Export" icon="pi pi-upload" severity="secondary" @click="exportCSV($event)" />
        </template>
      </Toolbar>

      <DataTable ref="dt" v-model:selection="selectedSuppliers" :value="suppliers" dataKey="id" :paginator="true"
        :rows="10" :filters="filters"
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
        :rowsPerPageOptions="[5, 10, 25]"
        currentPageReportTemplate="Mostra da {first} a {last} di {totalRecords} fornitori">
        <template #header>
          <div class="flex flex-wrap gap-2 items-center justify-between">
            <h4 class="m-0">Fornitori</h4>
            <IconField>
              <InputIcon>
                <i class="pi pi-search" />
              </InputIcon>
              <InputText v-model="filters['global'].value" placeholder="Search..." />
            </IconField>
          </div>
        </template>

        <Column selectionMode="multiple" style="width: 3rem" :exportable="false"></Column>
        <Column field="id" header="ID" sortable style="min-width: 5rem">
          <template #body="slotProps">
            {{ slotProps.data.id }}
          </template>
        </Column>
        <Column field="ragioneSociale" header="Rag. Soc." sortable style="min-width: 18rem">
          <template #body="slotProps">
            {{ slotProps.data.name }}
          </template>
        </Column>
        <Column field="indirizzo" header="Indirizzo" sortable style="min-width: 18rem">
          <template #body="slotProps">
            {{ slotProps.data.address }}
          </template>
        </Column>
        <Column field="telefono" header="Num. Telefono" sortable style="min-width: 12rem">
          <template #body="slotProps">
            {{ slotProps.data.phoneNumber }}
          </template>
        </Column>
        <Column :exportable="false" header="Azioni" style="min-width: 12rem">
          <template #body="slotProps">
            <Button icon="pi pi-pencil" outlined rounded class="mr-2" @click="openEditSupplierDialog(slotProps.data)" />
            <Button icon="pi pi-trash" outlined rounded severity="danger"
              @click="confirmDeleteSupplier(slotProps.data)" />
          </template>
        </Column>
      </DataTable>
    </div>

    <Dialog v-model:visible="supplierDialog" :style="{ width: '600px' }" header="Dettagli Fornitore" :modal="true">
      <div class="flex flex-col gap-6">
        <div>
          <label for="ragioneSociale" class="block font-bold mb-3">Rag. Sociale</label>
          <Textarea id="ragioneSociale" v-model="supplier.name" required="true" rows="1" cols="5" fluid />
        </div>
        <div>
          <label for="indirizzo" class="block font-bold mb-3">Indirizzo</label>
          <Textarea id="indirizzo" v-model="supplier.address" required="true" rows="3" cols="20" fluid />
        </div>
        <div>
          <label for="telefono" class="block font-bold mb-3">Num. Telefono</label>
          <Textarea id="telefono" v-model="supplier.phoneNumber" required="true" rows="1" cols="5" fluid />
        </div>
      </div>

      <template #footer>
        <Button label="Annulla" icon="pi pi-times" text @click="hideDialogEdit" />
        <Button label="Salva" icon="pi pi-check" @click="saveSupplier" />
      </template>
    </Dialog>

    <Dialog v-model:visible="deleteSupplierDialog" :modal="true" :style="{ width: '450px' }" header="Conferma"
      :closable="false">
      <div class="flex items-center gap-4">
        <i class="pi pi-exclamation-triangle !text-3xl" />
        <span v-if="supplier">Eliminare <b class="text-primary">{{ supplier.name }}</b> ?</span>
      </div>
      <template #footer>
        <Button label="No" icon="pi pi-times" text @click="deleteSupplierDialog = false" />
        <Button label="Si" icon="pi pi-check" @click="deleteSupplier(supplier.id)" />
      </template>
    </Dialog>

    <Dialog v-model:visible="deleteSuppliersDialog" :modal="true" :style="{ width: '450px' }" header="Conferma"
      :closable="false">
      <div class="flex items-center gap-4">
        <i class="pi pi-exclamation-triangle !text-3xl" />
        <span v-if="selectedSuppliers && selectedSuppliers.length">
          Eliminare
          <b class="text-primary">{{ selectedSuppliers.length }}</b> fornitori
          selezionati?
        </span>
      </div>
      <template #footer>
        <Button label="No" icon="pi pi-times" text @click="deleteSuppliersDialog = false" />
        <Button label="Sì" icon="pi pi-check" @click="deleteSelectedSuppliers" />
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

