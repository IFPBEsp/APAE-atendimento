import "@testing-library/jest-dom";

global.URL.createObjectURL = jest.fn(() => "blob:mock");
global.URL.revokeObjectURL = jest.fn();

afterEach(() => {
    global.URL.createObjectURL.mockClear();
    global.URL.revokeObjectURL.mockClear();
});