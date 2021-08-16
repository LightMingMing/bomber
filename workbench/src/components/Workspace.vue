<template>
  <q-card flat>
    <q-card-section class="no-padding">
      <q-item>
        <q-item-section> {{ name }}</q-item-section>
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
          <q-item
            clickable
            dense
            class="q-py-sm"
            @click="selectRequest(request.id)"
          >
            <q-item-section side style="width: 75px">
              <q-badge
                outline
                :label="request.method"
                :color="badgeColor(request.method)"
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
</template>
<script>
import { defineComponent } from "vue";

export default defineComponent({
  name: "Workspace",
  props: {
    name: {
      type: String,
      required: true,
    },
    groups: {
      type: Object,
      required: true,
    },
  },
  methods: {
    selectRequest(id) {
      this.$emit("openRequest", id);
    },
    badgeColor(method) {
      if (method === "GET") return "blue";
      else if (method === "POST") return "teal";
      else if (method === "DELETE") return "red";
      else if (method === "PUT") return "cyan";
      else if (method === "PATCH") return "orange";
      else return "primary";
    },
  },
});
</script>
