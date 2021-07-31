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

        <q-btn-dropdown
          stretch
          no-caps
          flat
          icon="dashboard"
          color="black"
          label="Workspaces"
          padding="xs"
        >
        </q-btn-dropdown>

        <q-space />
        <q-space />
        <q-space />

        <div class="q-gutter-sm q-mr-sm row items-center no-wrap">
          <q-btn
            dense
            flat
            round
            :icon="$q.fullscreen.isActive ? 'fullscreen_exit' : 'fullscreen'"
            @click="$q.fullscreen.toggle()"
          />

          <q-btn
            dense
            flat
            round
            type="a"
            href="https://github.com/LightMingMing/bomber"
            target="_blank"
            :icon="fabGithub"
          />
        </div>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered class="">
      <q-separator />
      <q-card flat>
        <q-card-section class="no-padding">
          <q-item>
            <q-item-section>My Workspace</q-item-section>
            <q-item-section side class="q-pl-xs">
              <q-btn
                no-caps
                unelevated
                color="grey-3"
                text-color="black"
                label="New"
                size="sm"
              />
            </q-item-section>
            <q-item-section side class="q-pl-xs">
              <q-btn
                no-caps
                unelevated
                color="grey-3"
                text-color="black"
                label="Import"
                size="sm"
              />
            </q-item-section>
          </q-item>
        </q-card-section>
      </q-card>
      <q-separator />
      <q-list>
        <q-expansion-item
          dense
          default-opened
          switch-toggle-side
          label="Restful API"
          icon="more_vert"
          header-class="q-py-sm"
          content-inset-level="0.3"
        >
          <q-card>
            <q-card-section class="no-padding">
              <q-item clickable dense class="q-py-sm">
                <q-item-section side style="width: 75px">
                  <q-badge outline color="blue" label="GET" />
                </q-item-section>
                <q-item-section>Query</q-item-section>
                <q-item-section side>
                  <q-icon name="more_vert" />
                </q-item-section>
              </q-item>
            </q-card-section>
          </q-card>
          <q-card>
            <q-card-section class="no-padding">
              <q-item clickable dense class="q-py-sm">
                <q-item-section side style="width: 75px">
                  <q-badge outline color="teal" label="POST" />
                </q-item-section>
                <q-item-section>Update</q-item-section>
                <q-item-section side>
                  <q-icon name="more_vert" />
                </q-item-section>
              </q-item>
            </q-card-section>
          </q-card>

          <q-card>
            <q-card-section class="no-padding">
              <q-item clickable dense class="q-py-sm">
                <q-item-section side style="width: 75px">
                  <q-badge outline color="red" label="DELETE" />
                </q-item-section>
                <q-item-section>Delete</q-item-section>
                <q-item-section side>
                  <q-icon name="more_vert" />
                </q-item-section>
              </q-item>
            </q-card-section>
          </q-card>
          <q-card>
            <q-card-section class="no-padding">
              <q-item clickable dense class="q-py-sm">
                <q-item-section side style="width: 75px">
                  <q-badge outline color="cyan" label="PUT" />
                </q-item-section>
                <q-item-section>Put</q-item-section>
                <q-item-section side>
                  <q-icon name="more_vert" />
                </q-item-section>
              </q-item>
            </q-card-section>
          </q-card>
          <q-card>
            <q-card-section class="no-padding">
              <q-item clickable dense class="q-py-sm">
                <q-item-section side style="width: 75px">
                  <q-badge outline color="orange" label="PATCH" />
                </q-item-section>
                <q-item-section>Patch</q-item-section>
                <q-item-section side>
                  <q-icon name="more_vert" />
                </q-item-section>
              </q-item>
            </q-card-section>
          </q-card>
        </q-expansion-item>
        <q-separator />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent, ref } from "vue";
import { fabGithub } from "@quasar/extras/fontawesome-v5";

export default defineComponent({
  name: "MainLayout",

  setup() {
    const leftDrawerOpen = ref(false);

    return {
      leftDrawerOpen,
      toggleLeftDrawer() {
        leftDrawerOpen.value = !leftDrawerOpen.value;
      },
      fabGithub,
    };
  },
});
</script>
