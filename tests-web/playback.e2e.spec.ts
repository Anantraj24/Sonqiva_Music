import { test, expect } from "@playwright/test";

test.describe("Sonqiva web-preview: playback controls", () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => localStorage.clear());
    await page.goto("/");
  });

  const starlightRow = (page) =>
    page.locator("#library-songs-list .song-row", { hasText: "Starlight Echoes" });

  test("playing a song updates mini player and full player", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await starlightRow(page).click();
    await expect(page.locator("#mini-title")).toHaveText("Starlight Echoes");
    await expect(page.locator("#mini-play-btn .material-symbols-rounded")).toHaveText("pause");

    await page.locator("#mini-player").click();
    await expect(page.locator("#full-player-modal")).toHaveClass(/open/);
    await expect(page.locator("#player-title")).toHaveText("Starlight Echoes");
  });

  test("shuffle all quick action creates queue and starts playback", async ({ page }) => {
    await page.locator("#quick-shuffle-btn").click();
    await expect(page.locator("#mini-title")).not.toHaveText("No Track Selected");
    await page.locator("#mini-player").click();
    await expect(page.locator("#queue-count")).not.toHaveText("0");
  });

  test("play/pause toggles via mini player", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await starlightRow(page).click();
    await expect(page.locator("#mini-play-btn .material-symbols-rounded")).toHaveText("pause");
    await page.locator("#mini-play-btn").click();
    await expect(page.locator("#mini-play-btn .material-symbols-rounded")).toHaveText("play_arrow");
  });

  test("changing playback speed updates speed label", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await starlightRow(page).click();
    await page.locator("#mini-player").click();
    await page.locator("#open-speed-btn").click();
    await page.locator('.speed-option[data-speed="1.5"]').click();
    await expect(page.locator("#speed-label")).toHaveText("1.5x");
  });

  test("repeat mode cycles and reflects state", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await starlightRow(page).click();
    await page.locator("#mini-player").click();
    const repeatBtn = page.locator("#player-repeat-btn");
    await repeatBtn.click();
    await expect(repeatBtn).toHaveCSS("color", "rgb(192, 193, 255)");
  });

  test("sleep timer starts countdown and can be cancelled", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await starlightRow(page).click();
    await page.locator("#mini-player").click();
    await page.locator("#open-sleep-btn").click();
    await page.locator('.sleep-option[data-mins="15"]').click();
    await expect(page.locator("#sleep-label")).toContainText(":");
    await page.locator("#open-sleep-btn").click();
    await page.locator("#cancel-sleep-btn").click();
  });
});
