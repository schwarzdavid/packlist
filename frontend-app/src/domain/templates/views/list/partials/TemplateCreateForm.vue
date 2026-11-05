<template>
  <VCardText>
    <form @submit.prevent>
      <VTextField v-model="name" :error-messages="errors.name" label="Name"/>
      <StowIconSelect v-model="icon" label="Icon"/>
      {{ icon }}
    </form>
  </VCardText>
</template>

<script lang="ts" setup>
  import {type TemplateCreateDto, zTemplateCreateDto} from "@packlist/lists-api";
  import {useForm} from "vee-validate";
  import {toTypedSchema} from "@vee-validate/zod";
  import StowIconSelect from "@/components/StowIconSelect.vue";

  const model = defineModel<TemplateCreateDto>({required: true})

  const {defineField, errors} = useForm({
    validationSchema: toTypedSchema(zTemplateCreateDto)
  })

  const [name] = defineField('name')
  const [icon] = defineField('icon')
</script>
