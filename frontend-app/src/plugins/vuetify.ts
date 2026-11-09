import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import {createVuetify} from "vuetify/framework";
import {VChip} from "vuetify/components";

export const vuetify = createVuetify({
  theme: {
    themes: {
      light: {
        dark: false,
        colors: {
          background: '#f0f3f1',
          'on-background': '#18211e',
          surface: '#ffffff',
          'surface-bright': '#ffffff',
          'surface-light': '#e3e9e6',
          'on-surface': '#18211e',
          'surface-variant': '#25302c',
          'on-surface-variant': '#eef3f0',
          primary: '#1d5c51',
          'primary-darken-1': '#164a41',
          'on-primary': '#ffffff',
          secondary: '#8a5a0f',
          'secondary-darken-1': '#6e4709',
          'on-secondary': '#ffffff',
          tag: '#f3d98c',
          'on-tag': '#3f2c05',
          error: '#b3261e',
          'on-error': '#ffffff',
          success: '#2d6a3e',
          'on-success': '#ffffff',
          warning: '#8a5300',
          'on-warning': '#ffffff',
          info: '#2a5d8f',
          'on-info': '#ffffff',
        },
        variables: {
          'font-body': 'Figtree, system-ui, -apple-system, "Segoe UI", sans-serif',
          'font-heading': 'Archivo, system-ui, sans-serif',
          'border-color': '#18211e',
          'border-opacity': 0.14,
          'high-emphasis-opacity': 1,
          'medium-emphasis-opacity': 0.72,
          'disabled-opacity': 0.4,
          'hover-opacity': 0.05,
          'focus-opacity': 0.12,
          'pressed-opacity': 0.14,
        },
      },
      dark: {
        dark: true,
        colors: {
          background: '#111715',
          'on-background': '#e3ebe7',
          surface: '#19211e',
          'surface-bright': '#222b28',
          'surface-light': '#26302c',
          'on-surface': '#e3ebe7',
          'surface-variant': '#dde6e1',
          'on-surface-variant': '#18211e',
          primary: '#8ed0c0',
          'primary-darken-1': '#6fb9a8',
          'on-primary': '#07322b',
          secondary: '#e8c06a',
          'secondary-darken-1': '#d4a94e',
          'on-secondary': '#2c1e02',
          tag: '#4a3d17',
          'on-tag': '#f3d98c',
          error: '#f2a097',
          'on-error': '#4a0d08',
          success: '#94d4a4',
          'on-success': '#0b2e16',
          warning: '#f0b766',
          'on-warning': '#3a2400',
          info: '#a3c8ec',
          'on-info': '#0b2740',
        },
        variables: {
          'font-body': 'Figtree, system-ui, -apple-system, "Segoe UI", sans-serif',
          'font-heading': 'Archivo, system-ui, sans-serif',
          'border-color': '#e3ebe7',
          'border-opacity': 0.16,
          'high-emphasis-opacity': 1,
          'medium-emphasis-opacity': 0.75,
          'disabled-opacity': 0.45,
          'hover-opacity': 0.06,
          'focus-opacity': 0.14,
          'pressed-opacity': 0.16,
        },
      },
    }
  },
  aliases: {
    StowTag: VChip
  },
  defaults: {
    VAppBar: {flat: true, color: 'background', density: 'comfortable'},
    VBottomNavigation: {
      grow: true,
      color: 'primary',
      bgColor: 'surface',
      elevation: 0,
      border: true
    },
    VBtn: {rounded: 'lg', variant: 'flat'},
    VFab: {color: 'primary', rounded: 'xl', size: 'large'},
    VCard: {rounded: 'lg', elevation: 0, border: true},
    VTextField: {variant: 'outlined', rounded: 'lg', color: 'primary', density: 'comfortable'},
    VCheckbox: {color: 'primary', density: 'comfortable', hideDetails: true},
    VCheckboxBtn: {color: 'primary', density: 'comfortable'},
    VChip: {rounded: 'pill', size: 'small', variant: 'tonal'},
    StowTag: {
      color: 'tag',
      variant: 'flat',
      label: true,
      size: 'small',
      prependIcon: 'mdi-tag-outline'
    },
    VList: {bgColor: 'surface'},
    VProgressLinear: {
      color: 'primary',
      bgColor: 'surface-light',
      bgOpacity: 1,
      height: 8,
      rounded: true
    },
    VProgressCircular: {color: 'primary', bgColor: 'surface-light', size: 40, width: 4},
    VSnackbar: {color: 'surface-variant', rounded: 'lg', timeout: 5000},
    VDialog: {maxWidth: 440},
    VBottomSheet: {inset: true},
    VToolbar: {
      VBtn: {
        rounded: 'pill'
      }
    }
  }
})
