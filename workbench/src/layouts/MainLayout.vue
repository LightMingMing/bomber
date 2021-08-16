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
      <workspace
        :name="name"
        :groups="groups"
        @openRequest="openRequest"
      ></workspace>
    </q-drawer>

    <q-page-container>
      <q-tabs
        switch-indicator
        inline-label
        mobile-arrows
        align="left"
        indicator-color="orange"
        v-model="activeRequest"
      >
        <q-tab
          no-caps
          :key="request.id"
          :label="request.name"
          :name="'req' + request.id"
          :requests="requests"
          v-for="request in requests"
          content-class="q-gutter-x-lg"
        >
          <q-btn
            flat
            icon="close"
            size="sm"
            dense
            @click.stop="closeRequest(request.id)"
          />
        </q-tab>
        <q-btn
          flat
          stretch
          style="padding-top: 12px; padding-bottom: 12px"
          icon="add"
        />
      </q-tabs>
      <q-separator />
      <q-tab-panels v-model="activeRequest">
        <q-tab-panel
          :key="request.id"
          :name="'req' + request.id"
          :requests="requests"
          v-for="request in requests"
        >
          <div class="q-gutter-x-sm row">
            <q-select
              dense
              outlined
              options-dense
              :options="methods"
              :model-value="request.method"
            />
            <q-input class="col-6" outlined dense :model-value="request.url" />
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
                  :rows="request.headers"
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
                          :model-value="props.row.key"
                        />
                      </q-td>
                      <q-td key="value" :props="props">
                        <q-input
                          dense
                          autofocus
                          outlined
                          :model-value="props.row.value"
                        />
                      </q-td>
                    </q-tr>
                  </template>
                </q-table>
              </q-tab-panel>
              <q-tab-panel name="body">
                <q-input outlined type="textarea" :model-value="request.body" />
              </q-tab-panel>
              <q-tab-panel name="assertions"> Assertions </q-tab-panel>
            </q-tab-panels>
          </div>
        </q-tab-panel>
      </q-tab-panels>
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent, ref } from "vue";
import { fabGithub } from "@quasar/extras/fontawesome-v5";
import Workspace from "src/components/Workspace.vue";

export default defineComponent({
  components: { Workspace },
  name: "MainLayout",

  setup() {
    const leftDrawerOpen = ref(false);
    const methods = ref(["GET", "POST", "PUT", "PATCH", "DELETE"]);
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
            url: "https://github.com",
            headers: [
              {
                key: "Content-Type",
                value: "application/json",
              },
            ],
            body: "",
            assertions: [],
          },
          {
            id: 1,
            name: "Post HttpRequest",
            method: "POST",
            url: "https://google.com",
            headers: [
              {
                key: "Content-Type",
                value: "application/json",
              },
            ],
            body: '{"id": 1}',
            assertions: [],
          },
          {
            id: 2,
            name: "Delete HttpRequest",
            method: "DELETE",
            url: "https://baidu.com",
            headers: [
              {
                key: "Content-Type",
                value: "application/json",
              },
            ],
            body: '{"id": 2}',
            assertions: [],
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
    const requests = ref([]);
    return {
      leftDrawerOpen,
      toggleLeftDrawer() {
        leftDrawerOpen.value = !leftDrawerOpen.value;
      },
      fabGithub,
      methods,
      title,
      subTab: ref("headers"),
      name: "MyWorkspace",
      groups,
      requests,
      activeRequest: ref(null),
    };
  },
  methods: {
    openRequest(id) {
      for (let request of this.requests) {
        if (request.id === id) {
          this.activeRequest = "req" + id;
          return;
        }
      }
      for (let group of this.groups) {
        for (let request of group.requests) {
          if (request.id === id) {
            this.requests.push(request);
            this.activeRequest = "req" + id;
            return;
          }
        }
      }
    },
    closeRequest(id) {
      for (let i = 0; i < this.requests.length; i++) {
        if (this.requests[i].id === id) {
          if ("req" + id === this.activeRequest) {
            if (this.requests.length === 1) {
              this.activeRequest = null;
            } else {
              this.activeRequest =
                "req" + this.requests[i === 0 ? 1 : i - 1].id;
            }
          }
          this.requests.splice(i, 1);
          return;
        }
      }
    },
  },
});
</script>
