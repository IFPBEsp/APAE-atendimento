import "@testing-library/jest-dom";

global.URL.createObjectURL = jest.fn(() => "blob:mock");
