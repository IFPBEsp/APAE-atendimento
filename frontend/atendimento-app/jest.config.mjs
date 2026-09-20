import nextJest from 'next/jest.js'

const createJestConfig = nextJest({
    dir: './',
})
/** @type {import('jest').Config} */
const config = {
    setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
    testEnvironment: 'jest-environment-jsdom',

    collectCoverageFrom: [
        'src/**/*.{ts,tsx}',
        '!src/**/*.d.ts',
        '!src/**/__tests__/**',
        '!src/**/*.{spec,test}.{ts,tsx}',
        '!src/**/index.{ts,tsx}',
        '!src/components/ui/**',
    ],

    coverageReporters: ['text-summary','lcov','text'],
    
}

export default createJestConfig(config)