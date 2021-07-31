<template>
  <q-layout view="hHh Lpr lFf">
    <q-header class="bg-grey-1 text-dark" bordered>
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
      <q-tabs
        switch-indicator
        inline-label
        mobile-arrows
        v-model="reqTab"
        align="left"
        indicator-color="orange"
      >
        <q-tab no-caps name="id0" label="Post" content-class="q-gutter-x-lg">
          <q-btn flat icon="close" size="sm" dense />
        </q-tab>
        <q-tab no-caps name="id1" label="Delete" content-class="q-gutter-x-lg">
          <q-btn flat icon="close" size="sm" dense />
        </q-tab>
        <q-btn flat unelevated stretch icon="add" />
      </q-tabs>
      <q-separator />
      <q-tab-panels v-model="reqTab" animated>
        <q-tab-panel name="id0">
          <div class="q-gutter-x-sm row">
            <q-select
              dense
              outlined
              options-dense
              v-model="method"
              :options="methods"
            />
            <q-input class="col-6" outlined dense v-model="url" />
            <q-btn
              no-caps
              unelevated
              color="blue"
              text-color="white"
              label="Submit"
              size="sm"
            />
          </div>
          <div class="q-pt-md">
            <q-tabs
              dense
              v-model="subTab"
              indicator-color="orange"
              align="left"
            >
              <q-tab no-caps name="headers" label="Headers" class="q-px-lg" />
              <q-tab no-caps name="body" label="Body" class="q-px-lg" />
              <q-tab
                no-caps
                name="assertions"
                label="Assertions"
                class="q-px-lg"
              />
            </q-tabs>
            <q-tab-panels v-model="subTab">
              <q-tab-panel name="headers"> Headers </q-tab-panel>
              <q-tab-panel name="body"> Body </q-tab-panel>
              <q-tab-panel name="assertions"> Assertions </q-tab-panel>
            </q-tab-panels>
          </div>
        </q-tab-panel>

        <q-tab-panel name="id1">
          <div class="text-h6">Request</div>
        </q-tab-panel>
      </q-tab-panels>
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
    const methods = ref(["GET", "POST", "PUT", "PATCH", "DELETE"]);

    return {
      leftDrawerOpen,
      toggleLeftDrawer() {
        leftDrawerOpen.value = !leftDrawerOpen.value;
      },
      fabGithub,
      reqTab: ref("id0"),
      subTab: ref("headers"),
      methods,
      method: ref("GET"),
      url: ref(null),
    };
  },
});
</script>
