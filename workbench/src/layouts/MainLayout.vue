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
          icon="more_vert"
          header-class="q-py-sm"
          content-inset-level="0.3"
          v-for="group in groups"
          :key="group.name"
        >
          <template v-slot:header>
            <q-item-section>
              {{ group.name }}
            </q-item-section>
            <q-item-section side>
              <q-btn flat dense round icon="more_vert" @click.stop="" />
            </q-item-section>
          </template>
          <q-card v-for="request in group.requests" :key="request.name">
            <q-card-section class="no-padding">
              <q-item clickable dense class="q-py-sm">
                <q-item-section side style="width: 75px">
                  <q-badge
                    outline
                    :label="request.method"
                    :color="displayColor(request.method)"
                  />
                </q-item-section>
                <q-item-section>{{ request.name }}</q-item-section>
                <q-item-section side>
                  <q-btn flat dense round icon="more_vert" @click.stop="" />
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
          <q-btn flat icon="close" size="sm" dense @click.stop="" />
        </q-tab>
        <q-tab no-caps name="id1" label="Delete" content-class="q-gutter-x-lg">
          <q-btn flat icon="close" size="sm" dense @click.stop="" />
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
              <q-tab-panel name="headers">
                <q-table
                  flat
                  dense
                  bordered
                  hide-pagination
                  separator="none"
                  :rows="headers"
                  :columns="title"
                  row-key="key"
                >
                  <template v-slot:body="props">
                    <q-tr :props="props" no-hover>
                      <q-td key="key" :props="props">
                        <q-input
                          dense
                          autofocus
                          outlined
                          v-model="props.row.key"
                        />
                      </q-td>
                      <q-td key="value" :props="props">
                        <q-input
                          dense
                          autofocus
                          outlined
                          v-model="props.row.value"
                        />
                      </q-td>
                    </q-tr>
                  </template>
                </q-table>
              </q-tab-panel>
              <q-tab-panel name="body">
                <q-input outlined v-model="body" type="textarea" />
              </q-tab-panel>
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
    const headers = ref([
      {
        key: "Content-Type",
        value: "appliction/json",
      },
      {
        key: "Cookie",
        value: "uuid=12345678;jsession=zzzddlllll",
      },
    ]);
    const title = ref([
      {
        name: "key",
        align: "left",
        label: "key",
        field: (row) => row.key,
        sortable: false,
      },
      {
        name: "value",
        align: "left",
        label: "value",
        field: (row) => row.value,
        sortable: false,
      },
    ]);
    const groups = ref([
      {
        id: 0,
        name: "Restful API0",
        requests: [
          {
            id: 0,
            name: "Get HttpRequest",
            method: "GET",
          },
          {
            id: 1,
            name: "Post HttpRequest",
            method: "POST",
          },
          {
            id: 2,
            name: "Delete HttpRequest",
            method: "DELETE",
          },
        ],
      },
      {
        id: 1,
        name: "Restful API1",
        requests: [
          {
            id: 3,
            name: "Put HttpRequest",
            method: "PUT",
          },
          {
            id: 4,
            name: "Patch HttpRequest",
            method: "PATCH",
          },
        ],
      },
    ]);
    return {
      leftDrawerOpen,
      toggleLeftDrawer() {
        leftDrawerOpen.value = !leftDrawerOpen.value;
      },
      fabGithub,
      methods,
      title,
      reqTab: ref("id0"),
      subTab: ref("headers"),
      groups,
      method: ref("GET"),
      url: ref(null),
      headers,
      body: ref(null),
    };
  },
  methods: {
    displayColor(method) {
      if (method == "GET") {
        return "blue";
      } else if (method == "POST") {
        return "teal";
      } else if (method == "DELETE") {
        return "red";
      } else if (method == "PUT") {
        return "cyan";
      } else if (method == "PATCH") {
        return "orange";
      } else {
        return "primary";
      }
    },
  },
});
</script>
