import AppLayout from '@/layout/AppLayout.vue';
import { getUserRole, isTokenExpired, logout, getUsername } from '@/service/auth';
import AuthService from '@/service/authService';
import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            component: AppLayout,
            children: [
                {
                    path: '/',
                    name: 'dashboard',
                    component: () => import('@/views/Dashboard.vue'),
                    meta: { requiresAuth: true}
                },
                {
                    path: '/sales',
                    name: 'sales',
                    component: () => import('@/views/Sales.vue'),
                    meta: { requiresAuth: true}
                },
                {
                    path: '/addUser',
                    name: 'addUser',
                    component: () => import('@/views/AddUser.vue'),
                    meta: {requiresAuth: true, requiresAdmin: true }
                },
                {
                    path: '/archive',
                    name: 'archive',
                    component: () => import('@/views/Archive.vue'),
                    meta: {requiresAuth: true, requiresAdmin: true }
                },
                {
                    path: '/products',
                    name: 'products',
                    component: () => import('@/views/Products.vue'),
                    meta: {requiresAuth: true, requiresAdmin: true }
                }
            ]
        },
        {
            path: '/auth/login',
            name: 'login',
            component: () => import('@/views/pages/auth/Login.vue')
        },
        {
            path: '/auth/access',
            name: 'accessDenied',
            component: () => import('@/views/pages/auth/Access.vue')
        },
        {
            path: '/auth/error',
            name: 'error',
            component: () => import('@/views/pages/auth/Error.vue')
        }
        
    ]
});

router.beforeEach(async (to, from) => {
    const accessToken = sessionStorage.getItem('accessToken');

    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!accessToken || isTokenExpired()) {
            try {
                const newAccessToken = await AuthService.refreshAccessToken();
                if (!newAccessToken) {
                    logout();
                    return false;
                }
            } catch (error) {
                logout();
                return false;
            }
        } else {
            const userRole = getUserRole();
            const username = getUsername();
            if (username === 'admin' && to.path !== '/addUser') {
                return '/addUser';
            }
            if (to.matched.some(record => record.meta.requiresAdmin) && userRole !== 'ROLE_ADMIN') {
                return '/auth/access';
            }
        }
    }

});

export default router;
