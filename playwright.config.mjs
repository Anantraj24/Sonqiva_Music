import { defineConfig, devices } from "@playwright/test";

const PORT = 3210;
const BASE = `http://127.0.0.1:${PORT}`;

export default defineConfig({
  testDir: "./tests-web",
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  reporter: [
    ["list"],
    ["html", { outputFolder: "web-preview/.playwright-report", open: "never" }],
    ["junit", { outputFile: "web-preview/.playwright-results/junit.xml" }],
  ],
  use: {
    baseURL: BASE,
    trace: "on-first-retry",
    screenshot: "only-on-failure",
    video: "retain-on-failure",
  },
  projects: [
    { name: "desktop", use: { ...devices["Desktop Chrome"] } },
    { name: "mobile", use: { ...devices["Pixel 5"] } },
  ],
  webServer: {
    command: `node web-preview/serve.mjs ${PORT}`,
    url: BASE,
    reuseExistingServer: false,
    timeout: 30000,
  },
});
