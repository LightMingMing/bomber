<template>
  <q-layout view="hHh Lpr lFf">
    <q-header class="bg-grey-1 text-dark">
      <q-toolbar class="q-px-none">
        <q-btn
          flat
          round
          icon="menu"
          aria-label="Menu"
          color="primary"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title> Bomber </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered class="">
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent, ref } from "vue";

export default defineComponent({
  name: "MainLayout",

  setup() {
    const leftDrawerOpen = ref(false);

    return {
      leftDrawerOpen,
      toggleLeftDrawer() {
        leftDrawerOpen.value = !leftDrawerOpen.value;
      },
    };
  },
});
</script>
