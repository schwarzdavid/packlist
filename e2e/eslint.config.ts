import {defineConfig} from 'eslint/config'
import tseslint from 'typescript-eslint'
import jseslint from '@eslint/js'
import pluginPlaywright from 'eslint-plugin-playwright'

export default defineConfig(
    {
        files: ['***/*.{js,ts}'],
        extends: [
            jseslint.configs.recommended,
            tseslint.configs.recommendedTypeChecked,
            tseslint.configs.stylisticTypeChecked,
        ],
        languageOptions: {
            parserOptions: {
                projectService: true
            }
        }
    },
    {
        ...pluginPlaywright.configs["flat/recommended"],
        files: ['tests/**/*.{js,ts}']
    }
)