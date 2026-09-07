import { test, expect } from "@playwright/test";

test.describe("Sonqiva web-preview: library & navigation flows", () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => {
      localStorage.clear();
    });
    await page.goto("/");
  });

  test("loads app shell and renders sample library counters", async ({ page }) => {
    await expect(page.locator(".bottom-nav")).toBeVisible();
    await expect(page.locator("#count-songs")).toHaveText("6");
    await expect(page.locator("#count-albums")).toHaveText("3");
    await expect(page.locator("#count-favs")).toHaveText("3");
  });

  test("bottom navigation switches between all 5 screens", async ({ page }) => {
    const nav = page.locator(".bottom-nav");
    const cases = [
      { btn: "Home", screen: "screen-home" },
      { btn: "Library", screen: "screen-library" },
      { btn: "Folders", screen: "screen-folders" },
      { btn: "Search", screen: "screen-search" },
      { btn: "Settings", screen: "screen-settings" },
    ];
    for (const c of cases) {
      await nav.getByRole("button", { name: c.btn }).click();
      await expect(page.locator(`#${c.screen}`)).toHaveClass(/active/);
    }
  });

  test("songs tab renders library song rows", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await expect(page.locator("#library-songs-list .song-row")).toHaveCount(6);
  });

  test("favorites tab shows only favorited tracks", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await page.locator('.tab-item[data-tab="favorites"]').click();
    await expect(page.locator("#library-favorites-list .song-row")).toHaveCount(3);
    await expect(page.locator("#library-favorites-list .song-name").nth(0)).toHaveText("Starlight Echoes");
  });

  test("sort songs changes list order", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await page.locator("#open-sort-sheet-btn").click();
    await page.locator('.sort-option[data-sort="title_desc"]').click();
    const titles = page.locator("#library-songs-list .song-name");
    await expect(titles.nth(0)).toHaveText("Starlight Echoes");
  });

  test("creates a playlist via dialog and lists it", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await page.locator('.tab-item[data-tab="playlists"]').click();
    await page.locator("#create-playlist-btn").click();
    await page.locator("#playlist-name-input").fill("QA Playlist");
    await page.locator("#dialog-confirm-btn").click();
    await expect(page.locator("#library-playlists-list")).toContainText("QA Playlist");
  });

  test("search filters songs by artist", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Search" }).click();
    await page.locator("#search-input").fill("Astral");
    await expect(page.locator("#search-results")).toContainText("Lunar Gravity");
    await expect(page.locator("#search-results")).toContainText("Cosmic Horizon");
  });
});
