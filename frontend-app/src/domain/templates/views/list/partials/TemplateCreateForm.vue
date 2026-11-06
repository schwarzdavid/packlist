<template>
  <VCardText>
    <form @submit.prevent="onSubmit">
      <VTextField v-model="name" :error-messages="errors.name" label="Name"/>
      <StowIconSelect v-model="icon" label="Icon"/>
    </form>
  </VCardText>
</template>

<script lang="ts" setup>
  import {type TemplateCreateDto, zTemplateCreateDto} from "@packlist/lists-api";
  import {useForm} from "vee-validate";
  import {toTypedSchema} from "@vee-validate/zod";
  import StowIconSelect from "@/components/StowIconSelect.vue";

  const model = defineModel<TemplateCreateDto>({required: true})

  const {defineField, errors, handleSubmit} = useForm({
    validationSchema: toTypedSchema(zTemplateCreateDto),
    initialValues: model.value,
  })

  const [name] = defineField('name')
  const [icon] = defineField('icon')

  const onSubmit = handleSubmit(async (values) => {
    model.value = values as TemplateCreateDto
  })
</script>
