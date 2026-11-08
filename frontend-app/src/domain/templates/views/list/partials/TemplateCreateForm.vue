<template>
  <VCardText>
    <form @submit.prevent="submit()">
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
  import {useFormDialog} from "@schwarzdavid/ui-kit";

  const model = defineModel<TemplateCreateDto>({required: true})

  const {defineField, errors, validate} = useForm({
    validationSchema: toTypedSchema(zTemplateCreateDto),
    initialValues: model.value,
  })
  const {onFinish, submit} = useFormDialog()

  const [name] = defineField('name')
  const [icon] = defineField('icon')

  onFinish(async () => {
    const result = await validate()
    console.log(result)
    if (!result.valid) {
      return false
    }
    model.value = result.values as TemplateCreateDto
  })
</script>
