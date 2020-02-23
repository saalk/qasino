<template>
  <div>
    <div class="layout-padding">
      <div class="flex wrap gutter">
        <div class="gt-md-width-1of3 width-1of1">
          <div class="card">
            <div class="card-title bg-teal text-white">
              Input
            </div>
            <div class="card-content bg-white">
              <e-input
                v-model="name"
                label="Name"
                :validation="$v.name"
                :validation-messages="validationMessages.name"
                :value="name"
              />
            </div>
          </div>
        </div>
        <div class="gt-md-width-1of3 width-1of1">
          <div class="card">
            <div class="card-title bg-teal text-white">
              Select
            </div>
            <div class="card-content bg-white">
              <e-select
                v-model="authentication"
                type="list"
                label="Authentication"
                :validation="$v.authentication"
                :validation-messages="validationMessages.authentication"
                :value="authentication"
                :options="options"
              />
            </div>
          </div>
        </div>
        <div class="gt-md-width-1of3 width-1of1">
          <div class="card">
            <div class="card-title bg-teal text-white">
              DateTime
            </div>
            <div class="card-content bg-white">
              <e-date-time
                v-model="dateOfBirth"
                format="YYYY/MM/DD"
                label="Date of birth"
                :validation="$v.dateOfBirth"
                :validation-messages="validationMessages.dateOfBirth"
                :value="dateOfBirth"
              />
            </div>
          </div>
        </div>
      </div>
      <div class="flex wrap justify-center">
        <div class="width-1of3">
          <button class="teal raised outline fit" @click="$v.$touch()">
            Try Submit
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { required, minLength, alpha } from 'vuelidate/lib/validators';
import eInput from './eInput.vue';
import eSelect from './eSelect.vue';
import eDateTime from './eDateTime.vue';

export default {
  name: 'EmbeededValidationsElements',
  components: {
    eInput,
    eSelect,
    eDateTime,
  },
  data() {
    return {
      name: '',
      authentication: '',
      dateOfBirth: '',
      validationMessages: {
        name: {
          required: 'Name is required.',
          minLength: 'Minimum 5 characters.',
          alpha: 'Only letters.',
        },
        authentication: {
          required: 'Authentication is required.',
        },
        dateOfBirth: {
          required: 'Date of birth is required.',
        },
      },
      options: [
        { label: 'Google', value: 'google' },
        { label: 'Facebook', value: 'facebook' },
        { label: '', value: '' },
      ],
    };
  },
  mounted() {},
  validations: {
    name: {
      required,
      minLength: minLength(5),
      alpha,
    },
    authentication: {
      required,
    },
    dateOfBirth: {
      required,
    },
  },
};
</script>
<style scoped></style>
