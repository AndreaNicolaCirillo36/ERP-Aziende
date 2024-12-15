<script setup>
import { ref } from 'vue';

import AppMenuItem from './AppMenuItem.vue';

const model = ref([
    {
        label: 'Home',
        items: [
            { label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' },
            { label: 'Sales', icon: 'pi pi-fw pi-shopping-cart', to: '/sales' },
            { label: 'Archive', icon: 'pi pi-fw pi-folder-open', to: '/archive' },
            { label: 'Products', icon: 'pi pi-fw pi-database', to: '/products' },
            { label: 'Suppliers', icon: 'pi pi-fw pi-shopping-bag', to: '/supplier' }
        ]
    }
]);
</script>

<template>
    <ul class="layout-menu">
        <template v-for="(item, i) in model" :key="item">
            <app-menu-item v-if="!item.separator" :item="item" :index="i"></app-menu-item>
            <li v-if="item.separator" class="menu-separator"></li>
        </template>
    </ul>
</template>

<style lang="scss" scoped></style>
