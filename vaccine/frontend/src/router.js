
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import ReservationManager from "./components/ReservationManager"

import VaccineMgmtManager from "./components/VaccineMgmtManager"


import MyPage from "./components/MyPage"
import NotificationManager from "./components/NotificationManager"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/reservations',
                name: 'ReservationManager',
                component: ReservationManager
            },

            {
                path: '/vaccineMgmts',
                name: 'VaccineMgmtManager',
                component: VaccineMgmtManager
            },


            {
                path: '/myPages',
                name: 'MyPage',
                component: MyPage
            },
            {
                path: '/notifications',
                name: 'NotificationManager',
                component: NotificationManager
            },



    ]
})
