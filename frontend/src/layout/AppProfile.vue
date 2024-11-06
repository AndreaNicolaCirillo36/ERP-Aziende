<script setup>
import { ref, onMounted } from 'vue';
import { logout, getUserRole } from '@/service/auth';
import router from '@/router';

const userRole = ref(null);

onMounted(() => {
    userRole.value = getUserRole();
});

const goToAddUser = () => {
    router.push('/addUser');
};


</script>

<template>
    <div :style="{ width: '10rem' }"
        class="config-panel hidden absolute top-[3.25rem] right-0 p-4 bg-surface-0 dark:bg-surface-900 border border-surface rounded-border origin-top shadow-[0px_3px_5px_rgba(0,0,0,0.02),0px_0px_2px_rgba(0,0,0,0.05),0px_1px_4px_rgba(0,0,0,0.08)]">
        <div class="flex flex-col gap-4">
            <div class="flex gap-4 flex-wrap justify-between">
                <Button v-if="userRole === 'ROLE_ADMIN'" label="Primary" @click="goToAddUser" outlined>
                    <i class="pi pi-plus"></i>Add User
                </Button>
                <Button label="Logout" @click="logout" severity="danger" outlined>
                    <i class="pi pi-sign-out"></i>Logout
                </Button>
            </div>
        </div>
    </div>
</template>